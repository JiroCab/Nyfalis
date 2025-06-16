package olupis.world.entities.entities;

import mindustry.entities.*;
import mindustry.gen.*;
import olupis.content.*;
import olupis.world.entities.units.*;

public class TonkNaval extends LegsUnit{

    public static TonkNaval create() {
        return new TonkNaval();
    }

    protected TonkNaval(){
        super();
    }

    @Override
    public int classId(){
        return NyfUnitMapper.tonkNaval;
    }

    @Override
    public EntityCollisions.SolidPred solidity() {
        //cant be bothered so just return this
        return EntityCollisions::solid;
    }

    @Override
    public float speed(){
        if(type instanceof LeggedWaterUnit lw){
            if(lw.deepSpeed >= 0 && lw.onDeepWater(this)) return lw.deepSpeed;
            return NyfalisUnitType.onWater(this) ? lw.navalSpeed : lw.speed;
        }
        return super.speed();
    }
}
