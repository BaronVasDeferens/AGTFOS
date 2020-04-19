package com.skot.core

abstract class Entity(val id: String, val movement: Int, val attack: Int, val constitution: Int) {

}

class Crewman(id: String, movement: Int, attack: Int, constitution: Int): Entity(id, movement, attack,
    constitution
) {

}

class Monster(id: String, movement: Int, attack: Int, constitution: Int): Entity(id, movement, attack,
    constitution
) {

}