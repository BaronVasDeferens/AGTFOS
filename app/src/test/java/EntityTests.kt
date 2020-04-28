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
    fun `can turn a def into a crewman instance`() {
        val def = CrewDefinition("CAPTAIN_YID")
        val crewMan = Crewman.fromDefinition(def)
        assertEquals("Captain Yid", crewMan.entityName)
    }

    @Test
    fun `can turn a def into a crewman instance with weapon`() {
        val def = CrewDefinition("CAPTAIN_YID", "ZOGWARTZ")
        val crewMan = Crewman.fromDefinition(def)
        assertEquals(Weapon.ZOGWARTZ, crewMan.carriedWeapon)
    }

      @Test
    fun `can create monster from type`() {
        val monsterBaby = MonsterType.valueOf("BABY")
        assertEquals(monsterBaby, MonsterType.BABY)
    }

    @Test
    fun `can get monster from next stage`() {
        val monsterMama = MonsterType.valueOf("BABY").nextStage()
        assertEquals(monsterMama, MonsterType.ADULT)
    }

}