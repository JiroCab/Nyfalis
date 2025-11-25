package olupis.world.ai;

import arc.math.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.gen.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.entities.abilities.*;
import olupis.world.entities.units.*;

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
    }

    public boolean checkMin(){
        int min = Math.round(unit.type.ammoCapacity * 0.1f);
        if(unit.type instanceof AmmoEnabledUnitType a){
            min = Math.round(a.setRetreat ? a.minRetreatAmmo : a.ammoCapacity * a.minRetreatAmmo);
        }
        return  min >= unit.ammo;
    }

    @Override
    public Teamc findFollow(float x, float y, float range){
        if(unit.type.ammoType == NyfalisUnits.carrierTypeAmmo){
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
