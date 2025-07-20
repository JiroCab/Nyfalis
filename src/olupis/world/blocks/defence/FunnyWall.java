package olupis.world.blocks.defence;

import arc.util.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;

public class FunnyWall extends Wall{

    @Override
    public void setStats(){
        super.setStats();


        if(synthetic() && armor < 0){
            stats.add(Stat.armor, Strings.autoFixed(armor, 0), StatUnit.none);
        }
    }

    public FunnyWall (String name){
        super(name);
    }


    public class FunnyWallBuild extends  WallBuild{

    }
}
