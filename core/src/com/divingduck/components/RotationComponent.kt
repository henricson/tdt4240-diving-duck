package com.divingduck.components

import com.badlogic.ashley.core.Component

class RotationComponent (
        // Number of degrees the component is to be rotated (clockwise)
        var rotation: Int = 0
) : Component