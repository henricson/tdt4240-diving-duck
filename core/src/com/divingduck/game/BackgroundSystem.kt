package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.divingduck.components.PositionComponent
import com.divingduck.components.TextureComponent

class BackgroundSystem(private val virtualWidth: Float) : EntitySystem() {
    private val backgroundFamily = Family.all(PositionComponent::class.java, TextureComponent::class.java).get()
    private var scrollingSpeed = 100f

    override fun update(deltaTime: Float) {
        val backgroundEntities = engine.getEntitiesFor(backgroundFamily)

        for (backgroundEntity in backgroundEntities) {
            val positionComponent = backgroundEntity.getComponent(PositionComponent::class.java)

            // Move background to the left
            positionComponent.position.x -= scrollingSpeed * deltaTime

            // Reset the background position when it's no longer visible
            if (positionComponent.position.x <= -virtualWidth) {
                positionComponent.position.x = virtualWidth
            }
        }
    }
}
