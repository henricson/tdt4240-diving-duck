package com.divingduck.game

data class GameConfig(
    val virtualWidth: Float,
    val virtualHeight: Float,

) {
    val pipeHeight: Float = virtualHeight * 0.9f
    val pipeGap: Float = virtualHeight * 0.2f
    val pipeWidth: Float = virtualWidth * 0.1f
    val birdHeight: Float = virtualHeight * 0.1f
    val pipeSpawnTime: Float = 1.5f
    val scrollVelocity: Float = -150f
}
