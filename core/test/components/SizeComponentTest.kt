package components

import com.divingduck.components.SizeComponent
import org.junit.Assert.*
import org.junit.Test

class SizeComponentTest{

    @Test
    fun `has right values`(){
        val comp = SizeComponent(30f, 10f);

        assertEquals(30f, comp.width);
        assertEquals(10f,comp.height);
    }
}
