 package olupis.world.ai;

 import arc.math.*;
 import arc.math.geom.*;
 import arc.struct.*;
 import arc.util.*;
 import mindustry.*;
 import mindustry.ai.types.*;
 import mindustry.entities.*;
 import mindustry.gen.*;
 import mindustry.logic.*;
 import mindustry.type.*;
 import mindustry.world.meta.*;
 import olupis.world.entities.units.*;
 import org.w3c.dom.ranges.*;

 import static mindustry.Vars.*;

 /*FlyingAi but really aggressive
 * was  but this has developed into something else */
public class SearchAndDestroyFlyingAi extends FlyingAI  implements InoperableAi{
    /*avoids stuttering on trying to go to spawn after target is null*/
    public float delay = 70f * 60f, idleAfter;
    /*screw crawlers in particular*/
    public boolean suicideOnSuicideUnits = false, suicideOnTarget = false, targetOverriden = false;
    /*Compensate for target speed, for better chasing */
    public boolean compensateTargetSpeed = true;
    /*Allow the ai to seek new targets or pick one and idle*/
    public boolean updateTargeting = true;

    public boolean circleBombing = false;
    public boolean targetFlames = false;
    public @Nullable Fire tarFire;

    public SearchAndDestroyFlyingAi(boolean suicideOnSuicideUnits){
        this.suicideOnTarget = suicideOnSuicideUnits;
    }
    public SearchAndDestroyFlyingAi(){}

    private boolean inoperable  = false;
    private float lastMoveX, lastMoveY;

    @Override
    public boolean inoperable(){
        return inoperable;
    }

    @Override
    public void updateMovement(){
        unloadPayloads();

        targetOverriden = false;
        if(invalid(target)){
            inoperable = true;
            if(updateTargeting) target = null;
        }
        Teamc parent = null;
        if(unit.type instanceof  AmmoEnabledUnitType ae && ae.relationship.containsKey(unit)){
            parent= ae.relationship.get(unit);
            if(parent != null){
                if( unit.ammo <= 0 || (parent instanceof Ranged pr && (!unit.within(pr, pr.range()) || (target == null || !target.within(pr, pr.range()))) )){
                    justMove(parent);
                    target = null;
                    return;
                }

            }
        }

        if(target == null){
            inoperable = true;
            if( Time.time >= idleAfter) {
                //protect key points on idle
                if(unit.closestEnemyCore() != null && unit.inFogTo(unit.team) && unit.within(unit.closestEnemyCore(), Math.min(600f, unit().range() * 2f))) justMove(unit.closestEnemyCore(), unit.range() * 2f);
                else if(getClosestSpawner() != null && unit.within(getClosestSpawner(), Math.min(600f, unit().range() * 1.5f) + state.rules.dropZoneRadius) ) justMove(unit.closestCore(), (unit().range() * 1.5f) + state.rules.dropZoneRadius);
                else if(unit.closestCore() != null && unit.within(unit.closestCore(), Math.min(800f, unit().range() * 2f))) justMove(unit.closestCore(), unit.range());
                else if(parent != null && unit.within(parent, Math.min(800f, unit().range() * 2f))){
                    target = null;
                    justMove(parent, unit.range());
                }

            }
            else findMainTarget(unit.x, unit.y, unit.range(), unit.type().targetAir, unit.type().targetGround);
        }
        /*screw crawlers in particular*/
        float range = ((suicideOnSuicideUnits || (targetFlames && tarFire != null && unit.ammo < (unit.type.ammoCapacity * 0.05f))) && suicideOnTarget) ? 0f : Math.min(unit.range() -5f, 5f) ;

        if(target != null && unit.hasWeapons()){
            idleAfter = Time.time + delay;
            float speed = target instanceof Unit tar ? unit().speed() + tar.speed() : unit.speed();
            Vec2 tarVec = Predict.intercept(unit, target, speed);

            if(unit.isFlying()){
                if(unit.type.circleTarget || circleBombing && (target instanceof Building || (target instanceof Unit p && p.isGrounded()))){
                    circleAttack(120f);
                }else if (compensateTargetSpeed){
                    float moveSpd = target instanceof Unit tar ? unit.within(tarVec, range) ?tar.moving() ?Math.min(tar.speed(), unit.speed()): Mathf.lerp(unit.speed(), 0, 1f) : unit.speed() : unit.speed();
                    vec.set(tarVec).sub(unit).setLength(moveSpd);
                    if(suicideOnSuicideUnits || !unit.within(target, unit.range() * 0.95f)) unit.moveAt(vec);
                } else  moveTo(target, range);
            } else {
                justMove(target);
            }

        }else if(target == null && targetFlames && !Groups.fire.isEmpty()){
            Seq<Fire> ff = Groups.fire.copy().sort(f -> f.dst(unit));
            tarFire = ff.find(f -> f.within(unit, 650f ));
            if(tarFire != null){
                idleAfter = Time.time + delay;
                unit.isShooting = targetOverriden = unit.within(tarFire, unit.range() * 1.05f);
                moveTo(tarFire, range * 0.8f, 300f, false, null, false);
            }
        }
    }

    public void updateWeapons(){
        if(targetOverriden && tarFire != null) {
            unit.isShooting = true;
            /*I don't know which one worked so have all of them*/
            unit.aimLook(tarFire); unit.lookAt(tarFire); unit.aim(tarFire);
            for(var mount : unit.mounts) {
                Weapon weapon = mount.weapon;

                //let uncontrollable weapons do their own thing
                if (!weapon.controllable || weapon.noAttack) continue;

                mount.aimX = tarFire.x;
                mount.aimY = tarFire.y;
                mount.shoot = true;
            }
        };
        if(compensateTargetSpeed){
            if(target == null) target = findMainTarget(unit.x, unit.y, unit.range(), unit.type.targetAir, unit.type.targetGround);
            noTargetTime += Time.delta;
            if(invalid(target)) return;
            else noTargetTime = 0f;

            float speed = target instanceof Unit tar ? unit().speed() + tar.speed() : unit.speed();
            Vec2 tarVec = Predict.intercept(unit, target, speed);
            boolean inRange = unit.within(tarVec, unit.range());

            /*I don't know which one worked so have all of them*/
            unit.aimLook(tarVec); unit.lookAt(tarVec); unit.aim(tarVec);
            unit.isShooting = inRange;
            for(var mount : unit.mounts) {
                Weapon weapon = mount.weapon;
                Vec2 to = Predict.intercept(unit, tarVec, weapon.bullet.speed);

                //let uncontrollable weapons do their own thing
                if (!weapon.controllable || weapon.noAttack) continue;

                if (!weapon.aiControllable) {
                    mount.rotate = false;
                    continue;
                }

                mount.aimX = to.x;
                mount.aimY = to.y;
                mount.shoot = inRange;
            }

        } else { super.updateWeapons();
            if( target != null && unit.within(target, unit.range())){
                unit.isShooting = true;
                for(var mount : unit.mounts){
                    if(!mount.weapon.controllable || mount.weapon.noAttack) continue;
                    mount.shoot = true;
         }}}
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){

        if(unit.type instanceof  AmmoEnabledUnitType ae && ae.relationship.containsKey(unit)){
            Teamc parent = ae.relationship.get(unit);
            if(parent != null){
                if(unit.ammo <= 0) return null;
                if(parent instanceof  Ranged pr){
                    if(!unit.within(pr, pr.range())) return null;
                    return Units.closestTarget(unit.team, parent.x(), parent.y(), pr.range() + 16f, u -> air && !u.inFogTo(unit.team), b -> ground && !b.inFogTo(unit.team)) ;
                }
            }

        }


        var search = Units.closestTarget(unit.team, x, y, Float.MAX_VALUE, u -> air && !u.inFogTo(unit.team), b -> ground && !b.inFogTo(unit.team)) ;
        if(search != null){
            suicideOnTarget = Units.closestTarget(unit.team, x, y, Float.MAX_VALUE, u -> u.type().weapons.find(w->w.bullet.killShooter) != null, b -> ground) != null;
            return search;
        }

        for(var flag : unit.type.targetFlags){
            if(flag == null){
                Teamc result = target(x, y, range, air, ground);
                if(result != null) return result;
            }else if(ground){
                Teamc result = targetFlag(x, y, flag, true);
                if(result != null) return result;
            }
        }
        return targetFlag(x, y, BlockFlag.core, true);
    }


    public void justMove(Teamc target){
      justMove(target, unit.range() * 0.85f);
    }
    public void justMove(Teamc target, float range){
        if (unit.type.flying) moveTo(target, range);
        else {
            if(!Mathf.equal(target.getX(), lastMoveX, 0.1f) || !Mathf.equal(target.getY(), lastMoveY, 0.1f)){
            //lastPathId ++;
                lastMoveX = target.getX();
                lastMoveY = target.getY();
            }
            if (Vars.controlPath.getPathPosition(unit, Tmp.v2.set(target.getX(), target.getY()), Tmp.v1, null)) {
                unit.lookAt(Tmp.v1);
                moveTo(Tmp.v1, 1f, Tmp.v2.epsilonEquals(Tmp.v1, 4.1f) ? 30f : 0f, false, null);
            } else unit.lookAt(unit.prefRotation());
        }
    }

}
