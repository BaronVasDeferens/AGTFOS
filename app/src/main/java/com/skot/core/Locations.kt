package com.skot.core



class Room (val name: String){

    val contiguousRooms = mutableListOf<Room>()
    val adjacentRooms = mutableListOf<Room>()
    val residentCrew = mutableListOf<Entity>()

}

