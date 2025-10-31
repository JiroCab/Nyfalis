package olupis.world.entities.abilities;

import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

import static mindustry.ai.UnitStance.holdFire;

public class ActivePayloadHandler extends Ability{
    public HashMap<Weapon, Float> Cooldown = new HashMap();

    @Override
    public void update(Unit unit){
        //AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH

        if(unit.isCommandable() && unit.command().hasStance(holdFire)) return;

        if(!(unit instanceof Payloadc c)) return;
        if(c.payloads().isEmpty()) return;
        int[] m ={-1};

        float range = c.payloads().first().content() instanceof UnitType ut ? ut.maxRange : 20f;
        Teamc target = Units.bestTarget(unit.team, unit.x, unit.y, range, a -> !a.dead(), b -> true, UnitSorts.closest);
        if(target == null) return;

        float aimX = target.x(), aimY = target.y();


        for(Payload p : c.payloads()){
            if(p instanceof UnitPayload up && up.unit.hasWeapons()){
                Unit u = up.unit;
                for(int i = 0; i < u.type.weapons.size; i++){
                    Weapon w = u.type.weapons.get(i);
                    u.mounts[i].shoot = true;
                    w.update(unit, u.mounts[i]);
                    Log.err("owo");
                }
            }
        }
    }
}
