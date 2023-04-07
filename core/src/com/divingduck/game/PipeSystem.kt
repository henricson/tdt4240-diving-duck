package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.divingduck.apiclient.apis.ScoreApi
import com.divingduck.apiclient.models.ScoreDTO
import com.divingduck.components.PipeComponent
import com.divingduck.components.PositionComponent
import com.divingduck.components.SizeComponent


class PipeSystem(private val shapeRenderer : ShapeRenderer, private val camera : OrthographicCamera, private val batch : Batch) : EntitySystem() {
    private val pipeFamily = Family.all(PipeComponent::class.java, PositionComponent::class.java).get()

    override fun update(deltaTime: Float) {
        val pipeEntities = engine.getEntitiesFor(pipeFamily)

        for (pipeEntity in pipeEntities) {
            val positionComponent = pipeEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = pipeEntity.getComponent(SizeComponent::class.java)

            // Move pipes to the left
            positionComponent.position.x -= PIPE_SPEED * deltaTime
            // Draw pipes as white rectangles
            shapeRenderer.projectionMatrix = camera.combined
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.rect(positionComponent.position.x, positionComponent.position.y, sizeComponent.width, sizeComponent.height)
            shapeRenderer.end()
            camera.update()
            batch.projectionMatrix = camera.combined
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