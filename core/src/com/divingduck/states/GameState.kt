package com.divingduck.states

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.divingduck.components.*
import com.divingduck.game.BirdSystem
import com.divingduck.game.DivingDuck
import com.divingduck.game.PipeSystem
import com.divingduck.game.RenderSystem

class GameState(private var game: DivingDuck) : Screen {

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private lateinit var birdEntity: Entity
    private var timeSinceLastPipe = 0f
    private var pipeGap = 0f
    private var pipeHeight = 0f
    private var birdHeight = 0f

    override fun show() {
        batch = SpriteBatch()
        pipeGap = game.virtualHeight * 0.1f
        pipeHeight = game.virtualHeight * 0.9f
        birdHeight = pipeGap * 0.5f
        shapeRenderer = ShapeRenderer()

        // Load textures
        val birdTexture = Texture("duck.png") // Replace with your bird image path

        // Create entities
        this.birdEntity = createBirdEntity(birdTexture)

        // Add entities to the engine
        game.engine.addEntity(birdEntity)
        // Add systems to the engine
        game.engine.addSystem(BirdSystem(game.virtualHeight))
        game.engine.addSystem(RenderSystem(game.camera, batch))
        game.engine.addSystem(PipeSystem(shapeRenderer, game.camera, batch))

        setInputProcessor()
    }

    override fun render(deltaTime: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)

        timeSinceLastPipe += deltaTime
        if (timeSinceLastPipe > PIPE_SPAWN_TIME) {
            spawnPipe()
            timeSinceLastPipe = 0f
        }
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
    }

    private fun spawnPipe() {
        val posY = MathUtils.random(game.virtualHeight * 0.1f, game.virtualHeight * 0.6f)

        // Create top pipe
        val topPipeEntity = createPipeEntity(posY + pipeGap)
        game.engine.addEntity(topPipeEntity)

        // Create bottom pipe
        val bottomPipeEntity = createPipeEntity(posY - pipeHeight - pipeGap)
        game.engine.addEntity(bottomPipeEntity)
    }

    private fun createPipeEntity(y: Float): Entity {
        val pipeEntity = Entity()
        pipeEntity.add(PositionComponent(Vector2(game.virtualWidth, y)))
        pipeEntity.add(PipeComponent())
        pipeEntity.add(SizeComponent(PIPE_WIDTH, PIPE_HEIGHT))
        pipeEntity.add(CollisionComponent())
        return pipeEntity
    }

    companion object {
        private const val PIPE_WIDTH = 50f
        private const val PIPE_HEIGHT = 400f
        private const val PIPE_SPAWN_TIME = 1.5f
    }

    private fun setInputProcessor() {
        println("Processor set")
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
                birdComponent.isJumping = true
                return true
            }
        }
    }

    private fun createBirdEntity(birdTexture: Texture): Entity {
        println("Bird created")
        val birdEntity = Entity()
        birdEntity.add(PositionComponent())
        birdEntity.add(RotationComponent())
        birdEntity.add(CollisionComponent())
        birdEntity.add(TextureComponent(birdTexture))
        val birdWidth = birdHeight * birdTexture.width / birdTexture.height.toFloat()
        birdEntity.add(SizeComponent(birdWidth, birdHeight))
        birdEntity.add(BirdComponent())
        return birdEntity
    }
}