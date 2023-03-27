package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.divingduck.components.PipeComponent
import com.divingduck.components.PositionComponent


class PipeSystem : EntitySystem() {
    private val pipeFamily = Family.all(PipeComponent::class.java, PositionComponent::class.java).get()

    override fun update(deltaTime: Float) {
        val pipeEntities = engine.getEntitiesFor(pipeFamily)

        for (pipeEntity in pipeEntities) {
            val positionComponent = pipeEntity.getComponent(PositionComponent::class.java)

            // Move pipes to the left
            positionComponent.position.x -= PIPE_SPEED * deltaTime

            // Remove pipes that are no longer visible on the screen
            if (positionComponent.position.x < -PIPE_WIDTH) {
                engine.removeEntity(pipeEntity)
            }
        }
    }

    fun stopMovement() {
        PIPE_SPEED = 0f
    }

    public companion object {
        private var PIPE_SPEED = 150f
        private const val PIPE_WIDTH = 50f
    }
}