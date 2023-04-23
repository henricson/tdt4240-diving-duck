package components

import com.badlogic.gdx.math.Vector2
import com.divingduck.components.RotationComponent
import org.junit.Assert.*
import org.junit.jupiter.api.Test

class RotationComponentTest{

    @Test
    fun `has right values`(){
        val comp = RotationComponent(50);
        assertEquals(50,comp.rotation);
    }

}
