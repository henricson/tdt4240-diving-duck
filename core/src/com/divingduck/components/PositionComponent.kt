package com.divingduck.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class PositionComponent(
        val position: Vector2 = Vector2()
) : Component