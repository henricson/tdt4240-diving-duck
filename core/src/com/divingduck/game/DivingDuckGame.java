package com.divingduck.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.divingduck.components.PositionComponent;
import com.divingduck.components.TextureComponent;
import com.divingduck.components.VelocityComponent;
import com.divingduck.systems.PipeSystem;


public class DivingDuckGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private PooledEngine engine;


    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
        engine = new PooledEngine();

        // Add the systems
        engine.addSystem(new MovementSystem());
        engine.addSystem(new PipeSystem());
        // Add the entities
        Entity bird = new Entity();
        PositionComponent position = new PositionComponent();
        position.position.set(100, 240);
        bird.add(position);

        VelocityComponent velocity = new VelocityComponent();
        velocity.velocity.set(0, 0);
        bird.add(velocity);

        TextureComponent texture = new TextureComponent();
        texture.texture = new Texture(Gdx.files.internal("bird.png"));
        bird.add(texture);

        engine.addEntity(bird);


    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(Gdx.graphics.getDeltaTime());

        // Render entities
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        /*for (Entity entity : engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get())) {
            PositionComponent position = entity.getComponent(PositionComponent.class);
            TextureComponent texture = entity.getComponent(TextureComponent.class);
            batch.draw(texture.texture, position.position.x, position.position.y);
        }*/


        batch.end();
    }
    @Override
    public void dispose() {
        batch.dispose();
        for (Entity entity : engine.getEntitiesFor(Family.all(TextureComponent.class).get())) {
            TextureComponent texture = entity.getComponent(TextureComponent.class);
            texture.texture.dispose();
        }
    }

}
