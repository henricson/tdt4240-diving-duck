package com.divingduck.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.drop.MainGame
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divingduck.components.BirdComponent
import com.divingduck.components.CollisionComponent
import com.divingduck.components.PipeComponent
import com.divingduck.components.PositionComponent
import com.divingduck.components.RotationComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent
import com.divingduck.components.VelocityComponent
import com.divingduck.game.DivingDuck
import com.divingduck.game.RenderSystem
import com.divingduck.game.UpdateSystem

class GameScreen(game: Game) : Screen {
    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var engine: Engine
    private lateinit var birdEntity: Entity
    private lateinit var shapeRenderer: ShapeRenderer
    private var timeSinceLastPipe = 0f
    private lateinit var viewport: FitViewport
    private var virtualWidth = 0f
    private var virtualHeight = 0f
    private var pipeGap = 0f
    private var pipeHeight = 0f
    private var birdHeight = 0f

    override fun show() {
        batch = SpriteBatch()
        camera = OrthographicCamera()
        engine = Engine()
        shapeRenderer = ShapeRenderer()
        virtualWidth = Gdx.graphics.width.toFloat()
        virtualHeight = Gdx.graphics.height.toFloat()
        viewport = FitViewport(virtualWidth, virtualHeight, camera)
        pipeGap = virtualHeight * 0.2f
        pipeHeight = virtualHeight * 0.9f
        birdHeight = pipeGap * 0.3F

        // Load textures
        val birdTexture = Texture("duck.png") // Replace with your bird image path

        // Create entities
        birdEntity = createBirdEntity(birdTexture)

        // Add entities to the engine
        engine.addEntity(birdEntity)

        // Add systems to the engine
        engine.addSystem(UpdateSystem(virtualHeight))

        engine.addSystem(RenderSystem(shapeRenderer, camera, batch))
        setInputProcessor()
    }

    override fun render(delta: Float) {
        timeSinceLastPipe += Gdx.graphics.deltaTime
        if (timeSinceLastPipe > DivingDuck.PIPE_SPAWN_TIME) {
            spawnPipe()
            timeSinceLastPipe = 0f
        }

        engine.update(Gdx.graphics.deltaTime)
    }

    private fun spawnPipe() {
        val posY = MathUtils.random(virtualHeight * 0.1f, virtualHeight * 0.6f)

        // Create top pipe
        val topPipeEntity = createPipeEntity(posY + pipeGap)
        engine.addEntity(topPipeEntity)

        // Create bottom pipe
        val bottomPipeEntity = createPipeEntity(posY - pipeHeight)
        engine.addEntity(bottomPipeEntity)
    }

    private fun createPipeEntity(y: Float): Entity {
        val pipeEntity = Entity()
        pipeEntity.add(PositionComponent(Vector2(virtualWidth, y)))
        pipeEntity.add(PipeComponent())
        pipeEntity.add(SizeComponent(DivingDuck.PIPE_WIDTH, DivingDuck.PIPE_HEIGHT))
        pipeEntity.add(CollisionComponent())
        pipeEntity.add(VelocityComponent(Vector2(-150F, 0F)))
        return pipeEntity
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    private fun setInputProcessor() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
                birdComponent.isJumping = true
                return true
            }
        }
    }

    private fun createBirdEntity(birdTexture: Texture): Entity {
        val birdEntity = Entity()
        birdEntity.add(PositionComponent())
        birdEntity.add(RotationComponent())
        birdEntity.add(CollisionComponent())
        birdEntity.add(TextureComponent(birdTexture))
        birdEntity.add(VelocityComponent())
        val birdWidth = birdHeight * birdTexture.width / birdTexture.height.toFloat()
        birdEntity.add(SizeComponent(birdWidth, birdHeight))
        birdEntity.add(BirdComponent())

        return birdEntity
    }


    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun hide() {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val PIPE_WIDTH = 50f
        private const val PIPE_HEIGHT = 400f
        private const val PIPE_SPAWN_TIME = 1.5f
    }


}