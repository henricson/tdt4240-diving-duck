package com.divingduck.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture

class ParallaxComponent(val texture: Texture, var speed: Float) : Component
