package olupis.world.interfaces;

import arc.struct.*;
import olupis.world.blocks.turret.TurretModes.*;

public interface TurretModeable{
     Seq<TurretMode> modes = new Seq<>();

    default void setModes(Seq<TurretMode> m){
        modes.set(m);
    }

}
