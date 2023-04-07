package com.divingduck.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divingduck.components.*

class DivingDuck : ApplicationAdapter() {
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
    override fun create() {
        batch = SpriteBatch()
        camera = OrthographicCamera()
        engine = Engine()
        shapeRenderer = ShapeRenderer()
        virtualWidth = Gdx.graphics.width.toFloat()
        virtualHeight = Gdx.graphics.height.toFloat()
        viewport = FitViewport(virtualWidth, virtualHeight, camera)
        pipeGap = virtualHeight * 0.1f
        pipeHeight = virtualHeight * 0.9f
        birdHeight = pipeGap * 0.5f

        // Load textures
        val birdTexture = Texture("duck.png") // Replace with your bird image path

        // Create entities
        birdEntity = createBirdEntity(birdTexture)

        // Add entities to the engine
        engine.addEntity(birdEntity)
        // Add systems to the engine
        engine.addSystem(BirdSystem(virtualHeight))
        engine.addSystem(RenderSystem(camera, batch))
        engine.addSystem(PipeSystem(shapeRenderer, camera, batch))

        setInputProcessor()
    }


    override fun render() {
        timeSinceLastPipe += Gdx.graphics.deltaTime
        if (timeSinceLastPipe > PIPE_SPAWN_TIME) {
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
        val bottomPipeEntity = createPipeEntity(posY - pipeHeight - pipeGap)
        engine.addEntity(bottomPipeEntity)
    }

    private fun createPipeEntity(y: Float): Entity {
        val pipeEntity = Entity()
        pipeEntity.add(PositionComponent(Vector2(virtualWidth, y)))
        pipeEntity.add(PipeComponent())
        pipeEntity.add(SizeComponent(PIPE_WIDTH, PIPE_HEIGHT))
        pipeEntity.add(CollisionComponent())
        return pipeEntity
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    companion object {
        private const val PIPE_WIDTH = 50f
        private const val PIPE_HEIGHT = 400f
        private const val PIPE_SPAWN_TIME = 1.5f
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
        val birdWidth = birdHeight * birdTexture.width / birdTexture.height.toFloat()
        birdEntity.add(SizeComponent(birdWidth, birdHeight))
        birdEntity.add(BirdComponent())
        return birdEntity
    }

}