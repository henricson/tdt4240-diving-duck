package com.divingduck.game

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.divingduck.components.*
import com.divingduck.screen.SettingsScreen
import kotlin.math.abs

class EntityManager() {
    companion object {
        private val graveTexture = Texture("grave.png");
        private val backgroundTexture = Texture("background.png")
        private val pipeUpTexturePath = if (SettingsScreen.map == 2) "pipeUp2.png" else "pipeUp.png"
        private val pipeDownTexturePath = if (SettingsScreen.map == 2) "pipeDown2.png" else "pipeDown.png"
        private val pipeUpTexture = Texture(pipeUpTexturePath)
        private val pipeDownTexture = Texture(pipeDownTexturePath)
        private val birdTexture = if (SettingsScreen.bird == 1) Texture("duck.png") else Texture("duck2.png")

        fun createPipeEntity(gameConfig : GameConfig, up: Boolean): Entity {
            val y = MathUtils.random(gameConfig.virtualHeight * 0.15f, gameConfig.virtualHeight * 0.6f - gameConfig.pipeGap)
            val pipeEntity = Entity()
            pipeEntity.add(PipeComponent())
            pipeEntity.add(SizeComponent(gameConfig.pipeWidth, gameConfig.pipeHeight))
            pipeEntity.add(CollisionComponent())
            if(up) {
                pipeEntity.add(TextureComponent(pipeUpTexture)) // Add texture component
                pipeEntity.add(PositionComponent(Vector2(gameConfig.virtualWidth, y + gameConfig.pipeGap)))

            }else {
                pipeEntity.add(TextureComponent(pipeDownTexture)) // Add texture component
                pipeEntity.add(PositionComponent(Vector2(gameConfig.virtualWidth, y - gameConfig.pipeHeight - gameConfig.pipeGap)))

            }
            pipeEntity.add(VelocityComponent(Vector2(gameConfig.scrollVelocity, 0F)))
            return pipeEntity
        }

        fun createTombstoneEntity(gameConfig: GameConfig, startXPosition: Float): Entity {
            val tombStoneEntity = Entity();
            tombStoneEntity.add(PositionComponent(Vector2(startXPosition, -10f)))
            tombStoneEntity.add(VelocityComponent(Vector2(-150F, 0F)))
            tombStoneEntity.add(SizeComponent(50f, 70f))
            tombStoneEntity.add(TextureComponent(graveTexture))
            tombStoneEntity.add(TombstoneComponent())
            return tombStoneEntity;
        }

         fun createBirdEntity(gameConfig: GameConfig): Entity {
            val birdEntity = Entity()
            birdEntity.add(PositionComponent())
            birdEntity.add(RotationComponent())
            birdEntity.add(CollisionComponent())
            birdEntity.add(TextureComponent(birdTexture))
            birdEntity.add(VelocityComponent())
            birdEntity.add(ScoreComponent())
            val birdWidth = gameConfig.birdHeight * birdTexture.width / birdTexture.height.toFloat()
            birdEntity.add(SizeComponent(birdWidth, gameConfig.birdHeight))
            birdEntity.add(BirdComponent())

            return birdEntity
        }

        fun createGameOverEntity(gameConfig: GameConfig): Entity {
            val gameOverTexture = Texture("gameover.png")

            val gameoverOverlayEntity = Entity();
            gameoverOverlayEntity.add(PositionComponent(Vector2(gameConfig.virtualWidth / 2 - gameOverTexture.width / 2, gameConfig.virtualHeight / 2 - gameOverTexture.height / 2)))
            gameoverOverlayEntity.add(VelocityComponent(Vector2(0F, 0F)))
            gameoverOverlayEntity.add(SizeComponent(gameOverTexture.width.toFloat(), gameOverTexture.height.toFloat()))
            gameoverOverlayEntity.add(TextureComponent(gameOverTexture))
            gameoverOverlayEntity.add(GameoverOverlayComponent())
            return gameoverOverlayEntity;
        }
        fun createBackgroundEntity(gameConfig: GameConfig, virtualWidth : Float): Entity {
            val backgroundEntity = Entity()
            backgroundEntity.add(PositionComponent(Vector2(virtualWidth, 0f))) // Set x position to the passed value
            backgroundEntity.add(SizeComponent(gameConfig.virtualWidth, gameConfig.virtualHeight))
            backgroundEntity.add(ParallaxComponent(backgroundTexture, abs(gameConfig.scrollVelocity)))
            return backgroundEntity
        }
    }

}
