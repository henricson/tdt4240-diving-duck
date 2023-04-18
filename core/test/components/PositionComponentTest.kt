package components

import com.badlogic.gdx.math.Vector2
import com.divingduck.components.PositionComponent
import org.junit.Assert.*
import org.junit.Test

class PositionComponentTest{


    @Test
    fun `has right values`(){
        val comp = PositionComponent(Vector2(30f, 10f));

        assertEquals(Vector2(30f,10f), comp.position);
           }

}
