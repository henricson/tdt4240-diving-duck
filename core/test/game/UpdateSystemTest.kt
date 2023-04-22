package game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.*
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.TextureData
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.divingduck.components.*
import com.divingduck.game.UpdateSystem
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.math.atan2


public class UpdateSystemTest{

    private lateinit var engine: Engine

    private val PIPE_WIDTH = 50f
    private val PIPE_HEIGHT = 400f



    private fun setup(): EntitySystem{
        val m : Sound = mock()


        val system = UpdateSystem(3000f, m, 2L)
        engine = Engine()
        engine.addSystem(system)

        return system
    }

    private fun createParallaxEntity(): Entity{
        val t : Texture = mock()

        Gdx.app = Mockito.mock(Application::class.java)
        Gdx.gl20 = Mockito.mock(GL20::class.java)
        Gdx.gl = Gdx.gl20;
        Gdx.graphics = Mockito.mock(Graphics::class.java)
        Gdx.files = Mockito.mock(Files::class.java)

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
        val system = setup()
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
        val system = setup()
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
        val system = setup()
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
    fun `test pipes`(){
        //Setup the engine and entities and components
        val system = setup()
        val pipeEntity = createPipeEntity()
        engine.addEntity(pipeEntity)
        var pipeFamily = Family.all(PipeComponent::class.java).get()

        val rotationComponent = pipeEntity.getComponent(RotationComponent::class.java)
        val velocityComponent = pipeEntity.getComponent(VelocityComponent::class.java)
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
        val system = setup()
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


}