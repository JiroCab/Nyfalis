package olupis.world.ai;

import arc.math.geom.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.type.weapons.RepairBeamWeapon.*;
import olupis.world.entities.units.*;

import static mindustry.Vars.state;

public class AgressiveFlyingAi extends FlyingAI {
    public boolean shouldCircle = false;
    public float circleDistance = 150f;
    public float parentCircle = 35f, shootSlowDown = 0.5f;

    @Override
    public void updateUnit(){
        if(unit.controller() instanceof CommandAI ai){
            ai.defaultBehavior();
            if(ai.attackTarget != null && shouldCircle){
                target = ai.attackTarget;
                circleAttack(circleDistance);
            }
        } else super.updateUnit();
    }

    public AgressiveFlyingAi(boolean hunt){
        if (hunt)fallback = new SearchAndDestroyFlyingAi();
    }

    public AgressiveFlyingAi(){}

    public AgressiveFlyingAi(boolean hunt, boolean shouldCircle){
        if (hunt)fallback = new SearchAndDestroyFlyingAi();
        this.shouldCircle = shouldCircle;
    }

    @Override
    public void updateMovement(){
        unloadPayloads();
        Unit parent = null;
        if(unit.type instanceof AmmoEnabledUnitType em && em.relationship.containsKey(unit) && em.relationship.get(unit) instanceof  Unit up) parent = up;

        //Allways follow parent regardless
        if(parent != null && !parent.dead() && unit.isAdded()) {
            /*Perhaps with more units, use the v5 formations instead*/
            float speed =  unit.within(parent, parentCircle * 1.1f) ?Math.min(parent.speed(), unit.isShooting ? unit.speed() * shootSlowDown: unit.speed()) : unit.speed() ;
            circle(parent, parentCircle, speed);
        }else if(unit.type instanceof AmmoLifeTimeUnitType unt && parent != null){
            unit.ammo = unt.deathThreshold * 0.5f;
        }else if(target != null && unit.hasWeapons()){
            if(unit.type.circleTarget || shouldCircle){
                circleAttack(circleDistance);
            }else{
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }
        } else if(target == null && state.rules.waves && unit.team == state.rules.defaultTeam){
            moveTo(getClosestSpawner(), state.rules.dropZoneRadius + 130f);
        }
    }

    public void updateWeapons(){
        //always loot at the healbeam's target (embryo)
        if(unit.mounts[0] instanceof HealBeamMount hm && hm.target != null){
            unit.aimLook(hm.target); unit.lookAt(hm.target); unit.aim(hm.target);

        }else{
            Unit parent = null;
            if(unit.type instanceof AmmoEnabledUnitType em && em.relationship.containsKey(unit) && em.relationship.get(unit) instanceof  Unit up) parent = up;
            if(parent != null && !parent.dead){

                Vec2 aimVec = Predict.intercept(vec , new Vec2(parent.aimX, parent.aimY), unit.type.weapons.first().bullet.speed);
                if(!parent.isShooting) aimVec = Predict.intercept(vec, unit, unit.speed());
                /*I don't know which one worked so have all of them*/
                unit.aimLook(aimVec); unit.lookAt(aimVec); unit.aim(aimVec);
                unit.isShooting = parent.isShooting();

                for(var mount : unit.mounts) {
                    Weapon weapon = mount.weapon;
                    //let uncontrollable weapons do their own thing
                    if (!weapon.aiControllable) {
                        mount.rotate = false;
                        continue;
                    }
                    if (weapon.noAttack) continue;

                    Vec2 to = Predict.intercept(vec, aimVec, weapon.bullet.speed);
                    mount.aimX = to.x;
                    mount.aimY = to.y;
                    mount.shoot = parent.isShooting;
                }
            } else{
                super.updateWeapons();
                if(!unit.isShooting && unit().hasWeapons()){
                    /*shoot range regardless if it's the target & there's enemies nearby*/
                    Teamc check = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(), u -> u.checkTarget(unit.type.targetAir, unit.type.targetGround));
                    unit.isShooting = check != null;
                }
            }
        }
    }

    @Override
    public boolean useFallback(){ /*allowed to be used in waves*/
        boolean hasParent = unit.type instanceof AmmoEnabledUnitType em && em.relationship.containsKey(unit) && em.relationship.get(unit) instanceof  Unit;
        return hasParent && (unit.team.isAI() || unit.team == state.rules.waveTeam);
    }

    public boolean hasTarget (){
        return target != null && !invalid(target);
    }
}
