package com.divingduck.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class VelocityComponent(
        val velocity: Vector2 = Vector2()
) : Component