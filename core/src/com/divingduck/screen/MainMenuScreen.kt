package com.divingduck.screen

import com.badlogic.drop.MainGame
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera

class MainMenuScreen(game: MainGame) : Screen {
    private var camera : OrthographicCamera = OrthographicCamera();

    init {
        camera.setToOrtho(false, 800F, 480F)
    }


    override fun show() {

    }

    override fun render(delta: Float) {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {

    }
}