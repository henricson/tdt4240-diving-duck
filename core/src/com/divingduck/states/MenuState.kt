package com.divingduck.states

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.divingduck.game.DivingDuck

class MenuState(private var game: DivingDuck) : Screen {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont

    override fun show() {
        batch = SpriteBatch()
        font = BitmapFont()
        font.data.setScale(2f)
    }

    override fun render(deltaTime: Float) {
        batch.projectionMatrix = game.camera.combined
        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "Hello, world!", 100f, 240f)
        batch.end()
    }

    /**
     * Resize the viewport provided from the states internal stage object
     */
    override fun resize(width: Int, height: Int) {
        // Provide the center camera value as "true"
        game.viewport.update(width, height, true)
    }

    override fun pause() {
        // Handle pause of the state
    }

    override fun resume() {
        // Handle resume after pause
    }

    override fun hide() {
        // Hide the content of the state
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}