package olupis.world.entities.entities;

import mindustry.gen.*;
import olupis.content.*;
import olupis.world.entities.units.*;

public class OnePayloadUnitClass extends PayloadUnit{

    public static OnePayloadUnitClass create() {
        return new OnePayloadUnitClass();
    }

    protected OnePayloadUnitClass(){
        super();
    }

    @Override
    public int classId(){
        return NyfUnitMapper.OnePayloadUnit;
    }

    @Override
    public boolean canPickup(Building build){
        if(payloads.size >= 1) return false;
        if(type instanceof NyfalisUnitType n && !n.pickupBlocks)  return false;
        return this.payloadUsed() + (float)(build.block.size * build.block.size * 8 * 8) <= this.type.payloadCapacity + 0.001F && build.canPickup() && build.team == this.team;
    }

    @Override
    public boolean canPickup(Unit unit){
        if(payloads.size >= 1) return false;
        return this.type.pickupUnits && this.payloadUsed() + unit.hitSize * unit.hitSize <= this.type.payloadCapacity + 0.001F && unit.team == this.team() && unit.isAI();
    }

}
