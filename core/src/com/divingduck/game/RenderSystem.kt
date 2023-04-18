package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.divingduck.components.PositionComponent
import com.divingduck.components.RotationComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent

class RenderSystem(private val camera: OrthographicCamera, private val batch: Batch) : EntitySystem() {
    private val renderFamily = Family.all(PositionComponent::class.java, TextureComponent::class.java).get()

    override fun update(deltaTime: Float) {
        val renderEntities = engine.getEntitiesFor(renderFamily)

        batch.projectionMatrix = camera.combined
        batch.begin()

        for (entity in renderEntities) {
            val positionComponent = entity.getComponent(PositionComponent::class.java)
            val sizeComponent = entity.getComponent(SizeComponent::class.java)
            val textureComponent = entity.getComponent(TextureComponent::class.java)
            val rotationComponent = entity.getComponent(RotationComponent::class.java)

            val sprite = Sprite(textureComponent.texture)
            sprite.setPosition(positionComponent.position.x, positionComponent.position.y)
            sprite.setSize(sizeComponent.width, sizeComponent.height)

            if (rotationComponent != null) {
                sprite.setOriginCenter()
                sprite.rotation = -rotationComponent.rotation.toFloat()
            }

            sprite.draw(batch)
        }

        batch.end()
    }
}
