package olupis.world.entities.weapons;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.*;
import mindustry.ai.types.LogicAI;
import mindustry.audio.SoundLoop;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import olupis.content.NyfalisStatusEffects;
import olupis.input.NyfalisUnitCommands;
import olupis.world.entities.NyfalisStats;
import olupis.world.entities.parts.NyfPartParms;
import olupis.world.entities.units.NyfalisUnitType;

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
    /*Check for ammo regardless of the rule*/
    alwaysUseAmmo = false,
    statusOnlyOnHit = false
    ;
    /*Margin where when a weapon can fire while transition from ground to air*/
    public  float boostedEvaluation = 0.95f, groundedEvaluation = 0.05f;
    /*Snek weapon helper so I don't have to override anything else there*/
    float shootXf = shootX, shootYf = shootY;
    boolean altWeaponPos = false;
    public float ammoPerShot = -1;
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
        if(parts.size > 0){
            float water = 0, ammop = 0;
            if(unit.type instanceof  NyfalisUnitType nyf){
                ammop = nyf.partAmmo(unit);
            }
            NyfPartParms.nyfparams.set(
                unit.healthf(),
                unit.team.id,
                unit.elevation(),
                ammop,
                 NyfalisUnitType.onWater(unit) ? 1 : 0,
                0,
                unit instanceof Payloadc p ? p.payloads().size : 0
            );
        }
        super.draw(unit, mount);
    }

    @Override
    public void update(Unit unit, WeaponMount mount){

        if(unit.hasEffect(NyfalisStatusEffects.unloaded)){
            mount.reload = reload;
        }

        boolean can = !unit.disarmed
                && (unit.onSolid() && fireOverSolids || !unit.onSolid()) && (!unit.type.canBoost ||
                (unit.isFlying() && boostShoot  && unit.elevation >= boostedEvaluation || unit.isGrounded() && groundShoot  && unit.elevation <= groundedEvaluation));
        float lastReload = mount.reload;
        mount.reload =Math.max(mount.reload - Time.delta *unit.reloadMultiplier,0);
        mount.recoil = Mathf.approachDelta(mount.recoil,0,unit.reloadMultiplier /recoilTime);
        if(recoils >0)

        {
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
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                    axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

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

        float weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
                mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
                bulletX = altWeaponPos  ? shootXf : mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
                bulletY =  altWeaponPos  ? shootYf : mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
                shootAngle = bulletRotation(unit, mount, bulletX, bulletY);

        //find a new target
        if(!controllable && autoTarget){
            if ((mount.retarget -= Time.delta) <= 0f) {
                mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround);
                mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
            }

            if (mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)) {
                mount.target = null;
            }

            boolean shoot;

            if (mount.target != null) {
                shoot = mount.target.within(mountX, mountY, bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize() / 2f : 0f)) && can;

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
                mount.aimX = partialControl ? unit.aimX : bulletX;
                mount.aimY = partialControl ? unit.aimY : bulletY;
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
                mount.bullet.rotation(weaponRotation + 90);
                mount.bullet.set(bulletX, bulletY);
                mount.reload = reload;
                mount.recoil = 1f;
                unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, mount.bullet.type.recoil * Time.delta));
                if (shootSound != Sounds.none && !headless) {
                    if (mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
                    mount.sound.update(bulletX, bulletY, true);
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
                mount.sound.update(bulletX, bulletY, false);
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
                (!useAmmo ||unit.ammo >0|| (!state.rules.unitAmmo && !alwaysUseAmmo) ||unit.team.rules().infiniteAmmo)&& //check ammo
                (!alternate ||wasFlipped ==flipSprite)&&
                mount.warmup >=minWarmup && //must be warmed up
                unit.vel.len()>=minShootVelocity && //check velocity requirements
                (mount.reload <=0.0001f||(alwaysContinuous &&mount.bullet ==null))&& //reload has to be 0, or it has to be an always-continuous weapon
                (alwaysShooting || (!strictAngle || Angles.within(rotate ?mount.rotation :unit.rotation +baseRotation,mount.targetRotation,shootCone))) //has to be within the cone
        ) {
            shoot(unit, mount, bulletX, bulletY, shootAngle);

            mount.reload = reload;

            if (useAmmo) {
                if(ammoPerShot == -1)unit.ammo--;
                else if (ammoPerShot > 0) unit.ammo -= ammoPerShot;
                if (unit.ammo < 0) unit.ammo = 0;
            }
        }
    }

    @Override
    public void addStats(UnitType u, Table t){
        if(inaccuracy > 0){
            t.row();
            t.add("[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int)inaccuracy + " " + StatUnit.degrees.localized());
        }
        if(!alwaysContinuous && reload > 0){
            t.row();
            t.add("[lightgray]" + Stat.reload.localized() + ": " + (mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f / reload * shoot.shots, 2) + " " + StatUnit.perSecond.localized());
        }
        if(statsBlocksOnly){
            NyfalisStats.ammoBlocksOnly(ObjectMap.of(u, bullet), null).display(t);
            return;
        }
        NyfalisStats.ammo(ObjectMap.of(u, bullet)).display(t);
    }

}
