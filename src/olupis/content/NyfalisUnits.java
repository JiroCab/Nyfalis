package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.struct.Queue;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;
import mindustry.world.meta.*;
import olupis.input.*;
import olupis.world.*;
import olupis.world.ai.*;
import olupis.world.blocks.defence.ItemUnitTurret.*;
import olupis.world.entities.abilities.*;
import olupis.world.entities.bullets.*;
import olupis.world.entities.entities.*;
import olupis.world.entities.parts.*;
import olupis.world.entities.units.*;
import olupis.world.entities.weapons.*;

import java.util.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.*;
import static mindustry.content.Items.*;
import static olupis.NyfalisVars.payloadWeaponIndex;
import static olupis.content.NyfalisColors.*;
import static olupis.content.NyfalisItemsLiquid.*;
import static olupis.content.NyfalisItemsLiquid.steam;
import static olupis.world.ai.NyfalisPathfind.costLeggedNaval;

public class NyfalisUnits {
    public static NyfalisUnitType
        /*Air units*/
        //Spearhead
        aero, striker, falcon, vortex, tempest,
        //Zoning / area denial - very funny i know
        zoner, regioner, district, division, territory,
        //Tank
        pteropus, acerodon, nyctalus, mirimiri , vampyrum, //Bat Genus

        /*segmented units*/
        //Spearhead
        venom, serpent, reaper, goliath, snek,

        /*Ground units*/
        //siege / roach
        supella, germanica , luridiblatta , vaga , parcoblatta, //smallest cockroaches

        /*naval*/
        //carriers-support
        sentry, warden, guardian, domination, sovereign,
        //naval glass cannons
        bay, blitz, crusader, torrent, vanguard,
        //transport
        porter, essex, lexington, resolute, nimitz,

        /*core units*/
        gnat, pedicia, phorid, diptera, midges, vespera, //Fruit flies

        /*assistant core units*/
        embryo, larva, pupa, sephera,

        /*scout boi(s)*/
        scarab, weevil,

        /*Misc/pending purpose units*/
        firefly, excess,
        lootbug, ladarHelper, searchHelper,

        /*hive spawns*/
        mite, tick, flea, lice
    ;

    public static BatHelperUnitType pteropusAir, acerodonAir, nyctalusAir, mirimiriAir , vampyrumAir;


    public static AmmoLifeTimeUnitType
        //support - yes, its just Phasmophobia ghost types
        spirit, phantom, banshee, revenant, poltergeis, shade
    ;
    public static Seq<BatHelperUnitType> batHelpers;

    public static void LoadUnits() {

        //region Air - Aero
        //Aero -> decently quick and shoot a tiny constant beam, make it fixed and do 10dps
        aero = new NyfalisUnitType("aero"){{
            hitSize = 9f;
            speed = 3.6f;
            health = 180f;
            engineSize = 3f;
            engineOffset = 7f;
            rotateSpeed = 30f;
            itemCapacity = 15;
            drag = accel = 0.08f;
            strafePenalty = 0.35f; //Aero Tree has lower strafe pen, something about they're deigned for it

            lowAltitude = flying = canCircleTarget = alwaysShootWhenMoving = true;
            constructor = UnitEntity::create;

            aiController = WaveAiHandler::new;
            defaultCommand = NyfalisUnitCommands.circleCommand;
            weapons.add(new NyfalisWeapon(""){{
                weaponIconString = "olupis-aero-ui-wep";
                top = mirror = false;
                continuous = alwaysContinuous = parentizeEffects  = true;
                shake = 0f;
                range = 30f;
                shootY = 9.1f;
                y = x = recoil = 0f;
                reload = shootCone = 30f;
                ejectEffect = Fx.none;
                outlineRegion = null;
                layerOffset = Layer.flyingUnit -1;
                shootSound = Sounds.loopElectricHum;

                bullet = new ContinuousShrapnelBulletType(){{
                    serrations = 1;
                    serrationAngle = 52f;
                    serrationFirstOffset = -1f;
                    serrationWidth = 4f;
                    serrationAlphaMul = 1f;
                    serrationLengthMul = 0.2f;
                    serrationLenScl = 5f;

                    shake = 0f;
                    width = 4f;
                    length = 20f;
                    lifetime = 32f;
                    pierceCap = 2;
                    lightStroke = 10;
                    frontLength = 10f;
                    damage = (40 / 12f);
                    homingPower = 0.06f;
                    buildingDamageMultiplier = 1.1f;
                    groundDamageMultiplier = (20 / 12f);
                    incendAmount = -1;
                    incendChance = incendSpread = 0f;
                    pierce = flatDamage = true;
                    statusDuration = 60 * 8f;
                    removeAfterPierce = false;
                    airStatus = StatusEffects.shocked;
                    smokeEffect = shootEffect = Fx.none;
                    chargeEffect = hitEffect = Fx.hitLancer;
                    colors = aeroLaserColours;
                }};
            }});
        }};

        //Striker ->pretty quick, maybe twice as fast as a flare, and shoots arc shots, like the Javelin from v5
        striker = new NyfalisUnitType("striker"){{
            armor = 4f;
            hitSize = 16f;
            drag = 0.05f;
            speed = 3.6f;
            accel = 0.07f;
            health = 300f;
            engineSize = 4f;
            itemCapacity = 30;
            engineOffset = 13.5f;
            strafePenalty = 0.35f; //Aero Tree has lower strafe pen, something about they're deigned for it
            rotateSpeed = baseRotateSpeed = 30f;

            constructor = UnitEntity::create;
            aiController = WaveAiHandler::new;
            defaultCommand = NyfalisUnitCommands.circleCommand;
            flying = canCircleTarget = alwaysCreateOutline = true;
            weapons.add(new NyfalisWeapon(""){{
                weaponIconString = "olupis-striker-ui-main";
                x = 0;
                y = 1.5f;
                inaccuracy = 3f;
                soundPitchMin = 0.2f;
                soundPitchMax = 0.5f;
                reload = 20f;
                shootCone = 15f;

                top = alternate =  mirror = false;
                alwaysShootWhenMoving = true;
                shootSound = NyfalisSounds.as2PlasmaShot;
                bullet = new ArcLightningBulletType(){{
                    rangeOverride = range = 40f;
                    damage = 13;
                    homingPower = 0.1f;

                    status = StatusEffects.none;
                    hitEffect= shootEffect = NyfalisFxs.hitSurgeZapped;
                    lightningColor = hitColor = Pal.surge;
                    lightningCone = 540;
                    failLightningBullet = true;
                    lightningType = new LightningBulletType(){{
                        damage = 7;
                        shootY = 0f;
                        drawSize = 55;
                        pierceCap = 3;
                        lightningLength = 13;
                        lightningLengthRand = 0;
                        pierce = true;
                        shootEffect = Fx.none;
                        lightningCone = 540;
                        lightningColor = hitColor = Pal.surge;
                        hitEffect = NyfalisFxs.hitSurgeZapped;

                        lightningType = new BulletType(0.0001f, 0f){{
                            pierceCap = 2;
                            statusDuration = 10f;
                            hittable = false;
                            pierce = true;
                            hitEffect = Fx.hitLancer;
                            despawnEffect = Fx.none;
                            status = StatusEffects.shocked;
                            lifetime = Fx.lightning.lifetime;
                        }};
                    }};
                }};
            }});
            weapons.add(new NyfalisWeapon(){{
                weaponIconString = "olupis-striker-ui-discharge";
                x = 0f;
                reload = 30;
                minShootVelocity = 3f;
                inaccuracy = shootCone = 180f;

                ejectEffect = Fx.none;
                shootSound = Sounds.shootArc;
                ignoreRotation = alwaysShooting=  true;
                bullet = new LightningBulletType(){{
                    damage = 10;
                    shoot.shots = 2;
                    shoot.firstShotDelay = 0.2f;
                    buildingDamageMultiplier = 1.1f;
                    lightningLength = 10;
                    lightningLengthRand = 6;

                    parentizeEffects = autoTarget = autoFindTarget = true;
                    top = alternate =  mirror =  aiControllable = controllable = false;
                    status = StatusEffects.none;
                    shootEffect = Fx.hitLancer;
                    lightningColor = hitColor = Color.valueOf("d1efff56"); //Pal.regen w/ custom alpha
                    lightningType = new BulletType(0.0001f, 0f){{
                        hittable = false;
                        hitEffect = Fx.hitLancer;
                        despawnEffect = Fx.none;
                        status = StatusEffects.none;
                        lifetime = Fx.lightning.lifetime;
                    }};
                }};
            }});
            parts.addAll(
                new ThrusterPartOwO(){{
                    under = mirror = true;
                    y = -4f;
                    x = 8f;
                    xScl = yScl = 0;
                    moveY = -6.1f;
                    rotation = 90;
                    moveRot = 0;
                    layerOffset = -1f;
                    progress = NyfPartParms.NyfPartProgress.speedP;
                    moves.addAll(new PartMove(NyfPartParms.NyfPartProgress.speedP, 0, 0, -1, -1,  1));
                }},
                new RegionPart("-booster"){{
                    under = mirror = true;
                    y = -4f;
                    x = 8f;
                    moveY = -3.5f;
                    layerOffset = -0.02f;
                    moveRot = 45f;
                    heatProgress = progress = NyfPartParms.NyfPartProgress.speedP;
                }}

            );

        }};

        // falcon -> infintode lightning ability/tesla ultimate - fast-ish no collision bullet that zaps targets
        falcon = new NyfalisUnitType("falcon"){{
            armor = 5f;
            hitSize = 28f;
            drag = 0.05f;
            speed = 2.7f;
            accel = 0.07f;
            health = 1200f;
            range = 170f;
            engineSize = -1;
            rotateSpeed = 45f;
            itemCapacity = 15;
            strafePenalty = 0.35f; //Aero Tree has lower strafe pen, something about they're deigned for it

            lowAltitude = flying = canCircleTarget = alwaysShootWhenMoving = faceTarget = waveHunts = true;
            constructor = UnitEntity::create;


            aiController = WaveAiHandler::new;
            defaultCommand = NyfalisUnitCommands.circleCommand;
            setEnginesMirror(
                new UnitEngine(33 / 4f, -67 / 4f, 4.5f, 300f)
            );
            abilities.add(new OrblessEnergyFieldAbillity(100f, 40f, 200f){{
                color = new Color().set(aeroLaserColours[0]).a(1);
                damageEffect = NyfalisFxs.shootChainLightning;
                layer = Layer.flyingUnitLow - 0.01f;
                status=  StatusEffects.none;
                shootSound = Sounds.shootPulsar;
                maxTargets = 5;
                statusDuration = curStroke = 0;
                maxDamageFraction = 0.75f;
                sameTypeHealMult = 0.5f;
                targetGround = hitBuildings = displayHeal =  orb = displayRange = false;
                hitUnits = targetAir = true;
                healPercent = -1f;
            }});


            weapons.add(new Weapon(){{
                x = 0f;
                reload = 30;
                minShootVelocity = 1.5f;
                inaccuracy = shootCone = 180f;

                ejectEffect = Fx.none;
                shootSound = Sounds.shootArc;
                ignoreRotation = alwaysShooting= parentizeEffects = autoTarget = autoFindTarget = true;
                top = alternate =  mirror =  aiControllable = controllable = false;

                bullet = new ArtilleryBulletType(0.5f, 25, "circle-bullet"){{
                    buildingDamageMultiplier = 1.25f;
                    width = height = 11f;
                    homingPower = 0.22f;
                    shrinkY = shrinkX = 0.75f;
                    velocityRnd = trailMult = 0;
                    frontColor = backColor = Pal.surge;
                    collides = collidesAir = collidesGround = collidesTeam = collidesTiles = keepVelocity = hittable = reflectable =false;
                    absorbable = true;
                    hitEffect = NyfalisFxs.hitSurgeZapped;
                    despawnEffect = trailEffect = Fx.none;
                    hitSound = Sounds.none;

                    fragBullets = intervalBullets = 1;
                    fragBullet = intervalBullet = new ArcLightningBulletType(){{
                        rangeOverride = range = 35f;
                        damage = 13;
                        homingPower = 0.1f;

                        status = StatusEffects.none;
                        hitEffect= shootEffect = NyfalisFxs.hitSurgeZapped;
                        lightningColor = hitColor = Pal.surge;
                    }};
                }};
            }});

        }};

        // vortex -> gun/Siege ship inspired by Thor gunships of Cnc:mental omega
        vortex = new NyfalisUnitType("vortex"){{
            armor = 10f;
            hitSize = 32;
            drag = 0.05f;
            speed = 1.8f;
            accel = 0.07f;
            health = 3000f;
            range = 110;
            engineSize = -1;
            rotateSpeed = 45f;
            itemCapacity = 15;
            strafePenalty = 0.35f; //Aero Tree has lower strafe pen, something about they're deigned for it

            flying = canCircleTarget = alwaysShootWhenMoving = faceTarget = waveHunts = true;
            constructor = UnitEntity::create;


            aiController = WaveAiHandler::new;
            defaultCommand = NyfalisUnitCommands.circleCommand;
            setEnginesMirror(
                new UnitEngine(33 / 4f, -89 / 4f, 4.5f, 300f)
            );
            parts.addAll(
                new ThrusterPartOwO(){{
                    under = mirror = true;
                    y = 0f;
                    x = 18;
                    xScl = yScl = 0;
                    moveX = 6.1f;
                    rotation = 45;
                    layerOffset = -1f;
                    progress = NyfPartParms.NyfPartProgress.speedP;
                    moves.addAll(new PartMove(NyfPartParms.NyfPartProgress.speedP, 0, 0, -1, -1,  0));
                }},
                new RegionPart(){{
                    name = "olupis-striker-booster";
                    under = mirror = true;
                    y = 0;
                    x = 18f;
                    moveX = 3.5f;
                    layerOffset = -0.02f;
                    heatProgress = progress = NyfPartParms.NyfPartProgress.speedP;
                }}

            );

            weapons.add(new Weapon(){{
                x = 0f;
                reload = 100;
                shootCone = 60f;

                ejectEffect = Fx.none;
                shootSound = Sounds.shootPulsar;
                ignoreRotation = parentizeEffects = autoTarget = autoFindTarget = true;
                top = alternate =  mirror = false;

                bullet = new ArcLightningBulletType(){{
                    rangeOverride = range = 120f;
                    damage = 40;
                    maxTargets = 5;
                    homingPower = 0.1f;
                    minTargetDistance = 30f;

                    status = StatusEffects.none;
                    hitEffect= shootEffect = NyfalisFxs.hitSurgeZappedBig;
                    chainEffect = NyfalisFxs.shootMiniMikuMikuBeam;
                    lightningColor = hitColor = Pal.surge;
                    failLightningBullet = true;
                    lightningType = new LightningBulletType(){{
                        damage = 7;
                        shootY = 0f;
                        drawSize = 55;
                        pierceCap = 3;
                        lightningLength = 13;
                        lightningLengthRand = 0;
                        pierce = true;
                        shootEffect = Fx.none;
                        lightningColor = hitColor = Pal.surge;
                        hitEffect = NyfalisFxs.hitSurgeZappedBig;
                        status = StatusEffects.electrified;

                        lightningType = new BulletType(0.0001f, 0f){{
                            pierceCap = 2;
                            statusDuration = 10f;
                            hittable = false;
                            pierce = true;
                            hitEffect = NyfalisFxs.hitSurgeZappedBig;
                            despawnEffect = Fx.none;
                            status = StatusEffects.shocked;
                            lifetime = Fx.lightning.lifetime;
                        }};
                    }};

                    fragBullets = 1;
                    fragBullet = new ArtilleryBulletType(0.55f, 25, "circle-bullet"){{
                        lifetime = 120f;
                        width = height = 11f;
                        homingPower = 0.44f;
                        homingRange = 100f;
                        shrinkY = shrinkX = 0.75f;
                        velocityRnd = trailMult = 0;
                        frontColor = backColor = Pal.surge;
                        collides = collidesAir = collidesGround = collidesTeam = collidesTiles = keepVelocity = hittable = reflectable =false;
                        absorbable = true;
                        hitEffect = NyfalisFxs.hitSurgeZappedBig;
                        despawnEffect = trailEffect = Fx.none;
                        hitSound = Sounds.none;

                        fragBullets = intervalBullets = 1;
                        bulletInterval = 40;
                        fragBullet = intervalBullet = new ArcLightningBulletType(){{
                            rangeOverride = range = 35f;
                            damage = 17;
                            homingPower = 0.1f;

                            status = StatusEffects.none;
                            hitEffect= shootEffect = NyfalisFxs.hitSurgeZappedBig;
                            lightningColor = hitColor = Pal.surge;
                        }};
                    }};
                }};

                shootStatus = StatusEffects.slow;
                shootStatusDuration = bullet.lifetime + shoot.firstShotDelay;
            }});


        }};

        // tempest -> gun ship fires particle spheres

        //endregion
        //region Air - Bats
        pteropus = new NyfalisUnitType("pteropus"){{
            hitSize = 11f;
            drag = 0.06f;
            accel = 0.08f;
            health = 250f;
            speed = 2.3f;
            engineSize = -1f;
            rotateSpeed = 25f;
            itemCapacity = 20;
            engineOffset = 7f;

            constructor = UnitEntity::create;
            aiController = DeployedAi::new;
            deployEffect = NyfalisStatusEffects.deployed;
            defaultCommand = NyfalisUnitCommands.nyfalisMoveCommand;
            lowAltitude = canDeploy = deployHasEffect = customMoveCommand = deployLands = alwaysBoosts = canBoost = canCharge = true;
            weapons.addAll(
                new NyfalisWeapon("", true, false){{
                    top = alternate = mirror = false;
                    y = 3f;
                    x = 0f;
                    reload = 20f;
                    inaccuracy = 3f;
                    shootCone = 45f;

                    shootSound = Sounds.shootMissile;
                    weaponIconUseFullString = true;
                    weaponIconString = "olupis-pteropus-ui-front";
                    bullet = new BasicBulletType(6, 7, "missile"){{
                        width = 7f;
                        height = 9f;
                        lifetime = 16f;
                        homingPower = 0.1f;
                        collidesGround = false;
                        shootEffect = Fx.none;
                        smokeEffect = Fx.shootSmallSmoke;
                        frontColor = rustyBullet;
                        hitEffect = despawnEffect = NyfalisFxs.hitHollowPointSmall;
                        backColor = rustyBulletBack;

                        trailColor = rustyBullet;
                        trailWidth = 1.5f;
                        trailLength = 3;
                    }};
                }},

                new NyfalisWeapon("", false, true){{
                    x = y = 0;
                    shootY = 5f;
                    recoil = 0.5f;
                    reload = 35f;
                    recoils = 1;
                    top = alternate = mirror = false;
                    rotate = alwaysRotate = true;
                    ejectEffect = Fx.casing1;

                    weaponIconString = "olupis-pteropus-turret-ui";

                    parts.addAll(
                        new RegionPart("olupis-pteropus-weapon"){{
                            mirror = true;
                            x = -1.75f;
                            y = 1.95f;
                            moveX = 1f;
                            moveY = -0.5f;
                            progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                            mixColor = new Color(1f, 1f, 1f, 0f);
                            mixColorTo = new Color(0f, 0f, 0f, 0.25f); //pops it out from rest of the sprite while landed bc there no outline
                        }},
                        new CellPart("olupis-pteropus-weapon-cell"){{
                            mirror = true;
                            x = -1.75f;
                            y = 1.95f;
                            moveX = 1f;
                            moveY = -0.5f;
                            progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                        }}
                    );

                    bullet = new BasicBulletType(4f, 18){{
                        spin = 30f;
                        width = 6f;
                        height = 8f;
                        lifetime = 28f;
                        splashDamage = 1f;
                        splashDamageRadius = 5f * 0.75f;
                        frontColor = rustyBullet;
                        backColor = Items.lead.color;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
                        sprite = "mine-bullet";
                        collidesAir = false;
                    }};
                }}
            );
            setEnginesMirror(
                    new UnitEngine(18 / 4f, -26 / 4f, 2.2f, 315f)
            );
        }};

        acerodon = new NyfalisUnitType("acerodon"){{
            hitSize = 12f;
            armor = 3;
            drag = 0.06f;
            accel = 0.08f;
            health = 600f;
            speed = 2.20f;
            engineSize = 4f;
            engineOffset = 8f;
            rotateSpeed = 19f;
            itemCapacity = 20;

            constructor = UnitEntity::create;
            aiController = DeployedAi::new;
            deployEffect = NyfalisStatusEffects.deployed;
            defaultCommand = NyfalisUnitCommands.nyfalisMoveCommand;
            lowAltitude  = canDeploy = deployHasEffect = customMoveCommand = deployLands = alwaysBoosts = canBoost = canCharge = inverseLanding = true;
            weapons.addAll(
                new NyfalisWeapon("", true, false){{
                    top = alternate = false;
                    reload = 25f;
                    shootY = 5;
                    x = 0;
                    shootCone = 15f;
                    ejectEffect = Fx.casing2Double;
                    shootSound = NyfalisSounds.shootCncOsprey;

                    shoot = new ShootSpread(5, 5);

                    showStatSprite = mirror = false;
                    bullet = new BasicBulletType(7f, 6.5f){{
                        lifetime = 20f;
                        sprite = "mine-bullet";
                        width = 8;
                        height = 10f;
                        hitSize = 4f;
                        hitColor = backColor = trailColor = ironBulletBack;
                        frontColor = ironBullet;
                        trailWidth = 1.8f;
                        trailLength = 3;
                        hitEffect = despawnEffect = NyfalisFxs.hitHollowPointSmall;
                        collidesAir = false;
                    }};
                }},

                new NyfalisWeapon("", false, true){{
                    x = y = 0;
                    recoils = 1;
                    recoil = 0.5f;
                    reload = 30f;
                    shootY = 0.5f;
                    rotateSpeed = 10f;
                    shootCone = 10f;
                    targetInterval = 20;
                    targetSwitchInterval = 20f;
                    rotate = alwaysRotate = true;
                    top = alternate = mirror = false;

                    shootSound = Sounds.shootMissile;
                    stepSoundPitch = 0.8f;
                    stepSoundPitchRange = 0.5f;
                    float cx = -4.2f, mx = 0.8f, cy = 2.8f, r = 1;
                    parts.addAll(
                        new RegionPart("olupis-acerodon-weapon"){{
                            mirror = true;
                            x = cx;
                            y = cy;
                            moveX = mx;
                            moveRot = r;
                            progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                            mixColor = new Color(1f, 1f, 1f, 0f);
                            mixColorTo = new Color(0f, 0f, 0f, 0.25f); //pops it out from rest of the sprite while landed bc there no outline
                        }},
                        new CellPart("olupis-acerodon-weapon-cell"){{
                            mirror = true;
                            x = cx;
                            y = cy;
                            moveX = mx;
                            moveRot = r;
                            progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                        }}
                    );

                    bullet = new BasicBulletType(7, 10, "missile-large"){{
                        width = 8f;
                        height = 10f;
                        lifetime = 20f;
                        homingPower = 0.25f;
                        collidesGround = false;
                        shootEffect = Fx.none;
                        smokeEffect = Fx.shootSmallSmoke;
                        frontColor = ironBullet;
                        hitEffect = despawnEffect = NyfalisFxs.hitHollowPointSmall;
                        backColor = ironBulletBack;

                        trailColor = rustyBullet;
                        trailWidth = 1.5f;
                        trailLength = 3;

                        fragRandomSpread = 0;
                        fragBullets = 1;
                        fragBullet = new BasicBulletType(7f, 5){{
                            width = 5f;
                            height = 12f;
                            shrinkY = 1f;
                            lifetime = 10f;
                            homingPower = 0.01f;
                            backColor = rustyBulletBack;
                            frontColor = rustyBullet;
                            despawnEffect = Fx.none;
                            collidesGround = false;
                        }};
                    }};
                }}

            );
        }};

        //nyctalus -> artillery bat, land = Cnczh nuke cannon style weapon
        nyctalus = new NyfalisUnitType("nyctalus"){{
            hitSize = 17f;
            armor = 5;
            drag = 0.06f;
            accel = 0.08f;
            health = 1300;
            speed = 2f;
            engineSize = 4f;
            engineOffset = 8f;
            rotateSpeed = 30f;
            itemCapacity = 20;
            fallSpeed = riseSpeed = 0.02f;//very slow setup

            constructor = UnitEntity::create;
            aiController = DeployedAi::new;
            deployEffect = NyfalisStatusEffects.deployed;
            defaultCommand = NyfalisUnitCommands.nyfalisMoveCommand;
            lowAltitude  = canDeploy = deployHasEffect = customMoveCommand = deployLands = alwaysBoosts = canBoost = canCharge = true;
            targetAir = false;
            abilities.add(new SationaryBoostAblity());
            weapons.addAll(
                new NyfalisWeapon("", false, true){{
                    x = y = 0;
                    shootY = 5f;
                    recoil = 0.5f;
                    reload = 150;
                    recoils = 1;
                    top = alternate = mirror = false;
                    rotate = alwaysRotate = true;
                    //strict weapon activation
                    groundedEvaluation = 0;
                    boostedEvaluation = 1;

                    weaponIconString = "olupis-aegis-core";

                    shootSound = Sounds.shootRipple;
                    parts.addAll(
                    new RegionPart("olupis-aegis-core"){{
                        mirror = false;
                        rotation = 180;
                        y = -1.95f;
                        moveY = -0.5f;
                        xScl = yScl = 1.5f;
                        progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                        mixColor = new Color(1f, 1f, 1f, 0f);
                        mixColorTo = new Color(0f, 0f, 0f, 0.25f); //pops it out from rest of the sprite while landed bc there no outline
                    }},
                    new CellPart("olupis-pteropus-weapon-cell"){{
                        mirror = true;
                        rotation = 45f;
                        x = -2.75f;
                        y = 1.95f;
                        moveX = 1f;
                        moveY = -0.5f;
                        progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                    }}
                    );

                    bullet = new BasicBulletType(2.5f, 100, "large-bomb"){{
                        spin = 10f;
                        lifetime = 100f;
                        shrinkX = 20f /60;
                        shrinkY = 30f /60;
                        width = height = 17f;
                        splashDamage = 100f;
                        splashDamageRadius = 80f;
                        trailEffect = Fx.artilleryTrail;
                        backColor = Color.purple.cpy().add(Color.lightGray);
                        frontColor = Color.blue.cpy().add(Color.gray);
                        hitEffect = despawnEffect = NyfalisFxs.hitHighYield;
                        shrinkInterp = Interp.slope;
                        collidesAir = false;
                        fragBullet = null;
                        hitSound = Sounds.explosionNavanax;
                    }};
                }}
            );
        }};

        //mirimiri -> tracter beam to hold units while | flying becomes support with a spot light on biggest target
        mirimiri = new NyfalisUnitType("mirimiri"){{
            hitSize = 17f;
            armor = 5;
            drag = 0.06f;
            accel = 0.08f;
            health = 1300;
            speed = 2f;
            engineSize = 4f;
            engineOffset = 8f;
            rotateSpeed = 30f;
            itemCapacity = 20;
            fallSpeed = riseSpeed = 0.015f;//very slow setup

            constructor = UnitEntity::create;
            aiController = DeployedAi::new;
            deployEffect = NyfalisStatusEffects.deployed;
            defaultCommand = NyfalisUnitCommands.nyfalisMoveCommand;
            lowAltitude  = canDeploy = deployHasEffect = customMoveCommand = deployLands = alwaysBoosts = canBoost = canCharge = true;
            targetAir = false;
            abilities.addAll(
                new SationaryBoostAblity(),
                new SpotLightAbility(){{
                    spotGrounded = false;
                    spotted = NyfalisStatusEffects.marked;
                }}
            );
            weapons.addAll(
            new NyfalisWeapon("", false, true){{
                x = 0f;
                y = 10f;
                reload = 0.5f;
                shootY = 1.5f;
                shootCone = 30f;
                rotateSpeed = 15f;
                autoTarget = mirror = top = false;
                rotate = controllable = parentizeEffects = continuous = alwaysContinuous = statusOnlyOnHit = true;
                shootSound = Sounds.beamParallax;
                ejectEffect = Fx.casing1;
                bullet = new TracterBeamBullet(){{
                    continuous = true;
                    shake = 0f;
                    width = 0.8f;
                    length = 100f;
                    lifetime = 20;
                    lightStroke = 10;
                    damage = 40 / 12f;
                    statusDuration = 60f;
                    absMag = absScl = 0f;
                    ownerStatusDuration = 10f;
                    statusOnOwner = true;
                    layer = Layer.groundUnit - 0.01f;
                    status = NyfalisStatusEffects.magnetized;
                    ownerStatus = StatusEffects.slow;
                    incendChance = incendSpread = 0f;
                    smokeEffect = hitEffect = shootEffect = Fx.none;
                    chargeEffect = NyfalisFxs.hitTractor;
                    colors = new Color[]{Pal.regen.cpy().a(.2f), Pal.regen.cpy().a(.5f), Pal.regen.cpy().mul(1.2f), Color.white};
                }};

                parts.addAll(
                    new RegionPart("olupis-mite"){{
                        mirror = false;
                        y = -1.95f;
                        moveY = -0.5f;
                        xScl = yScl = 1.5f;
                        progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                        mixColor = new Color(1f, 1f, 1f, 0f);
                        mixColorTo = new Color(0f, 0f, 0f, 0.25f); //pops it out from rest of the sprite while landed bc there no outline
                    }},
                    new CellPart("olupis-mite-cell"){{
                        mirror = true;
                        x = -2.75f;
                        y = 1.95f;
                        moveX = 1f;
                        moveY = -0.5f;
                        progress = NyfPartParms.NyfPartProgress.elevationP.inv();
                    }}
                );

            }}
            );
        }};

        pteropusAir = new BatHelperUnitType(pteropus);
        acerodonAir = new BatHelperUnitType(acerodon);
        nyctalusAir = new BatHelperUnitType(nyctalus);
        mirimiriAir = new BatHelperUnitType(mirimiri);
        //endregion
        //region Air - Area / from naval
        zoner = new AmmoEnabledUnitType("zoner"){{
            armor = 1f;
            speed = 3f;
            hitSize = 5.5f;
            drag = 0.05f;
            accel = 0.16f;
            health = 200f;
            fogRadius = 10f;
            itemCapacity = 5;
            engineSize = 1.6f;
            rotateSpeed = 19f;
            strafePenalty = 0.35f;
            engineOffset = 4.6f;

            constructor = AmmoEnabledUnitClass::create;
            aiController = WaveAiHandler::new;
            lowAltitude = flying = canGuardUnits = waveHunts = canRetreat=  true;
            defaultCommand = NyfalisUnitCommands.nyfalisGuardCommand;
            weapons.add(new Weapon("olupis-zoner-weapon"){{
                top = alternate = false;
                y = -1f;
                x = -1.8f;
                inaccuracy = 5f;
                reload = shootCone = 15f;
                ejectEffect = Fx.casing1;

                shootSound = NyfalisSounds.shootPVC1;
                soundPitchMax = 1.2f;
                soundPitchMin = 1f;
                showStatSprite = false;
                bullet = new BasicBulletType(3.2f, 5, "olupis-triangle-bullet"){{
                    width = 4;
                    height = 6f;
                    lifetime = 40f;
                    homingPower = 0.05f;
                    buildingDamageMultiplier = 0.5f;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.shootSmallSmoke;
                    frontColor = rustyBullet;
                    hitEffect = despawnEffect = NyfalisFxs.hitHollowPointSmall;
                    backColor = rustyBulletBack;
                }};
            }});
        }};

        //re.gioner - Yak from cnc games burst fire, needs to resupply
        regioner = new AmmoEnabledUnitType("regioner"){{
            drag = 0.05f;
            accel = 0.10f;
            health = 450f;
            fogRadius = 11f;
            engineSize = 1.6f;
            rotateSpeed = 19f;
            itemCapacity = 25;
            engineOffset = 4.6f;
            armor = 3f;
            speed = 2.5f;
            hitSize = 9f;
            circleTargetRadius = 10f;


            constructor = AmmoEnabledUnitClass::create;
            aiController = WaveAiHandler::new;
            lowAltitude = flying = waveHunts =attackRotationLock = circleTarget = altResupply = drawAmmo = canRetreat =  true;
            retreatStatus = NyfalisStatusEffects.retreating;
            omniMovement = false;

            weapons.add(new NyfalisWeapon("olupis-regioner-weapon"){{
                top  = alternate = false;
                rotate =  true;

                y = 0.9f;
                x = -3.6f;
                recoil = 0.47f;
                reload = 200f;
                shootCone = 10f;
                baseRotation = -7f;
                rotationLimit = 10f;
                shoot.shots = 10;
                shoot.shotDelay = 5f;
                shoot.firstShotDelay = 25;
                ejectEffect = Fx.none;
                shootSound = NyfalisSounds.shootPVC2;

                showStatSprite = false;
                bullet = new ShappedBulletType(){{
                    speed = 1.5f;
                    damage = 10f;
                    lifetime = 60f;
                    widthIn = heightIn = 2.5f;
                    buildingDamageMultiplier = 0.3f;

                    hitEffect = despawnEffect =NyfalisFxs.hitHollowPointSmall;
                    shootStatusDuration = 60 * 1.2f;
                    colourIn= ironBullet;
                    colourOut = ironBulletBack;
                    trailColor = rustyBulletBack;
                    trailWidth = 1f;
                    trailLength = 8;

                    pierce = true;
                    pierceCap = 2;
                    fragBullets = 3;
                    pierceFragCap = 1;

                    fragBullet = new ShappedBulletType(){{
                        drawOut =false;
                        rotIn = 0f;
                        speed = 1f;
                        shapeIn = 2;
                        damage = 4f;
                        widthIn  = 2.5f;
                        heightIn = 3.4f;
                        lifetime = 20f;
                        hitEffect = despawnEffect = Fx.none;
                        buildingDamageMultiplier = 0.3f;
                        colourIn = rustyBullet;
                    }};
                }};
            }});
        }};

        //district -> a gun ship, light gun as primary and ammo limited secondary that resupplies from mother ship/maker (resolute)
        district = new AmmoEnabledUnitType("district"){{
            drag = 0.05f;
            accel = 0.05f;
            health = 1000;
            fogRadius = 10f;
            engineSize = 2f;
            engineOffset = 7.5f;
            rotateSpeed = 6f;
            itemCapacity = 25;;
            armor = speed = 3f;
            hitSize = 12f;
            ammoZ = Layer.flyingUnitLow;
            range = 115f;

            constructor = AmmoEnabledUnitClass::create;
            aiController = WaveAiHandler::new;
            
            retreatStatus = NyfalisStatusEffects.retreating;
            faceTarget = false;
            lowAltitude = flying = canGuardUnits = waveHunts = altResupply = drawAmmo = canRetreat = true;

            weapons.addAll(
                new NyfalisWeapon("olupis-district pew"){{
                    alternate = mirror =   top =  false;
                    rotate = true;
                    x = 0f;
                    y = 2.1f;
                    shootY = 8f;
                    layerOffset = -0.01f;
                    recoil = 0.47f;
                    reload = 30f;
                    shootCone = 65f;
                    ejectEffect = Fx.none;

                    shootSound = NyfalisSounds.shootPVC2;
                    soundPitchMax = 0.8f;
                    soundPitchMin = 0.6f;
                    showStatSprite = false;
                    bullet = new BasicBulletType(3f, 12f){{
                        spin = 30f;
                        lifetime = 35f;
                        width = height = 7f;
                        splashDamage = 1f;
                        splashDamageRadius = 5f * 0.75f;
                        frontColor = ironBullet;
                        mixColorTo = mixColorFrom = backColor = ironBulletBack;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
                        sprite = "mine-bullet";
                        collidesAir = false;
                    }};
                }},
                new Weapon(""){{
                    alternate = mirror = false;
                    rotate = true;
                    x = 0;
                    y = 3;
                    shootY = 6;
                    reload = 15f;
                    recoil = 0.47f;
                    shootCone = 65f;
                    layerOffset = 0.05f;
                    ejectEffect = Fx.none;
                    var sht = new ShootHelix();
                    sht.scl = 5f;
                    shoot = sht;

                    showStatSprite = false;
                    shootSound = NyfalisSounds.shootPVC1;
                    bullet = new BasicBulletType(5f, 4f, "olupis-triangle-bullet"){{
                        width = 4;
                        height = 6f;
                        lifetime = 22f;
                        buildingDamageMultiplier = 0.3f;

                        frontColor = rustyBullet;
                        hitEffect = despawnEffect = NyfalisFxs.explosionScatteredDebris;
                        backColor = rustyBulletBack;
                    }};
                }}
            );

            parts.add(new ThrusterPartOwO(){{
                under = true;
                y = -8f;
                height = 7f;
                rotation = 90;
                layerOffset = -1f;
                xScl = yScl = moveRot = 0;
                progress = NyfPartParms.NyfPartProgress.speedP;
                moves.addAll(new PartMove(NyfPartParms.NyfPartProgress.speedP, 0, 0, -1, -1,  0));
            }});
        }};

        //division -> Fighter-bomber, Cnc Zero hour china mig style weapon
        division = new AmmoEnabledUnitType("division"){{
            drag = 0.05f;
            accel = 0.05f;
            health = 2000;
            fogRadius = 13f;
            engineSize = 2f;
            engineOffset = 7.5f;
            rotateSpeed = 2.5f;
            itemCapacity = 15;
            armor = 10f;
            speed = 5f;
            hitSize = 20f;
            ammoZ = Layer.flyingUnitLow;
            range = 125;
            trailLength = 7;

            omniMovement = circleTarget = false;
            lowAltitude = flying = canGuardUnits = waveHunts = altResupply = drawAmmo = canRetreat = true;
            constructor = AmmoEnabledUnitClass::create;
            aiController = WaveAiHandler::new;

            retreatStatus = NyfalisStatusEffects.retreating;
            defaultCommand = NyfalisUnitCommands.nyfalisRetreatCommand;

            weapons.addAll(
                new NyfalisWeapon(""){{
                    top = false;
                    rotate = true;
                    x = 7f;
                    y = shootY = 0f;
                    recoil = 0.47f;
                    reload = 40f;
                    shootCone = 50f;
                    ejectEffect = Fx.none;

                    showStatSprite = false;
                    shootSound = Sounds.shootMissilePlasmaShort;
                    bullet = new EffectivenessMissileType(1.2f, 100){{
                        backColor = alcoBulletBack;
                        frontColor = alcoBullet;
                        sprite = "missile-large";
                        splashDamage = 35f;
                        splashDamageRadius = 8f * 0.75f;

                        groundDamageSplashMultiplier = 0.5f;
                        groundDamageMultiplier = 0.5f;

                        homingPower = 0.2f;
                        homingRange = 150f;
                        drag = -0.05f;
                        lifetime = 35f;
                        trailEffect = Fx.missileTrail;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
                    }};
                }}
            );
            parts.add(new AmmoColouredPart("-pew", 0.8f));
        }};

        //endregion
        //region Ground - Snek
        venom = new SnekUnitType("venom"){{
            constructor = CrawlUnit::create;
            armor = 3;
            accel = 2.5f;
            health = 250;
            speed = 2f;
            rotateSpeed = 7.5f;
            itemCapacity = 20;
            legMoveSpace = 1.1f;
            crushDamage = 0.1f;
            hitSize = 12f;
            crawlSlowdown = 0.2f;
            crawlSlowdownFrac = 1f;
            drownTimeMultiplier = 4f;

            segments = 30;
            segmentScl = 100;
            segmentMag = 4.5f;
            segmentPhase = 60;
            sinOffset = -135;
            segmentRotSpeed = 5f;

            segmentMaxRot = 160f;
            spriteBasic(2, 0, new int[]{1, 3});
            generateOffsets(4.5f);

            omniMovement = drawBody = false;
            allowLegStep = canCharge =  true;
            drawType = 1;

            int[] ps = new int[]{1, 6, 12};
            for(int i = 0; i < 3; i++){
                int finalI = i;
                weapons.addAll(
                    new SnekWeapon("olupis-dark-pew"){{
                        y = x = 0f;
                        inaccuracy  = 3f;
                        reload = 40f;
                        shootY = 4.5f;
                        weaponSegmentParent = ps[finalI];
                        mirror = false;
                        rotate =  true;
                        if(finalI == 0)parts.addAll(
                        new RegionPart(){{
                            name = "olupis-dark-tur";
                            mirror = false;
                            y = 1;

                        }});
                        ejectEffect = Fx.casing1;
                        bullet = new BasicBulletType(2.5f, 9f){{
                            width = 7f;
                            height = 9f;
                            shrinkX = 25f /60;
                            shrinkY = 35f /60;
                            lifetime = 50f;
                        }};
                    }}
                );
            }
        }};

        serpent = new SnekUnitType("serpent"){{
            constructor = CrawlUnit::create;
            accel = 3f;
            armor = 7;
            hitSize = 12f;
            health = 550;
            speed = 1.70f;
            rotateSpeed = 15f;
            legMoveSpace = 1.2f;
            crushDamage = 0.35f;
            crawlSlowdown = 0.6f;
            crawlSlowdownFrac = 0.75f;
            drownTimeMultiplier = 4f;
            omniMovement = drawBody =  false;
            allowLegStep = canDash = canCharge = true;
            drawType = 2;

            segments = 12;
            segmentScl = 100;
            segmentMag = 4.5f;
            segmentPhase = 60;
            sinOffset = -135;
            crawlTimeMul = 10f;
            segmentRotSpeed = 5f;
            segmentMaxRot = 160f;
            spriteBasic(1, 0, new int[]{2, 3});
            generateOffsets(6.2f);

           weapons.addAll(
               new SnekWeapon(""){{
                   x = 0f;
                   y = 4.5f;
                   recoil = 0f;
                   shootY = 0f;
                   reload = 11f;
                   shootCone = 45f;
                   weaponSegmentParent = 11;
                   ejectEffect = Fx.none;
                   shootSound = Sounds.none;
                   top = mirror = false;
                   autoTarget = partialControl = rotate = alternate = controllable = strictAngle = true;
                   bullet = new ExplosionBulletType(40, 10){{
                       trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                       killShooter = collidesAir = false;
                       fragBullets = 8;
                       fragSpread = 360;
                       fragRandomSpread = 0;
                       fragBullet = new BulletType(){{
                           speed = 3;
                           damage = 0;
                           lifetime = 1f;
                           knockback = 0.5f;
                           trailEffect = despawnEffect = smokeEffect = shootEffect =  Fx.none;
                           hitEffect =  Fx.hitFlameSmall;
                           collidesAir = false;
                           hitSoundVolume = 0.5f;
                           hitSound = NyfalisSounds.sawCollision;
                       }};
                   }};
                   parts.addAll(
                       new RegionPart("olupis-serpent-blade"){{
                           layerOffset = -0.01f;
                           mirror = false;
                           under = true;
                           progress = PartProgress.warmup;
                           moves.add(new PartMove(PartProgress.reload.sustain(0,10,60), 0, 0, 360f));
                       }}
                   );
               }},
                new SnekPointDefence("cleroi-point-defense"){{
               x = y = 0f;
               reload = 18f;

               targetInterval = 9f;
               targetSwitchInterval = 12f;
               recoil = 0.5f;

               bullet = new BulletType(){{
                   shootSound = Sounds.shootLaser;
                   shootEffect = Fx.sparkShoot;
                   hitEffect = Fx.pointHit;
                   maxRange = 85f;
                   damage = 43f;
               }};
           }},
               new SnekWeapon(""){{ // dash
                    x = 0f;
                    y = -13f;
                    reload = 35f;
                    shootCone = 360f;
                    baseRotation = 180f;
                    minShootVelocity = 0.15f; //So they don't dash while on the target or something
                    weaponSegmentParent = 0;
                    ignoreRotation = dashShoot = dashExclusive = partialControl = weaponIconUseFullString = true;
                    rotate = alternate = mirror = aiControllable = false;
                    ejectEffect = Fx.casing1;
                    weaponIconString = "olupis-serpent-tail";
                    bullet = new BasicBulletType(2f, 10f){{
                        width = 8f;
                        height = 10f;
                        recoil = 10f;
                        shrinkX = 45f /60;
                        shrinkY = 65f /60;
                        lifetime = 16f;
                        smokeEffect = Fx.shootSmokeSquare;
                    }};
                }}
           );
        }};

        reaper = new SnekUnitType("reaper"){{
            constructor = CrawlUnit::create;
            accel = 1f;
            armor = 5;
            hitSize = 11f;
            health = 1600;
            segments = 3;
            speed = 2f;
            segmentScl = 7f;
            rotateSpeed = 10f;
            legMoveSpace = 1.2f;
            crushDamage = 0.2f;
            segmentMaxRot = 80f;
            crawlSlowdown = 0.4f;
            segmentRotSpeed = 5f;
            crawlSlowdownFrac = 1f;
            drownTimeMultiplier = 4f;
            omniMovement = drawBody =  false;
            allowLegStep = canDash = canCharge = true;

            weapons.addAll(
                new SnekWeapon(""){{
                    x = 0f;
                    y = 10f;
                    reload = 60 * 1.5f;
                    shootY = 1.5f;
                    shootCone = 30f;
                    rotateSpeed = 15f;
                    rotationLimit = 180;
                    weaponSegmentParent = 1;
                    autoTarget = mirror = top = false;
                    rotate = controllable = parentizeEffects = true;
                    shootSound = Sounds.beamParallax;
                    ejectEffect = Fx.casing1;
                    bullet = new RollBulletType(4.5f, 70){{
                        status = StatusEffects.slow;
                        collidesAir = ricochetHoming = false;
                        artilleryTrail = true;
                        width = 35f;
                        height = 10f;
                        lifetime = 35f;
                        knockback = 4.5f;
                        ammoMultiplier = 2;
                        homingPower = 0.3f;
                        artilleryTrailSize = 2;
                        homingRange = 80f;
                        statusDuration = 60f;
                        buildingDamageMultiplier = 0.35f;
                        shootEffect = smokeEffect = Fx.none;
                        trailEffect = Fx.artilleryTrail;
                        backColor = Pal.siliconAmmoBack;
                        frontColor = Pal.siliconAmmoFront;
                    }};
                }},
                new SnekWeapon("olupis-dark-pew"){{
                    x = y = 0f;
                    inaccuracy  = 3f;
                    reload = 30f;
                    shootY = 4.5f;
                    weaponSegmentParent = 3;
                    mirror = false;
                    rotate = true;
                    ejectEffect = Fx.casing1;
                    bullet = new ArtilleryBulletType(3f, 14){{
                        width = 7f;
                        height = 9f;
                        trailSize = 3f;
                        lifetime = 50f;
                        splashDamage = 2f;
                        splashDamageRadius = 25f * 0.75f;
                        collidesAir = false;
                        frontColor = new Color().set(rustyIron.color).lerp(Pal.bulletYellow, 0.8f);
                        backColor = new Color().set(rustyIron.color).lerp(Pal.bulletYellowBack, 0.8f);
                    }};
                }}
            );
        }};

        goliath = new SnekUnitType("goliath"){{
            constructor = CrawlUnit::create;
            accel = 1f;
            armor = 5;
            hitSize = 11f;
            health = 1600;
            segments = 3;
            speed = 2f;
            segmentScl = 7f;
            rotateSpeed = 10f;
            legMoveSpace = 1.2f;
            crushDamage = 0.2f;
            segmentMaxRot = 80f;
            crawlSlowdown = 0.4f;
            segmentRotSpeed = 5f;
            crawlSlowdownFrac = 1f;
            drownTimeMultiplier = 4f;
            omniMovement = drawBody =  false;
            allowLegStep = canDash = canCharge = true;

            weapons.addAll(
                new SnekWeapon(""){{
                    x = 0f;
                    y = 10f;
                    reload = 0.5f;
                    shootY = 1.5f;
                    shootCone = 30f;
                    rotateSpeed = 15f;
                    rotationLimit = 180;
                    weaponSegmentParent = 1;
                    autoTarget = mirror = top = false;
                    rotate = controllable = parentizeEffects = continuous = alwaysContinuous = statusOnlyOnHit = true;
                    shootSound = Sounds.beamParallax;
                    ejectEffect = Fx.casing1;
                    bullet = new TracterBeamBullet(){{
                        continuous = true;
                        shake = 0f;
                        width = 0.8f;
                        length = 100f;
                        lifetime = 20;
                        lightStroke = 10;
                        damage = 40 / 12f;
                        statusDuration = 60f;
                        absMag = absScl = 0f;
                        ownerStatusDuration = 10f;
                        statusOnOwner = true;
                        layer = Layer.groundUnit - 0.01f;
                        status = NyfalisStatusEffects.magnetized;
                        ownerStatus = StatusEffects.slow;
                        incendChance = incendSpread = 0f;
                        smokeEffect = hitEffect = shootEffect = Fx.none;
                        chargeEffect  = NyfalisFxs.hitTractor;
                        colors = new Color[]{Pal.regen.cpy().a(.2f), Pal.regen.cpy().a(.5f), Pal.regen.cpy().mul(1.2f), Color.white};
                    }};
                }});
        }};

        //-> has payload, takes a bunch of t3 and bellow and  lets them shoot out of them, Cnc:ra2 battle fortress / Cnc:Zh battle bus

        //endregion
        //region Ground - Roach
        supella = new NyfalisUnitType("supella"){{
            constructor = MechUnit::create;

            canBoost = lowAltitude = true;
            boostMultiplier = 0.8f;

            armor = 2;
            hitSize = 8;
            health = 300;
            speed = 0.65f;
            engineSize = -1;
            rotateSpeed = 1.72f;
            weapons.addAll(
                new NyfalisWeapon("", false, true){{
                    x = -0.1f;
                    y = 1.25f;
                    shootX = 6f;
                    reload = 20f;
                    shootCone = 15f;
                    top = false;
                    ejectEffect = Fx.casing1;
                    parts.addAll(
                      new RegionPart("olupis-supella-sidewep"){{
                          var p = PartProgress.warmup.add(-1f);
                          heatProgress = progress =p.mul(-1);
                          moveRot = 35f;
                          moveY = -0.8f;
                          moveX = -0.17f;

                      }}
                    );
                    bullet = new BasicBulletType(3f, 7){{
                        width = 5f;
                        height = 7f;
                        lifetime = 45;
                    }};
                }},
                new NyfalisWeapon("olupis-supella-cockgun", true, true){{
                    x = 0f;
                    y = 0.4f;
                    reload = 30;
                    shake = 0.4f;
                    recoil = 1f;
                    shootY = 0.5f;
                    shootCone = 55f;
                    rotateSpeed = 10f;
                    rotationLimit = 90f;
                    autoTarget = rotate = partialControl = true;
                    mirror = controllable = top = false;
                    soundPitchMin = 0.5f;
                    soundPitchMax = 0.8f;
                    shootSound = NyfalisSounds.shootPVC2;
                    bullet = new BasicBulletType(4f, 7){{
                        width = 5f;
                        height = 6f;
                        lifetime = 36f;
                        frontColor = rustyBullet;
                        backColor = rustyBulletBack;
                        hitEffect = despawnEffect = NyfalisFxs.hitHollowPointSmall;
                    }};
                }}
            );
            setEnginesMirror(new UnitEngine(22 / 4f, -5 / 4f, 2f, 5f));

        }};

        germanica = new NyfalisUnitType("germanica"){{
            constructor = MechUnit::create;

            canBoost = lowAltitude = alwaysShootWhenMoving = canAttack = true;
            armor = 5;
            hitSize = 12f;
            range = Vars.tilesize * 8;
            health = 700;
            speed = 0.7f;
            engineSize = -1;
            itemOffsetY = 3f;
            rotateSpeed = 2.25f;
            boostMultiplier = 0.8f;
            immunities.add(StatusEffects.burning);
            abilities.add(new MicroWaveFieldAbility(6.5f, 50f, Vars.tilesize * 9.5f, Vars.tilesize * 4.5f){{
                ideRangeDisplay = false;
                damageEffect = Fx.none;
                sectors = 3;
                maxTargetBoost = 7;
                maxTargetsGround = 14;

            }});
            //Gave up trying to make it, so it has a boost damage weapon since the ability's range display won't go away while boosting if triggered, so made it just it sole weapon that changes on boost or not
            setEnginesMirror(new UnitEngine(24 / 4f, -5 / 4f, 2f, 5f));
            parts.add(
                new RegionPart("-arm"){{
                    y = 5f;
                    x = -4f;
                    moveX = 1f;
                    moveRot = -20;
                    progress = NyfPartParms.NyfPartProgress.elevationP;
                    mirror = true;
                    layerOffset = -0.001f;
                    outlineLayerOffset = 0f;
                }},
                new CellPart("-arm-cell"){{
                    y = 4.075f;
                    x = -3.1f;
                    moveX = 1f;
                    moveRot = -20;
                    outlineLayerOffset = 0f;
                    progress = NyfPartParms.NyfPartProgress.elevationP;
                    outline = false;
                    mirror =  true;
                }}
            );
        }};

        //luridiblatta -> long range shell launcher, only fires at target +/- 5 tiles of max range (has min range)
        luridiblatta = new NyfalisUnitType("luridiblatta"){{
            constructor = MechUnit::create;

            canBoost = lowAltitude = true;
            boostMultiplier = 0.81f;

            armor = 3;
            hitSize = Vars.tilesize * 1.7f;
            health = 1250;
            speed = 0.60f;
            engineSize = -1;
            rotateSpeed = 1.72f;
            weapons.add(
                new NyfalisWeapon("olupis-tri-mount-barrel-m", false, true){{
                x = 0f;
                y = 3f;
                reload = 45;
                shake = 0.2f;
                recoil = 1f;
                shootY = 0.5f;
                shootCone = 55f;
                rotateSpeed = 10f;
                rotationLimit = 90f;
                rotate = true;
                mirror = top = false;
                shootSound = Sounds.shootSpectre;
                bullet = new BasicBulletType(2, 20){{
                    width = 5f;
                    height = 7f;
                    lifetime = 120;
                    trailWidth = 1.5f;
                    trailLength = 4;
                    weaveScale = 2;
                    weaveMag = 3f;
                    shrinkX = -0.70f;
                    shrinkY = -0.57f;
                    frontColor = ironBullet;
                    backColor = ironBulletBack;
                    trailColor = rustyBulletBack;
                    hitEffect = despawnEffect = Fx.flakExplosion;
                    hitSound = Sounds.explosion;

                    fragBullets = 1;
                    fragBullet = new DistanceScalingBulletType(30, 22){{
                        trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                        maxDst = 30 * Vars.tilesize;
                        killShooter = collidesAir = false;
                        fragBullets = 8;
                        fragSpread = 360;
                        fragRandomSpread = 0;
                        minDst = Vars.tilesize * 10;
                        minDmgMul = 0.3f;
                    }};
                }};
            }});
            setEnginesMirror(new UnitEngine(29 / 4f, 1 / 4f, 2f, 5f));

        }};

        vaga = new NyfalisUnitType("vaga"){{
            constructor = MechUnit::create;

            canBoost = lowAltitude = true;
            boostMultiplier = 0.8f;

            armor = 7;
            hitSize = Vars.tilesize * 2.7f;
            health = 2400;
            speed = 0.7f;
            engineSize = -1;
            rotateSpeed = 1.72f;
            range = 25 * Vars.tilesize;
            weapons.add(
                new NyfalisWeapon("", false, true){{
                    x = 10f;
                    y = 1.25f;
                    reload = 65f;
                    shootCone = 30f;
                    shootSound = Sounds.shootMissile;
                    top = false;
                    ejectEffect = Fx.casing1;
                    parts.addAll(
                        new RegionPart("olupis-aegis-core-barrel"){{
                            var p = PartProgress.warmup.add(-1f);
                            heatProgress = progress =p.mul(-1);
                            moveRot = 35f;
                            moveY = -0.8f;
                            moveX = -0.17f;
                            under  = true;

                        }}
                    );
                    bullet = new EffectivenessMissileType(9.5f, 80f) {{
                        //no bonus to air or ground
                        groundDamageMultiplier = 0.8f;
                        width = 13f;
                        shrinkX = 0;
                        lifetime = (25 * Vars.tilesize) / speed;
                        height = 18.5f;
                        trailChance = 0;
                        trailLength = 2;
                        knockback = 1.5f;
                        splashDamage = 3f;
                        homingPower = 0.6f;
                        homingRange = 100f;
                        homingDelay = 10;
                        splashDamageRadius = 20f * 0.75f;
                        frontColor = trailColor = Pal.graphiteAmmoFront;
                        backColor = Pal.graphiteAmmoBack;
                        collidesAir = collidesGround = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = new MultiEffect(NyfalisFxs.hitHollowPoint, Fx.pointHit);
                    }};
                }}
            );
            setEnginesMirror(new UnitEngine(22 / 4f, -5 / 4f, 2f, 5f));
            abilities.add(new MicroWaveFieldAbility(13f, 80f, Vars.tilesize * 9.5f, Vars.tilesize * 16f){{
                ideRangeDisplay = false;
                damageEffect = Fx.none;
                sectors = 4;
                maxTargetBoost = 30;
                maxTargetsGround = 20;
            }});
        }};

        //TODO: Mining unit that changes the ammo/bullet prop (prob an ability)
        parcoblatta = new NyfalisUnitType("parcoblatta"){{
            constructor = MechUnit::create;

            canBoost = lowAltitude = true;
            boostMultiplier = 0.81f;

            armor = 19;
            hitSize =UnitTypes.reign.hitSize;
            health = 2000;
            speed = 0.60f;
            engineSize = -1;
            rotateSpeed = 2f;
            weapons.add(
            new NyfalisWeapon("olupis-obliterator", false, true){{
                x = 20;
                y = 3f;
                reload = 200;
                shake = 0.76f;
                recoil = 10f;
                shootY = 10f;
                shootCone = 55f;
                rotateSpeed = 10f;
                rotationLimit = 45f;
                rotate = true;
                mirror = top = false;
                shootSound = NyfalisSounds.shootCncBattleMaster;
                bullet = new MineAffectedBulletType(2.5f, 250){{
                    width = 15f;
                    height = 20f;
                    lifetime = 150;
                    trailWidth = 6f;
                    trailLength = 10;
                    weaveScale = 1;
                    weaveMag = 1.5f;
                    shrinkX = -0.70f;
                    shrinkY = -0.57f;
                    frontColor = ironBullet;
                    backColor = ironBulletBack;
                    mixColorTo = mixColorFrom = Color.darkGray;
                    trailColor = rustyBulletBack;
                    hitEffect = despawnEffect = Fx.flakExplosionBig;

                    fragBullets = 1;
                    fragBullet = new DistanceScalingBulletType(100, 70){{
                        trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                        maxDst = 30 * Vars.tilesize;
                        killShooter = collidesAir = false;
                        fragBullets = 8;
                        fragSpread = 360;
                        fragRandomSpread = 0;
                        minDst = Vars.tilesize * 10;
                        minDmgMul = 0.3f;
                    }};
                }};
            }});
            setEnginesMirror(new UnitEngine(29 / 4f, 1 / 4f, 2f, 5f));
            abilities.addAll(
                new DeathStatusAbility(NyfalisStatusEffects.comradery)
            );
        }};
        //endregion
        //region Naval - Carrier
        sentry = new NyfalisUnitType("sentry"){{
            armor = 2f;
            hitSize = 12f;
            health = 350;
            speed = 0.75f;
            itemCapacity = 0;
            treadPullOffset = 3;
            rotateSpeed = 3.5f;
            researchCostMultiplier = 0f;

            rotateMoveFirst = canDeploy = true;
            constructor = UnitWaterMove::create;

            abilities.addAll(
                new CarrierResupplyAbility(1),
                new UnitRallySpawnAblity(zoner, 60f * 15f, 0, 4.5f){{
                    moveRot = 1800;
                }}
            );
        }};

        warden = new NyfalisUnitType("warden"){{
            armor = 6f;
            hitSize = 12f;
            health = 850;
            speed = 0.75f;
            itemCapacity = 0;
            treadPullOffset = 3;
            rotateSpeed = 3.5f;
            researchCostMultiplier = 0f;

            rotateMoveFirst = canDeploy = true;
            constructor = UnitWaterMove::create;
            abilities.addAll(
                new CarrierResupplyAbility(2),
                new UnitRallySpawnAblity(regioner, 60f * 15f, 0, 10, 0, 8f),
                new PointDefenceIndicatorAbility(160, 1)
            );
            weapons.add(new LaserPointerPointDefenceWeapon("olupis-warden-point-defense"){{
                x = 0;
                y = -10f;
                aoe = 0;
                reload = 6f;
                soundVol = 0.7f;
                targetInterval = targetSwitchInterval = 14f;
                mirror = false;
                shootSound = NyfalisSounds.shootCncAvengerPdl;
                bullet = new BulletType(){{

                    shootEffect = Fx.shootSmokeSquare;
                    hitEffect = Fx.pointHit;
                    maxRange = 160f;
                    damage = 45f;
                }};
            }});
        }};

        //Carrier a long range PDL w/ warm up & laser pointer
        guardian = new DuckyTubeTankUnitType("guardian"){{
            speed = 0.6f;
            navalSpeed = 1f;

            armor = 6f;
            hitSize = 21;
            health = 1150;
            itemCapacity = 0;
            legCount = 0;
            rotateSpeed = 3.5f;
            crushDamage = 1f;
            legMoveSpace = 0;
            researchCostMultiplier = 0f;

            immunities.add(StatusEffects.wet);
            rotateMoveFirst = canDeploy = naval = hovering =  true;
            canDrown = ammoDepletesOverTime = omniMovement = killOnAmmoDepletion = legPhysicsLayer = allowLegStep = false;
            constructor = TonkNavalUnitClass::create; //Legged so it doesnt slow down in deep water
            pathCost = NyfalisPathfind.costPreferTrackedNaval;

            weapons.add(new LaserPointerPointDefenceWeapon("olupis-guardian-point-defense"){{
                x = 0;
                y = 10f;
                reload = 6f;
                minWarmup = 0.9f;
                soundVol = 0.7f;
                soundPitchMin = 0.65f;
                soundPitchMax = 0.8f;
                targetInterval = targetSwitchInterval = 12f;
                mirror = false;
                shootSound = NyfalisSounds.shootCncAvengerPdl;

                hitAoeEffect = NyfalisFxs.hitMiniPointDefence;
                bullet = new BulletType(){{
                    shootEffect = Fx.shootSmokeSquare;
                    aoeBeamEffect = NyfalisFxs.hitAoePointDefence;
                    hitEffect = Fx.pointHit;
                    maxRange = 320f;
                    damage = 60f;
                }};
            }});
            abilities.addAll(
                new CarrierResupplyAbility(3),
                new PointDefenceIndicatorAbility(320, 2),
                new UnitRallySpawnAblity(district, 60f * 30f, 6f, 0,0, 15f, true){{
                    displayBars = false;
                    display = false;
                    xScl = yScl = 0.1f;
                    growX = growY = -0.9f;
                }},
                new UnitRallySpawnAblity(district, 60f * 30f, -6f, 0, 0, 15f, true){{
                    xScl = yScl = 0.1f;
                    growX = growY = -0.9f;
                }}
            );
            parts.addAll(
                    new FloaterTreadsPart("-treads"){{
                        mirror = under =true;
                        drawRegion  = false;
                        x = 8.75f;
                        y = 0;
                        moveX = 3;
                        treadPullOffset = 4;
                        layerOffset = -0.001f;
                        treadRects = new Rect[]{new Rect(-14f, -65, 28, 130)};
                        progress = NyfPartParms.NyfPartProgress.floatingP.inv();
                        alphaProgress =  NyfPartParms.NyfPartProgress.floatingP.inv();
                    }}
            );
        }};

        // resolute -> Constuct ablity, mini figther-bombers that need to reload at the ship, dis >= ability rebuild = sucide bombers
        //              -> Desigantor pointer, pointer that debuffs an unit, all units that hit it gains a atack speed buff
        domination = new DuckyTubeTankUnitType("domination"){{
            constructor = LeggedPayloadUnitClass::create; //Legged so it doesnt slow down in deep water
            pathCost = NyfalisPathfind.costPreferTrackedNaval;
            speed = 0.6f;
            navalSpeed = 1f;
            crushDamage = 2f;

            armor = 6f;
            hitSize = 20f;
            health = 2400;
            itemCapacity = 0;
            legCount = 0;
            rotateSpeed = 3.5f;
            researchCostMultiplier = 0f;
            legMoveSpace = 0;

            immunities.add(StatusEffects.wet);
            rotateMoveFirst = canDeploy = naval = hovering = true;
            canDrown = ammoDepletesOverTime = killOnAmmoDepletion = omniMovement= legPhysicsLayer = allowLegStep = pickupBlocks = false;

            payloadUnitsUpdate = true;

            weapons.addAll(
                new Weapon("olupis-dark-pew"){{
                    x = 0;
                    y = 5f;
                    reload = 60* 5;
                    rotate = true;
                    top = alternate = mirror = false;
                    ejectEffect = Fx.casing1;
                    parts.addAll(
                    );
                    bullet = new RailBulletType(){{
                        length = 160f;
                        continuous = true;
                        shake = 0f;
                        length = 100f;
                        lifetime = 20;
                        damage = 60f;
                        statusDuration = 60f * 5f;
                        layer = Layer.groundUnit - 0.01f;
                        hitEffect = endEffect = Fx.hitBulletColor;

                        status = NyfalisStatusEffects.marked;
                        endEffect = new Effect(14f, e -> {
                            color(e.color);
                            Drawf.tri(e.x, e.y, e.fout() * 1.5f, 5f, e.rotation);
                        });

                        shootEffect = new Effect(10, e -> {
                            color(e.color);
                            float w = 1.2f + 7 * e.fout();

                            Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                            color(e.color);

                            for(int i : Mathf.signs){
                                Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
                            }

                            Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                        });

                        lineEffect = new Effect(20f, e -> {
                            if(!(e.data instanceof Vec2 v)) return;

                            color(e.color);
                            stroke(e.fout() * 0.9f + 0.6f);

                            Fx.rand.setSeed(e.id);
                            for(int i = 0; i < 7; i++){
                                Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                                Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                            }

                            e.scaled(14f, b -> {
                                stroke(b.fout() * 1.5f);
                                color(e.color);
                                Lines.line(e.x, e.y, v.x, v.y);
                            });
                        });
                        incendChance = incendSpread = 0f;
                        smokeEffect = shootEffect = Fx.none;
                        chargeEffect = hitEffect = NyfalisFxs.hitTractor;
                        hitColor = Pal.accent;
                    }};
            }},
                new LaserPointerPointDefenceWeapon("olupis-Lexington-point-defense"){{
                    x = 5;
                    y = -5f;
                    reload = 6f;
                    minWarmup = 0.9f;
                    soundVol = 0.7f;
                    soundPitchMin = 0.65f;
                    soundPitchMax = 0.8f;
                    targetInterval = targetSwitchInterval = 12f;
                    shootSound = NyfalisSounds.shootCncAvengerPdl;

                    hitAoeEffect = NyfalisFxs.hitMiniPointDefence;
                    bullet = new BulletType(){{
                        shootEffect = Fx.shootSmokeSquare;
                        aoeBeamEffect = NyfalisFxs.hitAoePointDefence;
                        hitEffect = Fx.pointHit;
                        maxRange = 350f;
                        damage = 60f;
                    }};
                }})
            ;
            abilities.addAll(
                new CarrierResupplyAbility(4),
            new PointDefenceIndicatorAbility(350, 2),
                new ShieldArcAbility(){{
                    radius = 36f;
                    angle = 82f;
                    regen = 0.6f;
                    cooldown = 60f * 8f;
                    max = 2000f;
                    y = -20f;
                    width = 6f;
                    whenShooting = false;
                }},
                new StatusFieldAbility(NyfalisStatusEffects.overTuned, 60f * 15f, 60f * 10f, 70f)
                //new UnitRallySpawnAblity(district, 60f * 15f, 0, 6.5f)
            );
            parts.addAll(
                new FloaterTreadsPart(){{
                    name = "olupis-guardian-treads";
                    mirror = under =true;
                    drawRegion  = false;
                    x = 3.75f;
                    y = 0;
                    treadPullOffset = 4;
                    layerOffset = -0.001f;
                    treadRects = new Rect[]{new Rect(-14f, -65, 28, 130)};
                    progress = NyfPartParms.NyfPartProgress.floatingP.inv();
                    alphaProgress =  NyfPartParms.NyfPartProgress.floatingP.inv();
                }}
            );
        }};

        //->Flag ship, boost, Hex sheild when landed, prop/unit booster when flying



        //endregion
        //region Naval - Guard
        //Minigun turret mounted on the front, 10mm autocannon mounted on the back
        bay = new NyfalisUnitType("bay"){{
            armor = 4f;
            accel = 0.4f;
            drag = 0.14f;
            hitSize = 11f;
            health = 250;
            range = 100f;
            trailScl = 1.3f;
            speed = 1.15f;
            trailLength = 20;
            waveTrailX = rotateSpeed = 5f;

            faceTarget = customMoveCommand = idleFaceTargets = true;
            constructor = UnitWaterMove::create;
            weapons.add(new Weapon("olupis-missiles-mount-teamed"){{
                x = 0f;
                y = -8;
                reload = 26f;
                rotate= true;
                mirror = false;
                ejectEffect = Fx.casing1;
                shootSound = Sounds.shootStell;
                bullet = new ArtilleryBulletType(2.5f, 14){{
                    width = 7f;
                    height = 9f;
                    trailSize = 3f;
                    lifetime = 60f;
                    splashDamage = 2f;
                    splashDamageRadius = 25f * 0.75f;
                    collidesAir = false;
                    frontColor = new Color().set(rustyIron.color).lerp(Pal.bulletYellow, 0.8f);
                    backColor = new Color().set(rustyIron.color).lerp(Pal.bulletYellowBack, 0.8f);
                    hitSound = Sounds.none;
                }};
            }});
            weapons.add(new NyfalisWeapon(""){{
                x = 0f;
                y = 6.5f;
                reload = 7f;
                inaccuracy = 4f;
                shootCone = 30f;
                rotateSpeed = 10f;
                rotationLimit = 45f;
                targetInterval = 10f;
                targetSwitchInterval = 20f;
                soundPitchMax = 1.2f;
                soundPitchMin = 0.9f;

                autoTarget = rotate = partialControl = weaponIconUseFullString = true;
                mirror = controllable = false;
                weaponIconString = "olupis-bay-ui-front";
                shootSound = NyfalisSounds.shootCncQuad;
                bullet = new BasicBulletType(2.5f, 10){{
                    width = 3f;
                    height = 20f;
                    lifetime = 60f;
                    collidesAir = false;
                    frontColor = rustyBullet;
                    backColor = rustyBulletBack;
                    shootEffect = Fx.shootScepterSecondary;
                    hitEffect = despawnEffect = NyfalisFxs.hitHollowPointSmall;
                }};
            }});
        }};

        blitz = new NyfalisUnitType("blitz"){{
            armor = 5f;
            accel = 0.6f;
            drag = 0.14f;
            hitSize = 14f;
            range = 200f;
            trailScl = 1.9f;
            health = 550f;
            speed = 1.15f;
            trailLength = 22;
            waveTrailY = -4f;
            waveTrailX = 5.5f;
            rotateSpeed = 3.8f;

            idleFaceTargets = customMoveCommand = true;
            constructor = UnitWaterMove::create;
            weapons.add(
                new Weapon("olupis-twin-mount"){{
                    x = 0;
                    y = -9.5f;
                    recoils = 2;
                    recoil = 0.5f;
                    reload = 18f;
                    mirror = false;
                    rotate= top = true;
                    shoot = new ShootAlternate(3.6f);
                    for(int i = 0; i < 2; i ++){ int f = i;
                        parts.add(new RegionPart("-barrel-" + (i == 0 ? "r" : "l")){{
                            x = (f == 0) ? 1.8f : -1.8f;
                            y = 3f;
                            shootY = 6f;
                            recoilIndex = f;
                            outlineLayerOffset = 0f;
                            outlineColor = contentOutline;
                            outline = drawRegion = under = true;
                            progress = PartProgress.recoil;
                            moves.add(new PartMove(PartProgress.recoil, 0, -3f, 0));
                    }}); }
                    shootSound = Sounds.shootStell;

                    bullet = new ArtilleryBulletType(3f, 16){{
                        width = 7f;
                        height = 9f;
                        trailSize = 3f;
                        lifetime = 65f;
                        splashDamage = 7f;
                        splashDamageRadius = 2.5f *8f;
                        collidesAir = false;
                        frontColor = ironBullet;
                        backColor = ironBulletBack;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
                        hitSound = Sounds.none;
                    }};
                }},
                new Weapon("olupis-twin-auto-cannon"){{
                    x = 0f;
                    y = 10.5f;
                    recoil = 1f;
                    recoils = 2;
                    reload = 4f;
                    shootY = 5.3f;
                    inaccuracy = 8f;
                    rotateSpeed = 3f;
                    soundPitchMax = 1.4f;
                    soundPitchMin = 1f;
                    rotate = true;
                    mirror = false;
                    shoot = new ShootAlternate(4.1f);
                    for(int i = 0; i < 2; i ++){ int f = i;
                        parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")){{
                            x = (f == 0) ? 1.6f : -1.6f;
                            y = 2f;
                            recoilIndex = f;
                            outlineLayerOffset = 0f;
                            outlineColor = contentOutline;
                            outline = drawRegion = under = true;
                            progress = PartProgress.recoil;
                            moves.add(new PartMove(PartProgress.recoil, 0, -2f, 0));
                    }}); }
                    shootSound = NyfalisSounds.shootCncQuad;
                    bullet = new BasicBulletType(2.5f, 8, "olupis-diamond-bullet"){{
                        width = 3f;
                        height = 17f;
                        lifetime = 78f;
                        collidesAir = false;
                        frontColor = ironBullet;
                        backColor = ironBulletBack;

                        hitEffect = despawnEffect = NyfalisFxs.hitHollowPointSmall;
                        shootEffect = Fx.shootScepterSecondary;
                        shootSound = Sounds.none;
                    }};
                }}
            );
        }};

        crusader = new NyfalisUnitType("crusader"){{
            armor = 7f;
            hitSize = 20f;
            health = 1300;
            trailScl = 1.5f;
            trailLength = 22;
            waveTrailX = 7f;
            waveTrailY = -9f;
            itemCapacity = 60;
            constructor = bay.constructor;
            weapons.add(new Weapon("olupis-tri-mount"){{
                x = 0;
                y = -3f;
                recoils = 3;
                reload = 50;
                recoil = 0.8f;
                rotateSpeed = 10f;
                mirror = false;
                rotate= top = true;
                shoot = new ShootAlternate(2.7f);
                shootSound = NyfalisSounds.shootCncBattleMaster;
                for(int i = 0; i < 3; i ++){ int f = i;
                    parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : i == 1 ? "m" : "r")){{
                        x = (f == 0) ? 3.8f : (f == 1) ? 0f : -3.8f;
                        y = 5.25f;
                        shootY = 6f;
                        recoilIndex = f;
                        outlineLayerOffset = 0f;
                        outlineColor = contentOutline;
                        outline = drawRegion = under = true;
                        progress = PartProgress.recoil;
                        moves.add(new PartMove(PartProgress.recoil, 0, -3f, 0));
                    }});
                }
                bullet = new RollBulletType(3f, 23){{
                    width = 35f;
                    height = 9f;
                    lifetime = 65f;
                    splashDamage = 2f;
                    homingPower = 0.15f;
                    splashDamageRadius = 25f * 0.75f;
                    collidesAir = false;
                    frontColor = ironBullet;
                    backColor = ironBulletBack;
                    shootEffect = Fx.shootBig2;
                    hitEffect = despawnEffect = Fx.hitBulletSmall;
                }};
            }});

            for(int i = 0; i < 2; i++){
                int finalI = i;
                weapons.add(new Weapon("olupis-dark-tur"){{
                    x = 0;
                    y = (finalI == 1) ? 14 : -15f;
                    reload = 25f;
                    mirror = false;
                    rotate = true;
                    layerOffset = 0.01f;
                    rotateSpeed = 5f;
                    ejectEffect = Fx.casing1;
                    shootSound = NyfalisSounds.shootPVC1;
                    bullet = new CappedIntervalBullet(5f, 40, "olupis-diamond-bullet"){{
                        pierce = pierceBuilding = true;
                        collidesAir = false;

                        width = 7f;
                        height = 10f;
                        lifetime = 40f;
                        knockback = 0.3f;
                        ammoMultiplier = 1.5f;
                        pierceCap = 2;

                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        hitColor = backColor = trailColor = alcoBulletBack;
                        frontColor = alcoBullet;
                        intervalBullet =  this.copy();
                        intervalBullet.lifetime -= 2;
                        intervalSpread = 5f;
                        intervalRandomSpread = 10f;
                        bulletInterval = IntervalCap = 1;
                    }};
                    parts.addAll(
                        new RegionPart(){{
                            name = "olupis-dark-pew";
                            mirror = false;
                            under = true;
                            progress = PartProgress.recoil;
                            y = 1;
                            moves.add(new PartMove(PartProgress.recoil, 0, -2f, 0));

                        }}
                    );
                }});
            }
        }};

        //torret - broadside cram/doom cannons (artillery )
        torrent = new NyfalisUnitType("torrent"){{
            armor = 7f;
            hitSize = 20f;
            health = 2000;
            trailScl = 1.5f;
            trailLength = 22;
            waveTrailX = 7f;
            waveTrailY = -9f;
            itemCapacity = 60;
            constructor = bay.constructor;
            weapons.addAll(
                new Weapon(""){{
                    y = x = 0;
                    reload = 60f * 10;
                    mirror = false;
                    top = false;
                    rotate = true;

                    layerOffset = 0.01f;
                    ejectEffect = Fx.casing1;
                    shootSound = Sounds.shootSpectre;
                    soundPitchMin = 0.3f;
                    soundPitchMax = 0.8f;
                    rotateSpeed = 2f;

                    bullet = new ArtilleryBulletType(2f, 300f){{
                        shootEffect = Fx.shootSmokeTitan;
                        smokeEffect = Fx.shootSmokeTitan;
                        hitEffect = despawnEffect = deathExplosionEffect = Fx.massiveExplosion;
                        width = 16f;
                        height = 20f;
                        shrinkX = 25f /60;
                        shrinkY = 35f /60;
                        lifetime = 110f;
                        splashDamage = 220f;
                        splashDamageRadius = 60f;
                        suppressionRange = tilesize * 10;
                        suppressionDuration = Time.toSeconds * 15f;
                        hitSound = Sounds.explosionMissile;
                        status = StatusEffects.slow;
                        statusDuration = Time.toSeconds;
                    }};

                    parts.addAll(
                        new RegionPart(){{
                            name = "olupis-slash";
                            mirror = false;
                            moves.addAll(new PartMove(p -> 1, 0, 0, 0.3f, 0.3f,  0));
                        }},
                        new RegionPart(){{
                            name = "olupis-shear";
                            mirror = false;
                            top = true;
                            progress = PartProgress.smoothReload;
                            moveY = 18;
                            y = -1;
                            moves.addAll(new PartMove(p -> 1, 0, 0, 0.3f, 0.3f,  0));
                        }}
                    );
                }},
                new Weapon("olupis-twin-mount"){{
                    x = 0;
                    y = 0f;
                    layerOffset = 0.1f;
                    recoils = 2;
                    recoil = 0.5f;
                    reload = 20f;
                    mirror = false;
                    rotate= top = true;
                    shoot = new ShootAlternate(3.6f);
                    for(int i = 0; i < 2; i ++){ int f = i;
                        parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")){{
                            x = (f == 0) ? 1.8f : -1.8f;
                            y = 3f;
                            shootY = 6f;
                            recoilIndex = f;
                            outlineLayerOffset = 0f;
                            outlineColor = contentOutline;
                            outline = drawRegion = under = true;
                            progress = PartProgress.recoil;
                            moves.add(new PartMove(PartProgress.recoil, 0, -3f, 0));
                        }}); }

                    bullet = new ArtilleryBulletType(3f, 30){{
                        width = 9f;
                        height = 12f;
                        trailSize = 3f;
                        lifetime = 65f;
                        splashDamage = 15f;
                        splashDamageRadius = 3f *8f;
                        collidesAir = false;
                        frontColor = ironBullet;
                        backColor = ironBulletBack;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
                        hitSound = Sounds.none;
                    }};
                }}
            );
        }};

        NyfalisWeapon vanguardWep = new NyfalisWeapon(){{
            x = 7;
            y = 2.5f;
            reload = 600f;
            shootCone = 45;
            alternate = mirror = false ;
            shoot.firstShotDelay = 45f;

            ejectEffect = Fx.casing1;
            bullet = new BulletType(0f, 0f){{
                    shootEffect = Fx.shootBig;
                    shootSound = Sounds.shootMissileLarge;
                    smokeEffect = Fx.shootSmokeSquareSparse;
                    hitColor = Pal.redLight;
                    ammoMultiplier = 1f;

                    spawnUnit = new MissileUnitType("vanguard-missile"){{
                        controller = u -> new DumbMissileAI();
                        lifetime = 160f;
                        fragBullets = 1;
                        trailLength = 5;
                        trailWidth = 5f;
                        maxRange = 240;
                        speed =  8.5f;

                        armor  = 1;
                        health = 250f;
                        lowAltitude = despawnHit = true;
                        keepVelocity = collidesAir = targetUnderBlocks = false;

                        outlineColor = contentOutline;
                        engineLayer = Layer.effect;
                        engineSize = 3.1f;
                        engineOffset = 9f;
                        rotateSpeed = 0.75f;
                        missileAccelTime = 125;
                        shootStatus = StatusEffects.unmoving;
                        shootStatusDuration = 60f;
                        rangeOverride = Vars.tilesize *  86f;
                        shootEffect = despawnEffect = hitEffect = Fx.flakExplosion;
                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            deathExplosionEffect = shootEffect = despawnEffect = hitEffect = Fx.flakExplosion;
                            shootOnDeath = true;
                            shake = 10f;
                            bullet = new DistanceScalingBulletType(300, 23){{
                                //used by the dumbMissleAi
                                homingPower = 0.5f;
                                homingRange = 860;

                                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                                maxDst = 80 * Vars.tilesize;
                                minDst = 55 * Vars.tilesize;
                                killShooter = collidesAir = false;
                                fragBullets = 0;
                                minDmgMul = 0.3f;
                                hitSound = NyfalisSounds.shootCncV4MissileLand;
                                hitSoundPitchRange = 1f;
                            }};
                        }});
                    }};
            }};
        }};

        vanguard = new NyfalisUnitType("vanguard"){{
            armor = 10f;
            hitSize = 20f;
            health = 1000;
            trailScl = 1.5f;
            trailLength = 22;
            waveTrailX = 7f;
            waveTrailY = -9f;
            itemCapacity = 60;
            constructor = bay.constructor;
            targetAir = false;
            idleFaceTargets = faceTarget = rotateMoveFirst= true;
            range = Vars.tilesize *  80f;

            for(int i = 0; i < 4; i++){
                var front = vanguardWep.copy();
                if(i == 1 || i == 3) front.x *= -1;
                if(i > 2) front.y *= -1;
                front.shoot = vanguardWep.shoot.copy();
                front.shoot.firstShotDelay += (i * 7.5f);


                weapons.addAll(front);
            }

        }};

        //endregion
        //region Limited - Hive
        flea = new AmmoEnabledUnitType("flea"){{
            hitSize = 8f;
            range = 4f;
            armor = 10f;
            speed = 1.2f;
            drag = 0.04f;
            accel = 0.08f;
            health = 100;
            fogRadius = 0;
            lightRadius = 15f;
            itemCapacity = 0;
            crashDamageMultiplier = 0f;

            mechSideSway = 0.25f;
            maxRange = 15f * Vars.tilesize;

            targetGround = targetAir = drawAmmo = true;
            playerControllable  = logicControllable = useUnitCap  = false;
            constructor = LegsUnit::create;
            controller = u -> new SearchAndDestroyFlyingAi();
            weapons.add(new NyfalisWeapon(){{
                y = x = 0f;
                reload = 35f;
                inaccuracy = 12f;
                shootCone = 15f;
                targetInterval = 30f;
                targetSwitchInterval = 60f;

                shoot.shots = 4;
                shoot.shotDelay = 2.5f;
                shootSound = NyfalisSounds.shootCncOsprey;;
                bullet = new FlakBulletType(7f, 13){{
                    sprite = "mine-bullet";
                    width = 6f;
                    height = 8f;
                    lifetime = 25f;
                    splashDamage = 20f * 1.5f;
                    splashDamageRadius = 4f * 8f;
                    buildingDamageMultiplier = 0f;
                    collidesGround = true;
                    shootEffect = Fx.shootSmall;
                    hitEffect = Fx.flakExplosion;
                }};
            }});
        }};

        mite = new AmmoEnabledUnitType("mite"){{
            /*Rework: only no ammo deplete over time if with X of parent, once out, high  deplete amount*/
            hitSize = 8f;
            range = 45f;
            armor = 10f;
            speed = 2.7f;
            drag = 0.04f;
            accel = 0.08f;
            health = 100;
            fogRadius = 0;
            lightRadius = 15f;
            itemCapacity = 0;
            crashDamageMultiplier = 0f;


            flying = targetGround = targetAir = drawAmmo = altResupply = true;
            playerControllable  = logicControllable = useUnitCap = false;
            constructor = AmmoEnabledUnitClass::create;
            targetFlags = new BlockFlag[]{BlockFlag.factory, null};
            controller = u -> new SearchAndDestroyFlyingAi(true){{
                targetFlames = true;
            }};
            weapons.addAll(
                new NyfalisWeapon(){{
                    y = x = 0f;
                    reload = 10f;
                    shootCone = 15f;
                    targetInterval = 30f;
                    ammoPerShot = 3;
                    targetSwitchInterval = 60f;

                    shootSound = Sounds.shootDuo;
                    /*Gave up using LiquidBulletType*/
                    bullet = new NoBoilLiquidBulletType(NyfalisItemsLiquid.steam){{
                        pierce = true;

                        speed = 2f;
                        lifetime = 20f;
                        damage = 25f;
                        pierceCap = 1;
                        ammoMultiplier = 1.5f;
                        statusDuration = 1.5f *60f;
                        buildingDamageMultiplier = 0f;
                        status = StatusEffects.corroded;
                        shootEffect = Fx.shootLiquid;
                        despawnEffect = hitEffect = Fx.steam;
                    }};
                }},
                new NyfalisWeapon(){{
                    reload = 24f;
                    x = shootY = 0f;
                    shootCone = 180f;
                    soundPitchMax = 6f;
                    soundPitchMin = 0.2f;
                    ejectEffect  = Fx.none;
                    shootSound = Sounds.none;
                    mirror = controllable = aiControllable = false;
                    shootOnDeath = fireOnTimeOut = true;
                    bullet  = new FirePuddleBulletType(20,10, true){{
                            splashDelay = 3;
                            splashAmount = 1;
                            applySound = Sounds.none;
                            frontColor = backColor =  steam.color;
                            particleColor = steam.color;
                            particleEffect = Fx.hitLiquid;
                            shootEffect = hitEffect = despawnEffect = Fx.none;
                            extinguishIn = 250f;
                        }};
                }}
            );
        }};

        //fires 2 roll bullets in quick succession
        lice = new AmmoEnabledUnitType("lice"){{
            range = 5;
            hitSize = 8f;
            armor = 10f;
            speed = 2.7f;
            drag = 0.04f;
            accel = 0.08f;
            health = 100;
            fogRadius = 0;
            lightRadius = 15f;
            itemCapacity = 0;
            crashDamageMultiplier = 0f;

            flying = targetGround = targetAir = drawAmmo = true;
            playerControllable  = logicControllable = useUnitCap =  false;
            constructor = AmmoEnabledUnitClass::create;
            controller = u -> new SearchAndDestroyFlyingAi(true);
            weapons.add(new NyfalisWeapon(){{
                y = x = 0f;
                reload = 10f;
                shootCone = 15f;
                targetInterval = 30f;
                targetSwitchInterval = 60f;

                fireOverSolids = false;
                shootSound = NyfalisSounds.shootCncBattleMaster;

                bullet = new BarrelBulletType(3.5f, 38, "bullet"){{
                    status = StatusEffects.slow;
                    collidesAir = false;
                    width = 40f;
                    height = 11f;
                    lifetime = 50f;
                    pierceCap = 4;
                    knockback = 5f;
                    ammoMultiplier = 2;
                    homingPower = 0.2f;
                    homingRange = 100f;
                    statusDuration = 60f * 2f;
                    buildingDamageMultiplier = 0.15f;
                    shootEffect = smokeEffect = Fx.none;
                    frontColor = new Color().set(iron.color).lerp(Pal.bulletYellowBack, 0.1f);
                    backColor = new Color().set(iron.color).lerp(Pal.bulletYellow, 0.2f);
                }};
            }});
        }};

        //Explodes and gives glitched effect
        tick = new AmmoEnabledUnitType("tick"){{
            range = 5;
            hitSize = 8f;
            armor = 10f;
            speed = 5.5f;
            drag = 0.04f;
            accel = 0.08f;
            health = 100;
            fogRadius = 0;
            lightRadius = 15f;
            itemCapacity = 0;
            crashDamageMultiplier = 0f;

            flying = targetGround = targetAir = drawAmmo  = true;
            playerControllable  = logicControllable = useUnitCap  = false;
            constructor = AmmoEnabledUnitClass::create;
            controller = u -> new SearchAndDestroyFlyingAi(true);
            weapons.add(new NyfalisWeapon(){{
                reload = 24f;
                x = shootY = 0f;
                shootCone = 180f;
                soundPitchMax = 6f;
                soundPitchMin = 0.2f;
                ejectEffect = Fx.none;
                shootSound = NyfalisSounds.shootCncBattleMaster;
                mirror = false;
                shootOnDeath = fireOnTimeOut = true;
                bullet = new BulletType(){{

                    speed = 0f;
                    splashDamage = 90f;
                    statusDuration = 20f;
                    splashDamageRadius = 55f;
                    buildingDamageMultiplier = 0.1f;
                    hitSound = Sounds.explosion;
                    status = NyfalisStatusEffects.glitch;

                    hitEffect = NyfalisFxs.hitObliterator;
                    instantDisappear = collidesAir = killShooter = true;
                    collides = hittable = collidesTiles = false;
                }};
            }});
        }};

        //endregion
        //region Limited - Support Construct
        spirit = new AmmoLifeTimeUnitType("spirit"){{
            range = 30f;
            health = 150f;
            speed = 1.3f;
            mineTier = 1;
            hitSize = 8.5f;
            itemOffsetY = 5f;
            fogRadius =0;
            mineSpeed = 3.5f;
            itemCapacity = 20;
            passiveAmmoDepletion = 0.1f;
            ammoDepletionAmount = 0.15f;

            constructor = UnitEntity::create;
            timedOutSound = Sounds.explosionDull;
            controller = u -> new NyfalisMiningAi();
            flying = miningDepletesAmmo = depleteOnInteractionUsesPassive = constructHideDefault = drawAmmo = inoperableDepletes = true;
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = ammoDepletesInRange = false;
        }};

        phantom = new AmmoLifeTimeUnitType("phantom"){{
            armor = 2;
            hitSize = 7f;
            speed = 3.25f;
            fogRadius = 0f;
            health = 300;
            engineSize = -1f;

            weapons.add(new LimitedRepairBeamWeapon(""){{
                y = 6f;
                shootCone = 20f;
                shootX = shootY = x = 0;
                fractionRepairSpeed = 0.03f;
                beamWidth = 0.3f;
                repairSpeed = 0.5f;

                targetBuildings = autoTarget = healingIgnoresMines = true;
                controllable = top = mirror = false;
                bullet = new BulletType(){{
                    aimDst = 0f;
                    maxRange = 120f;
                    healPercent = 3f;
                    hitUnder = true;
                }};
            }});
            parts.addAll(
                new ThrusterPartOwO(){{
                    under = mirror = true;
                    y = 2f;
                    x = 2.3f;
                    xScl = yScl = 0;
                    moveY = -6.1f;
                    moveX = 3.5f;
                    rotation = 90;
                    layerOffset = -1f;
                    progress = NyfPartParms.NyfPartProgress.speedP;
                    moves.addAll(new PartMove(NyfPartParms.NyfPartProgress.speedP, 0, 0, -1, -1,  1));
                }},
                new RegionPart(){{
                    name = "olupis-striker-booster";
                    under = mirror = true;
                    x = 2.2f;
                    y = -3f;
                    rotation = 45;
                    moveX = 3.5f;
                    layerOffset = -0.02f;
                    heatProgress = progress = NyfPartParms.NyfPartProgress.speedP;
                }}

            );

            constructor = UnitEntity::create;
            aiController = UnitHealerAi::new;
            defaultCommand = NyfalisUnitCommands.nyfalisMendCommand;
            setEnginesMirror(new UnitEngine(8 / 4f, -21 / 4f, 2.1f, 245));
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = false;
            flying = miningDepletesAmmo = depleteOnInteractionUsesPassive = canMend = canHealUnits =  targetAir = targetGround = singleTarget  = drawAmmo  = true;
        }};

        banshee = new LeggedWaterUnit("banshee"){{
            hitSize = 18f;
            health = 150;
            legCount = 6;
            mineTier = 3;
            fogRadius = 0f;
            legLength = 10f;
            mineSpeed = 4f;
            stepShake = 0.3f;
            navalSpeed = 1.1f;
            itemCapacity = 35;
            legForwardScl = 0.8f;
            legBaseOffset = -2f;
            legMoveSpace = 1.4f;

            groundLayer = Layer.legUnit;
            constructor = LegsUnit::create;
            timedOutSound = Sounds.explosionDull;
            controller = u -> new NyfalisMiningAi();
            hovering = miningDepletesAmmo = depleteOnInteractionUsesPassive = showLegsOnLiquid = lockLegsOnLiquid= drawAmmo = customShadow = inoperableDepletes = true;
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = canDrown = false;
        }};

        revenant = new AmmoLifeTimeUnitType("revenant"){{
            armor = 2;
            speed = 3.25f;
            fogRadius = 0f;
            buildSpeed = 0.8f;

            weapons.add(new BuildWeapon("build-weapon"){{
                    rotate = true;
                    rotateSpeed = 7f;
                    x = 14/4f;
                    y = 15/4f;
                    layerOffset = -0.001f;
                    shootY = 3f;
                }


                Interval timer = new Interval(4);
                float lastProgress;

                @Override
                public void update(Unit unit, WeaponMount mount){
                    Queue<Teams.BlockPlan> blocks = unit.team.data().plans;

                    if(unit.buildPlan() != null && lastProgress == unit.buildPlan().progress && timer.get(3, 40) && !blocks.isEmpty()){
                        blocks.addLast(blocks.removeFirst());
                        lastProgress = unit.buildPlan().progress;
                    }

/*                    if(unit.activelyBuilding() && useAmmo ){ //TODO: AMMO SHOULDN'T USE WHEN TRYING TO BUILD W/O ITEMS FOR IT
                        //Since it isn't really shooting, ammo isn't used properly handled
                        unit.ammo--;
                        if(unit.ammo < 0) unit.ammo = 0;
                    }
                    super.update(unit, mount);*/
                }
            });

            constructor = UnitEntity::create;
            aiController = BuilderAI::new;
            defaultCommand = UnitCommand.rebuildCommand;
            setEnginesMirror(new UnitEngine(8 / 4f, -21 / 4f, 2.1f, 245));
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = ammoDepletesInRange = false;
            flying = miningDepletesAmmo = depleteOnInteractionUsesPassive =  targetAir = targetGround = singleTarget  = drawAmmo  = true;
        }};

        //endregion
        //region Limited - Sumoned
        //todo despawn is wack
        embryo = new AmmoLifeTimeUnitType("embryo"){{
            /*(trans) Egg if chan-version is made >;3c */
            speed = 3f;
            fogRadius = 0f;
            itemCapacity = 0;
            ammoDepletionAmount = ammoCapacity;
            ammoDepletionOffset = 60*10;

            flying = alwaysShootWhenMoving = drawAmmo = lookForParent = true;
            playerControllable = useUnitCap = false;
            constructor = UnitEntity::create;
            controller = u -> new AgressiveFlyingAi(true, true);


            weapons.add(new LimitedRepairBeamWeapon(""){{
                shootCone = 20f;
                y = 3;
                shootX = shootY = x = 0;
                fractionRepairSpeed = 0.02f;
                beamWidth = repairSpeed = 0.18f;

                autoTarget = healingIgnoresMines = targetUnits= true;
                controllable = top = targetBuildings = mirror =  false;
                bullet = new BulletType(){{
                    aimDst = 0f;
                    maxRange = 100f;
                    healPercent = 1f;
                    hitUnder = true;
                }};
            }});
        }};

        //Not quite core unit, but it's a "core spawn"
        shade = new AmmoLifeTimeUnitType("shade"){{
            health = 100;
            speed = 1.3f;
            mineTier = 1;
            hitSize = 8.5f;
            itemOffsetY = 5f;
            fogRadius = 0;
            mineSpeed = 3.5f;
            itemCapacity = 10;
            passiveAmmoDepletion = 0.07f;
            ammoDepletionAmount = 0.15f;

            aiController = RepairAI::new    ;
            constructor = UnitEntity::create;
            timedOutSound = Sounds.explosionDull;
            flying = miningDepletesAmmo = depleteOnInteractionUsesPassive = constructHideDefault = drawAmmo = cantMove =  customMineAi = inoperableDepletes  = true;
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = ammoDepletesInRange = false;

            weapons.add(new NyfalisWeapon(){{
                top = mirror = alternate = false;
                x = y = 0f;
                recoil = 2f;
                shootY = 4f;
                reload = 24f;


                ejectEffect = Fx.none;
                shootSound = Sounds.shootLaser;

                bullet = new HealOnlyBulletType(5.2f, 13, "olupis-diamond-bullet"){{
                    width = 9f;
                    lifetime = 15;
                    healPercent = 3f;
                    lightOpacity = 0.6f;
                    homingPower = 0.3f;
                    homingRange = 20f;
                    rangeOverride = 7.4f * Vars.tilesize;
                    collidesTeam = true;
                    frontColor = Color.white;
                    hitUnder = true;
                    hittable = reflectable = false;
                    backColor = lightColor = Pal.heal;
                    smokeEffect = hitEffect = despawnEffect =  Fx.hitLaser;
                }};
            }});
        }};
        //endregion
        //region Scout
        scarab = new NyfalisUnitType("scarab"){{
            hitSize = 9f;
            health = 50;
            speed = 3.6f;
            range = 32;
            fogRadius = 35;
            engineSize = 3f;
            lightRadius = 30;
            itemCapacity = 0;
            engineOffset = 7f;
            rotateSpeed = 30f;
            drag = accel = 0.08f;
            secondaryLightRadius = 250;
            payloadCapacity = (float) ((2.6 * 2.6) * 64);

            constructor = OnePayloadUnitClass::create;
            aiController = PayloadCarrierAi::new;
            payloadUpdateSE = NyfalisStatusEffects.alternate;
            //used this instead of disarm so weapons "works" allowing to be aimed and used by payload
            payloadDisarmSE = NyfalisStatusEffects.unloaded;
            useUnitCap = faceTarget = false;
            flying = canDeploy = canCharge = emitSecondaryLight = payloadDisarms =  payloadUpdateRequiresStatus = true;
            parts.addAll(
                    new RegionPart("-radar"){{
                        mirror = false;
                        under = false;
                        layerOffset = 2;

                        heatProgress = p -> Mathf.cos(Time.time / 10) / 2 + 0.5f * (2 * NyfPartParms.nyfparams.paylCount);
                        heatColor = Color.valueOf("3ed09a");
                        y = -3.5f;
                        moves.add(new PartMove(p ->( Mathf.cos(Time.time / 30) / 2 + 0.5f) * (1- NyfPartParms.nyfparams.paylCount), 0, 0, 360f));
                    }}
            );
            abilities.add(new ActivePayloadAblility());
            weapons.add(new NyfalisWeapon(""){{
                top = mirror = false;
                shake = 0f;
                shootY = -9.1f;
                y = x = recoil = 0f;
                reload = shootCone = 360f;
                shootOnDeath = statsBlocksOnly = dashShoot = true;
                ejectEffect = Fx.none;
                shootSound = Sounds.none;
                bullet = new BasicBulletType(){{
                    sprite = "large-bomb";
                    width = height = 60/4f;
                    maxRange = 30f;
                    ignoreRotation = true;
                    shootEffect = smokeEffect = Fx.none;
                    backColor = Color.valueOf("3ed09a");
                    frontColor = Color.white;
                    hitSound = Sounds.rockBreak;
                    hitSoundPitch = 0.5f;
                    hitSoundPitchRange = 0.01f;
                    shootCone = 180f;
                    ejectEffect = Fx.none;

                    collidesAir = false;

                    lifetime = 45f;
                    despawnEffect = Fx.none;
                    hitEffect = Fx.explosion;
                    keepVelocity = false;
                    spin = 2f;
                    shrinkX = shrinkY = 0.7f;
                    speed = 0f;
                    collides = false;
                    splashDamage = 0;
                    splashDamageRadius = 0;
                    fragBullets = 1;
                }};
            }});
        }};

        /*For testing for now, dont ship, TODO consider this as for bigger scout bois or just keep it carriers*/
//        weevil = new NyfalisUnitType("weevil"){{
//            hitSize = 9f;
//            health = 50;
//            speed = 3.6f;
//            engineSize = 3f;
//            lightRadius = 30;
//            itemCapacity = 0;
//            engineOffset = 7f;
//            rotateSpeed = 30f;
//            drag = accel = 0.08f;
//            secondaryLightRadius = 250;
//            payloadCapacity = Mathf.pow(3f, 2f) * 64;
//            constructor = PayloadUnit::create;
//            range = 32;
//            flying = canDeploy = canCharge = emitSecondaryLight = true;
//            useUnitCap = false;
//            fogRadius = 35;
//            parts.addAll(
//                    new RegionPart("-radar"){{
//                        mirror = false;
//                        under = false;
//                        layerOffset = 2;
//
//                        heatProgress = p -> Mathf.cos(Time.time / 10) / 2 + 0.5f;
//                        heatColor = Color.valueOf("3ed09a");
//                        y = -3.5f;
//                        moves.add(new PartMove(p ->( Mathf.cos(Time.time) / 2 + 0.5f), 0, 0, 360f));
//                    }}
//            );
//            payloadUnitsUpdate = true;
//        }};
        //endregion
        //region Nyfalis Core Units
        BulletType  gnatIntervals =  new HealOnlyBulletType(4,-5, "olupis-diamond-bullet", false) {{
            lifetime = 60;
            trailLength = 8;
            trailWidth = 1.5f;
            healAmount = 20;
            bulletInterval = 10;
            homingPower = 0.1f;
            homingRange =  300f;
            splashDamageRadius =  tilesize * 2 ;
            followAimSpeed = 10;

            collidesTeam = true;
            keepVelocity = false;
            hitEffect = despawnEffect = Fx.heal;
            backColor = frontColor = trailColor = lightColor = Pal.heal.a(0.4f);
        }};

        BulletType  gnatHealNade =  new HealOnlyBulletType(0,0) {{
            spin = 3.5f;
            drag = 0.9f;
            lifetime = 10*60;
            shrinkX = 25f/60f;
            shrinkY = 35f/60f;
            intervalBullets = 1;
            intervalRandomSpread = 360;
            height = width = 20;
            bulletInterval = 10f;
            trailInterval =  NyfalisFxs.intervalGnatBullCharge.lifetime /2f;
            layer = Layer.bullet + 0.001f;

            collidesTeam = keepVelocity = false;
            hitEffect = despawnEffect = Fx.heal;
            trailEffect = NyfalisFxs.intervalGnatBullCharge;
            backColor = frontColor = trailColor = lightColor = Pal.heal;

            intervalBullet = gnatIntervals;
        }};

        BulletType pediciaPew = new ShappedBulletType(){{
            speed = 3f;
            damage = 13f;
            lifetime = 35f;
            widthIn = 2.5f;
            heightIn = 6f;
            heightOut = 7.5f;
            shapeIn = shapeOut = 2;
            rotIn = rotOut = 0;
            followAimSpeed = 10f;
            buildingDamageMultiplier = 0.1f;

            hitEffect = despawnEffect =NyfalisFxs.hitHollowPointSmall;
            colourIn= rustyBullet;
            colourOut = rustyBulletBack;
            trailColor = NyfalisItemsLiquid.rustyIron.color;
            trailWidth = 1.25f;
            trailLength = 5;
        }};

        gnat = new NyfalisUnitType("gnat"){{
            armor = 1f;
            hitSize = 10f;
            speed = 2.4f;
            drag = 0.11f;
            health = 420;
            mineTier = 1;
            legCount = 0;
            fogRadius = 0f;
            /*Corner Engines only*/
            engineSize = -1;
            mineSpeed = 8.5f;
            buildSpeed = 0.5f;
            itemCapacity = 70;
            rotateSpeed = 4.5f;
            //range = mineRange;
            legMoveSpace = 1.2f; //Limits world tiles movement
            boostMultiplier = 0.75f;
            buildBeamOffset = 4.2f;
            shadowElevation = 0.1f;
            researchCostMultiplier = 0f;
            groundLayer = Layer.legUnit - 1f;

            legPhysicsLayer = false;
            canBoost = allowLegStep = hovering = alwaysBoostOnSolid= customMineAi = weaponsStartEmpty = true;
            constructor = LegsUnit::create;
            pathCost = costLeggedNaval;

            defaultCommand = NyfalisUnitCommands.nyfalisMineCommand;
            mineItems = Seq.with(rustyIron, lead, scrap);
            setEnginesMirror(
                new UnitEngine(24.5f / 4f, 18 / 4f, 2f, 45f), //front
                new UnitEngine(22 / 4f, -20 / 4f, 2.2f, 315f)
            );
            parts.add(new HoverPart(){{
                mirror = false;
                radius = 13f;
                phase = 320f;
                layerOffset = -0.001f;
                color = Color.valueOf("5C9F62");
            }});

            weapons.add(
                new NyfalisWeapon() {{
                    reload = 60*10;
                    x = y = shootX = shootY = 0;
                    shootStatus = StatusEffects.unmoving;
                    shootStatusDuration = shoot.firstShotDelay = Fx.heal.lifetime-1;
                    /*3 bullets deep, just so everything shoot at the same time, as being separate weapons causes early/late shooting*/
                    bullet = new BulletType() {{
                        collides = hittable = collidesTiles = mirror = false;
                        instantDisappear = collidesAir = true;
                        hitSound = Sounds.explosion;
                        hitEffect = NyfalisFxs.shootUnitDischarge;

                        splashDamage = 65f;
                        rangeOverride = 30f;
                        splashDamageRadius = 55f;
                        buildingDamageMultiplier = speed = 0f;
                        intervalBullet = gnatHealNade.copy();
                    }};
                }}
            );
        }};

        pedicia = new NyfalisUnitType("pedicia"){{
            armor = 2f;
            hitSize = 10f;
            speed = 2.5f;
            drag = 0.11f;
            health = 560;
            mineTier = 1;
            legCount = 0;
            fogRadius = 0f;
            engineOffset = 6f;
            engineSize = 2.85f;
            mineSpeed = 9f;
            buildSpeed = 0.6f;
            itemCapacity = 75;
            rotateSpeed = 5.5f;
            //range = mineRange;
            legMoveSpace = 1.2f; //Limits world tiles movement
            boostMultiplier = 0.75f;
            buildBeamOffset = 4.2f;
            shadowElevation = 0.1f;
            researchCostMultiplier = 0f;
            groundLayer = Layer.legUnit - 1f;

            legPhysicsLayer = false;
            canBoost = allowLegStep = hovering = alwaysBoostOnSolid= customMineAi =  weaponsStartEmpty = true;
            constructor = LegsUnit::create;
            pathCost = costLeggedNaval;

            defaultCommand = NyfalisUnitCommands.nyfalisMineCommand;
            mineItems = Seq.with(rustyIron, lead, scrap);
            setEnginesMirror(
                new UnitEngine(26.5f / 4f, 24 / 4f, 2f, 45f), //front
                new UnitEngine(24 / 4f, -30 / 4f, 2.2f, 315f)
            );
            parts.add(new HoverPart(){{
                mirror = false;
                radius = 13f;
                phase = 320f;
                layerOffset = -0.001f;
                color = Color.valueOf("5C9F62");
            }});

            weapons.add(
                new NyfalisWeapon() {{
                    shootY = 2;
                    reload = 60*10;
                    x = y = shootX = 0;
                    shootStatus = StatusEffects.unmoving;
                    shootStatusDuration = shoot.firstShotDelay = Fx.heal.lifetime-1;
                    /*3 bullets deep, just so everything shoot at the same time, as being separate weapons causes early/late shooting*/
                    bullet = new BulletType() {{
                        collides = hittable = collidesTiles = mirror = false;
                        instantDisappear = collidesAir = true;
                        hitSound = Sounds.explosion;
                        hitEffect = NyfalisFxs.shootUnitDischarge;

                        splashDamage = 65f;
                        rangeOverride = 30f;
                        splashDamageRadius = 55f;
                        buildingDamageMultiplier = speed = 0f;
                        intervalBullet = gnatHealNade.copy();
                    }};
                }},
                new NyfalisWeapon(){{
                    top = mirror = false;
                    reload = 50f;
                    shootCone = 30f;
                    shootSound = Sounds.shoot;
                    y =  2.5f;
                    x = shootX = inaccuracy = 0f;
                    bullet = pediciaPew.copy();
                }}
            );
        }};

        phorid = new NyfalisUnitType("phorid"){{
            armor = 3f;
            hitSize = 12.5f;
            speed = 2.6f;
            drag = 0.11f;
            health = 720;
            mineTier = 2;
            legCount = 0;
            fogRadius = 0f;
            /*Corner Engines only*/
            engineOffset = 10f;
            engineSize = 2.85f;
            rotateSpeed = 6.5f;
            mineSpeed = 9.5f;
            buildSpeed = 0.7f;
            itemCapacity = 80;
            legMoveSpace = 1.3f; //Limits world tiles movement
            shadowElevation = 0.1f;
            buildBeamOffset = 4.2f;
            boostMultiplier = 0.75f;
            researchCostMultiplier = 0f;
            groundLayer = Layer.legUnit - 1f;
            maxRange = range = 15f * Vars.tilesize;

            legPhysicsLayer = false;
            canBoost = allowLegStep = hovering = alwaysBoostOnSolid = customMineAi = weaponsStartEmpty =  true;
            constructor = LegsUnit::create;
            mineItems = Seq.with(rustyIron, lead, scrap);
            pathCost = costLeggedNaval;
            setEnginesMirror(
                    new UnitEngine(26.5f / 4f, 30 / 4f, 2f, 45f), //front
                    new UnitEngine(24 / 4f, -40 / 4f, 2.2f, 315f)
            );
            parts.add(new HoverPart(){{
                mirror = false;

                radius = 13f;
                phase = 320f;
                layerOffset = -0.001f;
                color = Color.valueOf("5C9F62");
            }});

            weapons.addAll(
                new NyfalisWeapon() {{
                    reload = 60*10;
                    x = y = shootX = shootY = 0;
                    shootStatus = StatusEffects.unmoving;
                    shootStatusDuration = shoot.firstShotDelay = Fx.heal.lifetime-1;
                    bullet = new SpawnHelperBulletType(){{
                        hasParent = true;
                        shootEffect = Fx.shootBig;
                        spawnUnit = embryo;
                        //rangeOverride = mineRange;
                        intervalBullet =  new BulletType() {{
                            instantDisappear = collidesAir = true;
                            collidesTiles = collides = hittable = mirror = false;
                            hitSound = Sounds.explosion;
                            hitEffect = NyfalisFxs.shootUnitDischarge;

                            rangeOverride = 30f;
                            splashDamage = 70f;
                            splashDamageRadius = 55f;
                            speed = buildingDamageMultiplier = 0f;
                            intervalBullet = gnatHealNade.copy();
                        }};
                    }};
                }},
                new NyfalisWeapon(){{
                    top = false;
                    mirror = true;
                    reload = 35f;
                    shootCone = 30f;
                    shootSound = Sounds.shoot;
                    x = 2.5f;
                    y = 7;
                    shootX = inaccuracy = 0f;
                    bullet = pediciaPew.copy();
                }}
            );
        }};

        diptera = new NyfalisUnitType("diptera"){{
            armor = 4f;
            hitSize = 12.5f;
            speed = 2.6f;
            drag = 0.11f;
            health = 720;
            mineTier = 3;
            legCount = 0;
            fogRadius = 0f;
            /*Corner Engines only*/
            engineSize = -1;
            rotateSpeed = 6.5f;
            mineSpeed = 11f;
            buildSpeed = 1f;
            itemCapacity = 80;
            buildBeamOffset = 4.2f;
            researchCostMultiplier = 0f;
            maxRange = range = 15f * Vars.tilesize;

            flying = customMineAi = weaponsStartEmpty =  true;
            constructor = UnitEntity::create;
            mineItems = Seq.with(rustyIron, lead, scrap);
            pathCost = costLeggedNaval;
            setEnginesMirror(
                    new UnitEngine(24.5f / 4f, 18 / 4f, 2f, 45f), //front
                    new UnitEngine(22 / 4f, -20 / 4f, 2.2f, 315f)
            );

            weapons.addAll(
                    new NyfalisWeapon() {{
                        reload = 60*10;
                        x = y = shootX = shootY = 0;
                        shootStatus = StatusEffects.unmoving;
                        shootStatusDuration = shoot.firstShotDelay = Fx.heal.lifetime-1;
                        bullet = new SpawnHelperBulletType(){{
                            hasParent = true;
                            shootEffect = Fx.shootBig;
                            unitRange = 100f;
                            spawnUnit = embryo;
                            //rangeOverride = mineRange;
                            intervalDelay = 0.5f;
                            intervalBullet =  new BulletType() {{
                                instantDisappear = collidesAir = true;
                                collidesTiles = collides = hittable = mirror = false;
                                hitSound = Sounds.explosion;
                                hitEffect = NyfalisFxs.shootUnitDischarge;

                                rangeOverride = 30f;
                                splashDamage = 70f;
                                splashDamageRadius = 55f;
                                speed = buildingDamageMultiplier = 0f;
                                intervalBullet = gnatHealNade.copy();
                                BulletType dipetaInterval = gnatIntervals.copy();
                                dipetaInterval.splashDamageRadius = Vars.tilesize * 2;
                                dipetaInterval.hitEffect = dipetaInterval.despawnEffect = NyfalisFxs.hitTaurusHeal;

                                intervalBullet.intervalBullet = dipetaInterval;
                            }};
                        }};
                    }}
            );
            float[] xp = {7.5f, 0, -7.5f}, yp = {-4, 0, -4};
            for(int i = 0; i < 3; i++){
                int ic = i;
                weapons.addAll(
                    new NyfalisWeapon(){{
                        top = mirror = false;
                        reload = 50f;
                        shootCone = 30f;
                        shootSound = Sounds.shoot;
                        x =  xp[ic];
                        y =  yp[ic];
                        shootX = inaccuracy = 0f;
                        bullet = pediciaPew.copy();
                    }}
                );
            }
        }};

        //diptera -> Flying unit that drops healing cluster bomb,  explode (w/ dmg) > split into 2 healing circles

        //added a death weapon
        //endregion
        //region Misc/Extra/Internal


        //Since blocks' frog of war is always on them, this is a work around as FogControl has no ways to add custom stuff
        ladarHelper = new NyfalisUnitType("ladar-hepler"){{
            isEnemy = logicControllable = playerControllable = bounded = hittable = targetable = physics = useUnitCap = hoverable = drawBody = drawCell = drawMinimap = false;
            hidden = flying = true;
            fogRadius = 10;
            engineSize = -1f;
            constructor = UnitEntity::create;
            controller =  u -> new AIController();
        }

            @Override
            public void draw(Unit unit){
                    if(Core.settings.getBool("nyfalis-debug")){
                        Tmp.v1.set(player.mouseX(), player.mouseY());
                        if(unit.within(Tmp.v1, 50f)){
                            Draw.z(Layer.flyingUnit);
                            Draw.color(unit.team.color);
                            Draw.rect(Icon.eye.getRegion(), unit.x, unit.y, unit.rotation - 90);
                            Draw.reset();
                        }
                    }
            }
        };


        //Why do i exist? no reason, hope u don't cause any bugs even if you are one
        firefly = new NyfalisUnitType("firefly"){{
            constructor = UnitTypes.mono.constructor;

            flying = hidden = true;
            isEnemy = false;

            range = 50f;
            health = 100;
            speed = 1.5f;
            drag = 0.06f;
            accel = 0.12f;
            mineTier = 1;
            engineSize = 1.8f;
            mineSpeed = 2.5f;
            engineOffset = 5.7f;
        }
            @Override
            public void update(Unit unit){
                super.update(unit);
                spirit.spawn( unit.team, unit.x(), unit.y());
                unit.remove();
            }
        };
        // 1.7 leftover just convert it when ever
        excess = new LeggedWaterUnit("excess"){{
            speed = 0.4f;
            navalSpeed = 2;
            constructor = PayloadUnit::create;
            pathCost = NyfalisPathfind.costPreferNaval; //Still prefer liquid movement
            canBoost = hovering = boostUsesNaval = naval = hidden = true;
            canDrown = ammoDepletesOverTime = killOnAmmoDepletion = false;
            payloadCapacity = (5.5f * 5.5f) * tilePayload;
            weapons.add(new Weapon("large-weapon"){{
                reload = 13f;
                x = 4f;
                y = 2f;
                top = false;
                ejectEffect = Fx.casing1;
                bullet = new BasicBulletType(2.5f, 9){{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                }};
            }});
        }
            @Override
            public void update(Unit unit){
                super.update(unit);
                //TODO: uncomment this when Resolute exists thanks!
                //resolute.spawn( unit.team, unit.x(), unit.y());
                unit.remove();
            }
        };
        //endregion
        /*
         To help add conhereance and give easy visual markers to what units can hit what (only attacking stuff)
         The following bullet sprites should be used in this pattern. (colour, trails and other aspects are free reign)
         Ground - mine-bullet, olupis-diamond-bullet
         Air - missile-large, olupis-arc-bullet
         Both - missile, bullet, olupis-triangle-bullet
        */
    }

    public static void GenerateWeapons(){
        payloadWeaponIndex = new HashMap<>();
        for(UnitType u : Vars.content.units()){
            if(u.weapons.isEmpty()) continue;

            Seq<Weapon> buffer = new Seq<>();
            for(Weapon ow : u.weapons){
                Weapon w = ow.copy();
                w.bullet = ow.bullet.copy();
                w.shoot = ow.shoot.copy();
                //Flat reload nerf since we can go beyond the unit cap and this is to "help" w/ balancing
                w.reload *=2.5f;
                //is always true despite not used can cause headaches down the line
                if(!w.mirror) w.alternate = false;
                w.rotate = w.ignoreRotation = w.controllable = w.aiControllable = true;
                w.mirror = w.flipSprite = w.autoTarget = false;

//                if(w.bullet instanceof BasicBulletType bb){
//                    bb.frontColor = Color.scarlet;
//                    bb.backColor = Color.red;
//                }

                w.shootCone = 360f;
                w.rotationLimit = 361f;
                w.minShootVelocity = 0;
                w.rotateSpeed = Math.max(w.rotateSpeed, 20);
                if(w.alternate){
                    w.alternate = false;
                    Weapon ws = w.copy();
                    ws.shoot = w.shoot.copy();
                    buffer.add(ws);
                    w.flipSprite = true; //identifier that this weapon has a twin
                };
                buffer.add(w);
            }

            Weapon[] out = new Weapon[buffer.size];
            for(int i = 0; i < buffer.size; i++) out[i] = buffer.get(i);
            payloadWeaponIndex.put(u, out);
        }

        diptera.defaultCommand = NyfalisUnitCommands.nyfalisMineCommand;
        phorid.defaultCommand =  NyfalisUnitCommands.nyfalisMineCommand;
        district.defaultCommand =  regioner.defaultCommand = zoner.defaultCommand = NyfalisUnitCommands.nyfalisGuardCommand;
        shade.defaultCommand = NyfalisUnitCommands.nyfalisMineCommand;
        //todo this no work
    }

    public static void PostLoadUnits(){
        /*Blocks are null while loading units, so this exists for as a work around*/
        scarab.weapons.get(0).bullet.fragBullet = new MineBulletType(NyfalisBlocks.scarabRadar,Fx.placeBlock);
        batHelpers = Seq.with(pteropusAir, acerodonAir, nyctalusAir);
        ((AmmoEnabledUnitType) embryo).parentTypes = Seq.with(diptera, phorid);

        for (UnitType u : Vars.content.units()) {
            if(u.name.contains("olupis-")){
                u.envEnabled = Env.terrestrial | NyfalisAttributeWeather.nyfalian;

                if(u instanceof  NyfalisUnitType n && n.customMineAi) u.defaultCommand = NyfalisUnitCommands.nyfalisMoveCommand;

            }
        }

        boolean val = Core.settings.getBool("nyfalis-display-bat-helper");
        for(UnitType b : NyfalisUnits.batHelpers){
            b.hidden = !val;
        }
    }


}
