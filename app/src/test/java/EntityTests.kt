import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EntityTests {

    @Test
    fun `can create crewman from id`() {
        val captYid = CrewMen.valueOf("CAPTAIN_YID")
        assertEquals(CrewMen.CAPTAIN_YID, captYid)
    }

    @Test
    fun `can get crewman instance from enum`() {
        val captYid = CrewMen.valueOf("CAPTAIN_YID").toCrewman()
        assertEquals("Captain Yid", captYid.entityName)
    }

    @Test
    fun `can create monster from type`() {
        val monsterBaby = MonsterType.valueOf("BABY")
        assertEquals(monsterBaby, MonsterType.BABY)
    }


}