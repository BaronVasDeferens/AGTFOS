import AreaEffect.ALL_IN_ROOM
import AreaEffect.SINGLE_TARGET
import WeaponNames.*

enum class WeaponNames {
    NET,
    ZOGWARTZ,
    KNIFE
}

enum class WeaponEffect {
    UNKNOWN,
    KILL_5,
    KILL_4,
    KILL_3,
    STUN_5,
    STUN_4,
    STUN_3,
    FRAGMENT_D6,
    NO_EFFECT
}

enum class AreaEffect {
    SINGLE_TARGET,
    ALL_IN_ROOM,
    ALL_IN_CONTIGUOUS_AREA
}

class Weapon(val weaponName: WeaponNames, val areaEffect: AreaEffect, var weaponEffect: WeaponEffect = WeaponEffect.UNKNOWN) {

    companion object {
        fun fromName(weaponName: WeaponNames): Weapon {
            return when (weaponName) {
                NET -> Weapon(NET, ALL_IN_ROOM)
                ZOGWARTZ -> {
                    Weapon(ZOGWARTZ, SINGLE_TARGET)
                }
                KNIFE -> {
                    Weapon(KNIFE, SINGLE_TARGET)
                }
            }
        }
    }
}