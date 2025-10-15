package olupis.world.blocks;

import arc.struct.*;
import arc.util.*;
import olupis.world.blocks.defence.*;
import olupis.world.blocks.defence.Articulator.*;

public interface Moduleable{
    //IF more blocks need thiis, make a interface to for setting stuff for the block class

    default Seq<ArticulatorBuild> getModules() {
        return new Seq<>();
    }

    default void updateModules(Articulator.ArticulatorBuild build){
        if(build.tier() >= minTier() && build.tier() <= maxTier()) getModules().addUnique(build);
    }

   default void removeModule(Articulator.ArticulatorBuild build){
        getModules().remove(build);
    }

    default int minTier(){
        return Integer.MIN_VALUE;
    }
    default int maxTier(){
        return Integer.MAX_VALUE;
    }

    default float moduleEfficiency(){
        if(getModules().size <= 0 ) return 1;
        float[] total = {1f};
        for(ArticulatorBuild m : getModules()) total[0] *= m.efficiency;
        return total[0];
    }

    default @Nullable String boosterDesc(){
        return null;
    }
}
