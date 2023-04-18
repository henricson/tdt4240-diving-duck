package com.badlogic.drop

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.divingduck.screen.GameScreen
import com.divingduck.screen.MainMenuScreen

class MainGame : Game() {
    var batch: SpriteBatch? = null
    var font: BitmapFont? = null
    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont() // use libGDX's default Arial font
        setScreen(MainMenuScreen(this))
    }

    override fun render() {
        super.render() // important!
    }

    override fun dispose() {
        batch!!.dispose()
        font!!.dispose()
    }
}