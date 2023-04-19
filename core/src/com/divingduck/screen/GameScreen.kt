package com.divingduck.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divingduck.client.apis.ScoreApi
import com.divingduck.components.BirdComponent
import com.divingduck.components.CollisionComponent
import com.divingduck.components.GameoverOverlayComponent
import com.divingduck.components.ParallaxComponent
import com.divingduck.components.PipeComponent
import com.divingduck.components.PositionComponent
import com.divingduck.components.RotationComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent
import com.divingduck.components.TombstoneComponent
import com.divingduck.components.VelocityComponent
import com.divingduck.game.ParallaxSystem
import com.divingduck.game.RenderSystem
import com.divingduck.game.UpdateSystem
import com.divingduck.helpers.TombstoneHelpers
import com.divingduck.helpers.TombstoneListener
import kotlin.properties.Delegates

class GameScreen(game: Game) : Screen, TombstoneListener {
    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var engine: Engine
    private lateinit var birdEntity: Entity
    private var timeSinceLastPipe = 0f
    private lateinit var viewport: FitViewport
    private var virtualWidth = 0f
    private var virtualHeight = 0f
    private var pipeGap = 0f
    private var pipeHeight = 0f
    private var pipeWidth = 0f
    private var birdHeight = 0f

    private lateinit var topPipeTexture: Texture
    private lateinit var birdTexture: Texture
    private lateinit var bottomPipeTexture: Texture
    private var totalTimePassed = 0f;
    private lateinit var calculationHelpers: TombstoneHelpers;
    private lateinit var music : Sound;
    private lateinit var ambient :Sound;
    private val scoreApi = ScoreApi("https://divingduckserver-v2.azurewebsites.net/")
    private var musicId by Delegates.notNull<Long>();

    override fun show() {
        val previousTimesElapsed = scoreApi.apiScoreGet().map { it.timeElapsed }.filterIsInstance<Float>()
        calculationHelpers = TombstoneHelpers(previousTimesElapsed.toMutableList())
        calculationHelpers.addListener(this);
        batch = SpriteBatch()
        camera = OrthographicCamera()
        engine = Engine()
        virtualWidth = Gdx.graphics.width.toFloat()
        virtualHeight = Gdx.graphics.height.toFloat()
        viewport = FitViewport(virtualWidth, virtualHeight, camera)
        pipeGap = virtualHeight * 0.1f
        pipeHeight = virtualHeight * 0.9f
        pipeWidth = virtualHeight * 0.1f
        birdHeight = pipeGap * 0.5f

        music = Gdx.audio.newSound(Gdx.files.internal("sounds/lofistudy.mp3"));
        ambient = Gdx.audio.newSound(Gdx.files.internal("sounds/underwater.mp3"));

        // Start playing the music
        musicId = music.play(1.0f);
        ambient.play()

        // Load textures
        birdTexture = Texture("duck.png") // Replace with your bird image path
        topPipeTexture = Texture("pipeUp.png")
        bottomPipeTexture = Texture("pipeDown.png")

        val backgroundTexture = Texture("background.png") // Replace with your background image path

        // Create background entities
        val backgroundEntity1 = createBackgroundEntity(0f, backgroundTexture, 150f)
        val backgroundEntity2 = createBackgroundEntity(virtualWidth, backgroundTexture, 150f)

        // Add background entities to the engine
        engine.addEntity(backgroundEntity1)
        engine.addEntity(backgroundEntity2)

        // Create entities
        birdEntity = createBirdEntity(birdTexture)

        // Add entities to the engine
        engine.addEntity(birdEntity)

        // Add systems to the engine
        engine.addSystem(ParallaxSystem(camera, batch))
        engine.addSystem(RenderSystem(camera, batch))
        engine.addSystem(UpdateSystem(virtualHeight, music, musicId))

        engine.addSystem(RenderSystem(camera, batch))
        setInputProcessor();

    }



    override fun render(delta: Float) {
        timeSinceLastPipe += Gdx.graphics.deltaTime
        totalTimePassed += Gdx.graphics.deltaTime
        if (timeSinceLastPipe > PIPE_SPAWN_TIME) {
            spawnPipe()
            timeSinceLastPipe = 0f
        }

        calculationHelpers.getInitialXPositionsInSlidingWindow(150f, totalTimePassed)

        engine.update(Gdx.graphics.deltaTime)
    }

    private fun spawnPipe() {
        val posY = MathUtils.random(virtualHeight * 0.1f, virtualHeight * 0.6f)

        // Create top pipe
        val topPipeEntity = createPipeEntity(posY + pipeGap, topPipeTexture)
        engine.addEntity(topPipeEntity)

        // Create bottom pipe
        val bottomPipeEntity = createPipeEntity(posY - pipeHeight - pipeGap, bottomPipeTexture)
        engine.addEntity(bottomPipeEntity)
    }


    private fun createPipeEntity(y: Float, pipeTexture: Texture): Entity {
        val pipeEntity = Entity()
        pipeEntity.add(PositionComponent(Vector2(virtualWidth, y)))
        pipeEntity.add(PipeComponent())
        pipeEntity.add(SizeComponent(pipeWidth, pipeHeight))
        pipeEntity.add(CollisionComponent())
        pipeEntity.add(TextureComponent(pipeTexture)) // Add texture component
        pipeEntity.add(VelocityComponent(Vector2(-150F, 0F)))
        return pipeEntity
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        //music.stop();
    }

    companion object {
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
        birdEntity.add(VelocityComponent())
        val birdWidth = birdHeight * birdTexture.width / birdTexture.height.toFloat()
        birdEntity.add(SizeComponent(birdWidth, birdHeight))
        birdEntity.add(BirdComponent())

        return birdEntity
    }

    private fun createTombstoneEntity(startXPosition : Float) : Entity {
        val tombStoneEntity = Entity();
        tombStoneEntity.add(PositionComponent(Vector2(startXPosition, 0f)))
        tombStoneEntity.add(VelocityComponent(Vector2(-150F, 0F)))
        val birdWidth = birdHeight * birdTexture.width / birdTexture.height.toFloat()
        tombStoneEntity.add(SizeComponent(birdWidth, birdHeight))
        tombStoneEntity.add(TextureComponent(birdTexture))
        tombStoneEntity.add(TombstoneComponent())
        return tombStoneEntity;
    }

    private fun createBackgroundEntity(x: Float, texture: Texture, speed: Float): Entity {
        val backgroundEntity = Entity()
        backgroundEntity.add(PositionComponent(Vector2(x, 0f))) // Set x position to the passed value
        backgroundEntity.add(SizeComponent(virtualWidth, virtualHeight))
        backgroundEntity.add(ParallaxComponent(texture, speed))
        return backgroundEntity
    }

    override fun onSpawn(initialXPos: Float) {
        engine.addEntity(createTombstoneEntity(initialXPos))
    }


}