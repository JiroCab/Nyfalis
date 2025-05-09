package olupis.content;

import arc.*;
import arc.graphics.*;
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
import mindustry.type.ammo.*;
import mindustry.type.weapons.*;
import mindustry.world.meta.*;
import olupis.input.*;
import olupis.world.ai.*;
import olupis.world.entities.abilities.*;
import olupis.world.entities.bullets.*;
import olupis.world.entities.entities.*;
import olupis.world.entities.parts.*;
import olupis.world.entities.units.*;
import olupis.world.entities.weapons.*;

import java.util.*;

import static mindustry.Vars.tilePayload;
import static mindustry.content.Items.*;
import static olupis.content.NyfalisItemsLiquid.*;

public class NyfalisUnits {

    public static AmmoType lifeTimeDrill, lifeTimeWeapon, lifeTimeSupport, carrierTypeAmmo;
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
        lootbug
    ;

    public static BatHelperUnitType pteropusAir, acerodonAir, nyctalusAir, mirimiriAir , vampyrumAir;
    public static HashMap<UnitType, Weapon[]> payloadWeaponIndex;

    public static AmmoLifeTimeUnitType
        mite, tick, flea, lice,
        //support - yes, its just Phasmophobia ghost types
        spirit, phantom, banshee, revenant, poltergeis, shade
    ;
    public static Seq<BatHelperUnitType> batHelpers;

    public static void LoadUnits() {
        LoadAmmoType();

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
                range = 32f;
                shootY = 9.1f;
                y = x = recoil = 0f;
                reload = shootCone = 30f;
                ejectEffect = Fx.none;
                outlineRegion = null;
                layerOffset = Layer.flyingUnit -1;
                shootSound = Sounds.electricHum;

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
                    damage = 15 / 12f;
                    homingPower = 0.06f;
                    buildingDamageMultiplier = 1.1f;
                    incendChance = incendSpread = 0f;
                    pierce = true;
                    removeAfterPierce = false;
                    smokeEffect = shootEffect = Fx.none;
                    chargeEffect = hitEffect = Fx.hitLancer;
                    colors = NyfalisColors.aeroLaserColours;
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
                    hitEffect= shootEffect = Fx.hitLancer;
                    lightningColor = hitColor = Pal.surge;
                    failLightnighBullet = true;
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
                        hitEffect = Fx.hitLancer;

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
                shootSound = Sounds.spark;
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
        }};

        // falcon -> infintode lightning ability/tesla ultimate - fast-ish no collision bullet that zaps targets
        falcon = new NyfalisUnitType("falcon"){{
            armor = 5f;
            hitSize = 20f;
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
            abilities.add(new OrblessEnergyFieldAbillity(45f, 40f, 200f){{
                color = new Color().set(NyfalisColors.aeroLaserColours[0]).a(1);
                damageEffect = NyfalisFxs.chainLightningAlt;
                layer = Layer.flyingUnitLow - 0.01f;
                status=  StatusEffects.none;
                maxTargets = 5;
                statusDuration = curStroke = 0;
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
                shootSound = Sounds.spark;
                ignoreRotation = alwaysShooting= parentizeEffects = autoTarget = autoFindTarget = true;
                top = alternate =  mirror =  aiControllable = controllable = false;

                bullet = new ArtilleryBulletType(0.5f, 25, "circle-bullet"){{
                    width = height = 11f;
                    homingPower = 0.22f;
                    shrinkY = shrinkX = 0.75f;
                    velocityRnd = trailMult = 0;
                    frontColor = backColor = Pal.surge;
                    collides = collidesAir = collidesGround = collidesTeam = collidesTiles = keepVelocity = hittable = reflectable =false;
                    absorbable = true;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = trailEffect = Fx.none;
                    hitSound = Sounds.none;

                    fragBullets = intervalBullets = 1;
                    fragBullet = intervalBullet = new ArcLightningBulletType(){{
                        rangeOverride = range = 35f;
                        damage = 13;
                        homingPower = 0.1f;

                        status = StatusEffects.none;
                        hitEffect= shootEffect = Fx.hitLancer;
                        lightningColor = hitColor = Pal.surge;
                    }};
                }};
            }});

        }};

        // vortex -> gun ship inspired by Thor gunships of cnc:mental omega
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
                    ejectEffect = Fx.casing1;

                    shootSound = Sounds.missile;
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
                        frontColor = NyfalisColors.rustyBullet;
                        hitEffect = despawnEffect = NyfalisFxs.hollowPointHitSmall;
                        backColor = NyfalisColors.rustyBulletBack;

                        trailColor = NyfalisColors.rustyBullet;
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
                        frontColor = NyfalisColors.ironBullet;
                        backColor = NyfalisColors.ironBulletBack;
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
                    ejectEffect = Fx.casing1;
                    shootSound = Sounds.missile;

                    shoot = new ShootSpread(5, 5);

                    showStatSprite = mirror = false;
                    bullet = new BasicBulletType(7f, 6.5f){{
                        lifetime = 20f;
                        sprite = "mine-bullet";
                        width = 8;
                        height = 10f;
                        hitSize = 4f;
                        hitColor = backColor = trailColor = NyfalisColors.ironBulletBack;
                        frontColor = NyfalisColors.ironBullet;
                        trailWidth = 1.8f;
                        trailLength = 3;
                        hitEffect = despawnEffect = NyfalisFxs.hollowPointHitSmall;
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

                    shootSound = Sounds.shootAlt;
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
                        frontColor = NyfalisColors.ironBullet;
                        hitEffect = despawnEffect = NyfalisFxs.hollowPointHitSmall;
                        backColor = NyfalisColors.ironBulletBack;

                        trailColor = NyfalisColors.rustyBullet;
                        trailWidth = 1.5f;
                        trailLength = 3;

                        fragRandomSpread = 0;
                        fragBullets = 1;
                        fragBullet = new BasicBulletType(7f, 5){{
                            width = 5f;
                            height = 12f;
                            shrinkY = 1f;
                            lifetime = 10f;
                            backColor = NyfalisColors.rustyBulletBack;
                            frontColor = NyfalisColors.rustyBullet;
                            despawnEffect = Fx.none;
                            collidesGround = false;
                        }};
                    }};
                }}

            );
        }};

        //nyctalus -> siege bat, land = cnczh nuke cannon style weapon | air = 2 air-to-air missiles from the back
        nyctalus = new NyfalisUnitType("nyctalus"){{
            hitSize = 17f;
            armor = 5;
            drag = 0.06f;
            accel = 0.08f;
            health = 1300;
            speed = 1.8f;
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
            abilities.add(new SationaryBoostAblity());
            weapons.addAll(
                new NyfalisWeapon("", false, true){{
                    x = y = 0;
                    shootY = 5f;
                    recoil = 0.5f;
                    reload = 80;
                    recoils = 1;
                    top = alternate = mirror = false;
                    rotate = alwaysRotate = true;
                    //strict weapon activation
                    groundedEvaluation = 0;
                    boostedEvaluation = 1;

                    weaponIconString = "olupis-serpent-tail";

                    shootSound = Sounds.artillery;
                    parts.addAll(
                    new RegionPart("olupis-serpent-tail"){{
                        mirror = false;
                        rotation = 180;
                        y = 1.95f;
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

                    bullet = new BasicBulletType(2.2f, 37, "large-bomb"){{
                        spin = 10f;
                        lifetime = 100f;
                        shrinkX = 20f /60;
                        shrinkY = 30f /60;
                        width = height = 17f;
                        splashDamage = 50f;
                        splashDamageRadius = 30f;
                        frontColor = NyfalisColors.ironBullet;
                        backColor = NyfalisColors.ironBulletBack;
                        hitEffect = despawnEffect = NyfalisFxs.highYieldExplosive;
                         shrinkInterp = Interp.slope;
                        collidesAir = false;
                    }};
                }}
            );
        }};

        //mirimiri -> deployed = fires a swarm of long range small missles (10) | air = short-medium range  shell that burst into mini swarm of missles (4)

        pteropusAir = new BatHelperUnitType(pteropus);
        acerodonAir = new BatHelperUnitType(acerodon);
        nyctalusAir = new BatHelperUnitType(nyctalus);
        //endregion
        //region Air - Area / from naval
        zoner = new NyfalisUnitType("zoner"){{
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

            constructor = UnitEntity::create;
            aiController = WaveAiHandler::new;
            lowAltitude = flying = canGuardUnits = waveHunts = true;
            defaultCommand = NyfalisUnitCommands.nyfalisGuardCommand;
            weapons.add(new Weapon("olupis-zoner-weapon"){{
                top = alternate = false;
                y = -1f;
                x = -1.8f;
                inaccuracy = 5f;
                reload = shootCone = 15f;
                ejectEffect = Fx.casing1;

                showStatSprite = false;
                bullet = new BasicBulletType(3.2f, 5, "olupis-diamond-bullet"){{
                    width = 4;
                    height = 6f;
                    lifetime = 40f;
                    homingPower = 0.05f;
                    buildingDamageMultiplier = 0.5f;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.shootSmallSmoke;
                    frontColor = NyfalisColors.rustyBullet;
                    hitEffect = despawnEffect = NyfalisFxs.hollowPointHitSmall;
                    backColor = NyfalisColors.rustyBulletBack;
                }};
            }});
        }};

        //re.gioner - shotgun aircraft!
        regioner = new NyfalisUnitType("regioner"){{
            drag = 0.05f;
            accel = 0.10f;
            health = 450f;
            fogRadius = 11f;
            engineSize = 1.6f;
            rotateSpeed = 19f;
            itemCapacity = 25;
            engineOffset = 4.6f;
            armor = speed = 3f;
            hitSize = 12f;


            constructor = UnitEntity::create;
            aiController = WaveAiHandler::new;
            lowAltitude = flying = canGuardUnits = waveHunts =true;

            weapons.add(new Weapon("olupis-regioner-weapon"){{
                top = alternate = false;
                y = 0.9f;
                x = -3.6f;
                recoil = 0.47f;
                reload = 13f;
                shootCone = 65f;
                baseRotation = -7f;
                ejectEffect = Fx.none;
                shoot = new ShootSpread(7, 1.3f);

                showStatSprite = false;
                bullet = new BasicBulletType(2f, 3.5f, "olupis-diamond-bullet"){{
                    width = 6;
                    height = 8f;
                    lifetime = 20f;
                    buildingDamageMultiplier = 0.3f;

                    frontColor = NyfalisColors.rustyBullet;
                    hitEffect = despawnEffect = NyfalisFxs.scatterDebris;
                    backColor = NyfalisColors.rustyBulletBack;
                }};
            }});
        }};

        //district -> a gun ship, light gun as primary and ammo limited secondary that resupplies from mother ship/maker (resolute)
        district = new AmmoEnabledUnitType("district"){{
            drag = 0.05f;
            accel = 0.05f;
            health = 1000;
            fogRadius = 10f;
            engineSize = 1.6f;
            rotateSpeed = 19f;
            itemCapacity = 25;
            engineOffset = 4.6f;
            armor = speed = 3f;
            hitSize = 12f;
            ammoZ = Layer.flyingUnitLow;


            constructor = UnitEntity::create;
            aiController = WaveAiHandler::new;
            ammoType = carrierTypeAmmo;
            faceTarget = false;
            lowAltitude = flying = canGuardUnits = waveHunts = altResupply = drawAmmo = true;

            weapons.addAll(
                new NyfalisWeapon("olupis-regioner-weapon"){{
                    alternate = mirror =  false;
                    rotate = alwaysUseAmmo = true;
                    x = y = 0;
                    recoil = 0.47f;
                    reload = 30f;
                    shootCone = 65f;
                    baseRotation = -7f;
                    ejectEffect = Fx.none;

                    showStatSprite = false;
                    bullet = new BasicBulletType(3f, 12f){{
                        spin = 30f;
                        lifetime = 30f;
                        width = height = 7f;
                        splashDamage = 1f;
                        layerOffset = 0.05f;
                        splashDamageRadius = 5f * 0.75f;
                        frontColor = NyfalisColors.ironBullet;
                        backColor = NyfalisColors.ironBulletBack;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
                        sprite = "mine-bullet";
                        collidesAir = false;
                    }};
                }},
                new Weapon("olupis-zoner-weapon"){{
                    alternate = mirror = useAmmo = false;
                    rotate = true;
                    x = 0;
                    y = 3;
                    reload = 15f;
                    recoil = 0.47f;
                    shootCone = 65f;
                    layerOffset = 0.05f;
                    ejectEffect = Fx.none;
                    var sht = new ShootHelix();
                    sht.scl = 5f;
                    shoot = sht;

                    showStatSprite = false;
                    bullet = new BasicBulletType(5f, 4f, "olupis-diamond-bullet"){{
                        width = 4;
                        height = 6f;
                        lifetime = 22f;
                        buildingDamageMultiplier = 0.3f;

                        frontColor = NyfalisColors.rustyBullet;
                        hitEffect = despawnEffect = NyfalisFxs.scatterDebris;
                        backColor = NyfalisColors.rustyBulletBack;
                    }};
                }}
            );
        }};

        //division -> Fighter-bomber, cnc Zero hour china mig style weapon
        //endregion
        //region Ground - Snek
        venom = new SnekUnitType("venom"){{
            constructor = CrawlUnit::create;
            armor = 2;
            accel = 2.5f;
            health = 250;
            speed = 2.2f;
            segments = 7;
            segmentScl = 8f;
            rotateSpeed = 10f;
            itemCapacity = 20;
            legMoveSpace = 1.1f;
            crushDamage = 0.1f;
            segmentMaxRot = 80f;
            hitSize = 12f;
            crawlSlowdown = 0.2f;
            segmentRotSpeed = 5f;
            crawlSlowdownFrac = 1f;
            drownTimeMultiplier = 4f;
            omniMovement = drawBody = false;
            allowLegStep = canCharge = true;

           weapons.addAll(new SnekWeapon("olupis-dark-pew"){{
                x = y = 0f;
                reload = 40f;
                shootY = 4.5f;
                shoot.shots = 3;
                shoot.shotDelay = 4f;
                weaponSegmentParent = 3;
                mirror = false;
                rotate = true;
                ejectEffect = Fx.casing1;
                bullet = new BasicBulletType(2.5f, 9f){{
                    width = 7f;
                    height = 9f;
                    shrinkX = 25f /60;
                    shrinkY = 35f /60;
                    lifetime = 50f;
                    fragBullets = 1;
                    fragVelocityMin = 1f;
                    fragRandomSpread = 0f;
                }};
            }});
        }};

        serpent = new SnekUnitType("serpent"){{
            constructor = CrawlUnit::create;
            accel = 3f;
            armor = 5;
            hitSize = 11f;
            health = 600;
            segments = 8;
            speed = 2.15f;
            segmentScl = 7f;
            rotateSpeed = 15f;
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
                   y = 13.5f;
                   recoil = 0f;
                   shootY = 0f;
                   reload = 11f;
                   shootCone = 45f;
                   weaponSegmentParent = 7;
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
               new SnekWeapon(""){{ // dash
                    x = 0f;
                    y = -13f;
                    reload = 35f;
                    shootCone = 360f;
                    baseRotation = 180f;
                    minShootVelocity = 0.1f; //So they don't dash while on the target or something
                    weaponSegmentParent = 1;
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
            accel = 3f;
            armor = 5;
            hitSize = 11f;
            health = 1600;
            segments = 3;
            speed = 2.15f;
            segmentScl = 7f;
            rotateSpeed = 15f;
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
                    shootSound = Sounds.tractorbeam;
                    ejectEffect = Fx.casing1;
                    bullet = new TracterBeamBullet(){{
                        continuous = true;
                        shake = 0f;
                        width = 0.7f;
                        length = 100f;
                        lifetime = 20;
                        lightStroke = 10;
                        damage = 40 / 12f;
                        statusDuration = 60f;
                        absMag = absScl = 0f;
                        statusOnOwner = true;
                        layer = Layer.groundUnit - 0.01f;
                        status = NyfalisStatusEffects.marked;
                        ownerStatus = StatusEffects.slow;
                        incendChance = incendSpread = 0f;
                        smokeEffect = shootEffect = Fx.none;
                        chargeEffect = hitEffect = NyfalisFxs.hitTracter;
                        colors = new Color[]{Pal.regen.cpy().a(.2f), Pal.regen.cpy().a(.5f), Pal.regen.cpy().mul(1.2f), Color.white};
                    }};
                }},
                new SnekWeapon(""){{
                    x = 0;
                    y = -10f;
                    shootSound = Sounds.flame;
                    shootY = 2f;
                    reload = 35f;
                    shootCone = 360f;
                    baseRotation = 180f;
                    minShootVelocity = 0.1f; //So they don't dash while on the target or something
                    weaponSegmentParent = 0;
                    ignoreRotation = dashShoot = dashExclusive = partialControl = true;
                    rotate = alternate = mirror = aiControllable = top = useAmmo = false;
                    ejectEffect = Fx.none;
                    bullet = new BulletType(4.2f, 37f){{
                        ammoMultiplier = 3f;
                        hitSize = 7f;
                        lifetime = 13f;
                        recoil = 10f;
                        pierce = true;
                        pierceBuilding = true;
                        pierceCap = 2;
                        statusDuration = 60f * 4;
                        shootEffect = Fx.shootSmallFlame;
                        hitEffect = Fx.hitFlameSmall;
                        despawnEffect = Fx.none;
                        status = StatusEffects.burning;
                        keepVelocity = false;
                        hittable = false;
                    }};
                }});
        }};
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
            weapons.add(
                new Weapon(""){{
                    x = -0.1f;
                    y = 1.25f;
                    shootX = 6f;
                    reload = 15f;
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
                    bullet = new BasicBulletType(2.5f, 8){{
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
                    bullet = new BasicBulletType(3f, 7){{
                        width = 5f;
                        height = 6f;
                        lifetime = 36f;
                        frontColor = NyfalisColors.rustyBullet;
                        backColor = NyfalisColors.rustyBulletBack;
                        hitEffect = despawnEffect = NyfalisFxs.hollowPointHitSmall;
                    }};
                }}
            );
            setEnginesMirror(new UnitEngine(22 / 4f, -5 / 4f, 2f, 5f));

        }};

        germanica = new NyfalisUnitType("germanica"){{
            constructor = MechUnit::create;

            canBoost = lowAltitude = alwaysShootWhenMoving = true;
            armor = 5;
            hitSize = 12f;
            range = 1.5f;
            health = 620;
            speed = 0.7f;
            engineSize = -1;
            itemOffsetY = 3f;
            rotateSpeed = 2.25f;
            boostMultiplier = 0.8f;
            immunities.add(StatusEffects.burning);
            abilities.add(new MicroWaveFieldAbility(6.5f, 40f, 40f, 20f){{
                ideRangeDisplay = false;
                damageEffect = Fx.none;
                sectors = 3;
                boostRange = 20f;
                maxTargetBoost = 7;
                maxTargetsGround = 14;

            }});
            //Gave up trying to make it, so it has a boost damage weapon since the ability's range display won't go away while boosting if triggered, so made it just it sole weapon that changes on boost or not
            setEnginesMirror(new UnitEngine(22 / 4f, -5 / 4f, 2f, 5f));
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
                new NyfalisWeapon("olupis-tri-mount-barrel-m", true, true){{
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
                    frontColor = NyfalisColors.ironBullet;
                    backColor = NyfalisColors.ironBulletBack;
                    trailColor = NyfalisColors.rustyBulletBack;
                    hitEffect = despawnEffect = Fx.flakExplosion;

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
        //luridiblatta -> long range shell launcher, only fires at target +/- 5 tiles of max range (has min range)


        parcoblatta = new NyfalisUnitType("parcoblatta"){{
            constructor = MechUnit::create;

            canBoost = lowAltitude = true;
            boostMultiplier = 0.81f;

            armor = 3;
            hitSize =UnitTypes.reign.hitSize;
            health = 125;
            speed = 0.60f;
            engineSize = -1;
            rotateSpeed = 1.72f;
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
                shootSound = NyfalisSounds.cncZhBattleMasterWeapon;
                bullet = new BasicBulletType(2.5f, 20){{
                    width = 15f;
                    height = 20f;
                    lifetime = 150;
                    trailWidth = 6f;
                    trailLength = 10;
                    weaveScale = 1;
                    weaveMag = 1.5f;
                    shrinkX = -0.70f;
                    shrinkY = -0.57f;
                    frontColor = NyfalisColors.ironBullet;
                    backColor = NyfalisColors.ironBulletBack;
                    trailColor = NyfalisColors.rustyBulletBack;
                    hitEffect = despawnEffect = Fx.flakExplosionBig;

                    fragBullets = 1;
                    fragBullet = new DistanceScalingBulletType(90, 70){{
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
                new CarrierResupplyAblity(1),
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
                new CarrierResupplyAblity(2),
                new UnitRallySpawnAblity(regioner, 60f * 15f, 0, 0f, 0, -10f)
            );
            weapons.add(new LaserPointerPointDefenceWeapon("olupis-warden-point-defense"){{
                x = 0;
                y = -7f;
                aoe = 0;
                reload = 6f;
                soundVol = 0.7f;
                targetInterval = targetSwitchInterval = 14f;
                mirror = false;
                shootSound = NyfalisSounds.cncZhAvengerPdl;
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
            groundSpeed = 0.6f;
            navalSpeed = 1f;

            armor = 6f;
            hitSize = 21;
            health = 1150;
            itemCapacity = 0;
            legCount = 0;
            rotateSpeed = 3.5f;
            researchCostMultiplier = 0f;
            legMoveSpace = 0;

            immunities.add(StatusEffects.wet);
            rotateMoveFirst = canDeploy = naval = hovering = true;
            canDrown = ammoDepletesOverTime = killOnAmmoDepletion = omniMovementGround = omniMovementNaval = legPhysicsLayer = allowLegStep = false;
            constructor = LegsUnit::create; //Legged so it doesnt slow down in deep water
            pathCost = NyfalisPathfind.costPreferNaval;

            weapons.add(new LaserPointerPointDefenceWeapon("olupis-Lexington-point-defense"){{
                x = 0;
                y = -7f;
                reload = 6f;
                minWarmup = 0.9f;
                soundVol = 0.7f;
                soundPitchMin = 0.65f;
                soundPitchMax = 0.8f;
                targetInterval = targetSwitchInterval = 12f;
                mirror = false;
                shootSound = NyfalisSounds.cncZhAvengerPdl;

                hitAoeEffect = new MultiEffect( NyfalisFxs.miniPointHit);
                bullet = new BulletType(){{
                    shootEffect = Fx.shootSmokeSquare;
                    aoeBeamEffect = NyfalisFxs.getMiniPointHit;
                    hitEffect = Fx.pointHit;
                    maxRange = 320f;
                    damage = 60f;
                }};
            }});
            abilities.addAll(
                new CarrierResupplyAblity(3),
                new UnitRallySpawnAblity(district, 60f * 30f, 5.5f, 0,0, 15f, true){{
                    displayBars = false;
                }},
                new UnitRallySpawnAblity(district, 60f * 30f, -5.5f, 0, 0, 15f, true)
            );
            parts.addAll(
                    new FloaterTreadsPart("-treads"){{
                        mirror = under = true;
                        drawRegion  = false;
                        x = 4;
                        y = 0;
                        moveX = 4;
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
            pathCost = NyfalisPathfind.costPreferNaval;
            groundSpeed = 0.6f;
            navalSpeed = 1f;

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
            canDrown = ammoDepletesOverTime = killOnAmmoDepletion = omniMovementGround = omniMovementNaval = legPhysicsLayer = allowLegStep = pickupBlocks = false;

            payloadUnitsUpdate = true;

            weapons.addAll(
                new Weapon("olupis-dark-pew"){{
                    x = 0;
                    y = 5f;
                    reload = 15f;
                    rotate = true;
                    top = alternate = mirror = false;
                    ejectEffect = Fx.casing1;
                    parts.addAll(
                    );
                    bullet = new TracterBeamBullet(){{
                        continuous = true;
                        shake = 0f;
                        width = 0.3f;
                        length = 100f;
                        lifetime = 20;
                        lightStroke = 10;
                        damage = 40 / 12f;
                        statusDuration = 60f;
                        absMag = absScl = 0f;
                        statusOnOwner = true;
                        layer = Layer.groundUnit - 0.01f;

                        status = NyfalisStatusEffects.marked;

                        incendChance = incendSpread = 0f;
                        smokeEffect = shootEffect = Fx.none;
                        chargeEffect = hitEffect = NyfalisFxs.hitTracter;
                        colors = new Color[]{Pal.regen.cpy().a(.2f), Pal.regen.cpy().a(.5f), Pal.regen.cpy().mul(1.2f), Pal.accent};
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
                    shootSound = NyfalisSounds.cncZhAvengerPdl;

                    hitAoeEffect = new MultiEffect( NyfalisFxs.miniPointHit);
                    bullet = new BulletType(){{
                        shootEffect = Fx.shootSmokeSquare;
                        aoeBeamEffect = NyfalisFxs.getMiniPointHit;
                        hitEffect = Fx.pointHit;
                        maxRange = 350f;
                        damage = 60f;
                    }};
                }})
            ;
            abilities.addAll(
                new CarrierResupplyAblity(4),
                new ShieldArcAbility(){{
                    radius = 36f;
                    angle = 82f;
                    regen = 0.6f;
                    cooldown = 60f * 8f;
                    max = 2000f;
                    y = -20f;
                    width = 6f;
                    whenShooting = false;
                }}
                //new UnitRallySpawnAblity(district, 60f * 15f, 0, 6.5f)
            );
        }};

        //->Flag ship, boost, payload, Hex sheild when landed, prop/unit booster when flying
        //-> has payload, takes a bunch of t3 and bellow and  lets them shoot out of them, cnc:ra2 battle fortress / cnc:Zh battle bus


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
            ammoType = new PowerAmmoType(900);
            weapons.add(new Weapon("olupis-missiles-mount-teamed"){{
                x = 0f;
                y = -8;
                reload = 26f;
                rotate= true;
                mirror = false;
                ejectEffect = Fx.casing1;
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
                autoTarget = rotate = partialControl = weaponIconUseFullString = true;
                mirror = controllable = false;
                weaponIconString = "olupis-bay-ui-front";
                bullet = new BasicBulletType(2.5f, 10){{
                    width = 5f;
                    height = 6f;
                    lifetime = 60f;
                    collidesAir = false;
                    frontColor = NyfalisColors.rustyBullet;
                    backColor = NyfalisColors.rustyBulletBack;
                    hitEffect = despawnEffect = NyfalisFxs.hollowPointHitSmall;
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
                            outlineColor = NyfalisColors.contentOutline;
                            outline = drawRegion = under = true;
                            progress = PartProgress.recoil;
                            moves.add(new PartMove(PartProgress.recoil, 0, -3f, 0));
                    }}); }

                    bullet = new ArtilleryBulletType(3f, 16){{
                        width = 7f;
                        height = 9f;
                        trailSize = 3f;
                        lifetime = 65f;
                        splashDamage = 7f;
                        splashDamageRadius = 2.5f *8f;
                        collidesAir = false;
                        frontColor = NyfalisColors.ironBullet;
                        backColor = NyfalisColors.ironBulletBack;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
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
                    rotate = true;
                    mirror = false;
                    shootSound = Sounds.shoot;
                    shoot = new ShootAlternate(4.1f);
                    for(int i = 0; i < 2; i ++){ int f = i;
                        parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")){{
                            x = (f == 0) ? 1.6f : -1.6f;
                            y = 2f;
                            recoilIndex = f;
                            outlineLayerOffset = 0f;
                            outlineColor = NyfalisColors.contentOutline;
                            outline = drawRegion = under = true;
                            progress = PartProgress.recoil;
                            moves.add(new PartMove(PartProgress.recoil, 0, -2f, 0));
                    }}); }

                    bullet = new BasicBulletType(2.5f, 8){{
                        width = 5f;
                        height = 6f;
                        lifetime = 78f;
                        collidesAir = false;
                        frontColor = NyfalisColors.rustyBullet;
                        backColor = NyfalisColors.rustyBulletBack;
                        hitEffect = despawnEffect = NyfalisFxs.hollowPointHitSmall;
                        shootEffect = Fx.shootSmallSmoke;
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
                y = -9f;
                recoils = 3;
                reload = 20;
                recoil = 0.5f;
                rotateSpeed = 5f;
                mirror = false;
                rotate= top = true;
                shoot = new ShootAlternate(2.7f);
                shootSound = NyfalisSounds.cncZhBattleMasterWeapon;
                for(int i = 0; i < 3; i ++){ int f = i;
                    parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : i == 1 ? "m" : "r")){{
                        x = (f == 0) ? 3.8f : (f == 1) ? 0f : -3.8f;
                        y = 5.25f;
                        shootY = 6f;
                        recoilIndex = f;
                        outlineLayerOffset = 0f;
                        outlineColor = NyfalisColors.contentOutline;
                        outline = drawRegion = under = true;
                        progress = PartProgress.recoil;
                        moves.add(new PartMove(PartProgress.recoil, 0, -3f, 0));
                    }}); }

                shootSound = Sounds.shootBig;
                bullet = new RollBulletType(3f, 16){{
                    width = 35f;
                    height = 9f;
                    lifetime = 65f;
                    splashDamage = 2f;
                    homingPower = 0.15f;
                    splashDamageRadius = 25f * 0.75f;
                    collidesAir = false;
                    frontColor = NyfalisColors.ironBullet;
                    backColor = NyfalisColors.ironBulletBack;
                    hitEffect = despawnEffect = Fx.hitBulletSmall;
                }};
            }},
            new Weapon("olupis-dark-pew"){{
                x = 0;
                y = -9f;
                reload = 35f;
                mirror = false;
                rotate = true;
                layerOffset = 0.01f;
                ejectEffect = Fx.casing1;
                shoot = new ShootSpread(6, 2f);
                bullet = new BasicBulletType(2.5f, 10f){{
                    width = 7f;
                    height = 9f;
                    shrinkX = 25f /60;
                    shrinkY = 35f /60;
                    lifetime = 40f;
                    fragBullets = 1;
                    fragVelocityMin = 1f;
                    fragRandomSpread = 0f;
                }};
            }},
            new Weapon("olupis-dark-pew"){{
                x = 0;
                y = 11f;
                reload = 35f;
                mirror = false;
                rotate = true;
                ejectEffect = Fx.casing1;
                shoot = new ShootSpread(6, 2f);
                bullet = new BasicBulletType(2.5f, 10f){{
                    width = 7f;
                    height = 9f;
                    shrinkX = 25f /60;
                    shrinkY = 35f /60;
                    lifetime = 40f;
                    fragBullets = 1;
                    fragVelocityMin = 1f;
                    fragRandomSpread = 0f;
                }};
            }});
        }};

        //torret - 2 broadside cram/doom cannons (artillery )

        //endregion
        //region Limited - Hive
        float hiveDepletionRate = 1;
        flea = new AmmoLifeTimeUnitType("flea"){{
            hitSize = 8f;
            range = 4f;
            armor = 10f;
            speed = 2.7f;
            drag = 0.04f;
            accel = 0.08f;
            health = 100;
            fogRadius = 0;
            lightRadius = 15f;
            itemCapacity = 0;
            penaltyMultiplier = 1f;
            ammoDepletionAmount = hiveDepletionRate;
            maxRange = 15f * Vars.tilesize;
            ammoCapacity = (int) (600f/(speed * ammoDepletionAmount));

            flying = targetGround = targetAir = drawAmmo = ammoDepletesOverTime = true;
            playerControllable  = logicControllable = useUnitCap = ammoDepletesInRange = false;
            constructor = UnitEntity::create;
            controller = u -> new SearchAndDestroyFlyingAi();
            weapons.add(new NyfalisWeapon(){{
                y = x = 0f;
                reload = 30f;
                inaccuracy = 12f;
                shootCone = 15f;
                targetInterval = 30f;
                targetSwitchInterval = 60f;
                ammoPerShot = (float) ammoCapacity / 4f;

                shootSound = Sounds.pew;
                ammoType = lifeTimeWeapon;
                bullet = new FlakBulletType(6f, 3){{
                    width = 6f;
                    height = 8f;
                    lifetime = 30f;
                    ammoMultiplier = 5f;
                    reloadMultiplier = 0.5f;
                    splashDamage = 20f * 1.5f;
                    splashDamageRadius = 4f * 8f;
                    buildingDamageMultiplier = 0f;
                    collidesGround = true;
                    shootEffect = Fx.shootSmall;
                    hitEffect = Fx.flakExplosion;
                }};
            }});
        }};

        mite = new AmmoLifeTimeUnitType("mite"){{
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
            penaltyMultiplier = 1f;
            ammoDepletionAmount = hiveDepletionRate;

            flying = targetGround = targetAir = drawAmmo = ammoDepletesOverTime = true;
            playerControllable  = logicControllable = useUnitCap = ammoDepletesInRange = false;
            constructor = UnitEntity::create;
            targetFlags = new BlockFlag[]{BlockFlag.factory, null};
            controller = u -> new SearchAndDestroyFlyingAi(true);
            weapons.add(new NyfalisWeapon(){{
                y = x = 0f;
                reload = 10f;
                shootCone = 15f;
                targetInterval = 30f;
                ammoPerShot = 2;
                targetSwitchInterval = 60f;

                shootSound = Sounds.pew;
                ammoType = lifeTimeWeapon;
                /*Gave up using LiquidBulletType*/
                bullet = new NoBoilLiquidBulletType(NyfalisItemsLiquid.steam){{
                    useAmmo = true;
                    pierce = true;

                    speed = 2f;
                    lifetime = 18f;
                    damage = 10f;
                    pierceCap = 1;
                    ammoMultiplier = 1.5f;
                    statusDuration = 1.5f *60f;
                    buildingDamageMultiplier = 0f;
                    status = StatusEffects.corroded;
                    shootEffect = Fx.shootLiquid;
                    despawnEffect = hitEffect = Fx.steam;
                }};
            }});
        }};

        //fires 2 roll bullets in quick succession
        lice = new AmmoLifeTimeUnitType("lice"){{
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
            penaltyMultiplier = 1f;
            ammoDepletionAmount = hiveDepletionRate;
            ammoCapacity = (int) (600f/(speed * ammoDepletionAmount));

            flying = targetGround = targetAir = drawAmmo = ammoDepletesOverTime = true;
            playerControllable  = logicControllable = useUnitCap = ammoDepletesInRange = false;
            constructor = UnitEntity::create;
            controller = u -> new SearchAndDestroyFlyingAi(true);
            weapons.add(new NyfalisWeapon(){{
                y = x = 0f;
                reload = 10f;
                shootCone = 15f;
                targetInterval = 30f;
                targetSwitchInterval = 60f;
                ammoPerShot = (float) ammoCapacity / 2f;

                fireOverSolids = false;
                shootSound = NyfalisSounds.cncZhBattleMasterWeapon;
                ammoType = lifeTimeWeapon;

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
        tick = new AmmoLifeTimeUnitType("tick"){{
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
            penaltyMultiplier = 1f;
            ammoDepletionAmount = hiveDepletionRate;
            ammoCapacity = (int) (600f/(speed * ammoDepletionAmount));

            flying = targetGround = targetAir = drawAmmo = ammoDepletesOverTime = true;
            playerControllable  = logicControllable = useUnitCap = ammoDepletesInRange = false;
            constructor = UnitEntity::create;
            controller = u -> new SearchAndDestroyFlyingAi(true);
            weapons.add(new NyfalisWeapon(){{
                reload = 24f;
                x = shootY = 0f;
                shootCone = 180f;
                soundPitchMax = 6f;
                soundPitchMin = 0.2f;
                ejectEffect = Fx.none;
                ammoPerShot = ammoCapacity ;
                shootSound = NyfalisSounds.cncZhBattleMasterWeapon;
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

                    hitEffect = NyfalisFxs.obliteratorShockwave;
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
            fogRadius = 6;
            mineSpeed = 3.5f;
            itemCapacity = 20;
            ammoCapacity = 150;
            passiveAmmoDepletion = 0.1f;
            ammoDepletionAmount = 0.15f;

            ammoType = lifeTimeDrill;
            constructor = UnitEntity::create;
            timedOutSound = Sounds.dullExplosion;
            controller = u -> new NyfalisMiningAi();
            flying = miningDepletesAmmo = depleteOnInteractionUsesPassive = constructHideDefault = drawAmmo = inoperableDepletes = true;
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = ammoDepletesInRange = false;
        }};

        phantom = new AmmoLifeTimeUnitType("phantom"){{
            armor = 2;
            hitSize = 7f;
            speed = 3.25f;
            fogRadius = 6f;
            ammoCapacity = 320;


            weapons.add(new LimitedRepairBeamWeapon(""){{
                y = 6f;
                engineSize = -1;
                shootCone = 20f;
                shootX = shootY = x = 0;
                fractionRepairSpeed = 0.03f;
                beamWidth = repairSpeed = 0.3f;

                targetBuildings = useAmmo = true;
                controllable = top = false;
                bullet = new BulletType(){{
                    aimDst = 0f;
                    maxRange = 120f;
                    healPercent = 1f;
                }};
            }});

            ammoType = lifeTimeSupport;
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
            fogRadius = 8f;
            legLength = 10f;
            mineSpeed = 4f;
            navalSpeed = 1.1f;
            legForwardScl = 0.8f;
            legBaseOffset = -2f;
            legMoveSpace = 1.4f;
            ammoCapacity = 300;

            ammoType = lifeTimeDrill;
            groundLayer = Layer.legUnit;
            constructor = LegsUnit::create;
            timedOutSound = Sounds.dullExplosion;
            controller = u -> new NyfalisMiningAi();
            hovering = miningDepletesAmmo = depleteOnInteractionUsesPassive = showLegsOnLiquid = lockLegsOnLiquid= drawAmmo = customShadow = inoperableDepletes = true;
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = canDrown = false;
        }};

        revenant = new AmmoLifeTimeUnitType("revenant"){{
            armor = 2;
            speed = 3.25f;
            fogRadius = 6f;
            buildSpeed = 0.8f;
            ammoCapacity = 2500;

            weapons.add(new BuildWeapon("build-weapon"){{
                    rotate = useAmmo = true;
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

                    if(unit.activelyBuilding() && useAmmo ){ //TODO: AMMO SHOULDN'T USE WHEN TRYING TO BUILD W/O ITEMS FOR IT
                        //Since it isn't really shooting, ammo isn't used properly handled
                        unit.ammo--;
                        if(unit.ammo < 0) unit.ammo = 0;
                    }
                    super.update(unit, mount);
                }
            });

            ammoType = lifeTimeSupport;
            constructor = UnitEntity::create;
            aiController = BuilderAI::new;
            defaultCommand = UnitCommand.rebuildCommand;
            setEnginesMirror(new UnitEngine(8 / 4f, -21 / 4f, 2.1f, 245));
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = ammoDepletesInRange = false;
            flying = miningDepletesAmmo = depleteOnInteractionUsesPassive =  targetAir = targetGround = singleTarget  = drawAmmo  = true;
        }};

        //endregion
        //region Limited - Sumoned
        embryo = new AmmoLifeTimeUnitType("embryo"){{
            /*(trans) Egg if chan-version is made >;3c */
            speed = 3f;
            fogRadius = 0f;
            itemCapacity = 0;
            ammoCapacity = 150;
            ammoDepletionAmount = ammoCapacity;
            ammoDepletionOffset = 60*10;

            flying = alwaysShootWhenMoving = drawAmmo = true;
            playerControllable = useUnitCap = false;
            constructor = UnitEntity::create;
            controller = u -> new AgressiveFlyingAi(true);
            weapons.add(new Weapon(){{
                top = false;
                reload = 25f;
                shootCone = 30f;
                shootSound = Sounds.lasershoot;
                x = y = shootX = inaccuracy = 0f;
                bullet = new LaserBoltBulletType(6f, 10){{
                    lifetime = 30f;
                    healPercent = 5f;
                    homingPower = 0.03f;
                    buildingDamageMultiplier = 0.01f;
                    collidesTeam = true;
                    backColor = Pal.heal;
                    frontColor = Color.white;
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
            fogRadius = 6;
            mineSpeed = 3.5f;
            itemCapacity = 10;
            ammoCapacity = 150;
            passiveAmmoDepletion = 0.07f;
            ammoDepletionAmount = 0.15f;

            ammoType = lifeTimeDrill;
            aiController = RepairAI::new    ;
            constructor = UnitEntity::create;
            timedOutSound = Sounds.dullExplosion;
            flying = miningDepletesAmmo = depleteOnInteractionUsesPassive = constructHideDefault = drawAmmo = cantMove =  customMineAi = inoperableDepletes  = true;
            isEnemy = ammoDepletesOverTime = depleteOnInteraction = ammoDepletesInRange = false;

            weapons.add(new NyfalisWeapon(){{
                top = mirror = alternate = false;
                x = y = 0f;
                recoil = 2f;
                shootY = 4f;
                reload = 24f;


                ejectEffect = Fx.none;
                shootSound = Sounds.lasershoot;

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
            useUnitCap = faceTarget = false;
            flying = canDeploy = canCharge = emitSecondaryLight = payloadDisarms =  true;
            parts.addAll(
                    new RegionPart("-radar"){{
                        mirror = false;
                        under = false;
                        layerOffset = 2;

                        heatProgress = p -> Mathf.cos(Time.time / 10) / 2 + 0.5f;
                        heatColor = Color.valueOf("3ed09a");
                        y = -3.5f;
                        moves.add(new PartMove(p ->( Mathf.cos(Time.time) / 2 + 0.5f), 0, 0, 360f));
                    }}
            );
            weapons.add(new NyfalisWeapon(""){{
                top = mirror = false;
                shake = 0f;
                shootY = -9.1f;
                y = x = recoil = 0f;
                reload = shootCone = 360f;
                noAttack = shootOnDeath = statsBlocksOnly = dashShoot = true;
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
                    hitSound = Sounds.mineDeploy;
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
            pathCost = NyfalisPathfind.costLeggedNaval;
            ammoType = new PowerAmmoType(1000);

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
                        collides = hittable = collidesTiles = false;
                        instantDisappear = collidesAir = true;
                        hitSound = Sounds.explosion;
                        hitEffect = NyfalisFxs.unitDischarge;

                        splashDamage = 65f;
                        rangeOverride = 30f;
                        splashDamageRadius = 55f;
                        buildingDamageMultiplier = speed = 0f;
                        intervalBullet = new HealOnlyBulletType(0,0) {{
                            spin = 3.5f;
                            drag = 0.9f;
                            lifetime = 10*60;
                            shrinkX = 25f/60f;
                            shrinkY = 35f/60f;
                            intervalBullets = 2;
                            intervalSpread = 180;
                            intervalRandomSpread = 90;
                            height = width = bulletInterval = healAmount = 20;

                            collidesTeam = true;
                            keepVelocity = false;
                            hitEffect = despawnEffect = Fx.heal;
                            backColor = frontColor = trailColor = lightColor = Pal.heal;

                            intervalBullet = new HealOnlyBulletType(4,-5, "olupis-diamond-bullet", false) {{
                                lifetime = 60;
                                trailLength = 10;
                                trailWidth = 1.5f;
                                healAmount = 20;
                                bulletInterval = 10;
                                homingPower = 0.09f;

                                collidesTeam = true;
                                keepVelocity = false;
                                hitEffect = despawnEffect = Fx.heal;
                                backColor = frontColor = trailColor = lightColor = Pal.heal.a(0.4f);
                            }};
                        }};
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
            pathCost = NyfalisPathfind.costLeggedNaval;
            ammoType = new PowerAmmoType(1000);

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
                        collides = hittable = collidesTiles = false;
                        instantDisappear = collidesAir = true;
                        hitSound = Sounds.explosion;
                        hitEffect = NyfalisFxs.unitDischarge;

                        splashDamage = 65f;
                        rangeOverride = 30f;
                        splashDamageRadius = 55f;
                        buildingDamageMultiplier = speed = 0f;
                        intervalBullet = new HealOnlyBulletType(0,0) {{
                            spin = 3.6f;
                            drag = 0.9f;
                            lifetime = 10*60;
                            shrinkX = 25f/60f;
                            shrinkY = 35f/60f;
                            bulletInterval = 25;
                            intervalBullets = 2;
                            intervalSpread = 180;
                            intervalRandomSpread = 90;
                            height = width = healAmount = 20;

                            collidesTeam = true;
                            keepVelocity = false;
                            hitEffect = despawnEffect = Fx.heal;
                            backColor = frontColor = trailColor = lightColor = Pal.heal;

                            intervalBullet = new HealOnlyBulletType(4,-5, "olupis-diamond-bullet", false) {{
                                lifetime = 60;
                                trailLength = 10;
                                trailWidth = 1.5f;
                                healAmount = 20;
                                bulletInterval = 10;
                                homingPower = 0.09f;

                                collidesTeam = true;
                                keepVelocity = false;
                                hitEffect = despawnEffect = Fx.heal;
                                backColor = frontColor = trailColor = lightColor = Pal.heal.a(0.4f);
                            }};
                        }};
                    }};
                }},
                new Weapon(){{
                    top = false;
                    reload = 50f;
                    shootCone = 30f;
                    shootSound = Sounds.lasershoot;
                    x = y = shootX = inaccuracy = 0f;
                    bullet = new LaserBoltBulletType(6f, 5){{
                        lifetime = 15f;
                        healPercent = 1f;
                        homingPower = 0.03f;
                        buildingDamageMultiplier = 0.01f;
                        collidesTeam = true;
                        backColor = Pal.heal;
                        frontColor = Color.white;
                    }};
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
            pathCost = NyfalisPathfind.costLeggedNaval;
            ammoType = new PowerAmmoType(1000);
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
                            collidesTiles = collides = hittable = false;
                            hitSound = Sounds.explosion;
                            hitEffect = NyfalisFxs.unitDischarge;

                            rangeOverride = 30f;
                            splashDamage = 70f;
                            splashDamageRadius = 55f;
                            speed = buildingDamageMultiplier = 0f;
                            intervalBullet = new HealOnlyBulletType(0,-5) {{
                                spin = 3.7f;
                                drag = 0.9f;
                                lifetime = 10*60;
                                shrinkX = 25f/60f;
                                shrinkY = 35f/60f;
                                bulletInterval = 30;
                                intervalBullets = 2;
                                intervalSpread = 180;
                                intervalRandomSpread = 90;
                                height = width = healAmount = 20;

                                fogVisible = true;
                                keepVelocity = false;
                                hitEffect = despawnEffect = Fx.heal;
                                backColor = frontColor = trailColor = lightColor = Pal.heal.a(0.4f);
                                intervalBullet = new HealOnlyBulletType(5,0, "olupis-diamond-bullet", false) {{
                                    lifetime = 60;
                                    trailLength = 11;
                                    trailWidth = 1.5f;
                                    healAmount = 30;
                                    bulletInterval = 10;
                                    homingPower = 0.11f;

                                    keepVelocity = false;
                                    hitEffect = despawnEffect = Fx.heal;
                                    backColor = frontColor = trailColor = lightColor = Pal.heal.a(0.7f);
                                }};
                            }};
                        }};
                    }};
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
            pathCost = NyfalisPathfind.costLeggedNaval;
            ammoType = new PowerAmmoType(1000);
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
                                collidesTiles = collides = hittable = false;
                                hitSound = Sounds.explosion;
                                hitEffect = NyfalisFxs.unitDischarge;

                                rangeOverride = 30f;
                                splashDamage = 70f;
                                splashDamageRadius = 55f;
                                speed = buildingDamageMultiplier = 0f;
                                intervalBullet = new HealOnlyBulletType(0,-5) {{
                                    spin = 3.7f;
                                    drag = 0.9f;
                                    lifetime = 10*60;
                                    shrinkX = 25f/60f;
                                    shrinkY = 35f/60f;
                                    bulletInterval = 60;
                                    intervalBullets = 3;
                                    intervalSpread = 180;
                                    intervalRandomSpread = 90;
                                    height = width = healAmount = 20;

                                    fogVisible = true;
                                    keepVelocity = false;
                                    hitEffect = despawnEffect = Fx.heal;
                                    backColor = frontColor = trailColor = lightColor = Pal.heal.a(0.4f);
                                    intervalBullet = new HealOnlyBulletType(10,0, "olupis-diamond-bullet", false) {{
                                        width = 9f;
                                        height = 13f;
                                        lifetime = 15;
                                        trailLength = 5;
                                        trailWidth = 2f;
                                        healAmount = 40;
                                        homingPower = 0.2f;
                                        splashDamageRadius = Vars.tilesize * 2;

                                        keepVelocity = false;
                                        backColor = frontColor = trailColor = lightColor = Pal.heal.a(0.7f);
                                        hitEffect = despawnEffect = NyfalisFxs.taurusHeal;
                                    }};
                                }};
                            }};
                        }};
                    }}
            );
        }};

        //diptera -> Flying unit that drops healing cluster bomb,  explode (w/ dmg) > split into 2 healing circles

        //added a death weapon
        //endregion
        //region Misc/Extra/Internal

        //Why do i exist? no reason, hope u don't cause any bugs even if you are one
        firefly = new NyfalisUnitType("firefly"){{
            constructor = UnitTypes.mono.constructor;
            ammoType = new PowerAmmoType(500);

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
            groundSpeed = 0.4f;
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
    }

    /*Common custom ammo types for the lifetime units*/
    public static void LoadAmmoType(){
        //Make them last long
        //TODO: refactor this

        lifeTimeDrill = new AmmoType() {
            @Override
            public String icon() {
                return Iconc.production + "";
            }

            @Override
            public Color color() {
                return Pal.ammo;
            }

            @Override
            public Color barColor() {
                return Color.green;
            }

            @Override
            public void resupply(Unit unit) {}
        };

        lifeTimeWeapon = new AmmoType() {
            @Override
            public String icon() {
                return Iconc.commandAttack + "";
            }

            @Override
            public Color color() {
                return Pal.accent;
            }

            @Override
            public Color barColor() {
                return Pal.ammo;
            }

            @Override
            public void resupply(Unit unit) {}
        };

        lifeTimeSupport = new AmmoType() {
            @Override
            public String icon() {
                return Iconc.add + "";
            }

            @Override
            public Color color() {
                return Pal.ammo;
            }

            @Override
            public Color barColor() {
                return Color.green;
            }

            @Override
            public void resupply(Unit unit) {}
        };

        carrierTypeAmmo = new AmmoType() {
            @Override
            public String icon() {
                return Iconc.itchio + "";
            }

            @Override
            public Color color() {
                return Pal.ammo;
            }

            @Override
            public Color barColor() {
                return Pal.ammo;
            }

            @Override
            public void resupply(Unit unit) {
                float ammoPerTier = 2.5f;
                float range = 90f + unit.hitSize;

                Unit carrier = Units.closest(unit.team, unit.x, unit.y, range,
                    u -> Arrays.stream(u.abilities).anyMatch(a -> a instanceof CarrierResupplyAblity)
                , UnitSorts.strongest);

                if(carrier != null){
                    int tier = 1;
                    for(Ability ability : carrier.abilities){
                        if(ability instanceof CarrierResupplyAblity owo && owo.tier > tier) tier = owo.tier;
                    }

                    Fx.itemTransfer.at(carrier.x, carrier.y, 15f , Pal.ammo, unit);
                    unit.ammo = Math.min(unit.ammo + ((ammoPerTier * tier)), unit.type.ammoCapacity);
                };

            }
        };
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
                w.reload *=2f;
                w.rotate = true;
                //TODO: Fireports?
                w.shootX =  w.x = w.shootY = w.y = 0;
                w.rotationLimit = 361f;
                w.rotateSpeed = Math.max(w.rotateSpeed, 20);
                if(w.alternate){
                    w.alternate = false;
                    Weapon ws = w.copy();
                    ws.shoot = w.shoot.copy();
                    ws.shoot.firstShotDelay = Math.max(w.shoot.firstShotDelay, 1) * (ws.reload * 0.5f);
                    buffer.add(ws);
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
