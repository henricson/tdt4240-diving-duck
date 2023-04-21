package com.divingduck.screen

import com.badlogic.drop.MainGame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.scenes.scene2d.ui.Table


class SettingsScreen private constructor(private val game: MainGame) : Screen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val stage = Stage()
    private val skin = Skin(Gdx.files.internal("default/skin/uiskin.json"))
    private val checkBox = CheckBox(" Music", skin)
    private val table = Table()
    private val gravitySlider: Slider

    companion object {
        private var instance: SettingsScreen? = null
        var gravity: Float = 600F
        var musicBoolean: Boolean = false
        var map: Int = 1
        var bird: Int = 1

        fun openSettings(game: MainGame) {
            if (instance == null) {
                instance = SettingsScreen(game)
            }
            game.setScreen(instance)
        }
    }

    init {
        camera.setToOrtho(false, 800F, 480F)
        Gdx.input.inputProcessor = stage

        val titleLabel = Label("Innstillinger", skin).apply {
            setPosition(Gdx.graphics.width / 2f, Gdx.graphics.height * 0.9f, Align.center)
        }
        val gravityLabel = Label("Tyngdekraft:", skin).apply {
            setPosition(Gdx.graphics.width / 2f - 110f - width, Gdx.graphics.height * 0.6f, Align.right)
        }

        // Create the gravity slider
        gravitySlider = Slider(400F, 800F, 1F, false, skin).apply {
            value = 600F
            width = Gdx.graphics.width * 0.6f
            setPosition(Gdx.graphics.width / 2f + 50f, Gdx.graphics.height * 0.6f, Align.center)
        }

        // Add a ChangeListener to the slider to update the gravity value
        gravitySlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                gravity = gravitySlider.value
            }
        })

        // Add the main menu button
        val mainMenuButton = TextButton("Hovedmeny", skin).apply {
            setPosition(Gdx.graphics.width * 0.9f, Gdx.graphics.height * 0.9f, Align.topRight)
        }

        mainMenuButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.setScreen(MainMenuScreen(game))
            }
        })

        // Initialize the checkmark
        checkBox.isChecked = musicBoolean
        checkBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                musicBoolean = checkBox.isChecked
            }
        })

        // Create the dropdown menu button (SelectBox)
        val selectBox = SelectBox<Int>(skin).apply {
            setItems(1, 2)
            setSelected(map)
        }

        // Add a ChangeListener to the SelectBox to update the map value
        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                map = selectBox.selected
            }
        })

        // Create a label for the Map text
        val mapLabel = Label("Bane:", skin)

        // Create the bird dropdown menu button (SelectBox) with "cute" and "bulky" options
        val birdSelectBox = SelectBox<String>(skin).apply {
            setItems("cute", "bulky")
            setSelected(if (bird == 1) "cute" else "bulky")
        }

        // Add a ChangeListener to the bird SelectBox to update the bird value
        birdSelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                bird = if (birdSelectBox.selected == "cute") 1 else 2
            }
        })

        // Create a label for the Bird text
        val birdLabel = Label("Fugl:", skin)

        // Initialize the table
        table.setFillParent(true)
        table.add(birdLabel).pad(10f).padRight(10f)
        table.add(birdSelectBox).pad(10f).padRight(50f)
        table.add(mapLabel).pad(10f).padRight(10f)
        table.add(selectBox).pad(10f).padRight(50f)
        table.add(checkBox).pad(10f).padLeft(50f)

        stage.addActor(table)
        stage.addActor(gravityLabel)
        stage.addActor(titleLabel)
        stage.addActor(gravitySlider)
        stage.addActor(mainMenuButton)
    }

    // Implement the required Screen interface methods here:

    override fun show() {

    }

    override fun render(delta: Float) {
        Gdx.input.inputProcessor = stage
        // Clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update and draw the stage
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
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
