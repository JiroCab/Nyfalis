package olupis.world.blocks.turret;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;

public class TurretModes{
    public static TurretMode
        modeNormal = new TurretMode("mode-normal"),
        modeAirOnly = new TurretMode("mode-airOnly", u ->  u.isFlying() && !u.dead, b -> false),
        modeGroundOnly = new TurretMode("mode-groundOnly", u ->  !u.isFlying() && !u.dead, b -> false),

        modeLineOfSight = new TurretMode("mode-lineOfSight")

    ;

    public static final Seq<TurretMode> modes = Seq.with(
        modeNormal, modeAirOnly, modeGroundOnly, modeLineOfSight
    );



    public static class TurretMode{
        public String name = "";

        /** Filter for types of units to attack. */
        public Boolf<Unit> unitFilter = u -> true;
        /** Filter for types of buildings to attack. */
        public Boolf<Building> buildingFilter = b -> true;

        public TurretMode(String name, Boolf<Unit> un, Boolf<Building> bu){
            this.buildingFilter = bu;
            this.unitFilter = un;
            this.name = name;
        }

        public TurretMode(String name){
            this.name = name;
        }


        public String String(){
            return name;
        }

        TurretMode(){}
    }
}
