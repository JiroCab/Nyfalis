package olupis.world.entities.weapons;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.audio.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.*;
import olupis.world.entities.bullets.*;
import olupis.world.entities.units.*;
import olupis.world.interfaces.*;

import static mindustry.Vars.*;

public  class NyfalisWeapon extends Weapon {
    public boolean
    /*Determines if the weapon can shoot while boosting or not*/
    boostShoot = true, groundShoot = true,
    /*Allows weapon to be shot by the player when Ai is not using it*/
    partialControl = false,
    idlePrefRot = true, alwaysRotate = false,
    /*Shoot while dash command is selected*/
    dashShoot = false, dashExclusive = false,
    /*Check for angle to target before shooting */
    strictAngle = true,
    /*Solid check*/
    fireOverSolids = true,
    /*Fire on time out*/
    fireOnTimeOut = false,
    /*Stats*/
    statsBlocksOnly = false,
    /*Shoot even if the unit has no ammo*/
    ignoreAmmo = false,
    statusOnlyOnHit = false,
    /*Determines if the weapon can shoot while Borrowed or not*/
    borrowShoot = true, unBorrowShoot = true,
    /*Determines if this weapon can fire in active payload, overrides elevation logic*/
    activePayloadShoot = true
    ;
    /*Margin where when a weapon can fire while transition from ground to air*/
    public  float boostedEvaluation = 0.95f, groundedEvaluation = 0.05f;
    public float ammoPerShot = 1;
    public boolean weaponIconUseFullString = false;
    public String weaponIconString = "";

    public NyfalisWeapon(String name){super(name);}
    public NyfalisWeapon(String name, boolean boostShoot, boolean groundShoot ){
        super(name);
        this.boostShoot = boostShoot;
        this.groundShoot = groundShoot;
    }
    public NyfalisWeapon(){
        super();
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        if(parts.size > 0) NyfPartParms.nyfparams.set(unit);
        super.draw(unit, mount);
    }

    @Override
    public void update(Unit unit, WeaponMount mount){

        if(unit.hasEffect(NyfalisStatusEffects.unloaded)){
            mount.reload = reload;
        }

        //this is mess
        boolean
            elevation = (!unit.isAdded() && activePayloadShoot) || (!unit.type.canBoost || (unit.isFlying() && boostShoot  && unit.elevation >= boostedEvaluation || unit.isGrounded() && groundShoot  && unit.elevation <= groundedEvaluation)),
            can = !unit.disarmed
                && (unit.onSolid() && fireOverSolids || !unit.onSolid()) && elevation;

        float lastReload = mount.reload;
        mount.reload =Math.max(mount.reload - Time.delta *unit.reloadMultiplier,0);
        mount.recoil = Mathf.approachDelta(mount.recoil,0,unit.reloadMultiplier /recoilTime);
        if(recoils >0) {
            if (mount.recoils == null) mount.recoils = new float[recoils];
            for (int i = 0; i < recoils; i++) {
                mount.recoils[i] = Mathf.approachDelta(mount.recoils[i], 0, unit.reloadMultiplier / recoilTime);
            }
        }

        mount.smoothReload =Mathf.lerpDelta(mount.smoothReload,mount.reload /reload,smoothReloadSpeed);
        mount.charge =mount.charging &&shoot.firstShotDelay >0?Mathf.approachDelta(mount.charge,1,1/shoot.firstShotDelay):0;

        float warmupTarget = (can && mount.shoot) || (continuous && mount.bullet != null) || mount.charging ? 1f : 0f;
        if(linearWarmup)mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
        else mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed);

        //rotate if applicable
        if(rotate &&(mount.rotate ||mount.shoot)&&can){
            float axisX = mountX(unit),
                    axisY = mountY(unit);

            mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
            if (rotationLimit < 360) {
                float dst = Angles.angleDist(mount.rotation, baseRotation);
                if (dst > rotationLimit / 2f) {
                    mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit / 2f);
                }
            }
        }else if(!rotate){
            mount.rotation = baseRotation;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        } else if ( ( alwaysRotate || !mount.rotate || !mount.shoot) && idlePrefRot) {
            mount.targetRotation = baseRotation;
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
        }

        float shootAngle = bulletRotation(unit, mount, bulletX(unit, mount), bulletY(unit, mount));

        //find a new target
        if(!controllable && autoTarget){
            if ((mount.retarget -= Time.delta) <= 0f) {
                mount.target = findTarget(unit, mountX(unit), mountY(unit), bullet.range, bullet.collidesAir, bullet.collidesGround);
                mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
            }

            if (mount.target != null && checkTarget(unit, mount.target, mountX(unit), mountY(unit), bullet.range)) {
                mount.target = null;
            }

            boolean shoot;

            if (mount.target != null) {
                shoot = mount.target.within(mountX(unit), mountY(unit), bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize() / 2f : 0f)) && can;

                if (predictTarget) {
                    Vec2 to = Predict.intercept(unit, mount.target, bullet.speed);
                    mount.aimX = to.x;
                    mount.aimY = to.y;
                } else {
                    mount.aimX = mount.target.x();
                    mount.aimY = mount.target.y();
                }
            } else{
                shoot = partialControl && unit.isShooting && can;
                mount.aimX = partialControl ? unit.aimX : bulletX(unit, mount);
                mount.aimY = partialControl ? unit.aimY : bulletY(unit, mount);
            }

            mount.shoot = mount.rotate = shoot;

            //note that shooting state is not affected, as these cannot be controlled
            //logic will return shooting as false even if these return true, which is fine
        }

        if(alwaysShooting)mount.shoot =true;
        // deploying units can shoot regardless of elevation && LogicAi 's shouldShoot checks for boosting and this is a work around
        if(unit.type instanceof NyfalisUnitType nyf && nyf.canDeploy && unit.controller() instanceof LogicAI ai && ai.shoot)mount.shoot = true;

        if(!unit.isPlayer()) {
            boolean isDashing = unit.isCommandable() && (unit.command().command == NyfalisUnitCommands.nyfalisDashCommand || unit.command().command == NyfalisUnitCommands.nyfalisChargeCommand);
            if (dashShoot && isDashing) mount.shoot = true;
            else if (dashExclusive && !isDashing) mount.shoot = false;
        }
        //update continuous state
        if(continuous &&mount.bullet !=null) {
            if (!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != bullet) {
                mount.bullet = null;
            } else {
                mount.bullet.rotation(weaponRotation(unit, mount) + 90);
                mount.bullet.set(bulletX(unit, mount), bulletY(unit, mount));
                mount.reload = reload;
                mount.recoil = 1f;
                unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, mount.bullet.type.recoil * Time.delta));
                if (shootSound != Sounds.none && !headless) {
                    if (mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
                    mount.sound.update(bulletX(unit, mount), bulletY(unit, mount), true);
                }

                if (alwaysContinuous && mount.shoot) {
                    mount.bullet.time = mount.bullet.lifetime * mount.bullet.type.optimalLifeFract * mount.warmup;
                    mount.bullet.keepAlive = true;

                    if(!statusOnlyOnHit)unit.apply(shootStatus, shootStatusDuration);
                }
            }
        }else {
            //heat decreases when not firing
            mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0);

            if (mount.sound != null) {
                mount.sound.update(bulletX(unit, mount), bulletY(unit, mount), false);
            }
        }

        //flip weapon shoot side for alternating weapons
        boolean wasFlipped = mount.side;
        if(otherSide !=-1&&alternate &&mount.side ==flipSprite &&mount.reload <=reload /2f&&lastReload >reload /2f){
            unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
            mount.side = !mount.side;
        }

        //shoot if applicable
        if((mount.shoot || partialControl && unit.isShooting && !controllable) && //must be shooting
                can && //must be able to shoot
                !(bullet.killShooter &&mount.totalShots >0)&& //if the bullet kills the shooter, you should only ever be able to shoot once
                (ignoreAmmo || (!(unit instanceof AmmoNyf an) || an.currentAmmo() >= ammoPerShot)) &&
                (!alternate ||wasFlipped ==flipSprite)&&
                mount.warmup >=minWarmup && //must be warmed up
                unit.vel.len()>=minShootVelocity && //check velocity requirements
                (mount.reload <=0.0001f||(alwaysContinuous &&mount.bullet ==null))&& //reload has to be 0, or it has to be an always-continuous weapon
                (alwaysShooting || (!strictAngle || Angles.within(rotate ?mount.rotation :unit.rotation +baseRotation,mount.targetRotation,shootCone))) //has to be within the cone
        ) {
            shoot(unit, mount, bulletX(unit, mount), bulletY(unit, mount), shootAngle);

            mount.reload = reload;

            if(unit instanceof AmmoNyf an)an.setAmmo(Math.max(an.currentAmmo() - ammoPerShot, 0));
        }
    }


    //me when hardcoding
    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
        unit.apply(shootStatus, shootStatusDuration);

        if(shoot.firstShotDelay > 0){
            mount.charging = true;
            chargeSound.at(bulletX(unit, mount), bulletY(unit, mount), Mathf.random(soundPitchMin, soundPitchMax));
            bullet.chargeEffect.at(bulletX(unit, mount), bulletY(unit, mount), rotation, bullet.keepVelocity || parentizeEffects ? unit : null);
        }

        shoot.shoot(mount.barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
            //this is incremented immediately, as it is used for total bullet creation amount detection
            mount.totalShots ++;
            int barrel = mount.barrelCounter;

            if(delay > 0f){
                Time.run(delay, () -> {
                    //hack: make sure the barrel is the same as what it was when the bullet was queued to fire
                    int prev = mount.barrelCounter;
                    mount.barrelCounter = barrel;
                    bullet(unit, mount, xOffset, yOffset, angle, mover);
                    mount.barrelCounter = prev;
                });
            }else{
                bullet(unit, mount, xOffset, yOffset, angle, mover);
            }
        }, () -> mount.barrelCounter++);
    }

    @Override
    protected void bullet(Unit unit, WeaponMount mount, float xOffset, float yOffset, float angleOffset, Mover mover){
        if(!unit.isAdded()) return;

        mount.charging = false;
        float
        xSpread = Mathf.range(xRand),
        ySpread = Mathf.range(yRand),
        mountX = mountX(unit, xSpread, ySpread),
        mountY = mountY(unit, xSpread, ySpread),
        bulletX = bulletX(unit, mount),
        bulletY = bulletY(unit, mount),
        shootAngle = bulletRotation(unit, mount, bulletX, bulletY) + angleOffset,
        ran = bullet instanceof EffectivenessMissileType m  && m.maxRangeLifeScale? bullet.maxRange: bullet.range,
        lifeScl = bullet.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, mount.aimX, mount.aimY) / ran) : 1f,
        angle = shootAngle + Mathf.range(inaccuracy + bullet.inaccuracy);

        Entityc shooter = unit.controller() instanceof MissileAI ai ? ai.shooter : unit; //Pass the missile's shooter down to its bullets
        mount.bullet = bullet.create(unit, shooter, unit.team, bulletX, bulletY, angle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd) + extraVelocity, lifeScl, null, mover, mount.aimX, mount.aimY, mount.target);
        handleBullet(unit, mount, mount.bullet);

        if(!continuous){
            shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
        }

        ejectEffect.at(mountX, mountY, angle * Mathf.sign(this.x));
        bullet.shootEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);
        bullet.smokeEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);

        unit.vel.add(Tmp.v1.trns(shootAngle + 180f, bullet.recoil));
        Effect.shake(shake, shake, bulletX, bulletY);
        mount.recoil = 1f;
        if(recoils > 0){
            mount.recoils[mount.barrelCounter % recoils] = 1f;
        }
        mount.heat = 1f;
    }

    public float weaponRotation(Unit unit, WeaponMount mount){
        return unit.rotation - 90 + (rotate ? mount.rotation : baseRotation);
    }


    public float mountX(Unit unit){
        return mountX(unit, 0 ,0);
    }

    public float mountX(Unit unit, float xoff, float yoff){
        return unit.x + Angles.trnsx(unit.rotation - 90, x + xoff, y + yoff);
    }

    public float bulletX(Unit unit, WeaponMount mount) {
        return   mountX(unit) + Angles.trnsx(weaponRotation(unit, mount), this.shootX, this.shootY);
    }
    
    public float mountY(Unit unit){
        return mountY(unit, 0, 0);
    }

    public float mountY(Unit unit, float xoff, float yoff) {
        return unit.y + Angles.trnsy(unit.rotation - 90, x + xoff, y + yoff);
    }

    public float bulletY(Unit unit, WeaponMount mount){
        return mountY(unit) + Angles.trnsy(weaponRotation(unit, mount), this.shootX, this.shootY);
    }
}
