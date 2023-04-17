package com.divingduck.screen

import com.badlogic.drop.MainGame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align


enum class Screens {
    MainMenu, GamePlaying
}
interface SceneChange {
    fun onScreenChange(newScreen : Screens)
}

class MainMenuScreen(var game: MainGame) : Screen {
    private var camera : OrthographicCamera = OrthographicCamera();
    private val stage = Stage()
    private val skin = Skin(Gdx.files.internal("default/skin/uiskin.json"))
    private val usernameInput = TextField("", skin)

    init {
        camera.setToOrtho(false, 800F, 480F);
        Gdx.input.inputProcessor = stage

        // Create UI elements
        val titleLabel = Label("Diving Duck", skin, "default").apply {
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height * 0.75f, Align.center)
        }

        val playButton = TextButton("Start spillet", skin).apply {
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height * 0.5f, Align.center)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.setScreen(GameScreen(game));
                }
            })
        }

        usernameInput.apply {
            messageText = "Enter your username"
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height * 0.25f, Align.center)
            isVisible = false
        }

        stage.addActor(titleLabel)
        stage.addActor(playButton)
        stage.addActor(usernameInput)
    }


    override fun show() {

    }

    private fun getStoredUsername(): String? {
        // Retrieve the stored username from local storage
        // If no username is stored, return null
        return null
    }

    private fun saveUsername(username: String) {
        // Save the username to local storage
    }

    private fun startGame(username: String) {
        // Switch to the game screen and pass the username as a parameter
    }

    private fun showUsernameDialog() {
        val dialog = Dialog("Enter Username", skin).apply {
            text("Please enter your username:")
            contentTable.add(usernameInput).width(200f).padTop(10f)
            button("OK", true)
            button("Cancel", false)
            key(com.badlogic.gdx.Input.Keys.ENTER, true)
            key(com.badlogic.gdx.Input.Keys.ESCAPE, false)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val username = usernameInput.text
                    saveUsername(username)
                    startGame(username)
                }
            })
            show(stage)
        }
        usernameInput.isVisible = true
        stage.keyboardFocus = usernameInput
    }

    override fun render(delta: Float) {
        // Clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Draw the stage
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }
}