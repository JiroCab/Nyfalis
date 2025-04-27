package olupis.world.ai;

import arc.math.*;
import mindustry.ai.types.*;
import mindustry.gen.*;
import mindustry.logic.*;

public class PayloadCarrierAi extends FlyingAI{
    boolean rush = false, rangeC = false;

    @Override
    public void init(){
        rush = Mathf.randomSeed(unit.id, 0, 0.1f) == 0;
        rangeC = Mathf.randomSeed(unit.id, 0, 0.1f) == 0;
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground){
        if(rush) return findMainTarget(x, y, range, air, ground);

        return super.findTarget(x, y, range, air, ground);
    }

    @Override
    public void updateMovement(){
        if(unit instanceof Payloadc pay){

            if(rangeC && target instanceof Ranged r && unit.within(r, (r.range() * 1.15f))) pay.dropLastPayload();
            unloadPayloads();

            if(target != null) circleAttack(20f);

        }else super.updateMovement();

    }
}
