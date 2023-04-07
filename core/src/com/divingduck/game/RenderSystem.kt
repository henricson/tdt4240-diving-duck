package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ScreenUtils
import com.divingduck.components.PositionComponent
import com.divingduck.components.RotationComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent

class RenderSystem(
        private val camera: OrthographicCamera,
        private val batch: SpriteBatch
) : EntitySystem() {
    private val renderFamily = Family.all(PositionComponent::class.java, TextureComponent::class.java).get()

    override fun update(deltaTime: Float) {
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
    }
}