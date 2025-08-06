package olupis.world.entities.units;

import arc.*;
import olupis.world.ai.*;

/*ehehe snek*/
public class SnekUnitType extends NyfalisUnitType{
    public boolean customShadow = true;

    public SnekUnitType(String name){
        super(name);
        pathCost = NyfalisPathfind.costSnek;
    }

    @Override
    public void load(){
        super.load();

        if(customShadow) softShadowRegion = Core.atlas.find("olupis-shadow-long");
    }

}

