package components

import com.badlogic.gdx.math.Vector2
import com.divingduck.components.BirdComponent
import org.junit.Assert.*
import org.junit.Test

class BirdComponentTest{

    @Test
    fun `has right values`(){

        val comp = BirdComponent(Vector2(0f, 20f), false);

        assertFalse(comp.isJumping);
        assertEquals(Vector2(0f, 20f), comp.velocity);
        assertEquals(0f,comp.rotation);

        val comp2 = BirdComponent(Vector2(50f, 70f), true, 20f);
        assertTrue(comp2.isJumping);
        assertEquals(Vector2(50f, 70f), comp2.velocity);
        assertEquals(20f,comp2.rotation);

    }
}

