package com.divingduck.game

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.divingduck.components.*
import io.swagger.client.apis.ScoreApi
import io.swagger.client.models.ScoreDTO
import kotlin.math.atan2
import kotlinx.coroutines.*
import java.util.concurrent.Executors

private val apiClient = ScoreApi("https://divingduckserver-v2.azurewebsites.net/")

suspend fun postApiScoreAsync(pipePositionX: Int) {
    apiClient.apiScorePost(ScoreDTO(pipePositionX, 1))
}


class BirdSystem(private val virtualHeight: Float) : EntitySystem() {
    private val birdFamily = Family.all(BirdComponent::class.java, PositionComponent::class.java).get()
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val sizeMapper = ComponentMapper.getFor(SizeComponent::class.java)
    private var shouldReportScore = true;


    override fun update(deltaTime: Float) {
        val birdEntities = engine.getEntitiesFor(birdFamily)

        for (birdEntity in birdEntities) {
            val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
            val positionComponent = birdEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = birdEntity.getComponent(SizeComponent::class.java)
            val rotationComponent = birdEntity.getComponent(RotationComponent::class.java)



            // TODO: Når hastighet er integrert med posisjon burde vi ha inn at rotasjonen er beregnet av
            //  fuggelens hastighet i både x og y retning (altså ikke en 200F under)

            rotationComponent.rotation = (-atan2(birdComponent.velocity.y.toFloat(), 200F) * 180 / Math.PI).toInt();

            if (collisionMapper.has(birdEntity)) {
                val collidable = collisionMapper.get(birdEntity)
                val bounds = Rectangle(positionComponent.position.x, positionComponent.position.y, sizeComponent.width, sizeComponent.height)
                val pipeEntities = engine.getEntitiesFor(Family.all(PipeComponent::class.java, PositionComponent::class.java, CollisionComponent::class.java).get())

                pipeEntities.forEach { pipeEntity ->
                    val pipePosition = positionMapper.get(pipeEntity)
                    val pipeSize = sizeMapper.get(pipeEntity)
                    val pipeBounds = Rectangle(pipePosition.position.x, pipePosition.position.y, pipeSize.width, pipeSize.height)

                    if (bounds.overlaps(pipeBounds)) {
                        // Collision detected
                        engine.getSystem(PipeSystem::class.java).stopMovement()

                        if(shouldReportScore) {
                            println("Collision detected! Score is ${pipePosition.position.x}")
                            shouldReportScore = false
                        }

                    }
                }
            }
        }
    }

    companion object {
        private const val GRAVITY = -500f
        private const val JUMP_VELOCITY = 200f
    }
}