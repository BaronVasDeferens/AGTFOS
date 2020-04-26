import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameStateTests {

    @Test
    fun `game phase should return correct phase on NEXT`() {

        assertEquals(GamePhase.MONSTER_PLACEMENT, GamePhase.CREW_PLACEMENT.nextInSequence())
        assertEquals(GamePhase.MONSTER_GROW, GamePhase.MONSTER_PLACEMENT.nextInSequence())
        assertEquals(GamePhase.MONSTER_MOVE, GamePhase.MONSTER_GROW.nextInSequence())
        assertEquals(GamePhase.MONSTER_ATTACK, GamePhase.MONSTER_MOVE.nextInSequence())
        assertEquals(GamePhase.CREW_GRAB_WEAPONS, GamePhase.MONSTER_WAKE_UP.nextInSequence())
        assertEquals(GamePhase.CREW_MOVE, GamePhase.CREW_GRAB_WEAPONS.nextInSequence())
        assertEquals(GamePhase.CREW_ATTACK, GamePhase.CREW_MOVE.nextInSequence())
        assertEquals(GamePhase.CREW_WAKE_UP, GamePhase.CREW_ATTACK.nextInSequence())
        assertEquals(GamePhase.MONSTER_GROW, GamePhase.CREW_WAKE_UP.nextInSequence())
    }

    @Test
    fun `game state initializes`() {

    }

}