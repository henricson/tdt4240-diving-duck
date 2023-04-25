package com.divingduck.game

import com.badlogic.gdx.Gdx

data class GameConfig(
    val virtualWidth: Float,
    val virtualHeight: Float,

) {
    val pipeHeight: Float = virtualHeight * 0.9f
    val pipeGap: Float = virtualHeight * 0.2f
    val pipeWidth: Float = virtualWidth * 0.1f
    val birdHeight: Float = virtualHeight * 0.1f
    val pipeSpawnTime: Float = 2f
    val scrollVelocity: Float = -150f
}
