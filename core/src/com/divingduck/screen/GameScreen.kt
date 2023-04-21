package com.divingduck.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divingduck.client.apis.ScoreApi
import com.divingduck.components.BirdComponent
import com.divingduck.components.GameoverOverlayComponent
import com.divingduck.components.ParallaxComponent
import com.divingduck.components.PositionComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent
import com.divingduck.components.VelocityComponent
import com.divingduck.game.EntityManager
import com.divingduck.game.GameConfig
import com.divingduck.game.GlobalEventListener
import com.divingduck.game.GlobalEvents
import com.divingduck.game.ParallaxSystem
import com.divingduck.game.RenderSystem
import com.divingduck.game.UpdateSystem
import com.divingduck.helpers.EffectHelpers
import com.divingduck.helpers.TombstoneHelpers
import kotlin.properties.Delegates

class GameScreen(val game: Game) : Screen, GlobalEventListener {
    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var engine: Engine
    private lateinit var birdEntity: Entity
    private var timeSinceLastPipe = 75f
    private lateinit var viewport: FitViewport
    private var virtualWidth = 0f
    private var virtualHeight = 0f
    private var currentGameEvent = GlobalEvents.Playing;

    val gameConfig = GameConfig(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat());


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

        music = Gdx.audio.newSound(Gdx.files.internal("sounds/lofistudy.mp3"));
        ambient = Gdx.audio.newSound(Gdx.files.internal("sounds/underwater.mp3"));

        // Start playing the music if music is enabled
        if (SettingsScreen.musicBoolean) {
            musicId = music.play(1.0f);
        }
        else {
            musicId = music.play(0.0f);
        }
        ambient.play()

        // Load textures



        // Create background entities
        val backgroundEntity1 = EntityManager.createBackgroundEntity(gameConfig, 0f);
        val backgroundEntity2 = EntityManager.createBackgroundEntity(gameConfig, Gdx.graphics.width.toFloat());

        // Add background entities to the engine
        engine.addEntity(backgroundEntity1)
        engine.addEntity(backgroundEntity2)

        // Create entities
        birdEntity = EntityManager.createBirdEntity(gameConfig)

        // Add entities to the engine
        engine.addEntity(birdEntity)

        val updateSystem = UpdateSystem(virtualHeight);
        updateSystem.addListener(this);

        // Add systems to the engine
        engine.addSystem(ParallaxSystem(camera, batch))
        engine.addSystem(RenderSystem(camera, batch))
        engine.addSystem(updateSystem);

        engine.addSystem(RenderSystem(camera, batch))
        setInputProcessor();

    }



    override fun render(delta: Float) {
        timeSinceLastPipe += Gdx.graphics.deltaTime
        totalTimePassed += Gdx.graphics.deltaTime
        if (timeSinceLastPipe > gameConfig.pipeSpawnTime && currentGameEvent != GlobalEvents.GameOver) {
            spawnPipe()
            timeSinceLastPipe = 0f
        }

        calculationHelpers.getInitialXPositionsInSlidingWindow(150f, totalTimePassed)

        engine.update(Gdx.graphics.deltaTime)
    }

    private fun spawnPipe() {

        // Create top pipe
        val topPipeEntity = EntityManager.createPipeEntity(gameConfig, true);
        engine.addEntity(topPipeEntity)

        // Create bottom pipe
        val bottomPipeEntity = EntityManager.createPipeEntity(gameConfig, false);
        engine.addEntity(bottomPipeEntity)
    }




    override fun resize(width: Int, height: Int) {
        virtualWidth = Gdx.graphics.width.toFloat()
        virtualHeight = Gdx.graphics.height.toFloat()
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

    private fun setInputProcessor() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                when (currentGameEvent) {
                    GlobalEvents.Playing -> {
                        println("Jumping!")
                        val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
                        birdComponent.isJumping = true
                    }
                    GlobalEvents.GameOver -> {
                        // Reset game
                        game.screen = GameScreen(game)

                    }

                    else -> {}
                }

                return true
            }
        }
    }







    override fun onEvent(event: GlobalEvents) {
        when (event) {
            GlobalEvents.Playing -> {

            }
            GlobalEvents.GameOver -> {
                currentGameEvent = GlobalEvents.GameOver;
                engine.addEntity(EntityManager.createGameOverEntity(gameConfig))
                EffectHelpers.fadeOut(music, musicId)
            }
            GlobalEvents.SpawnGrave -> {
                engine.addEntity(EntityManager.createTombstoneEntity(gameConfig, gameConfig.virtualWidth))
            }

        }
    }


}