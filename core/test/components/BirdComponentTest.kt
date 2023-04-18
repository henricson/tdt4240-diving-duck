package components

import com.badlogic.gdx.math.Vector2
import com.divingduck.components.BirdComponent
import org.junit.Assert.*
import org.junit.Test

class BirdComponentTest{

    @Test
    fun `has right values`(){

        val comp = BirdComponent();

        assertFalse(comp.isJumping);
        assertEquals(Vector2(0f, 250f), comp.jumpVector);
        assertEquals(-600f,comp.gravity);

        val comp2 = BirdComponent(true, Vector2(50f, 70f), -300f );
        assertTrue(comp2.isJumping);
        assertEquals(Vector2(50f, 70f), comp2.jumpVector);
        assertEquals(300f,comp2.gravity);

    }
}

