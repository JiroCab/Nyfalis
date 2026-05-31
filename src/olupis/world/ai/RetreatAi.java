package olupis.world.ai;

import arc.math.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.gen.*;
import olupis.content.*;
import olupis.world.entities.abilities.*;
import olupis.world.entities.entities.*;
import olupis.world.entities.units.*;
import olupis.world.interfaces.*;

public class RetreatAi extends ArmDefenderAi{

    @Override
    public void updateMovement(){
        if(checkMin()) {
            target = null;
            super.updateMovement();
            return;
        }

        if(unit.controller() instanceof CommandAI ai){
            ai.defaultBehavior();
        }
        if(unit.isCommandable()){
            boolean hold = false;
            if(!unit.command().hasStance(UnitStance.pursueTarget)){
                if(unit.command().targetPos != null && !unit.within(unit.command().targetPos , unit.range())) hold = true;
                if(unit.command().attackTarget != null && !unit.within(unit.command().attackTarget , unit.range())) hold = true;
            }

            unit.command().setStance(UnitStance.holdFire, hold);
        }
    }

    public boolean checkMin(){
        if(unit instanceof AmmoNyf a){
            return a.shouldRetreat();
        }
        return  false;
    }

    @Override
    public Teamc findFollow(float x, float y, float range){
        if(unit instanceof AmmoEnabledUnitClass){
            return  Units.closest(unit.team, x, y, Float.MAX_VALUE, u -> !u.dead() && u.type != unit.type && u.type.abilities.contains(a -> a instanceof CarrierResupplyAbility),
            (u, tx, ty) -> -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400f);
        }
        return super.findFollow(x, y, range);
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground){
        if(checkMin()) return null;
        return super.findTarget(x, y, range, air, ground);
    }
}
