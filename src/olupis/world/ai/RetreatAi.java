package olupis.world.ai;

import arc.math.*;
import mindustry.entities.*;
import mindustry.gen.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.entities.abilities.*;

public class RetreatAi extends ArmDefenderAi{

    @Override
    public Teamc findFollow(float x, float y, float range){
        if(unit.type.ammoType == NyfalisUnits.carrierTypeAmmo){
            return  Units.closest(unit.team, x, y, Float.MAX_VALUE, u -> !u.dead() && u.type != unit.type && u.type.abilities.contains(a -> a instanceof CarrierResupplyAbility),
            (u, tx, ty) -> -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400f);
        }
        return super.findFollow(x, y, range);
    }
}
