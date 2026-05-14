package olupis.world.blocks.environment;

import mindustry.content.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import olupis.*;
import olupis.content.*;
import olupis.input.*;

import static olupis.content.NyfalisAttributeWeather.*;

public class FloodPlaneFloor extends Floor{

    public FloodPlaneFloor(String name){
        super(name);
        albedo = 0.5f;
        liquidMultiplier = 0f;

        liquidDrop = Liquids.water;
        isLiquid = true;

        statusDuration = 30f;
        status = StatusEffects.muddy;

        //This is additive and added to nyfalis pumps
        attributes.set(flood, 1);
        attributes.set(bio, 0.1f);
        speedMultiplier = 0.75f;
        cacheLayer = NyfalisShaders.floodPlaneC;
        isLiquid = false;
    }
}
