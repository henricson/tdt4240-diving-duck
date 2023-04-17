package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.divingduck.components.*

class RenderSystem(
    private val shapeRenderer : ShapeRenderer,
    private val camera: OrthographicCamera,
    private val batch: SpriteBatch,
) : EntitySystem() {
    private val renderFamily = Family.all(PositionComponent::class.java, TextureComponent::class.java).get()
    private val pipeFamily = Family.all(PipeComponent::class.java).get()

    override fun update(deltaTime: Float) {

        // Render textures
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        batch.begin()
        val renderEntities = engine.getEntities();
        for (renderEntity in renderEntities) {

            val positionComponent = renderEntity.getComponent(PositionComponent::class.java)
            val textureComponent = renderEntity.getComponent(TextureComponent::class.java)
            val sizeComponent = renderEntity.getComponent(SizeComponent::class.java)
            val rotationComponent = renderEntity.getComponent(RotationComponent::class.java)
            val texture = textureComponent?.texture

            // SpriteBatch.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation);

            if (texture != null && sizeComponent != null) {
                if(rotationComponent === null) {
                    batch.draw(
                        texture,
                        positionComponent.position.x,
                        positionComponent.position.y,
                        sizeComponent.width,
                        sizeComponent.height,
                        )
                }else{
                    batch.draw(
                        TextureRegion(texture, 0, 0, texture.width, texture.height),
                        positionComponent.position.x,
                        positionComponent.position.y,
                        (sizeComponent.width / 2),
                        (sizeComponent.height / 2),
                        sizeComponent.width,
                        sizeComponent.height,
                        1F,1F,
                        -rotationComponent.rotation.toFloat()
                    )
                }
            }
        }

        batch.end()

        // Draw pipes, TODO: When the pipes textures are inserted, this code will not be needed
        val pipeEntities = engine.getEntitiesFor(pipeFamily)

        for (pipeEntity in pipeEntities) {

            val positionComponent = pipeEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = pipeEntity.getComponent(SizeComponent::class.java)

            // Draw pipes as white rectangles
            shapeRenderer.projectionMatrix = camera.combined
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.rect(positionComponent.position.x, positionComponent.position.y, sizeComponent.width, sizeComponent.height)
            shapeRenderer.end()
            camera.update()
            batch.projectionMatrix = camera.combined
        }

    }
}