package components

import com.badlogic.gdx.math.Vector2
import com.divingduck.components.RotationComponent
import org.junit.Assert.*
import org.junit.Test

class RotationComponentTest{

    @Test
    fun `has right values`(){
        val comp = RotationComponent();

        assertEquals(90, comp.rotation);

        val comp2 = RotationComponent(50);
        assertEquals(50,comp2.rotation);
    }

}
