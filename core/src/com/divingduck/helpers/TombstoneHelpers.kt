package com.divingduck.helpers

import com.badlogic.gdx.Gdx

interface TombstoneListener {
    fun onSpawn(initialXPos: Float)
}

class TombstoneHelpers(val elapsedTimes: MutableList<Float>) {
    private val listeners = mutableListOf<TombstoneListener>()

    fun addListener(listener: TombstoneListener) {
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
                    listener.onSpawn(xPosition)
                }
                iterator.remove() // Remove the elapsed time from the list
            }
        }

        return xPositions
    }
}


