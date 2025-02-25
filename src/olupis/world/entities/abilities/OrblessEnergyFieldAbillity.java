package olupis.world.entities.abilities;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.Vars.state;

public class OrblessEnergyFieldAbillity extends EnergyFieldAbility{
    private static final Seq<Healthc> all = new Seq<>();
    public boolean orb = false, displayRange  = false, parentizeEffects = true;

    public OrblessEnergyFieldAbillity(float damage, float reload, float range){
        super(damage, reload, range );
    }

    @Override
    public void draw(Unit unit){

        Draw.z(layer);
        Draw.color(color);
        Tmp.v1.trns(unit.rotation - 90, x, y).add(unit.x, unit.y);
        float rx = Tmp.v1.x, ry = Tmp.v1.y;
        float orbRadius = effectRadius * (1f + Mathf.absin(blinkScl, blinkSize));

        if(orb){
            Fill.circle(rx, ry, orbRadius);
            Draw.color();
            Fill.circle(rx, ry, orbRadius / 2f);

            Lines.stroke((0.7f + Mathf.absin(blinkScl, 0.7f)), color);
            for(int i = 0; i < sectors; i++){
                float rot = unit.rotation + i * 360f/sectors - Time.time * rotateSpeed;
                Lines.arc(rx, ry, orbRadius + 3f, sectorRad, rot);
            }
        }

        Lines.stroke(Lines.getStroke() * curStroke);

        if(curStroke > 0 && displayRange){
            for(int i = 0; i < sectors; i++){
                float rot = unit.rotation + i * 360f/sectors + Time.time * rotateSpeed;
                Lines.arc(rx, ry, range, sectorRad, rot);
            }
        }

        Drawf.light(rx, ry, range * 1.5f, color, curStroke * 0.8f);

        Draw.reset();
    }


    @Override
    public void update(Unit unit){

        curStroke = Mathf.lerpDelta(curStroke, anyNearby ? 1 : 0, 0.09f);

        if((timer += Time.delta) >= reload && (!useAmmo || unit.ammo > 0 || !state.rules.unitAmmo)){
            Tmp.v1.trns(unit.rotation - 90, x, y).add(unit.x, unit.y);
            float rx = Tmp.v1.x, ry = Tmp.v1.y;
            anyNearby = false;

            all.clear();

            if(hitUnits){
                Units.nearby(null, rx, ry, range, other -> {
                    if(other != unit && other.checkTarget(targetAir, targetGround) && other.targetable(unit.team) && (other.team != unit.team || other.damaged())){
                        all.add(other);
                    }
                });
            }

            if(hitBuildings && targetGround){
                Units.nearbyBuildings(rx, ry, range, b -> {
                    if((b.team != Team.derelict || state.rules.coreCapture) && (b.team != unit.team || b.damaged())){
                        all.add(b);
                    }
                });
            }

            all.sort(h -> h.dst2(rx, ry));
            int len = Math.min(all.size, maxTargets);
            for(int i = 0; i < len; i++){
                Healthc other = all.get(i);

                //lightning gets absorbed by plastanium
                var absorber = Damage.findAbsorber(unit.team, rx, ry, other.getX(), other.getY());
                if(absorber != null){
                    other = absorber;
                }

                if(((Teamc)other).team() == unit.team ){
                    if(other.damaged() && healPercent >= 0){
                        anyNearby = true;
                        float healMult = (other instanceof Unit u && u.type == unit.type) ? sameTypeHealMult : 1f;
                        other.heal(healPercent / 100f * other.maxHealth() * healMult);
                        healEffect.at(other);
                        damageEffect.at(rx, ry, unit.rotation, color, other);
                        hitEffect.at(rx, ry, unit.angleTo(other), color);

                        if(other instanceof Building b){
                            Fx.healBlockFull.at(b.x, b.y, 0f, color, b.block);
                        }
                    }
                }else{
                    anyNearby = true;
                    if(other instanceof Building b){
                        b.damage(unit.team, damage * state.rules.unitDamage(unit.team));
                    }else{
                        other.damage(damage * state.rules.unitDamage(unit.team));
                    }
                    if(other instanceof Statusc s){
                        s.apply(status, statusDuration);
                    }
                    hitEffect.at(other.x(), other.y(), unit.angleTo(other), color);
                    damageEffect.at(rx, ry, unit.rotation, color, other);
                    hitEffect.at(rx, ry, unit.angleTo(other), color);
                }
            }

            if(anyNearby){
                shootSound.at(unit);

                if(useAmmo && state.rules.unitAmmo){
                    unit.ammo --;
                }
            }

            timer = 0f;
        }
    }
}
