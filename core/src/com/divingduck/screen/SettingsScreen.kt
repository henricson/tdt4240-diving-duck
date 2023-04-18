package com.divingduck.screen

import com.badlogic.drop.MainGame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport


class SettingsScreen private constructor(private val game: MainGame) : Screen {

    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("default/skin/uiskin.json"))

    private val gravitySlider: Slider

    companion object {
        private var instance: SettingsScreen? = null
        var gravity: Float = -600F

        fun openSettings(game: MainGame) {
            if (instance == null) {
                instance = SettingsScreen(game)
            }
            game.setScreen(instance)
        }
    }

    init {
        val titleLabel = Label("Innstillinger", skin).apply {
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height * 0.9f, Align.center)
        }
        stage.addActor(titleLabel)

        // Create the gravity slider
        gravitySlider = Slider(-900F, -300F, 1F, false, skin).apply {
            value = -600F
            width = Gdx.graphics.width * 0.8f
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height * 0.6f, Align.center)
        }
        stage.addActor(gravitySlider)

        // Add a listener to the slider to update the gravity value
        gravitySlider.addListener { event ->
            if (event.isHandled) {
                gravity = gravitySlider.value
                true
            } else {
                false
            }
        }

        // Add the main menu button
        val mainMenuButton = TextButton("Main menu", skin).apply {
            setPosition(Gdx.graphics.width * 0.9f, Gdx.graphics.height * 0.9f, Align.topRight)
        }
        stage.addActor(mainMenuButton)

        mainMenuButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.setScreen(MainMenuScreen(game))
            }
        })

        Gdx.input.inputProcessor = stage
    }


    fun changeGravity(newGravity: Float) {
        gravity = newGravity
    }

    // Implement the required Screen interface methods here:

    override fun show() {

    }

    override fun render(delta: Float) {
        // Clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update and draw the stage
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        // Implement the logic to handle the resizing of the settings screen
        stage.viewport.update(width, height, true)
    }

    override fun pause() {
        // Implement the logic to pause the settings screen
    }

    override fun resume() {
        // Implement the logic to resume the settings screen
    }

    override fun hide() {
        // Implement the logic to hide the settings screen
    }

    override fun dispose() {
        // Implement the logic to dispose of resources used by the settings screen
        stage.dispose()
    }
}
