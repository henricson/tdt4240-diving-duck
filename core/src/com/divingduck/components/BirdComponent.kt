package com.divingduck.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.divingduck.screen.SettingsScreen

class BirdComponent(
        var isJumping: Boolean = false,
        val jumpVector: Vector2 = Vector2(0F, 250F)
) : Component {
        var gravity: Float = -SettingsScreen.gravity

        init {
                println("Gravity: $gravity")
        }
}
