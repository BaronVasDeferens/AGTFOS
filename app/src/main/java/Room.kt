data class Room (val roomDefinition: RoomDefinition) {

    val name = roomDefinition.name
    val id = roomDefinition.id

    val adjacentRooms = mutableListOf<Room>()
    val contiguousRooms = mutableListOf<Room>()
    val residentCrew = mutableListOf<Crewman>()

    val weapons = mutableListOf<Weapon>()

    init {

        roomDefinition.residentCrew.forEach { crewDef ->
            residentCrew.add(Crewman.fromDefinition(crewDef))
        }

        roomDefinition.startingWeapons.forEach { weaponDef ->
            weapons.add(Weapon.valueOf(weaponDef))
        }
    }

    fun establishConnections(roomMap: Map<String, Room>) {

        roomDefinition.adjacentRooms.forEach { roomName ->
            roomMap[roomName]?.apply { adjacentRooms.add(this) }
        }
        roomDefinition.contiguousRooms.forEach{ roomName ->
            roomMap[roomName]?.apply { contiguousRooms.add(this) }
        }
    }
}