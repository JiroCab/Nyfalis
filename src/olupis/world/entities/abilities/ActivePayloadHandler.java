package olupis.world.entities.abilities;

import arc.math.*;
import arc.math.geom.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;
import olupis.*;
import olupis.content.*;

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

                m[0] = 0;
                if(u.type.weapons.size >= 1){
                    WeaponMount[] mounts = u.mounts();
                    for(WeaponMount mount : mounts){
                        mount.shoot = true;
                        Weapon w = NyfalisVars.payloadWeaponIndex.get(u.type)[m[0]];
                        mount.rotation = Angles.angle(w.x + unit.x, w.y + unit.y, aimX, aimY) - unit.rotation;


                        if(w.predictTarget){
                            Vec2 to = Predict.intercept(unit, target, w.bullet.speed);
                            aimX = to.x;
                            aimY = to.y;
                        }

                        mount.aimX = aimX;
                        mount.aimY = aimY;

                        w.update(unit, mount);
                        m[0]++;
                    }
                }
            }
        }


    }
}
