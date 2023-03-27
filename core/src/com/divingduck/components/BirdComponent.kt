package com.divingduck.components
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class BirdComponent(
        val velocity: Vector2 = Vector2(),
        var isJumping: Boolean = false,
        var rotation: Float = 0f
) : Component
