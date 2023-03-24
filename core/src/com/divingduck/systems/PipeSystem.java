package com.divingduck.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.divingduck.components.PipeComponent;
import com.divingduck.components.PositionComponent;
import com.divingduck.components.SpriteComponent;
import com.divingduck.components.VelocityComponent;

import java.util.Random;

/**
 * So far i've made a simple System that is used by DivingDuckGame. It works something like:
 *
 * 1. Two pairs of pipes are added to the game engine. Instead of removing and adding pipes,
 * they are just repositioned when they go out of bounds to the left.
 *
 * 2. Pipes have position, pipe (This is probably redundant and useless), velocity and sprite components.
 *
 * System should probably depend less on fixed pixel heights and widths than it is now
 */
public class PipeSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    ComponentMapper<PositionComponent> pm;
    ComponentMapper<VelocityComponent> vc;
    ComponentMapper<SpriteComponent> sp;
    private SpriteBatch batch;
    Random random;


    private Engine engine;

    public PipeSystem() {
        pm = ComponentMapper.getFor(PositionComponent.class);
        vc = ComponentMapper.getFor(VelocityComponent.class);
        sp = ComponentMapper.getFor(SpriteComponent.class);
        batch = new SpriteBatch();
        random = new Random();
    }

    public void createPipes(int x, int y, boolean upperPipe) {
        Entity pipe = new Entity();

        PositionComponent pipePosition = new PositionComponent();
        VelocityComponent pipeVelociy = new VelocityComponent();
        PipeComponent pipeComponent = new PipeComponent();
        SpriteComponent spriteComponent = new SpriteComponent();

        Texture texture = new Texture("pipePlaceholder.png");
        spriteComponent.sprite = new Sprite(texture);
        pipeVelociy.velocity.set(-200, 0);

        if (upperPipe) {
            pipeComponent.upperPipe = true;
            spriteComponent.sprite.flip(false, true);
            pipePosition.position.set(x, y + spriteComponent.sprite.getHeight() + 200);
        } else {
            pipePosition.position.set(x, y);
        }

        pipe.add(pipePosition);
        pipe.add(pipeVelociy);
        pipe.add(pipeComponent);
        pipe.add(spriteComponent);

        engine.addEntity(pipe);
    }


    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, SpriteComponent.class, PipeComponent.class).get());
        this.engine = engine;

        // Random number is (upperBound - lowerBound + 1) + lowerBound
        // So far it is just based on the height of the pipe png file, so not a perfect system.
        int number = random.nextInt(-50 + 550 + 1) - 550;
        createPipes(400, number, false);
        createPipes(400, number, true);
        int number2 = random.nextInt(-50 + 550 + 1) - 550;
        createPipes(750, number2, false);
        createPipes(750, number2, true);
    }

    public void update(float deltaTime) {
        batch.begin();
        int currentHeight = 0;
        for (Entity entity : entities) {
            PositionComponent position = entity.getComponent(PositionComponent.class);
            SpriteComponent sprite = entity.getComponent(SpriteComponent.class);
            PipeComponent pipeComponent = entity.getComponent(PipeComponent.class);
            batch.draw(sprite.sprite, position.position.x, position.position.y);
            if (position.position.x < 0 - sprite.sprite.getWidth()) {
                position.position.x = 500 + sprite.sprite.getWidth();
                if (pipeComponent.upperPipe) {
                    position.position.y = currentHeight + sprite.sprite.getHeight() + 200;
                } else {
                    currentHeight = random.nextInt(-50 + 550 + 1) - 550;
                    position.position.y = currentHeight;
                }
            }
            // Can use sprite.getBoundingRectangle.overlaps() to check overlapping
            sprite.sprite.setBounds(position.position.x, position.position.y, sprite.sprite.getWidth(), sprite.sprite.getHeight());
        }

        batch.end();
    }
}
