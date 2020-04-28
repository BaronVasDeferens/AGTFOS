import AreaEffect.*

enum class Weapon (val areaEffect: AreaEffect, val reusable: Boolean){
    NET(ALL_MONSTERS_IN_ROOM, true),
    ZOGWARTZ(SINGLE_TARGET, false),
    KNIFE(SINGLE_TARGET, true)
}

enum class WeaponEffect(val dice: Int) {
    UNKNOWN (0),
    KILL_5(5),
    KILL_4(4),
    KILL_3(3),
    STUN_5(5),
    STUN_4(4),
    STUN_3(3),
    FRAGMENTS(1),
    NO_EFFECT(0)
}

enum class AreaEffect {
    SINGLE_TARGET,
    ALL_ENTITIES_IN_ROOM,
    ALL_MONSTERS_IN_ROOM,
    ALL_IN_CONTIGUOUS_AREA
}



