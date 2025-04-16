package olupis.world.ai;

import arc.math.Angles;
import arc.util.Tmp;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.Units;
import mindustry.gen.*;
import olupis.input.NyfalisUnitCommands;
import olupis.world.entities.units.NyfalisUnitType;

public class DeployedAi extends FlyingAI {

    @Override
    public void updateUnit(){
        if(unit.controller() instanceof CommandAI ai){
            if(unit.isCommandable() && unit.command().command != NyfalisUnitCommands.nyfalisDeployCommand){
                unit.updateBoosting(true);
                ai.defaultBehavior();
                if(ai.attackTarget != null && unit.within(ai.attackTarget, unit.range() - 10f) && shouldLand(ai.attackTarget) && unit.isCommandable()) unit.command().command(NyfalisUnitCommands.nyfalisDeployCommand);
            }

        }
        super.updateUnit();
    }

    @Override
    public void updateMovement(){
        if(unit.isFlying()) {
            if (unit.controller() instanceof CommandAI ai) {
                ai.defaultBehavior();

                if(ai.attackTarget != null && unit.within(ai.attackTarget, unit.type.range) && shouldLand(ai.attackTarget)){
                    if (!unit.canLand() || (unit.elevation < 1)) circleAltAttack(unit.type.range + 5f, ai.attackTarget);
                }
            }else if (target != null){
                if(unit.within(target, unit.type.range) && shouldLand(target)){
                    if (!unit.canLand() || (unit.elevation < 1)) circleAltAttack(unit.type.range + 5f, target);
                } else {
                    if(unit.type.circleTarget) circleAttack(unit.type.range + 10f);
                    else{
                        moveTo(target, unit.type.range * 0.8f);
                        unit.lookAt(target);
                    }
                }

            }

        } else moveTo(Tmp.v3.set(unit.x, unit.y), 5f, 100, false, null);

        if (unit.type.canBoost && unit.type instanceof NyfalisUnitType nyf && nyf.deployLands && shouldLand(target)) {
            if(unit.isCommandable() && unit.controller() instanceof CommandAI) unit.updateBoosting(unit.command().command != NyfalisUnitCommands.nyfalisDeployCommand);

            else{
                boolean boost =(target != null && !unit.within(target, unit.range() + 10f)) && shouldLand(target) || target == null;

                unit.updateBoosting(boost);
            }
        }

    }

    @Override
    public void updateWeapons(){
        super.updateWeapons();
        if(!unit.isShooting && unit().hasWeapons()){
            /*shoot range regardless if it's the target & there's enemies nearby*/
            Teamc check = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(), u -> u.checkTarget(unit.type.targetAir, unit.type.targetGround));
            unit.isShooting = check != null;
        }
    }

    public boolean shouldLand(Teamc target){
        boolean out = (target instanceof Unitc f && f.isFlying());
        if(inverseLanding()) out = !out;
        return out;
    }

    public boolean inverseLanding(){
        return unit.type instanceof NyfalisUnitType nyf && !nyf.inverseLanding;
    }

    public void circleAltAttack(float circleLength, Teamc tar){
        vec.set(tar).sub(unit);

        float ang = unit.angleTo(tar);
        float diff = Angles.angleDist(ang, unit.rotation());

        if(diff > 70f && vec.len() < circleLength){
            vec.setAngle(unit.vel().angle());
        }else{
            vec.setAngle(Angles.moveToward(unit.vel().angle(), vec.angle(), 6f));
        }

        vec.setLength(unit.speed() / 2f);

        unit.moveAt(vec);
    }
}