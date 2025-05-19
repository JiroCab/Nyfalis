package olupis.world.consumer;

import arc.struct.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.consumers.*;
import olupis.content.*;

public class ConsumePressureAgent  extends ConsumeCoolant{
    Seq<Liquid> allowedCoolants = Seq.with(Liquids.water, NyfalisItemsLiquid.steam);

    public ConsumePressureAgent(float amount){
        super(amount);
        this.filter = liquid -> allowedCoolants.contains(liquid);
    }
}
