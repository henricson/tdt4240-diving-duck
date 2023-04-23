package game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.*
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.divingduck.components.*
import com.divingduck.game.UpdateSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.math.atan2


class UpdateSystemTest{

    private lateinit var engine: Engine
    private lateinit var application: HeadlessApplication
    private lateinit var system: EntitySystem

    private val PIPE_WIDTH = 50f
    private val PIPE_HEIGHT = 400f


    @BeforeEach
    fun setup(){
        application = HeadlessApplication(object : ApplicationAdapter() {}, object : HeadlessApplicationConfiguration() {})
        Gdx.gl = mock(GL20::class.java)

        val m : Sound = mock()
        system = UpdateSystem(3000f, m, 2L)

        engine = Engine()
        engine.addSystem(system)
    }

    @AfterEach
    fun cleanUp() {
        application.exit()
        engine.removeAllSystems()
        engine.removeAllEntities()
    }


    private fun createParallaxEntity(): Entity{
        val t : Texture = mock()

        val parallaxEntity = Entity()
        parallaxEntity.add(PositionComponent())
        parallaxEntity.add(SizeComponent(1000f,3000f))
        parallaxEntity.add(ParallaxComponent( t,10f))
        return parallaxEntity
    }



    private fun makeBirdEntity(): Entity {
        val birdEntity = Entity()
        birdEntity.add(PositionComponent())
        birdEntity.add(RotationComponent())
        birdEntity.add(CollisionComponent())
        birdEntity.add(VelocityComponent())
        birdEntity.add(SizeComponent(300f, 500f))
        birdEntity.add(BirdComponent())
        birdEntity.add(ScoreComponent())
        return birdEntity;
    }

    private fun createPipeEntity(): Entity {
        val pipeEntity = Entity()
        pipeEntity.add(PositionComponent(Vector2(500f, 300f)))
        pipeEntity.add(PipeComponent())
        pipeEntity.add(SizeComponent(PIPE_WIDTH, PIPE_HEIGHT))
        pipeEntity.add(CollisionComponent())
        pipeEntity.add(VelocityComponent(Vector2(-150F, 0F)))
        return pipeEntity
    }

    @Test
    fun `test bird jump`(){
        //Setup the engine and entities and components

        val birdEntity = makeBirdEntity()
        engine.addEntity(birdEntity)

        val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
        val positionComponent = birdEntity.getComponent(PositionComponent::class.java)
        val velocityComponent = birdEntity.getComponent(VelocityComponent::class.java)

        //Change values for components to how they should be before test
        birdComponent.isJumping=true;
        velocityComponent.velocity.set(Vector2(10f,30f))
        positionComponent.position.set(Vector2(10f, 500f))

        //Base test to make sure that update actually does something
        assertNotEquals(velocityComponent.velocity, birdComponent.jumpVector);

        //Run the update function, that runs the code to be tested
        system.update(3f)

        //Assert that the result of running it is what we expected
        assertEquals(velocityComponent.velocity, birdComponent.jumpVector);
        assertFalse(birdComponent.isJumping);
    }

    @Test
    fun `test rotate`(){
        //Setup the engine and entities and components
        val birdEntity = makeBirdEntity()
        engine.addEntity(birdEntity)

        val rotationComponent = birdEntity.getComponent(RotationComponent::class.java)
        val velocityComponent = birdEntity.getComponent(VelocityComponent::class.java)
        val positionComponent = birdEntity.getComponent(PositionComponent::class.java)


        //Change values for components to how they should be before test
        rotationComponent.rotation = 30
        velocityComponent.velocity.y = 200f
        positionComponent.position.set(Vector2(30f,90f))

        //Base test to make sure that update actually does something
        assertNotEquals(rotationComponent.rotation,(-atan2(velocityComponent.velocity.y.toFloat(), 150F) * 180 / Math.PI).toInt())

        //Run the update function, that runs the code to be tested
        system.update(1f)

        //Assert that the result of running it is what we expected
        assertEquals(rotationComponent.rotation, (-atan2(velocityComponent.velocity.y.toFloat(), 150F) * 180 / Math.PI).toInt())

        assertTrue(rotationComponent.rotation > 30)

        //Test rotation when jumping
        val birdEntity2 = makeBirdEntity()
        engine.addEntity(birdEntity2)

        val birdComponent = birdEntity2.getComponent(BirdComponent::class.java)
        val rotationComponent2 = birdEntity2.getComponent(RotationComponent::class.java)
        val positionComponent2 = birdEntity2.getComponent(PositionComponent::class.java)

        rotationComponent2.rotation = 90
        positionComponent2.position.set(Vector2(30f,90f))
        birdComponent.isJumping=true
        system.update(1f)

        assertTrue(rotationComponent2.rotation < 90)

    }


    @Test
    fun `test fall`(){
        //Setup the engine and entities and components
        val birdEntity = makeBirdEntity()
        engine.addEntity(birdEntity)

        val rotationComponent = birdEntity.getComponent(RotationComponent::class.java)
        val velocityComponent = birdEntity.getComponent(VelocityComponent::class.java)
        val positionComponent = birdEntity.getComponent(PositionComponent::class.java)
        val sizeComponent = birdEntity.getComponent(SizeComponent::class.java)
        val birdComponent = birdEntity.getComponent(BirdComponent::class.java)

        //Change values for components to how they should be before test
        rotationComponent.rotation = 30
        velocityComponent.velocity.y=40f
        positionComponent.position.set(Vector2(400f,50f))

        //Base test to make sure that update actually does something
        assertNotEquals(velocityComponent.velocity.y, 40f+birdComponent.gravity*3f)

        //Run the update function, that runs the code to be tested
        system.update(3f)

        //Assert that the result of running it is what we expected
        assertEquals(velocityComponent.velocity.y, 40f+birdComponent.gravity*3f)
        assertNotEquals(0f,velocityComponent.velocity.y )

        //Now wants to test that one cant fall outside of the screen
        positionComponent.position.set(Vector2(2f,-5f))

        system.update(3f)

        //Assert the y velocity is 0 when outside of screen, and the position is on the edge
        assertEquals(0f,velocityComponent.velocity.y )
        assertEquals(positionComponent.position.y, MathUtils.clamp(positionComponent.position.y, 0f, 3000f - sizeComponent.height))

        //Test can go over the screen
        positionComponent.position.set(Vector2(2f,3000f+sizeComponent.height))

        system.update(3f)

        //Assert the y velocity is 0 when outside of screen, and the position is on the edge
        assertEquals(0f,velocityComponent.velocity.y)
        assertEquals(positionComponent.position.y, MathUtils.clamp(positionComponent.position.y, 0f, 3000f - sizeComponent.height))

    }

    @Test
    fun `test collide`(){
        //Setup the engine and entities and components
        val birdEntity = makeBirdEntity()
        val pipeEntity = createPipeEntity()
        val parallaxEntity = createParallaxEntity()

        engine.addEntity(pipeEntity)
        engine.addEntity(birdEntity)
        engine.addEntity(parallaxEntity)

        val pipeVelocityComponent = pipeEntity.getComponent(VelocityComponent::class.java)
        val pipePositionComponent = pipeEntity.getComponent(PositionComponent::class.java)

        val birdVelocityComponent = birdEntity.getComponent(VelocityComponent::class.java)
        val birdPositionComponent = birdEntity.getComponent(PositionComponent::class.java)

        val parallaxComponent = parallaxEntity.getComponent(ParallaxComponent::class.java)

        //Set components to values for test
        pipePositionComponent.position.set(Vector2(500f, 500f))
        birdPositionComponent.position.set(Vector2(500f, 500f))
        pipeVelocityComponent.velocity.set(Vector2(5f, 0f))
        birdVelocityComponent.velocity.set(Vector2(0f, 10f))
        parallaxComponent.speed = 10f

        //Run code to be tested
        system.update(1f)

        assertEquals(0f,pipeVelocityComponent.velocity.x)
        assertEquals(0f,birdVelocityComponent.velocity.x)
        assertEquals(0f,parallaxComponent.speed)

    }


    @Test
    fun `test pipes`(){
        //Setup the engine and entities and components
        val pipeEntity = createPipeEntity()
        val birdEntity = makeBirdEntity()
        engine.addEntity(pipeEntity)
        engine.addEntity(birdEntity)
        var pipeFamily = Family.all(PipeComponent::class.java).get()

        val positionComponent = pipeEntity.getComponent(PositionComponent::class.java)
        val sizeComponent = pipeEntity.getComponent(SizeComponent::class.java)


        positionComponent.position.x = -sizeComponent.width -2

        //Basetest
        assertFalse(engine.getEntitiesFor(pipeFamily).size()==0)

        //Run code to test
        system.update(3f)

        //Assert that pipe outside of screen is removed
        assertTrue(engine.getEntitiesFor(pipeFamily).size()==0)
    }

    @Test
    fun `test position`(){
        //Setup the engine and entities and components
        val birdEntity = makeBirdEntity()
        val pipeEntity = createPipeEntity()
        engine.addEntity(pipeEntity)
        engine.addEntity(birdEntity)

        val pipeVelocityComponent = pipeEntity.getComponent(VelocityComponent::class.java)
        val pipePositionComponent = pipeEntity.getComponent(PositionComponent::class.java)

        val birdVelocityComponent = birdEntity.getComponent(VelocityComponent::class.java)
        val birdPositionComponent = birdEntity.getComponent(PositionComponent::class.java)

        //Set components to values for test
        pipePositionComponent.position.set(Vector2(600f, 2500f))
        birdPositionComponent.position.set(Vector2(500f, 1000f))

        //Run code to be tested
        system.update(1f)

        //Test that update pipe position works
        assertNotEquals(Vector2(600f, 2500f), pipePositionComponent.position)
        assertTrue(pipePositionComponent.position.x < 600f)
        assertTrue(pipePositionComponent.position.y == 2500f)
        assertEquals(pipePositionComponent.position.x,pipeVelocityComponent.velocity.cpy().scl(1f).x+600f)


        //Test that update bird position works
        assertNotEquals(Vector2(500f, 1000f), birdPositionComponent.position)
        assertTrue(birdPositionComponent.position.x == 500f)
        assertTrue(birdPositionComponent.position.y < 1000f)
        assertEquals(birdPositionComponent.position.x,birdVelocityComponent.velocity.cpy().scl(1f).x+500f)

    }

    @Test
    fun `test score`(){
        val birdEntity = makeBirdEntity()
        val pipeEntityUp = createPipeEntity()
        val pipeEntityDown = createPipeEntity()

        engine.addEntity(pipeEntityUp)
        engine.addEntity(pipeEntityDown)
        engine.addEntity(birdEntity)

        val pipePositionComponentDown = pipeEntityDown.getComponent(PositionComponent::class.java)

        val pipePositionComponentUp = pipeEntityUp.getComponent(PositionComponent::class.java)

        val birdComponent = birdEntity.getComponent(BirdComponent::class.java)
        val birdPositionComponent = birdEntity.getComponent(PositionComponent::class.java)
        val birdScoreComponent = birdEntity.getComponent(ScoreComponent::class.java)

        assertEquals(0,birdScoreComponent.score)

        //Set components to values for test
        pipePositionComponentDown.position.set(Vector2(600f, 300f))
        pipePositionComponentUp.position.set(Vector2(600f, 2500f))
        birdPositionComponent.position.set(Vector2(2000f, 1000f))
        birdComponent.isJumping = true

        system.update(2f)

        assertEquals(1,birdScoreComponent.score)
    }
}