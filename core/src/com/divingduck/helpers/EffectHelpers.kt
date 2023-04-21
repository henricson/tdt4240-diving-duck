package com.divingduck.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Timer
import com.divingduck.screen.SettingsScreen

class EffectHelpers {
    companion object {
        fun fadeOut(music : Sound, musicId: Long) {
            val duration = 5.0f // Duration in seconds
            val updateInterval = 1 / 60f

            Timer.schedule(
                object : Timer.Task() {
                    var elapsedTime = 0f
                    override fun run() {
                        elapsedTime += Gdx.graphics.deltaTime
                        var progress = elapsedTime / duration
                        if (progress > 1.0f) {
                            progress = 1.0f
                        }

                        // Use interpolation to calculate pitch and volume
                        val pitch = Interpolation.linear.apply(1.0f, 0.0f, progress)
                        val volume = Interpolation.fade.apply(1.0f, 0.0f, progress)
                        if (SettingsScreen.musicBoolean) {
                            music.setPitch(musicId, pitch)
                            music.setVolume(musicId, volume)
                        }
                        if (progress >= 1.0f) {
                            music.stop()
                            cancel()
                        }
                    }
                },
                0f,
                updateInterval,
            )
        }
    }
}