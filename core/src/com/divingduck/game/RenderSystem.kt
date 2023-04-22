package com.divingduck.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.divingduck.components.PositionComponent
import com.divingduck.components.RotationComponent
import com.divingduck.components.ScoreComponent
import com.divingduck.components.SizeComponent
import com.divingduck.components.TextureComponent
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout

class RenderSystem(private val camera: OrthographicCamera, private val batch: Batch) : EntitySystem() {
    private val renderFamily = Family.all(PositionComponent::class.java, TextureComponent::class.java).get()
    private val scoreBatch = SpriteBatch()
    private val font = BitmapFont()
    private val glyphLayout = GlyphLayout()

    init {
        font.data.setScale(3f)
        font.setUseIntegerPositions(false)
    }

    override fun update(deltaTime: Float) {
        val renderEntities = engine.getEntitiesFor(renderFamily)

        batch.projectionMatrix = camera.combined
        batch.begin()

        for (entity in renderEntities) {
            val positionComponent = entity.getComponent(PositionComponent::class.java)
            val sizeComponent = entity.getComponent(SizeComponent::class.java)
            val textureComponent = entity.getComponent(TextureComponent::class.java)
            val rotationComponent = entity.getComponent(RotationComponent::class.java)

            val sprite = Sprite(textureComponent.texture)
            sprite.setPosition(positionComponent.position.x, positionComponent.position.y)
            sprite.setSize(sizeComponent.width, sizeComponent.height)

            if (rotationComponent != null) {
                sprite.setOriginCenter()
                sprite.rotation = -rotationComponent.rotation.toFloat()
            }

            sprite.draw(batch)
        }

        batch.end()

        // Draw the score
        val birdEntity = engine.getEntitiesFor(Family.all(ScoreComponent::class.java).get()).first()
        val birdScore = birdEntity.getComponent(ScoreComponent::class.java)

        scoreBatch.projectionMatrix = camera.combined
        scoreBatch.begin()
        glyphLayout.setText(font, "${birdScore.score}") // Remove "Score: " prefix
        val textX = camera.viewportWidth / 2 - glyphLayout.width / 2
        val textY = camera.viewportHeight * 0.75f // Move the score to the top center
        font.draw(scoreBatch, glyphLayout, textX, textY)
        scoreBatch.end()
    }
}
