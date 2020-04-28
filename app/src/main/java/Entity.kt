abstract class Entity(
    open val movement: Int,
    open val attack: Int,
    open val constitution: Int,
    open var isStunned: Boolean = false
)

enum class MonsterType(val movement: Int, val attack: Int, val constitution: Int) {
    EGG(0, 0, 0),
    BABY(1, 1, 1),
    FRAGMENT(2, 2, 2),
    ADULT(3, 3, 3);

    fun nextStage(): MonsterType {
        return when (this) {
            EGG,
            FRAGMENT -> BABY
            BABY -> ADULT
            ADULT -> EGG
        }
    }
}

class Monster(
    val type: MonsterType,
    override val movement: Int,
    override val attack: Int,
    override val constitution: Int
) : Entity(
    movement, attack, constitution
)

enum class CrewRank(val peckingOrder: Int) {
    CAPTAIN(5),
    FIRST_OFFICER(4),
    PILOT(3),
    COXSWAIN(2),
    MARINE(1),
    OTHER(0)
}

class Crewman(
    val entityName: String,
    override val movement: Int,
    override val attack: Int,
    override val constitution: Int,
    override var isStunned: Boolean,
    var carriedWeapon: Weapon? = null,
    val phaseOfLastAction: GamePhase = GamePhase.CREW_PLACEMENT

) : Entity(
    movement, attack, constitution) {

    companion object {
        fun fromDefinition(def: CrewDefinition): Crewman {

            val crew = CrewMen.valueOf(def.crewMenId).toCrewman()
            def.weaponId?.apply {
                crew.carriedWeapon = Weapon.valueOf(this)
            }
            return crew
        }
    }

}

enum class CrewMen(
    val fullName: String,
    val movement: Int,
    val attack: Int,
    val constitution: Int
) {

    CAPTAIN_YID("Captain Yid", 1, 2, 3),
    FIRST_OFFICER("First Officer", 1, 2, 3),
    ROBOT("Leadfoot", 4, 5, 6),
    MASCOT("Mascot", 1, 1, 1);

    fun toCrewman(): Crewman {
        return Crewman(fullName, movement, attack, constitution, false)
    }
}

class CrewDefinition(
    val crewMenId: String,
    val weaponId: String? = null,
    val isStunned: Boolean = false,
    val phaseOfLastAction: GamePhase = GamePhase.CREW_PLACEMENT
) {

}