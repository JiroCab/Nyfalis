package olupis.content;

import mindustry.gen.*;
import olupis.world.entities.*;

public class NyfUnitMapper{

    public static int LeggedPayload;

    public static void load(){
        //Thank you Siede for explaining how to do this!! ^w^
        LeggedPayload = EntityMapping.register("nyf-legged-naval", LeggedPayloadUnitClass::create);
    }

}
