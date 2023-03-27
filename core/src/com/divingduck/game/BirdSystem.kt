package com.divingduck.game

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.divingduck.components.*

class BirdSystem(private val virtualHeight: Float) : EntitySystem() {
    private val birdFamily = Family.all(BirdComponent::class.java, PositionComponent::class.java).get()
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val sizeMapper = ComponentMapper.getFor(SizeComponent::class.java)

    override fun update(deltaTime: Float) {
        val birdEntities = engine.getEntitiesFor(birdFamily)

        for (birdEntity in birdEntities) {
            val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
            val positionComponent = birdEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = birdEntity.getComponent(SizeComponent::class.java)

            if(positionComponent.position.y > 0) {
                birdComponent.velocity.y +=  GRAVITY * deltaTime

            }

            if (birdComponent.isJumping) {
                birdComponent.velocity.y = JUMP_VELOCITY
                birdComponent.isJumping = false
            }
            positionComponent.position.y = MathUtils.clamp(positionComponent.position.y, 0f, virtualHeight - sizeComponent.height)
            positionComponent.position.add(birdComponent.velocity.cpy().scl(deltaTime))

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