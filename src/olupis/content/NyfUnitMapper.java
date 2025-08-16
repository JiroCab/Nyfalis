package olupis.content;

import mindustry.gen.*;
import olupis.world.entities.entities.*;

public class  NyfUnitMapper{

    public static int LeggedPayload, OnePayloadUnit, tonkNaval, snekUnit;

    public static void load(){
        //Thank you Siede for explaining how to do this!! ^w^
        LeggedPayload = EntityMapping.register("nyf-legged-naval", LeggedPayloadUnitClass::create);
        OnePayloadUnit = EntityMapping.register("nyf-one-payload", OnePayloadUnitClass::create);
        tonkNaval = EntityMapping.register("nyf-tonk-naval", TonkNavalUnitClass::create);
        snekUnit = EntityMapping.register("nyf-snek", SnekUnitClass::create);

    }

}
