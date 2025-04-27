package olupis.world.blocks.defence;

import arc.Core;
import arc.audio.Sound;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.core.World;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.DrawPart;
import mindustry.entities.pattern.ShootPattern;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Styles;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

/*CoreBlock  & PowerTurret's very horrible offspring*/
public class PropellerCoreTurret extends PropellerCoreBlock {
    /*Refer to Turret for what these do*/
    public final int timerTarget = timers++, recoils = -1;
    public final static float logicControlCooldown = 60 * 2;
    public float
        reload = 10f,
            rotateSpeed = 5,
            range = 80f,
            shootCone = 8f,
            recoilPow = 1.8f,
            targetInterval = 20,
            ammoEjectBack = 1f,
            cooldownTime = 20f,
            soundPitchMin = 0.9f,
            soundPitchMax = 1.1f,
            shootWarmupSpeed = 0.1f,
            recoilTime = -1f, elevation = -1f,
            recoil = 1f, fogRadiusMultiplier =1f,
            shootY = Float.NEGATIVE_INFINITY,
            inaccuracy = 0f, velocityRnd = 0f,shootX = 0f, minRange = 0f,  minWarmup = 0f, warmupMaintainTime = 0f, xRand = 0f, shake = 0f
    ;
    public boolean
            accurateDelay = true,  moveWhileCharging = true,  targetAir = true, targetGround = true,  targetUnderBlocks = true,  predictTarget = true,
            alwaysShooting = false, targetHealing = false,  linearWarmup = false
    ;
    public BulletType shootType;
    public Effect ammoUseEffect = Fx.none;
    public Boolf<Unit> unitFilter = u -> true;
    public Units.Sortf unitSort = UnitSorts.closest;
    public ShootPattern shoot = new ShootPattern();
    public @Nullable Effect shootEffect, smokeEffect;
    public Sound shootSound = Sounds.shoot, chargeSound = Sounds.none;
    public Boolf<Building> buildingFilter = b -> targetUnderBlocks || !b.block.underBullets;
    public Seq<DrawPart> parts = new Seq<>();
    TextureRegion topRegion;

    public PropellerCoreTurret(String name){
        super(name);
        modes = Seq.with(
                new CoreMode(false, false, true ),
                new CoreMode( true, false, true ),
                new CoreMode(false, true, false )
        );
    }

    @Override
    public void setStats(){
        stats.add(Stat.ammo, table ->{
            table.table(Styles.grayPanel, t ->{
                t.row();

                Table ic = new Table();
                StatValues.ammo(ObjectMap.of(this, shootType), 0, false).display(ic);
                Collapser coll = new Collapser(ic, true);
                coll.setDuration(0.1f);

                t.table(it -> {
                    it.left().defaults().left();

                    it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8 * 3).scaling(Scaling.bounded).padLeft(6f).expandX().left();
                });
                t.row();
                t.add(coll);
            }).growX();
        });

        super.setStats();
        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);
        stats.add(Stat.shootRange, range / tilesize, StatUnit.blocks);
        stats.add(Stat.reload, 60f / (reload) * shoot.shots, StatUnit.perSecond);
    }

    @Override
    public void init(){
        if(shootY == Float.NEGATIVE_INFINITY) shootY = size * tilesize / 2f;
        if(elevation < 0) elevation = size / 2f;
        if(recoilTime < 0f) recoilTime = reload;
        if(cooldownTime < 0f) cooldownTime = reload;

        super.init();
    }

    @Override
    public void load(){
        topRegion = Core.atlas.find(name + "-top");

        super.load();

        for(var part : parts){
            part.turretShading = true;
            part.load(name);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);

        if(fogRadiusMultiplier < 0.99f && state.rules.fog){
            Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * fogRadiusMultiplier, Pal.lightishGray);
        }
    }

    public void limitRange( float margin){
        if(this.shootType == null) return;
        float realRange = shootType.rangeChange + range;
        //doesn't handle drag
        shootType.lifetime = (realRange + margin) / shootType.speed;
    }

    public class PropellerCoreTurretBuild extends PropellerCoreBuild {
        public @Nullable Posc target;
        public boolean wasShooting, logicShooting = false;
        public @Nullable float[] curRecoils;
        public Vec2 targetPos = new Vec2();
        public Vec2 recoilOffset = new Vec2();
        public BlockUnitc unit = (BlockUnitc) UnitTypes.block.create(team);
        public int totalAmmo, totalShots, barrelCounter, queuedBullets = 0;
        public float curRecoil, heat, shootWarmup, charge, warmupHold = 0f, reloadCounter, logicControlTime = -1;

        public float estimateDps() {
            return shoot.shots / reload * 60f * 0f * potentialEfficiency * timeScale;
        }

        @Override
        public double sense(LAccess sensor) {
            super.sense(sensor);
            return switch (sensor) {
                case ammo -> totalAmmo;
                case rotation -> rotation;
                case shootX -> World.conv(targetPos.x);
                case shootY -> World.conv(targetPos.y);
                case shooting -> isShooting() ? 1 : 0;
                case progress -> progress();
                case range -> range();
                default -> super.sense(sensor);
            };
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if (type == LAccess.shoot && !unit.isPlayer()) {
                targetPos.set(World.unconv((float) p1), World.unconv((float) p2));
                logicControlTime = logicControlCooldown;
                logicShooting = !Mathf.zero(p3);
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4) {
            if (type == LAccess.shootp && (unit == null || !unit.isPlayer())) {
                logicControlTime = logicControlCooldown;
                logicShooting = !Mathf.zero(p2);

                if (p1 instanceof Posc pos) {
                    targetPosition(pos);
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        public boolean isShooting() {
            return alwaysShooting || (logicControlled() ? logicShooting : target != null);
        }

        @Override
        public void draw(){
            super.draw();

            if(parts.size > 0) {
                float progress = progress();
                //No making parts weird for now as all that uses it looks weird
                DrawPart.params.set(warmup(), progress, progress, heat, curRecoil, charge, x + recoilOffset.x, y + recoilOffset.y, 0);

                for (var part : parts) {
                    DrawPart.params.setRecoil(part.recoilIndex >= 0 && curRecoils != null ? curRecoils[part.recoilIndex] : curRecoil);
                    part.draw(DrawPart.params);
                }
            }

            if(topRegion.found())Draw.rect(topRegion, this.x, this.y, this.drawrot());

        }

        @Override
        public float progress(){
            return Mathf.clamp(reloadCounter / reload);
        }

        @Override
        public float warmup(){
            return shootWarmup;
        }

        public boolean hasAmmo() {
            return true;
        }

        public BulletType peekAmmo() {
            return shootType;
        }

        public boolean logicControlled() {
            return logicControlTime > 0;
        }

        public boolean shouldTurn() {
            return moveWhileCharging || !charging();
        }

        protected void handleBullet(@Nullable Bullet bullet, float offsetX, float offsetY, float angleOffset) {
        }

        protected boolean canHeal() {
            return targetHealing && peekAmmo().collidesTeam && peekAmmo().heals();
        }

        protected boolean validateTarget() {
            return !Units.invalidateTarget(target, canHeal() ? Team.derelict : team, x, y) || logicControlled();
        }

        protected void turnToTarget(float targetRot) {
            rotation = (int) Angles.moveToward(rotation, targetRot, rotateSpeed * delta() * potentialEfficiency);
        }

        public boolean charging() {
            return queuedBullets > 0 && shoot.firstShotDelay > 0;
        }

        public float range() {
            if (peekAmmo() != null) {
                return range + peekAmmo().rangeChange;
            }
            return range;
        }

        protected void findTarget() {
            float range = range();

            if (targetAir && !targetGround) {
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
            } else {
                target = Units.bestTarget(team, x, y, range, e -> !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> targetGround && buildingFilter.get(b), unitSort);
            }

            if (target == null && canHeal()) {
                target = Units.findAllyTile(team, x, y, range, b -> b.damaged() && b != this);
            }
        }

        public void updateTile() {
            unit.ammo(unit.type().ammoCapacity);
            if (!validateTarget()) target = null;

            float warmupTarget = (isShooting() && canConsume()) || charging() ? 1f : 0f;
            if (warmupTarget > 0 && shootWarmup >= minWarmup) {
                warmupHold = 1f;
            }
            if (warmupHold > 0f) {
                warmupHold -= Time.delta / warmupMaintainTime;
                warmupTarget = 1f;
            }

            if (linearWarmup) {
                shootWarmup = Mathf.approachDelta(shootWarmup, warmupTarget, shootWarmupSpeed * (warmupTarget > 0 ? efficiency : 1f));
            } else {
                shootWarmup = Mathf.lerpDelta(shootWarmup, warmupTarget, shootWarmupSpeed * (warmupTarget > 0 ? efficiency : 1f));
            }

            wasShooting = false;

            curRecoil = Mathf.approachDelta(curRecoil, 0, 1 / recoilTime);
            if (recoils > 0) {
                if (curRecoils == null) curRecoils = new float[recoils];
                for (int i = 0; i < recoils; i++) {
                    curRecoils[i] = Mathf.approachDelta(curRecoils[i], 0, 1 / recoilTime);
                }
            }
            heat = Mathf.approachDelta(heat, 0, 1 / cooldownTime);
            charge = charging() ? Mathf.approachDelta(charge, 1, 1 / shoot.firstShotDelay) : 0;

            unit.tile(this);
            unit.rotation(rotation);
            unit.team(team);
            recoilOffset.trns(rotation, -Mathf.pow(curRecoil, recoilPow) * recoil);

            if (logicControlTime > 0) {
                logicControlTime -= Time.delta;
            }

            updateReload();

            if (hasAmmo()) {
                if (Float.isNaN(reloadCounter)) reloadCounter = 0;

                if (timer(timerTarget, targetInterval)) {
                    findTarget();
                }

                if (validateTarget()) {
                    boolean canShoot = true;
                    if (logicControlled()) { //logic behavior
                        canShoot = logicShooting;
                    } else {
                        //default AI behavior
                        targetPosition(target);
                    }

                    unit.aimX(targetPos.x);
                    unit.aimY(targetPos.y);

                    float targetRot = angleTo(targetPos);

                    if (shouldTurn()) {
                        turnToTarget(targetRot);
                    }

                    if (!alwaysShooting && Angles.angleDist(rotation, targetRot) < shootCone && canShoot) {
                        wasShooting = true;
                        updateShooting();
                    }
                }

                if (alwaysShooting) {
                    wasShooting = true;
                    updateShooting();
                }
            }
            super.updateTile();
        }

        public void targetPosition(Posc pos) {
            if (!hasAmmo() || pos == null) return;
            BulletType bullet = peekAmmo();

            var offset = Tmp.v1.setZero();

            //when delay is accurate, assume unit has moved by chargeTime already
            if (accurateDelay && !moveWhileCharging && pos instanceof Hitboxc h) {
                offset.set(h.deltaX(), h.deltaY()).scl(shoot.firstShotDelay / Time.delta);
            }

            if (predictTarget) {
                targetPos.set(Predict.intercept(this, pos, offset.x, offset.y, bullet.speed <= 0.01f ? 99999999f : bullet.speed));
            } else {
                targetPos.set(pos);
            }

            if (targetPos.isZero()) {
                targetPos.set(pos);
            }
        }

        protected float baseReloadSpeed() {
            return efficiency;
        }


        protected void updateReload() {
            float multiplier = 1f;
            reloadCounter += delta() * multiplier * baseReloadSpeed();

            //cap reload for visual reasons
            reloadCounter = Math.min(reloadCounter, reload);
        }

        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover) {
            queuedBullets--;

            if (dead || !hasAmmo()) return;

            float
                    xSpread = Mathf.range(xRand),
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);

            float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) : 1f;

            //TODO aimX / aimY for multi shot turrets?
            handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);

            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));

            ammoUseEffect.at(
                    x - Angles.trnsx(rotation, ammoEjectBack),
                    y - Angles.trnsy(rotation, ammoEjectBack),
                    rotation * Mathf.sign(xOffset)
            );

            if (shake > 0) {
                Effect.shake(shake, shake, this);
            }

            curRecoil = 1f;
            if (recoils > 0) {
                curRecoils[barrelCounter % recoils] = 1f;
            }
            heat = 1f;
            totalShots++;
        }

        protected void shoot(BulletType type) {
            float
                    bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);

            if (shoot.firstShotDelay > 0) {
                chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                type.chargeEffect.at(bulletX, bulletY, rotation);
            }

            shoot.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;
                if (delay > 0f) {
                    Time.run(delay, () -> bullet(type, xOffset, yOffset, angle, mover));
                } else {
                    bullet(type, xOffset, yOffset, angle, mover);
                }
            }, () -> barrelCounter++);
        }

        protected void updateShooting() {

            if (reloadCounter >= reload && !charging() && shootWarmup >= minWarmup && currentMode().stats[1]) {
                BulletType type = peekAmmo();

                shoot(type);

                reloadCounter %= reload;
            }
        }
    }
}
