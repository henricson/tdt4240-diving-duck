package com.divingduck.game

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Timer
import com.divingduck.client.apis.ScoreApi
import com.divingduck.client.models.ScoreDTO
import com.divingduck.components.BirdComponent
import com.divingduck.components.CollisionComponent
import com.divingduck.components.GameoverOverlayComponent
import com.divingduck.components.ParallaxComponent
import com.divingduck.components.PipeComponent
import com.divingduck.components.PositionComponent
import com.divingduck.components.RotationComponent
import com.divingduck.components.ScoreComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent
import com.divingduck.components.TombstoneComponent
import com.divingduck.components.VelocityComponent
import com.divingduck.screen.SettingsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.atan2

 enum class GlobalEvents {
GameOver, Playing, SpawnGrave
}

interface GlobalEventListener {
    fun onEvent(event: GlobalEvents)
}


class UpdateSystem(private val virtualHeight: Float) : EntitySystem() {
    private var listeners = mutableListOf<GlobalEventListener>();
    private val birdFamily = Family.all(BirdComponent::class.java).get()
    private val tombstoneFamily = Family.all(TombstoneComponent::class.java).get()
    private val pipeFamily = Family.all(PipeComponent::class.java).get()
    private val velocityFamily = Family.all(VelocityComponent::class.java, PositionComponent::class.java).get()

    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val parallaxMapper = ComponentMapper.getFor(ParallaxComponent::class.java)
    private val sizeMapper = ComponentMapper.getFor(SizeComponent::class.java)
    private val apiClient = ScoreApi("https://divingduckserver-v2.azurewebsites.net/")
    private var shouldReportScore = true;
    private var totalTimePassed = 0f;

    fun addListener(listener: GlobalEventListener) {
        listeners.add(listener);
    }


    private fun onCollision() {
        listeners.forEach{
            it.onEvent(GlobalEvents.GameOver);
        }
    }

    override fun update(deltaTime: Float) {
        updateState(deltaTime);
    }

    private fun updateState(deltaTime: Float) {
        updateBird(deltaTime)
        updatePipes(deltaTime)
        updatePosition(deltaTime)
        updateScore()
    }

    private fun updateScore() {
        val birdEntity = engine.getEntitiesFor(birdFamily).first()
        val birdPosition = positionMapper.get(birdEntity)
        val birdScore = birdEntity.getComponent(ScoreComponent::class.java)

        val pipeEntities = engine.getEntitiesFor(pipeFamily)
        for (pipeEntity in pipeEntities) {
            val pipePosition = positionMapper.get(pipeEntity)
            val pipeSize = sizeMapper.get(pipeEntity)
            val pipeComponent = pipeEntity.getComponent(PipeComponent::class.java)

            if (birdPosition.position.x > pipePosition.position.x + pipeSize.width) {
                // Only consider pipeDown for scoring
                if (!pipeComponent.isScored && pipePosition.position.y < birdPosition.position.y) {
                    pipeComponent.isScored = true
                    birdScore.score += 1
                }
            }
        }
    }
    private fun updateTombstone(deltaTime: Float) {
        val tombstoneEntities = engine.getEntitiesFor(tombstoneFamily)
        for (birdEntity in tombstoneEntities) {

        }
    }

    private fun updateBird(deltaTime: Float) {
        // Update bird state
        val birdEntities = engine.getEntitiesFor(birdFamily)
        for (birdEntity in birdEntities) {
            val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
            val positionComponent = birdEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = birdEntity.getComponent(SizeComponent::class.java)
            val rotationComponent = birdEntity.getComponent(RotationComponent::class.java)
            val velocityComponent = birdEntity.getComponent(VelocityComponent::class.java)

            // Fall and stay within screen
            if(positionComponent.position.y <= 0 || positionComponent.position.y + sizeComponent.height > virtualHeight) {
                velocityComponent.velocity.y = 0F
                positionComponent.position.y = MathUtils.clamp(positionComponent.position.y, 0f, virtualHeight - sizeComponent.height)
            }else{
                velocityComponent.velocity.y += birdComponent.gravity * deltaTime
            }

            // Jumping
            if (birdComponent.isJumping) {
                velocityComponent.velocity.set(birdComponent.jumpVector)
                birdComponent.isJumping = false
            }

            // Rotating
            rotationComponent.rotation = (-atan2(velocityComponent.velocity.y.toFloat(), 150F) * 180 / Math.PI).toInt()

            // Collisions
            if (collisionMapper.has(birdEntity)) {
                val bounds = Rectangle(positionComponent.position.x, positionComponent.position.y, sizeComponent.width, sizeComponent.height)
                val pipeEntities = engine.getEntitiesFor(Family.all(PipeComponent::class.java).get())
                val velocityEntities = engine.getEntitiesFor(Family.all(VelocityComponent::class.java, PositionComponent::class.java).get())
                val parallaxEntities = engine.getEntitiesFor(Family.all(ParallaxComponent::class.java).get())
                pipeEntities.forEach { pipeEntity ->
                    val pipePosition = positionMapper.get(pipeEntity)
                    val pipeSize = sizeMapper.get(pipeEntity)
                    val pipeBounds = Rectangle(pipePosition.position.x, pipePosition.position.y, pipeSize.width, pipeSize.height)

                    if (bounds.overlaps(pipeBounds)) {
                        velocityEntities.forEach{ velocityEntity ->
                            val velocity = velocityMapper.get(velocityEntity)
                            velocity.velocity.set(0f, velocity.velocity.y)
                        }
                        parallaxEntities.forEach { parallaxEntity ->
                            val parallaxComponent = parallaxMapper.get(parallaxEntity)
                            parallaxComponent.speed = 0f
                        }
                        if(shouldReportScore) {
                            onCollision()
                             // Update four times as often for smoother transition
                            // Do the networking on a background thread
                            GlobalScope.launch(Dispatchers.IO) {
                                apiClient.apiScorePost(ScoreDTO(totalTimePassed, 1))
                            }
                            shouldReportScore = false
                        }
                    }
                }
            }
        }
    }

    private fun updatePipes(deltaTime: Float) {
        totalTimePassed += deltaTime;
        // Update pipe state
        val pipeEntities = engine.getEntitiesFor(pipeFamily)
        for (pipeEntity in pipeEntities) {
            val positionComponent = pipeEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = pipeEntity.getComponent(SizeComponent::class.java)
            val velocityComponent = pipeEntity.getComponent(VelocityComponent::class.java)

            // Remove pipes that are no longer visible on the screen
            if (positionComponent.position.x < -sizeComponent.width) {
                engine.removeEntity(pipeEntity)
            }
        }
    }

    private fun updatePosition(deltaTime: Float) {
        // Move things with both VelocityComponent and PositionComponent
        val velocityEntities = engine.getEntitiesFor(velocityFamily)
        for (velocityEntity in velocityEntities) {
            val positionComponent = velocityEntity.getComponent(PositionComponent::class.java)
            val velocityComponent = velocityEntity.getComponent(VelocityComponent::class.java)


            positionComponent.position.add(velocityComponent.velocity.cpy().scl(deltaTime))
        }
    }
}