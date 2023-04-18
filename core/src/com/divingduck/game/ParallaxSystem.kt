package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.divingduck.components.ParallaxComponent
import com.divingduck.components.PositionComponent
import com.divingduck.components.SizeComponent

class ParallaxSystem(private val camera: OrthographicCamera, private val batch: Batch) : EntitySystem() {
    private val parallaxFamily = Family.all(ParallaxComponent::class.java, PositionComponent::class.java, SizeComponent::class.java).get()

    override fun update(deltaTime: Float) {
        val parallaxEntities = engine.getEntitiesFor(parallaxFamily)

        batch.projectionMatrix = camera.combined
        batch.begin()

        for (parallaxEntity in parallaxEntities) {
            val parallaxComponent = parallaxEntity.getComponent(ParallaxComponent::class.java)
            val positionComponent = parallaxEntity.getComponent(PositionComponent::class.java)
            val sizeComponent = parallaxEntity.getComponent(SizeComponent::class.java)

            // Move the background
            positionComponent.position.x -= parallaxComponent.speed * deltaTime

            // Draw the parallax background
            batch.draw(
                parallaxComponent.texture,
                positionComponent.position.x,
                positionComponent.position.y,
                sizeComponent.width,
                sizeComponent.height
            )

            // Reposition the background to create an infinite scrolling effect
            if (positionComponent.position.x + sizeComponent.width < camera.position.x - camera.viewportWidth / 2) {
                positionComponent.position.x += 2 * sizeComponent.width
            }
        }

        batch.end()
    }
}
