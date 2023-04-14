package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.divingduck.components.PipeComponent
import com.divingduck.components.PositionComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent

class PipeSystem(private val camera: OrthographicCamera, private val batch: Batch) : EntitySystem() {
    private val pipeFamily = Family.all(PipeComponent::class.java, PositionComponent::class.java).get()

    override fun update(deltaTime: Float) {
        val pipeEntities = engine.getEntitiesFor(pipeFamily)

        batch.projectionMatrix = camera.combined
        batch.begin()

        for (pipeEntity in pipeEntities) {
            val positionComponent = pipeEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = pipeEntity.getComponent(SizeComponent::class.java)
            val textureComponent = pipeEntity.getComponent(TextureComponent::class.java)

            // Move pipes to the left
            positionComponent.position.x -= PIPE_SPEED * deltaTime

            // Draw pipes using the provided textures
            batch.draw(
                textureComponent.texture,
                positionComponent.position.x,
                positionComponent.position.y,
                sizeComponent.width,
                sizeComponent.height
            )

            // Remove pipes that are no longer visible on the screen
            if (positionComponent.position.x < -PIPE_WIDTH) {
                engine.removeEntity(pipeEntity)
            }
        }

        batch.end()
    }

    fun stopMovement() {
        PIPE_SPEED = 0f
    }

    companion object {
        private var PIPE_SPEED = 150f
        private const val PIPE_WIDTH = 50f
    }
}
