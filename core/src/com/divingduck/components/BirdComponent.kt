package com.divingduck.components
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class BirdComponent(
        var isJumping: Boolean = false,
        val jumpVector: Vector2 = Vector2(0F, 250F),
        var gravity: Float = -600F
) : Component
