import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MapTests {

    @Test
    fun `should read map definition`() {
        val mapReader = MapDefinition()
        val rooms = mapReader.readRoomDefinitions("map.json")
        assert(rooms.isNotEmpty())
    }

    @Test
    fun `should convert room definitions into rooms`() {
        val mapReader = MapDefinition()
        val roomDefs = mapReader.readRoomDefinitions("map.json")
        val rooms = mapReader.createRoomList(roomDefs)
        assert(rooms.isNotEmpty())
    }

    @Test
    fun `should read crewmen from map def`() {
        val mapReader = MapDefinition()
        val roomDefs = mapReader.readRoomDefinitions("map.json")
        val cargoHold = mapReader.createRoomList(roomDefs)
            .first { it.id == "MAIN_CARGO_HOLD" }

        assertTrue(cargoHold.residentCrew.isNotEmpty())
    }

    @Test
    fun `can read carried weapon in map def`() {
        val mapReader = MapDefinition()
        val roomDefs = mapReader.readRoomDefinitions("map.json")
        val cargoHold = mapReader.createRoomList(roomDefs)
            .first { it.id == "MAIN_CARGO_HOLD" }
        val captYid = cargoHold.residentCrew.first()
        assertTrue(captYid.carriedWeapon == Weapon.ZOGWARTZ)
    }

    @Test
    fun `should create room map from room list`() {
        val mapReader = MapDefinition()
        val roomDefs = mapReader.readRoomDefinitions("map.json")
        val roomMap = mapReader.createRoomMap(roomDefs)

        assert(roomMap.isNotEmpty())
    }

    @Test
    fun `should associate adjacent rooms from definitions`() {
        val mapReader = MapDefinition()
        val roomDefs = mapReader.readRoomDefinitions("map.json")
        val rooms = mapReader.createRoomList(roomDefs)
        val roomMap = mapReader.createRoomMap(roomDefs)

        rooms.forEach { room ->
            room.establishConnections(roomMap)
        }

        roomMap.values.forEach { room ->
            assert(room.adjacentRooms.isNotEmpty())
        }
    }

}