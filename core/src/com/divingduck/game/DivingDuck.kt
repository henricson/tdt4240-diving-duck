package com.divingduck.game

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divingduck.states.GameState
import com.divingduck.states.MenuState

class DivingDuck : ApplicationAdapter() {
    lateinit var viewport: FitViewport
    lateinit var camera: OrthographicCamera
    lateinit var engine: Engine
    var virtualWidth = 0f
    var virtualHeight = 0f


    // Mapping of all states for the game.
    private var states: HashMap<String, Screen> = HashMap()
    // The current state that's rendered.
    private var currentState: Screen? = null

    /**
     * Set the initial screen for the game.
     */
    override fun create() {
        camera = OrthographicCamera()
        engine = Engine()
        virtualWidth = Gdx.graphics.width.toFloat()
        virtualHeight = Gdx.graphics.height.toFloat()
        viewport = FitViewport(virtualWidth, virtualHeight, camera)

        // Initialize the HashMap of screens
        states["menu"] = MenuState(this)
        states["gameplay"] = GameState(this)

        setState("menu")
    }


    override fun render() {

        // Render the current screen
        if (currentState != null) {
            currentState!!.render(Gdx.graphics.deltaTime)
        }
        engine.update(Gdx.graphics.deltaTime)
    }

    /**
     * Switches the game screen to the state corresponding to the provided state name.
     */
    fun setState(stateString: String) {
        val state = states[stateString]
        if (state != null) {
            if (currentState != null) {
                currentState!!.dispose()
            }
            currentState = state
            currentState?.show()
        }
    }

    override fun dispose() {
        if (currentState != null) {
            currentState!!.dispose()
        }
    }
}