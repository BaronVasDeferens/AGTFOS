enum class GamePhase (val sequenceId: Int) {

    CREW_PLACEMENT(0),
    MONSTER_PLACEMENT(1),

    MONSTER_GROW(2),
    MONSTER_MOVE(3),
    MONSTER_ATTACK(4),
    MONSTER_WAKE_UP(5),
    CREW_GRAB_WEAPONS(6),
    CREW_MOVE(7),
    CREW_ATTACK(8),
    CREW_WAKE_UP(9),

    GAME_OVER(99);

    fun nextInSequence(): GamePhase {
        return when (sequenceId) {
            0 -> MONSTER_PLACEMENT
            1 -> MONSTER_GROW
            2 -> MONSTER_MOVE
            3 -> MONSTER_ATTACK
            4 -> MONSTER_WAKE_UP
            5 -> CREW_GRAB_WEAPONS
            6 -> CREW_MOVE
            7 -> CREW_ATTACK
            8 -> CREW_WAKE_UP
            9 -> MONSTER_GROW

            else -> GAME_OVER
        }
    }
}

data class GameState(val phase: GamePhase = GamePhase.CREW_PLACEMENT) {

}


class GameManager {



}