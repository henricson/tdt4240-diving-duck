package com.divingduck.helpers

import com.badlogic.gdx.Gdx
import com.divingduck.game.GlobalEventListener
import com.divingduck.game.GlobalEvents


class TombstoneHelpers(private val elapsedTimes: MutableList<Float>) {
    private val listeners = mutableListOf<GlobalEventListener>()

    fun addListener(listener: GlobalEventListener) {
        listeners.add(listener)
    }

    fun getInitialXPositionsInSlidingWindow(
            velocityX: Float,
            currentElapsedTime: Float
    ): List<Float> {
        val screenWidth = Gdx.graphics.width
        val xPositions = mutableListOf<Float>()

        // Calculate the time it will take for the screen to make one full rotation
        val rotationTime = screenWidth / velocityX

        // Define the start and end times of the sliding window
        val windowEndTime = currentElapsedTime + rotationTime

        // Use an iterator to safely remove elements from the list while iterating over it
        val iterator = this.elapsedTimes.iterator()
        while (iterator.hasNext()) {
            val elapsedTime = iterator.next()
            // Check if the elapsed time is within the sliding window
            if (elapsedTime in currentElapsedTime..windowEndTime) {
                val xPosition = ((elapsedTime - currentElapsedTime) * velocityX) % screenWidth
                xPositions.add(xPosition)
                for (listener in listeners) {
                    listener.onEvent(GlobalEvents.SpawnGrave)
                }
                iterator.remove() // Remove the elapsed time from the list
            }
        }

        return xPositions
    }
}


