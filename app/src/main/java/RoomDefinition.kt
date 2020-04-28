import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


data class RoomDefinition(
    val name: String,
    val id: String,
    val contiguousRooms: List<String>,
    val adjacentRooms: List<String>,
    val startingWeapons: List<String>,
    val residentCrew: List<CrewDefinition> = listOf()
)

class MapDefinition {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun readRoomDefinitions(fileName: String): List<RoomDefinition> {

        val data =
            RoomDefinition::class.java.getResourceAsStream(fileName).bufferedReader().readText()
        val typeAdapter = Types.newParameterizedType(
            List::class.java,
            RoomDefinition::class.java,
            CrewDefinition::class.java
        )
        val adapter = moshi.adapter<List<RoomDefinition>>(typeAdapter)
        val listOfRooms = adapter.fromJson(data)!!

        return listOfRooms

    }

    fun createRoomList(roomDefs: List<RoomDefinition>): List<Room> {

        return roomDefs.map { roomDef ->
            Room(roomDef)
        }.toList()

    }

    fun createRoomMap(roomDefs: List<RoomDefinition>): Map<String, Room> {
        return roomDefs.associateBy ({ it.id }, { Room(it)})
    }


}