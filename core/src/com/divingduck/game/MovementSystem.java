package com.divingduck.game;// MovementSystem.java
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.divingduck.components.PositionComponent;
import com.divingduck.components.VelocityComponent;

public class MovementSystem extends EntitySystem {
    private ComponentMapper<PositionComponent> positionMapper;
    private ComponentMapper<VelocityComponent> velocityMapper;

    public MovementSystem() {
        positionMapper = ComponentMapper.getFor(PositionComponent.class);
        velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : getEngine().getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class).get())) {
            PositionComponent position = positionMapper.get(entity);
            VelocityComponent velocity = velocityMapper.get(entity);

            position.position.x += velocity.velocity.x * deltaTime;
            position.position.y += velocity.velocity.y * deltaTime;
        }
    }
}