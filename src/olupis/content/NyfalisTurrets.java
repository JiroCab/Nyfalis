package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.world.blocks.defence.*;
import olupis.world.blocks.turret.*;
import olupis.world.consumer.*;
import olupis.world.entities.*;
import olupis.world.entities.bullets.*;
import olupis.world.entities.parts.*;

import static mindustry.Vars.headless;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;
import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisColors.*;
import static olupis.content.NyfalisItemsLiquid.*;
import static olupis.content.NyfalisUnits.*;

public class NyfalisTurrets {
    /*Rushie wanted dynamic content with this settings so this the pain she has made*/
    public static boolean cascadeAlt;
    public static Color cascadeColor = updateColor();
    public static Effect cascadeEffect = updateTrail();

    public static void LoadTurrets(){

        //region Turrets
        corroder = new NyfalisLiquidTurret("corroder"){{ //architronito
                targetAir = emitLight = true;

                size = 2;
                recoil = 1;
                shootY = 7f;
                range = 15 * 8f;
                health = 750;
                fogRadius = 13;
                shootCone = 50f;
                inaccuracy = 8.5f;
                rotateSpeed = 3f;
                lightRadius = 150;
                coolantMultiplier = 2.5f;
                liquidCapacity = reload = 5f;

                ammo(
                    Liquids.water, new LiquidBulletType(Liquids.water){{
                        status = StatusEffects.corroded;
                        layer = Layer.bullet -2f;
                        trailColor = hitColor;

                        speed = 5.5f;
                        drag = 0.008f;
                        damage = 10f;
                        pierceCap = 1;
                        lifetime = 9999f;
                        rangeChange = 15f;
                        ammoMultiplier = 1.5f;
                        trailInterval = trailParam = 1.5f;
                        buildingDamageMultiplier = 0.5f;

                        statusDuration = 60f * 2;
                    }},
                    steam, new NoBoilLiquidBulletType(steam){{
                            evaporatePuddles = pierce = true;
                            status = StatusEffects.corroded;
                            trailColor = hitColor;

                            speed = 8f;
                            drag = 0.009f;
                            lifetime = 9999f;
                            damage = 15f;
                            pierceCap = 3;
                            ammoMultiplier = 2.5f;
                            statusDuration = 60f * 5;
                            trailInterval = trailParam = 1.5f;
                            buildingDamageMultiplier = 0.5f;
                    }},
                    Liquids.slag, new LiquidBulletType(Liquids.slag){{
                        speed = 5.8f;
                        damage = 17;
                        pierceCap = 1;
                        drag = 0.0009f;
                        lifetime = 9999f;
                        rangeChange = 20f;
                        ammoMultiplier = 3f;
                        statusDuration = 60f * 2;
                        layer = Layer.bullet -2f;
                        hitSize = puddleSize = 7f;
                        trailInterval = trailParam = 1.5f;
                        buildingDamageMultiplier = 0.4f;

                        trailColor = hitColor;
                        status = StatusEffects.melting;
                    }},
                    emulsiveSlop, new LiquidBulletType(emulsiveSlop){{
                        speed = 5.8f;
                        damage = 13;
                        pierceCap = 1;
                        drag = 0.0009f;
                        lifetime = 9999f;
                        rangeChange = 20f;
                        ammoMultiplier = 3f;
                        statusDuration = 60f * 2;
                        layer = Layer.bullet -2f;
                        hitSize = puddleSize = 7f;
                        trailInterval = trailParam = 1.5f;
                        buildingDamageMultiplier = 0.4f;

                        trailColor = hitColor;
                        status = NyfalisStatusEffects.sloppy;
                    }}
                );
                drawer = new DrawTurret("iron-"){{
                    parts.addAll(
                        new RegionPart("-barrel"){{
                            mirror = false;
                            under = true;
                            progress = PartProgress.recoil;
                            moves.add(new PartMove(PartProgress.recoil, 0f, -3f, 0f));
                        }},  new RegionPart("-front-wing"){{
                            mirror = true;
                            under = true;
                            layerOffset = -0.1f;
                            progress = PartProgress.warmup;
                            moves.add(new PartMove(PartProgress.recoil, 0f, 0, -12f));
                        }}, new RegionPart("-back-wing"){{
                            mirror = true;
                            under = true;
                            layerOffset = -0.1f;
                            progress = PartProgress.smoothReload;
                            moves.add(new PartMove(PartProgress.recoil, 0f, -1f, 12f));
                        }}
                    );
                }};
                limitRange(0.5f);
                loopSound = Sounds.steam;
                consumePower(1f);
                lightColor = turretLightColor;
                outlineColor = nyfalisBlockOutlineColour;
                researchCost = with(rustyIron, 100, lead, 100);
                flags = EnumSet.of(BlockFlag.turret, BlockFlag.extinguisher);
                requirements(Category.turret, with(rustyIron, 40, lead, 20));

            }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.add(Stat.ammo, NyfalisStats.ammoWithInfo(ammoTypes, this));
            }
        };

        avenger = new AirPriorityItemTurret("avenger"){
            {
                targetAir = slowFogOfWar = emitLight = true;
                size = 3;
                recoil = 0;
                reload = 42f;
                health = 300;
                range = 34 * 8;
                lightRadius = 320;
                rotateSpeed = 0;
                shootCone = 360;
                inaccuracy = 180;
                minWarmup = 0.96f;
                shootY = shootX= 0f;
                coolantMultiplier = 2.5f;
                warmupMaintainTime = 1f;
                fogRadiusMultiplier = 0.75f;
                shootWarmupSpeed = 0.11f;

                final float groundPenalty = 0.05f, splashPenalty = 0.05f;
                ammo(
                    copper, new EffectivenessMissleType(4.8f, 20f){{
                        width = 6f;
                        shrinkX = 0;
                        lifetime = 70f;
                        height = 10.5f;
                        knockback = 0.4f;
                        splashDamage = 10f;
                        statusDuration = 120f;
                        homingPower = 0.1f;
                        homingRange = 34 * 8;
                        splashDamageRadius = 25f * 0.75f;
                        backColor = trailColor = copper.color;
                        collidesAir = collidesGround = flatDamage = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = StatusEffects.shocked;
                        groundDamageMultiplier = groundPenalty;
                        groundDamageSplashMultiplier = splashPenalty;
                    }},
                    lead, new EffectivenessMissleType(4.8f, 60f){{
                        width = 6f;
                        shrinkX = 0;
                        lifetime = 70;
                        height = 10.5f;
                        inaccuracy = 180; //180 (turret) + 180 (bullet) = 360 inaccuracy
                        knockback = 0.4f;
                        splashDamage = 10f;
                        statusDuration = 60f;
                        homingPower = 0.4f;
                        homingRange = 34 * 8;
                        splashDamageRadius = 25f * 0.75f;
                        backColor = trailColor = lead.color;
                        collidesAir = collidesGround = flatDamage = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = NyfalisStatusEffects.drained;
                        groundDamageMultiplier = groundPenalty;
                        groundDamageSplashMultiplier = splashPenalty;
                    }},
                    iron, new EffectivenessMissleType(5.2f, 80f){{
                        width = 6f;
                        shrinkX = 0;
                        lifetime = 70;
                        height = 10.5f;
                        inaccuracy = 180; //180 (turret) + 180 (bullet) = 360 inaccuracy
                        knockback = 0.4f;
                        splashDamage = 10f;
                        statusDuration = 60f;
                        homingPower = 0.04f;
                        homingRange = 34 * 8;
                        splashDamageRadius = 25f * 0.75f;
                        backColor = trailColor = iron.color;
                        collidesAir = collidesGround = flatDamage = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = StatusEffects.slow;
                        groundDamageMultiplier = groundPenalty;
                        groundDamageSplashMultiplier = splashPenalty;
                    }},
                    graphite, new EffectivenessMissleType(6f, 110f){{
                        width = 7f;
                        shrinkX = 0;
                        height = 11f;
                        lifetime = 60;
                        trailChance = 0;
                        trailLength = 3;
                        inaccuracy = 20; // more refined firing
                        knockback = 0.4f;
                        homingPower = 0.04f;
                        homingRange = 34 * 8;
                        backColor = trailColor = graphite.color;
                        collidesAir = collidesGround = flatDamage = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = Fx.hitBulletSmall;
                        groundDamageMultiplier = groundPenalty;
                        groundDamageSplashMultiplier = splashPenalty;
                    }},
                    cobalt, new EffectivenessMissleType(4.8f, 20f){{
                        width = 6f;
                        shrinkX = 0;
                        lifetime = 70;
                        height = 10.5f;
                        knockback = 0.4f;
                        inaccuracy = 180; //180 (turret) + 180 (bullet) = 360 inaccuracy
                        splashDamage = 10f;
                        statusDuration = 20f;
                        homingPower = 0.5f;
                        homingRange = 34 * 8;
                        splashDamageRadius = 25f * 0.75f;
                        backColor = trailColor = cobalt.color;
                        absorbable = false;
                        collidesAir =  collidesGround = flatDamage = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = NyfalisStatusEffects.corupt;
                        groundDamageMultiplier = groundPenalty;
                        groundDamageSplashMultiplier = splashPenalty;
                    }}
                );
                lightColor = floodLightColor;
                limitRange(3f);
                shootSound = Sounds.missile;
                shootEffect = Fx.blastsmoke;

                drawer = new DrawTurret(){{
                    parts.add(
                        new RegionPart("-door-bl"){{
                            progress = PartProgress.warmup;
                            moveX = moveY = -2.8f;
                            layerOffset = -2.8f;
                            mirror = outline = false;
                        }},
                        new RegionPart("-door-tl"){{
                            progress = PartProgress.warmup;
                            moveX = -2.8f;
                            moveY = 2.8f;
                            layerOffset = -2;
                            mirror = outline = false;
                        }},
                        new RegionPart("-door-br"){{
                            progress = PartProgress.warmup;
                            moveX = 2.8f;
                            moveY = -2.8f;
                            layerOffset = -2;
                            mirror = outline = false;
                        }},
                        new RegionPart("-door-tr"){{
                            progress = PartProgress.warmup;
                            moveX = moveY = 2.8f;
                            layerOffset = -2;
                            mirror = outline = false;
                        }}
                    );

                }};
                researchCost = with(lead, 100, rustyIron, 100);
                coolant = consume(new ConsumeLubricant(30f / 60f));
                requirements(Category.turret, with(rustyIron, 20, lead, 40));
            }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.add(Stat.ammo, NyfalisStats.ammoWithInfo(ammoTypes, this));
            }
        };

        slash = new NyfalisPowerTurret("slash"){{
            reload = 6;
            shootY = 11;
            inaccuracy = 0.5f;
            rotateSpeed = 3f;
            minWarmup = 0.9f;
            smokeEffect = shootEffect =  Fx.none;

            shootType = new ExplosionBulletType(40, 13.4f){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = false;
                fragBullets = 8;
                fragSpread = 360;
                fragRandomSpread = 0;
                fragBullet = new BulletType(){{
                    speed = 3;
                    damage = 0;
                    lifetime = 2;
                    knockback = 0.8f;
                    hitSoundVolume = 0.5f;
                    trailEffect = despawnEffect = smokeEffect = shootEffect =  Fx.none;
                    hitEffect =  Fx.hitFlameSmall;
                    collidesAir = false;
                    hitSound = NyfalisSounds.sawCollision;
                }};
            }};
            drawer = new DrawTurret("iron-"){{
                targetAir = false;
                emitLight = true;

                size = 2;
                recoil = 0;
                armor = 2f;
                range = 20f;
                health = 3000;
                fogRadius = 13;
                lightRadius = 37;
                shootCone = 180f;
                liquidCapacity = 5f;
                coolantMultiplier = 3f;

                parts.addAll(
                        new RegionPart("-buzzsaw"){{
                            layer = Layer.legUnit + 0.1f;
                            mirror = false;
                            under = true;
                            progress = PartProgress.warmup;
                            y = 4;
                            moveY = 8;
                            moves.add(new PartMove(PartProgress.reload.sustain(0,10,20), 0, 0, 360f));
                        }}
                );
            }};
            shootSound = Sounds.none;
            lightColor = turretLightColor;
            outlineColor = nyfalisBlockOutlineColour;
            loopSound = NyfalisSounds.sawActiveLoop;
            coolant = consume(new ConsumeLubricant(15f / 60f));
            consumePower(20f / 60f);
            researchCost = with(iron, 200, copper, 150);
            requirements(Category.turret, with(iron, 60, copper, 50));

        }
            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.remove(Stat.inaccuracy);
                stats.remove(Stat.reload);
                stats.remove(Stat.booster);
                stats.add(new Stat("olupis-damage", StatCat.function),shootType.splashDamage * (60 / reload) , StatUnit.perSecond);
                stats.add(Stat.booster, NyfalisStats.sawBoosters(reload, coolant.amount, coolantMultiplier, false, this::consumesLiquid));
            }
        };

        shredder = new NyfalisItemTurret("shredder"){{
            //TODO: check for clear path to unit
            targetAir = false;
            emitLight = true;

            size = 3;
            armor = 5;
            reload = 70f;
            range = 170;
            health = 750;
            shootCone = 15f;
            lightRadius = 200;
            rotateSpeed = 10f;
            coolantMultiplier = 6f;
            ammoUseEffect = Fx.casing1;
            researchCostMultiplier = 0.05f;
            shootY = (Vars.tilesize * size) - 10f;
            outlineColor = nyfalisBlockOutlineColour;

            limitRange(1f);
            lightColor = turretLightColor;
            drawer = new DrawTurret("iron-");
            shoot = new ShootSpread(3, 15);
            smokeEffect = Fx.shootSmokeSquareSparse;
            shootSound = NyfalisSounds.cncZhBattleMasterWeapon;
            researchCost = with(lead, 1000, iron, 850, graphite, 850, copper, 1000);
            requirements(Category.turret, with(iron, 100, lead, 20, graphite, 20, copper, 30));
            coolant = consume(new ConsumeLubricant(15f / 60f));
            ammo(
                rustyIron, new RollBulletType(3.5f, 38){{
                    status = StatusEffects.slow;
                    collidesAir = false;
                    height = 9f;
                    width = 40f;
                    lifetime = 60f;
                    knockback= 3f;
                    homingRange = 100;
                    homingPower = 0.2f;
                    reloadMultiplier = 1.1f;
                    statusDuration = 60f * 1.1f;
                    buildingDamageMultiplier = 0.4f;
                    ammoMultiplier = pierceCap = 2;
                    shootEffect = smokeEffect = Fx.none;
                    frontColor = new Color().set(rustyIron.color).lerp(Pal.bulletYellowBack, 0.3f);
                    backColor = new Color().set(rustyIron.color).lerp(Pal.bulletYellow, 0.3f);
                }},
                iron, new RollBulletType(4f, 42){{
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
                    buildingDamageMultiplier = 0.4f;
                    shootEffect = smokeEffect = Fx.none;
                    frontColor = new Color().set(iron.color).lerp(Pal.bulletYellowBack, 0.1f);
                    backColor = new Color().set(iron.color).lerp(Pal.bulletYellow, 0.2f);
                }},
                quartz, new RollBulletType(4.1f, 46){{
                    status = StatusEffects.slow;
                    collidesAir = false;
                    width = 40f;
                    height = 11f;
                    lifetime = 50f;
                    fragBullets = 4;
                    knockback = 3.5f;
                    ammoMultiplier = 2;
                    homingPower = 0.2f;
                    homingRange = 100f;
                    reloadMultiplier = 0.9f;
                    statusDuration = 60f * 2f;
                    buildingDamageMultiplier = 0.35f;
                    fragBullet = new BasicBulletType(3f, 14, "bullet"){{
                        width = 5f;
                        height = 12f;
                        shrinkY = 1f;
                        lifetime = 20f;
                        despawnEffect = Fx.none;
                        frontColor = quartz.color;
                        backColor = Pal.gray;
                    }};
                    shootEffect = smokeEffect = Fx.none;
                    backColor = new Color().set(quartz.color).lerp(Pal.bulletYellowBack, 0.1f);
                    frontColor = new Color().set(quartz.color).lerp(Pal.bulletYellow, 0.3f);
                }},
                silicon, new RollBulletType(4.5f, 100){{
                    status = StatusEffects.slow;
                    collidesAir = ricochetHoming = false;
                    artilleryTrail = true;
                    width = 40f;
                    height = 11f;
                    lifetime = 50f;
                    knockback = 4.5f;
                    ammoMultiplier = 2;
                    homingPower = 0.3f;
                    artilleryTrailSize = 2;
                    homingRange = 50f;
                    reloadMultiplier = 1.15f;
                    statusDuration = 60f * 2f;
                    buildingDamageMultiplier = 0.35f;
                    shootEffect = smokeEffect = Fx.none;
                    trailEffect = Fx.artilleryTrail;
                    backColor = new Color().set(silicon.color).lerp(Pal.bulletYellowBack, 0.1f);
                    frontColor = new Color().set(silicon.color).lerp(Pal.bulletYellow, 0.3f);
                }},
                graphite, new BarrelBulletType(5f, 70, "bullet"){{
                    artilleryTrail = false;
                    status = StatusEffects.slow;
                    width = 35f;
                    height = 16f;
                    lifetime = 50f;
                    trailLength = 3;
                    trailWidth = 5f;
                    trailChance = 0;
                    knockback = 5f;
                    statusDuration = 60f * 2f;
                    buildingDamageMultiplier = 0.35f;
                    reloadMultiplier = 0.85f;
                    shootEffect = smokeEffect = Fx.none;
                    trailColor = graphite.color;
                    backColor = new Color().set(graphite.color).lerp(Pal.bulletYellowBack, 0.1f);
                    frontColor = new Color().set(graphite.color).lerp(Pal.bulletYellow, 0.3f);
                }}
            );
            limitRange();
        }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.add(Stat.ammo, NyfalisStats.ammoWithInfo(ammoTypes, this));
            }
        };

        //TODO: Mixed items can stop firing at all
        strata = new NyfalisItemTurret("strata"){{

            ammo(
                iron, new BasicBulletType(0,0){{
                    lifetime = 0;
                    fragBullets = 8;
                    fragRandomSpread = 10;
                    fragSpread = 40;
                    fragBullet = new BulletType(){{
                        damage = 0;
                        lifetime = 250;
                        scaleLife = true;
                        collidesAir = collidesGround = false;
                        hitColor = trailColor = iron.color;
                        trailWidth = 1f;
                        trailLength = 4;
                        hitEffect = cascadeEffect;

                        despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                            sizeTo = 3.75f;
                            colorFrom = colorTo = iron.color;
                            lifetime = 2f;
                        }});
                        trailRotation = true;
                        trailEffect = Fx.disperseTrail;
                        fragBullets = 1;
                        fragRandomSpread = 0;
                        fragSpread = 360;
                        fragBullet = new MineBulletType(NyfalisBlocks.heavyMine,Fx.ballfire, 0.80f);
                    }};
                }},
                cobalt, new BasicBulletType(0,0){{
                    lifetime = 0;
                    fragBullets = 12;
                    fragRandomSpread = 10;
                    fragSpread = 40;
                    fragBullet = new BulletType(){{
                        damage = 0;
                        lifetime = 250;
                        scaleLife = true;
                        collidesAir = collidesGround = false;
                        hitColor = trailColor = cobalt.color;
                        trailWidth = 1f;
                        trailLength = 4;
                        hitEffect = cascadeEffect;

                        despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                            sizeTo = 3.75f;
                            colorFrom = colorTo = cobalt.color;
                            lifetime = 2f;
                        }});
                        trailRotation = true;
                        trailEffect = Fx.disperseTrail;
                        fragBullets = 1;
                        fragRandomSpread = 0;
                        fragSpread = 360;
                        fragBullet = new MineBulletType(NyfalisBlocks.glitchMine,Fx.ballfire, 0.55f);
                    }};
                }},
                quartz, new BasicBulletType(0,0){{
                    lifetime = 0;
                    fragBullets = 10;
                    fragRandomSpread = 10;
                    fragSpread = 40;
                    fragBullet = new BulletType(){{
                        damage = 0;
                        lifetime = 250;
                        scaleLife = true;
                        collidesAir = collidesGround = false;
                        hitColor = trailColor = quartz.color;
                        trailWidth = 1f;
                        trailLength = 4;
                        hitEffect = cascadeEffect;

                        despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                            sizeTo = 3.75f;
                            colorFrom = colorTo = quartz.color;
                            lifetime = 2f;
                        }});
                        trailRotation = true;
                        trailEffect = Fx.disperseTrail;
                        fragBullets = 1;
                        fragRandomSpread = 0;
                        fragSpread = 360;
                        fragBullet = new MineBulletType(NyfalisBlocks.fragMine,Fx.ballfire, 0.65f);
                    }};
                }},
                condensedBiomatter, new BasicBulletType(0,0){{
                    lifetime = 0;
                    fragBullets = 20;
                    fragRandomSpread = 10;
                    fragSpread = 40;
                    fragBullet = new BulletType(){{
                        damage = 0;
                        lifetime = 250;
                        scaleLife = true;
                        collidesAir = collidesGround = false;
                        hitColor = trailColor = condensedBiomatter.color;
                        trailWidth = 1f;
                        trailLength = 4;
                        hitEffect = cascadeEffect;

                        despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                            sizeTo = 3.75f;
                            colorFrom = colorTo = condensedBiomatter.color;
                            lifetime = 2f;
                        }});
                        trailRotation = true;
                        trailEffect = Fx.disperseTrail;
                        fragBullets = 1;
                        fragRandomSpread = 0;
                        fragSpread = 360;
                        fragBullet = new MineBulletType(NyfalisBlocks.mossMine,Fx.ballfire, 0.50f);
                    }};
                }}
            );
            drawer = new DrawTurret(){{
                targetAir = false;
                shootCone = 360;
                inaccuracy = 0;
                size = 3;
                recoil = 0;
                lockRotation = true;
                rotateSpeed = 0;
                rotateDraw = false;
                shootY = 0;
                range = 320f;
                predictTarget = true;
                health = 1500;
                fogRadius = 13;
                coolantMultiplier = 7.5f;
                reload = 12*16;
                parts.addAll(
                new RegionPart("-piston"){{
                    layerOffset = 3;
                    mirror = outline = false;
                    under = true;
                    growProgress = PartProgress.reload.inv();
                    growX = growY = 0.2f;
                }}
                );
            }};
            ammoPerShot = 24;
            loopSound = Sounds.release;
            outlineColor = nyfalisBlockOutlineColour;
            researchCost = with(iron, 500, copper, 500, silicon, 300, quartz, 500);
            coolant = consume(new ConsumeLubricant(35f / 60f));
            requirements(Category.turret, with(iron, 100, copper, 150, silicon, 50, quartz, 100));

        }
            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.add(Stat.ammo, NyfalisStats.ammoBlocksOnly(ammoTypes, this));
            }
        };

        aegis = new AirPriorityItemTurret("aegis"){
            {
                targetAir = slowFogOfWar = targetGround = emitLight = true;
                size = 3;
                shootY = 5;
                shootX = 0f;
                reload = 120;
                range = 50f * 8f;
                lightRadius = 450;
                inaccuracy = 5;
                minWarmup = 0.8f;
                fogRadiusMultiplier = 0.75f;
                shootWarmupSpeed = 0.05f;

                final float airGroundPend = 1.5f, airBuildPend = 0.5f;
                ammo(
                    copper, new EffectivenessMissleType(6f, 10f) {{
                        width = 6f;
                        reloadMultiplier = 1.6f;
                        shrinkX = 0;
                        lifetime = 70f;
                        height = 10.5f;
                        knockback = 0.8f;
                        splashDamage = 2f;
                        ammoMultiplier = 1;
                        statusDuration = 120f;
                        homingPower = 0.4f;
                        homingRange = 150f;
                        homingDelay = 20;
                        splashDamageRadius = 25f * 0.75f;
                        frontColor = trailColor = copper.color;
                        collidesAir = collidesGround = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = StatusEffects.shocked;
                    }},
                    lead, new EffectivenessMissleType(3f, 15f) {{
                        width = 6f;
                        reloadMultiplier = 2f;
                        shrinkX = 0;
                        lifetime = 140f;
                        height = 10.5f;
                        knockback = 0.8f;
                        splashDamage = 5f;
                        statusDuration = 120f;
                        homingPower = 0.4f;
                        homingRange = 150f;
                        homingDelay = 20;
                        ammoMultiplier = 1;
                        splashDamageRadius = 25f * 0.75f;
                        frontColor = trailColor = lead.color;
                        collidesAir = collidesGround = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = NyfalisStatusEffects.drained;
                        groundDamageMultiplier = 0.8f;
                        buildingDamageMultiplier = airBuildPend;
                    }},

                    rustyIron, new EffectivenessMissleType(3f, 8f) {{
                        width = 6f;
                        reloadMultiplier = 2f;
                        shrinkX = 0;
                        lifetime = 140f;
                        height = 10.5f;
                        knockback = 0.8f;
                        splashDamage = 2.5f;
                        ammoMultiplier = 1;
                        statusDuration = 120f;
                        homingPower = 0.4f;
                        homingRange = 150f;
                        homingDelay = 20;
                        splashDamageRadius = 25f * 0.75f;
                        frontColor = trailColor = rustyIron.color;
                        collidesAir = collidesGround = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = StatusEffects.corroded;
                        groundDamageMultiplier = airGroundPend;
                        buildingDamageMultiplier = airBuildPend;
                    }},
                    cobalt, new EffectivenessMissleType(8f, 15f) {{
                        width = 6f;
                        shrinkX = 0;
                        lifetime = 140f;
                        height = 10.5f;
                        knockback = 0.8f;
                        splashDamage = 2.5f;
                        statusDuration = 120f;
                        homingPower = 0.4f;
                        homingRange = 150f;
                        homingDelay = 20;
                        splashDamageRadius = 25f * 0.75f;
                        frontColor = trailColor = cobalt.color;
                        collidesAir = collidesGround = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        status = NyfalisStatusEffects.corupt;
                        groundDamageMultiplier = airGroundPend;
                        buildingDamageMultiplier = airBuildPend;
                    }},
                    graphite, new EffectivenessMissleType(9f, 30f) {{
                        //no bonus to air or ground
                        width = 6f;
                        shrinkX = 0;
                        lifetime = 140f;
                        height = 11.5f;
                        trailChance = 0;
                        trailLength = 2;
                        knockback = 1.5f;
                        splashDamage = 3f;
                        homingPower = 0.6f;
                        homingRange = 100f;
                        homingDelay = 10;
                        splashDamageRadius = 20f * 0.75f;
                        frontColor = trailColor = graphite.color;
                        collidesAir = collidesGround = true;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = NyfalisFxs.hollowPointHit;
                        buildingDamageMultiplier = airBuildPend;
                    }}
                );
                drawer = new DrawTurret("iron-"){{
                    parts.add(new RegionPart("-core"){{
                        heatProgress = PartProgress.recoil;
                        recoilIndex = 1;
                        mirror = false;
                        under = true;
                        layerOffset = 1;
                        outlineLayerOffset = -1;
                        children.addAll(
                        new RegionPart("-core-barrel"){{
                            progress = PartProgress.recoil;
                            recoilIndex = 1;
                            mirror = false;
                            under = false;
                            moveY = -1f;
                            moveX = 0f;
                            layerOffset = 0.5f;
                            heatLayerOffset = 3f;
                        }},
                        new RegionPart("-frame"){{
                            mirror = false;
                            under = true;
                            growProgress = PartProgress.warmup.delay(0.3f);
                            growX = 0.5f;
                            layerOffset = 0.5f;
                            heatLayerOffset = 3f;
                            moveY = 0f;
                            moveX = 0f;
                        }},
                        new RegionPart("-side-l"){{
                            recoilIndex = 0;
                            progress = PartProgress.warmup.delay(0.6f);
                            mirror = false;
                            under = false;
                            moveY = 0f;
                            moveRot = -2.5f;
                            layerOffset = 0.5f;
                            heatLayerOffset = 3f;
                            outlineLayerOffset = -0.4f;
                            moveX = 3f;

                            moves.add(new PartMove(PartProgress.recoil, 0.25f, -0.5f, 0f));
                            children.add(new RegionPart("-side-barrel-l"){{
                                             recoilIndex = 0;
                                             progress = PartProgress.recoil;
                                             mirror = false;
                                             under = false;
                                             layerOffset = 0.5f;
                                             heatLayerOffset = 3f;
                                             moveY = -1f;
                                         }},
                            new RegionPart("-side-heatsink-l"){{
                                recoilIndex = 0;
                                progress = PartProgress.warmup.delay(0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                                mirror = false;
                                under = false;
                                layerOffset = 0.3f;
                                heatLayerOffset = 3f;
                                moveY = -0.9f;
                                moveX = 0.9f;
                                moveRot = 0;
                            }});
                        }},
                        new RegionPart("-side-r"){{
                            recoilIndex = 2;
                            progress = PartProgress.warmup.delay(0.6f);
                            mirror = false;
                            under = false;
                            moveY = 0f;
                            moveRot = 2.5f;
                            layerOffset = 0.5f;
                            heatLayerOffset = 3f;
                            outlineLayerOffset = -0.4f;
                            moveX = -3f;

                            moves.add(new PartMove(PartProgress.recoil, -0.25f, -0.5f, 0f));
                            children.add(new RegionPart("-side-barrel-r"){{
                                             recoilIndex = 1;
                                             progress = PartProgress.recoil;
                                             mirror = false;
                                             under = false;
                                             layerOffset = 0.5f;
                                             heatLayerOffset = 3f;
                                             moveY = -1f;
                                             moveRot = 0;
                                         }},
                            new RegionPart("-side-heatsink-r"){{
                                recoilIndex = 2;
                                progress = PartProgress.warmup.delay(0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                                mirror = false;
                                under = false;
                                layerOffset = 0.3f;
                                heatLayerOffset = 3f;
                                moveY = -0.9f;
                                moveX = -0.9f;
                                moveRot = 0;
                            }});
                        }},
                        new RegionPart("-core-heatsink-l"){{
                            recoilIndex = 1;
                            progress = PartProgress.warmup.delay(0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                            mirror = false;
                            under = false;
                            layerOffset = 0.5f;
                            heatLayerOffset = 3f;
                            moveY = -0.75f;
                            moveX = -0.75f;
                            moveRot = 0;
                        }},
                        new RegionPart("-core-heatsink-r"){{
                            recoilIndex = 1;
                            progress = PartProgress.warmup.delay(0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                            mirror = false;
                            under = false;
                            layerOffset = 0.5f;
                            heatLayerOffset = 3f;
                            moveY = -0.75f;
                            moveX = 0.75f;
                            moveRot = 0;
                        }});
                    }});
                }};
                recoils = 3;
                shoot = new ShootBarrel(){{
                    shots = 6;
                    shotDelay = 6;
                    barrels = new float[]{
                    -9, 1, 0,
                    0, 3, 0,
                    9, 1, 0
                    };
                }};
                ammoPerShot = 20;
                maxAmmo = 220;
                shootSound = Sounds.missile;
                lightColor = floodLightColor;
                outlineColor = nyfalisBlockOutlineColour;
                shootEffect = Fx.shootSmallSmoke;
                researchCost = with(quartz, 3000 , iron, 1500, alcoAlloy, 1500, silicon, 1500);
                coolant = consume(new ConsumeLubricant(30f / 60f));
                coolantMultiplier = 2.2f;
                requirements(Category.turret, with(iron, 100, quartz, 200, alcoAlloy, 75, silicon, 75));
                limitRange(1f);
            }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.add(Stat.ammo, NyfalisStats.ammoWithInfo(ammoTypes, this));
            }
        };

        laceration = new NyfalisPowerTurret("laceration"){{
            emitLight = true;
            reload = 8;
            shootY = 18;
            inaccuracy = 0.5f;
            rotateSpeed = 3f;
            minWarmup = 0.9f;
            smokeEffect = shootEffect =  Fx.none;
            shoot = new ShootAlternate(){{
                shots = barrels = 3;
                spread = 12;
                shotDelay = 0;
            }};

            shootType = new ExplosionBulletType(200, 10){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = false;
                fragBullets = 8;
                fragSpread = 360;
                fragRandomSpread = 0;
                fragBullet = new BasicBulletType(){{
                    speed = 3;
                    damage = 0;
                    lifetime = 8.5f;
                    knockback = 0.5f;
                    hitSoundVolume = 0.5f;
                    trailEffect = despawnEffect = smokeEffect = shootEffect = Fx.none;
                    hitEffect =  Fx.hitFlameSmall;
                    collidesAir = false;
                    hitSound = NyfalisSounds.sawCollision;
                }};
            }};
            drawer = new DrawTurret("iron-"){{
                targetAir = false;

                size = 4;
                recoil = 0;
                range = 45f;
                health = 6000;
                fogRadius = 13;
                shootCone = 180f;
                coolantMultiplier = 3.5f;
                liquidCapacity = 5f;

                parts.addAll(
                new RegionPart("-buzzsaw"){{
                    mirror = false;
                    under = true;
                    progress = PartProgress.warmup;
                    layerOffset = -0.1f;
                    x = -12;
                    y = 6.5f;
                    moveY = 13.5f;
                    layer = Layer.legUnit + 0.1f;
                    moves.add(new PartMove(PartProgress.reload.sustain(0,10,20), 0, 0, 360f));
                }},
                new RegionPart("-buzzsaw"){{
                    mirror = false;
                    under = true;
                    progress = PartProgress.warmup;
                    layerOffset = -0.001f;
                    y = 8;
                    moveY = 13;
                    layer = Layer.legUnit + 0.1f;
                    moves.add(new PartMove(PartProgress.reload.sustain(0,10,20), 0, 0, 360f));
                }},
                new RegionPart("-buzzsaw"){{
                    mirror = false;
                    under = true;
                    progress = PartProgress.warmup;
                    layerOffset = -0.1f;
                    x = 12;
                    y = 6.5f;
                    moveY = 13.5f;
                    layer = Layer.legUnit + 0.1f;
                    moves.add(new PartMove(PartProgress.reload.sustain(0,10,20), 0, 0, 360f));
                }}

                );
            }};
            loopSound = NyfalisSounds.sawActiveLoop;
            shootSound = Sounds.none;
            lightColor = turretLightColor;
            outlineColor = nyfalisBlockOutlineColour;
            coolant = consume(new ConsumeLubricant(30f / 60f));
            consumePower(2.5f);
            researchCost = with(iron, 4500, copper, 4500, quartz, 300, cobalt, 1000);
            requirements(Category.turret, with(iron, 150, copper, 200, quartz, 150, cobalt, 50 ));

        }
            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.remove(Stat.inaccuracy);
                stats.remove(Stat.reload);
                stats.remove(Stat.booster);
                stats.add(new Stat("olupis-damage", StatCat.function),(shootType.splashDamage * 3) * (60 / reload) , StatUnit.perSecond);
                stats.add(Stat.booster, NyfalisStats.sawBoosters(reload, coolant.amount, coolantMultiplier, false, this::consumesLiquid));
            }
        };

        porcupine = new NyfalisLiquidTurret("porcupine"){{
            //TODO: check for clear path to unit
            targetAir = false;
            emitLight = true;

            size = 3;
            armor = 5;
            reload = 90f;
            range = 8*80;
            health = 750;
            shootCone =inaccuracy= 25f;
            lightRadius = 200;
            rotateSpeed = 0.5f;
            coolantMultiplier = 6f;
            ammoUseEffect = Fx.casing1;
            researchCostMultiplier = 0.05f;
            shootY = (Vars.tilesize * size) - 10f;
            outlineColor = nyfalisBlockOutlineColour;
            lightColor = turretLightColor;
            shootSound = NyfalisSounds.barrelLaunch;
            researchCost = with(iron, 4500, copper, 4500, quartz, 3000, aluminum, 3000);
            requirements(Category.turret, with(iron, 100, copper, 100, quartz, 50, aluminum, 50));

            drawer = new DrawTurret("iron-"){{
                parts.addAll(
                        new RegionPart("-mid"){{
                            progress = PartProgress.recoil;
                            heatProgress = PartProgress.recoil;
                            heatColor = Pal.techBlue;
                            mirror = false;
                            under = true;
                            children.addAll(new RegionPart("-barrel") {{
                                progress = PartProgress.reload.curve(Interp.pow2In);

                                colorTo = new Color(1f, 1f, 1f, 0f);
                                color = Color.white;
                                mixColorTo = Pal.accent;
                                mixColor = new Color(1f, 1f, 1f, 0f);
                                outline = false;
                                under = true;
                                y = 2;



                                layerOffset = -0.2f;

                                moves.add(new PartMove(PartProgress.warmup.inv(), 0f, -4f, 0f));
                            }}, new RegionPart("-front-r"){{
                                mirror = false;
                                under = true;
                                layerOffset = -0.1f;
                                progress = PartProgress.recoil;
                                moveX = 1;
                                moveY = 1;
                                moveRot = 5;
                            }}, new RegionPart("-back-r"){{
                                mirror = false;
                                under = true;
                                layerOffset = -0.1f;
                                progress = PartProgress.smoothReload;
                                moveX = -4;
                                moveY = 4;
                                moves.add(new PartMove(PartProgress.recoil, 4f, -4f, 0));
                            }}, new RegionPart("-front-l"){{
                                mirror = false;
                                under = true;
                                layerOffset = -0.1f;
                                progress = PartProgress.recoil;
                                moveX = -1;
                                moveY = 1;
                                moveRot = -5;
                            }}, new RegionPart("-back-l"){{
                                mirror = false;
                                under = true;
                                layerOffset = -0.1f;
                                progress = PartProgress.smoothReload;
                                moveX = -4;
                                moveY = 4;
                                moves.add(new PartMove(PartProgress.recoil, 4f, -4f, 0));
                            }});
                        }}
                );
            }};

            ammo(
                    heavyOil, new BarrelBulletType(4f, 300){{
                        bounceOnEnemyWalls = collidesTiles = true;
                        max = 30;
                        height = 8f;
                        width = 16f;
                        lifetime = 400f;
                        knockback= 8f;
                        homingDelay = bounceDelay = 15;
                        homingPower = 0.3f;
                        homingRange = 50f;
                        buildingDamageMultiplier = 0.4f;
                        ammoMultiplier = 2;
                        trailInterval = trailParam = 1.5f;
                        shootEffect = smokeEffect = Fx.none;
                        frontColor = new Color().set(Pal.bulletYellowBack).lerp(heavyOil.color, 0.3f);
                        backColor = new Color().set(Pal.bulletYellow).lerp(heavyOil.color, 0.3f);
                        fragBullets = 1;
                        fragBullet = new FirePuddleBulletType(50,30){{
                            splashDelay = 10;
                            splashAmount = 16;
                        }};
                    }},
                    lubricant, new BarrelBulletType(4, 150){{
                        collidesTiles = true;
                        maxBounces = 5;
                        height = 8f;
                        width = 16f;
                        lifetime = 200f;
                        knockback= 2f;
                        homingDelay = bounceDelay = 15;
                        trailInterval = trailParam = 1.5f;
                        homingPower = 0.3f;
                        homingRange = 50f;
                        buildingDamageMultiplier = 0.8f;
                        ammoMultiplier = 2;
                        shootEffect = smokeEffect = Fx.none;
                        frontColor = new Color().set(Pal.bulletYellowBack).lerp(lubricant.color, 0.3f);
                        backColor = new Color().set(Pal.bulletYellow).lerp(lubricant.color, 0.3f);
                        fragBullets = 1;
                        fragBullet = new FirePuddleBulletType(50,60){{
                            splashDelay = 5;
                            splashAmount = 8;
                        }};
                    }}
            );
            limitRange(2f);
        }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.ammo);
                stats.add(Stat.ammo, NyfalisStats.ammoWithInfo(ammoTypes, this));
            }
        };

        hive = new ItemUnitTurret("hive"){{
            size = 4;
            shootY = 0f;
            range = 650;
            reload = 600f;
            maxAmmo  = 20;
            itemCapacity = 60;
            lightRadius = (size * 8) + 4;
            fogRadiusMultiplier = 0.5f;
            shootSound = Sounds.respawn;
            hasAlternate = false;

            ammo(
                lead, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    spawnUnit = flea;
                    unitRange = 650;
                }},
                iron, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    spawnUnit = mite;
                    unitRange = 650;
                }},
                silicon, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    unitRange = 650;
                    spawnUnit = lice;
                }},
                cobalt, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    spawnUnit = tick;
                    unitRange = 650;
                }}
            );
            playerControllable = drawOnTarget = emitLight = true;
            commandable = configurable = rallyAim = false;
            requiredAlternate = with();
            buildingFilter = b -> false; //dont
            lightColor = floodLightColor;
            requiredItems = with(copper, 10);
            researchCost = with(aluminum, 1500, silicon, 1500,  iron, 1500, copper, 1500);
            requirements(Category.turret, with(iron, 100, aluminum, 40, silicon, 40, copper, 40));
        }};

        cascade = new UnstablePowerTurret("cascade"){{
            String visName = (Core.settings.getBool("nyfalis-bread-gun") ? "PH-cascade" : "cascade");
            localizedName = Core.bundle.get(getContentType() + ".olupis-" + visName + ".name");
            outlineColor = NyfalisColors.contentOutline;
            details = Core.bundle.getOrNull(getContentType() + "." + visName + ".details");
            hideDetails = false;
            targetAir = targetGround = emitLight =true;
            size = 4;
            shootY = 2;
            shootX = 0f;
            reload = 120;
            lightRadius = 450;
            minWarmup = 0.8f;
            fogRadiusMultiplier = 0.75f;
            shootWarmupSpeed = 0.05f;
            range = (50f * 8f) * (cascadeAlt ? 4f : 1f);
            explosionRadius = 25 * (cascadeAlt ? 4f : 1f);
            explosionDamage = 1000 * (cascadeAlt ? 4f : 1f);
            consumePower(17f * (cascadeAlt ? 4f : 1f));
            
            shootType = new BasicBulletType(3f, 20f) {{
                sprite = "large-orb";
                width = 10f;
                height = 20f;
                hitSize = 8f;

                shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                    colorFrom = colorTo = outlineColor;
                    lifetime = 6f;
                    sizeTo = 10f;
                    strokeFrom = 3f;
                    strokeTo = 0.3f;
                }});
                smokeEffect = Fx.shootSmokeSmite;
                pierceCap = 4;
                pierce = true;
                pierceBuilding = true;
                hitColor = backColor = trailColor = cascadeColor;
                frontColor = Color.white;
                trailWidth = 2f;
                trailLength = 8;
                hitEffect = cascadeEffect;

                despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                    sizeTo = 15f;
                    colorFrom = colorTo = cascadeColor;
                    lifetime = 6f;
                }});
                trailRotation = true;
                trailEffect = Fx.disperseTrail;
                trailInterval = 6f;
                lifetime = 75f;
                knockback = 0.8f;
                collidesAir = collidesGround = true;
                buildingDamageMultiplier = 0.1f;
                intervalBullets = 2;
                intervalSpread = -30;
                intervalRandomSpread = 0;
                bulletInterval = 6;
                intervalBullet = new BasicBulletType(3f, 5f) {{
                    sprite = "large-orb";

                    width = 5f;
                    height = 10f;
                    hitSize = 4f;

                    shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                        colorFrom = colorTo = cascadeColor;
                        lifetime = 4f;
                        sizeTo = 5f;
                        strokeFrom = 3f;
                        strokeTo = 0.3f;
                    }});
                    smokeEffect = Fx.shootSmokeSmite;
                    pierceCap = 4;
                    pierce = true;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = cascadeColor;
                    frontColor = Color.white;
                    trailWidth = 1.5f;
                    trailLength = 6;
                    hitEffect = cascadeEffect;

                    despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                        sizeTo = 7.5f;
                        colorFrom = colorTo = cascadeColor;
                        lifetime = 4f;
                    }});
                    trailRotation = true;
                    trailEffect = Fx.disperseTrail;
                    trailInterval = 4f;
                    lifetime = 40f;
                    knockback = 0.6f;
                    collidesAir = collidesGround = true;
                    buildingDamageMultiplier = 0.1f;
                    intervalBullets = 2;
                    intervalSpread = -30;
                    intervalRandomSpread = 0;
                    bulletInterval = 4;
                    intervalBullet = new ConditionalIntervalBulletType(3f, 15f) {{
                        intervalCheck  = (b)-> cascadeAlt;
                        sprite = "large-orb";
                        width = 2.5f;
                        height = 5f;
                        hitSize = 2f;
                        homingPower = 0.4f;
                        homingRange = 150f;

                        shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                            colorFrom = colorTo = cascadeColor;
                            lifetime = 2f;
                            sizeTo = 2.5f;
                            strokeFrom = 3f;
                            strokeTo = 0.3f;
                        }});
                        smokeEffect = Fx.shootSmokeSmite;
                        pierceCap = 4;
                        pierce = true;
                        pierceBuilding = true;
                        hitColor = backColor = trailColor = cascadeColor;
                        frontColor = Color.white;
                        trailWidth = 1f;
                        trailLength = 4;
                        hitEffect = cascadeEffect;

                        despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                            sizeTo = 3.75f;
                            colorFrom = colorTo = cascadeColor;
                            lifetime = 2f;
                        }});
                        trailRotation = true;
                        trailEffect = Fx.disperseTrail;
                        trailInterval = 6f;
                        lifetime = 15f;
                        knockback = 0.6f;
                        collidesAir = collidesGround = true;
                        buildingDamageMultiplier = 0.1f;
                        intervalBullets = 2;
                        intervalSpread = -30;
                        intervalRandomSpread = 0;
                        bulletInterval = 6;

                        intervalBullet = new BasicBulletType(3f, 10f, "large-orb") {{
                            width = 5f;
                            height = 10f;
                            hitSize = 4f;

                            shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                                colorFrom = colorTo = cascadeColor;
                                lifetime = 4f;
                                sizeTo = 5f;
                                strokeFrom = 3f;
                                strokeTo = 0.3f;
                            }});
                            smokeEffect = Fx.shootSmokeSmite;
                            pierceCap = 4;
                            pierce = true;
                            pierceBuilding = true;
                            hitColor = backColor = trailColor = cascadeColor;
                            frontColor = Color.white;
                            trailWidth = 1.5f;
                            trailLength = 6;
                            hitEffect = cascadeEffect;

                            despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                                sizeTo = 7.5f;
                                colorFrom = colorTo = cascadeColor;
                                lifetime = 4f;
                            }});
                            trailRotation = true;
                            trailEffect = Fx.disperseTrail;
                            trailInterval = 6f;
                            lifetime = 25f;
                            knockback = 0.6f;
                            collidesAir = collidesGround = true;
                            buildingDamageMultiplier = 0.1f;
                            intervalBullets = 2;
                            intervalSpread = -30;
                            intervalRandomSpread = 0;
                            bulletInterval = 8;
                            intervalBullet = new BasicBulletType(3f, 15f, "large-orb") {{
                                width = 2.5f;
                                height = 5f;
                                hitSize = 2f;
                                homingPower = 0.4f;
                                homingRange = 150f;

                                shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                                    colorFrom = colorTo = cascadeColor;
                                    lifetime = 2f;
                                    sizeTo = 2.5f;
                                    strokeFrom = 3f;
                                    strokeTo = 0.3f;
                                }});
                                smokeEffect = Fx.shootSmokeSmite;
                                pierceCap = 4;
                                pierce = true;
                                pierceBuilding = true;
                                hitColor = backColor = trailColor = cascadeColor;
                                frontColor = Color.white;
                                trailWidth = 1f;
                                trailLength = 4;
                                hitEffect = cascadeEffect;

                                despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                                    sizeTo = 3.75f;
                                    colorFrom = colorTo = cascadeColor;
                                    lifetime = 2f;
                                }});
                                trailRotation = true;
                                trailEffect = Fx.disperseTrail;
                                trailInterval = 6f;
                                lifetime = 15f;
                                knockback = 0.6f;
                                collidesAir = collidesGround = true;
                                buildingDamageMultiplier = 0.1f;
                                intervalBullets = 2;
                                intervalSpread = -30;
                                intervalRandomSpread = 0;
                                bulletInterval = 8;
                                intervalBullet = new BasicBulletType(3f, 30f, "large-orb") {{
                                    width = 2.5f;
                                    height = 5f;
                                    hitSize = 2f;
                                    homingPower = 0.4f;
                                    homingRange = 150f;

                                    shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                                        colorFrom = colorTo = cascadeColor;
                                        lifetime = 2f;
                                        sizeTo = 2.5f;
                                        strokeFrom = 3f;
                                        strokeTo = 0.3f;
                                    }});
                                    smokeEffect = Fx.shootSmokeSmite;
                                    pierceCap = 4;
                                    pierce = true;
                                    pierceBuilding = true;
                                    hitColor = backColor = trailColor = cascadeColor;
                                    frontColor = Color.white;
                                    trailWidth = 1f;
                                    trailLength = 4;
                                    hitEffect = cascadeEffect;

                                    despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
                                        sizeTo = 3.75f;
                                        colorFrom = colorTo = cascadeColor;
                                        lifetime = 2f;
                                    }});
                                    trailRotation = true;
                                    trailEffect = Fx.disperseTrail;
                                    trailInterval = 6f;
                                    lifetime = 15f;
                                    knockback = 0.6f;
                                    collidesAir = collidesGround = true;
                                    buildingDamageMultiplier = 0.1f;
                                }};
                            }};
                        }};

                    }};
                }};
            }};

            drawer = new DrawUnstableTurret("iron-"){{
                parts.addAll(
                new UnstableRegionPart(""),

                    new UnstableRegionPart("-crest" +  "-l",  "-bread", b -> cascadeAlt){{
                        mirror = false;
                        moveRot = 25f;
                        moveX = -6;
                        under = true;
                        moves.add(new PartMove(UnstablePartProgress.reload, 8f, 0f, -5f));
                        layerOffset  = -1;
                        heatLayerOffset = -0.95f;

                        heatColor = cascadeColor;
                    }},

                    new UnstableRegionPart("-blade" + "-l", "-bread", b -> cascadeAlt){{
                        outlineLayerOffset = -2.99f;
                        layerOffset = 2;
                        mirror = false;
                        moveRot = 10f;
                        moveY = -0.8f;
                        moves.add(new PartMove(UnstablePartProgress.reload.shorten(0.5f), -1f, 0f, -7f));
                        under = true;

                        heatColor = cascadeColor;
                    }},
                    new UnstableRegionPart("-crest" + "-l", "-bread", b -> cascadeAlt){{
                        mirror = false;
                        moveRot = -175f;
                        moveX = 2;
                        moveY = 2f;
                        x = -1.5f;
                        layerOffset  = -0.5f;
                        moves.add(new PartMove(UnstablePartProgress.reload.shorten(0.5f), 0.5f, 2f, -120f));
                        under = true;
                        heatColor = cascadeColor;
                    }},
                    new UnstableRegionPart("-crest" +  "-r", "-bread", b -> cascadeAlt){{
                        mirror = false;
                        moveRot = -25f;
                        moveX = 6;
                        under = true;
                        moves.add(new PartMove(UnstablePartProgress.reload, -8f, 0f, 5f));
                        layerOffset  = -1;
                        heatLayerOffset = -0.95f;

                        heatColor = cascadeColor;
                    }},
                    new UnstableRegionPart("-blade"+ "-r", "-bread", b -> cascadeAlt){{
                        outlineLayerOffset = -2.99f;
                        layerOffset = 2;
                        mirror = false;
                        moveRot = -10f;
                        moveY = -0.8f;
                        moves.add(new PartMove(UnstablePartProgress.reload.shorten(0.5f), 1f, 0f, 7));
                        under = true;
                        heatColor = cascadeColor;
                    }},
                    new UnstableRegionPart("-crest"  +  "-r", "-bread", b -> cascadeAlt){{
                            mirror = false;
                            moveRot = 175f;
                            moveY = 2f;
                            moveX = -2;
                            x = 1.5f;
                            layerOffset  = -0.5f;
                            moves.add(new PartMove(UnstablePartProgress.reload.shorten(0.5f), -0.5f, 2f, 120f));
                            under = true;
                            heatColor = cascadeColor;
                        }}
                );

            }};
            shootSound = Sounds.shootSmite;
            lightColor = floodLightColor;
            shootEffect = new MultiEffect(Fx.shootPayloadDriver, NyfalisFxs.fastSquareSmokeCloud);
            researchCost = with(iron, 45000 , aluminum, 3000, cobalt, 3000, quartz, 4500, silicon, 3000);
            coolant = consumeCoolant(2 ,true,true);
            liquidCapacity = 120;
            coolantMultiplier = 0.4f;
            requirements(Category.turret, with(iron, 200 * (cascadeAlt ? 4f : 1f), aluminum, 200 * (cascadeAlt ? 4f : 1f), cobalt, 150 * (cascadeAlt ? 4f : 1f), quartz, 150 * (cascadeAlt ? 4f : 1f), silicon, 150 * (cascadeAlt ? 4f : 1f)));
            heatColor = cascadeColor;
            cooldownTime = 240;
        }};



        //endregion
        //region Env Hazzards
        boomPuffPassive = new PowerTurret("non-reproductive-boompuff"){{
            alwaysShooting = true;
            inaccuracy =  360;
            playerControllable = logicConfigurable = false;
            var T = this;

            shootType = new ExplosionBulletType(200,100){{
                shootEffect = NyfalisFxs.smolPorpolKaboom;
                killShooter = false;
            }};

            solid = createRubble = rebuildable = false;

            breakEffect = Fx.breakProp;
            breakSound = Sounds.plantBreak;


            drawer = new DrawRegion("");
            hasShadow = false;
            drawTeamOverlay = false;
            size = 1;
            shootY = 0;
            range = 0;
            health = 1;
            reload = 60 * 25;
            shootSound = Sounds.none;

        }};
        boomPuffActive = new PowerTurret("reproductive-boompuff"){{
            alwaysShooting = true;
            inaccuracy =  360;
            playerControllable = logicConfigurable = false;
            var T = this;

            shootType = new ExplosionBulletType(50,100){{
                shootEffect = NyfalisFxs.smolPorpolKaboom;
                killShooter = true;
                fragBullets = 8;
                fragRandomSpread = 0;
                fragSpread = 45;
                fragBullet = new BulletType(){{
                    damage = 0;
                    knockback = 2f;
                    speed = 3;
                    lifetime = 10;
                    trailEffect = despawnEffect = smokeEffect = shootEffect = Fx.none;
                    hitEffect =  Fx.none;
                    collidesAir = true;
                    status = StatusEffects.sapped;
                    statusDuration = 240;
                    fragBullets = 1;
                    fragRandomSpread = 0;
                    fragSpread = 45;
                    fragBullet = new BulletType(3,0){{

                        trailLength = 2;
                        trailColor = Items.sporePod.color;
                        trailEffect = Fx.sporeSlowed;
                        despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                        lifetime = 100;
                        collidesAir = collidesGround = false;
                        fragBullets = 1;
                        fragRandomSpread = fragSpread = 0;
                        fragBullet = new VariantBulletType(){{
                            variants.addAll(
                                    new MineBulletType(boomPuffPassive,Fx.sporeSlowed,0.60f),
                                    new MineBulletType(boomPuffPassive,Fx.sporeSlowed,0.60f),
                                    new MineBulletType(boomPuffPassive,Fx.sporeSlowed,0.60f),
                                    new MineBulletType(boomPuffPassive,Fx.sporeSlowed,0.60f),
                                    new MineBulletType(T,Fx.sporeSlowed,0.40f)
                            );
                        }};
                    }};
                }};
            }};

            solid = createRubble = rebuildable = false;

            breakEffect = Fx.breakProp;
            breakSound = Sounds.plantBreak;


            drawer = new DrawRegion("");
            hasShadow = false;
            drawTeamOverlay = false;
            size = 1;
            shootY = 0;
            range = 0;
            health = 1;
            reload = 60 * 20;
            shootSound = Sounds.none;

        }};
        //endregion
        //region Legacy
        dissolver = new LegacyBlock("dissolver"){ //architonnerre
            public void removeSelf(Tile tile){
                tile.remove();
                tile.setNet(rustyWallHuge, tile.team(), 0);
            }
        };

        //A recursive mortar turret that shoots long ranged recursive shells at the enemy (Has Really low rate of fire, high range, shells explode into multiple more shells on impact)
        obliterator = new LegacyBlock("obliterator"){
            public void removeSelf(Tile tile){
                tile.remove();
                tile.setNet(rustyWallGigantic, tile.team(), 0);
            }
        };
        //endregion

        //TODO: Escalation - A early game rocket launcher that acts similarly to the scathe but with lower range and damage. (Decent rate of fire, weak against high health single targets, slow moving rocket, high cost but great AOE)
        //TODO:Shatter - A weak turret that shoots a spray of glass shards at the enemy. (High rate of fire, low damage, has pierce, very low defense, low range)
    }

    public static void dynamicTurretContent(){

        cascade.consumePower(17 * (cascadeAlt ? 4f : 1f));
        cascade.range = (55f * 8f) * (cascadeAlt ? 2f : 1f);
        cascade.explosionRadius = 25 * (cascadeAlt ? 4f : 1f);
        cascade.explosionDamage = 1000 * (cascadeAlt ? 4f : 1f);
        cascade.limitRange(-1f);
        if(!headless)dynamicTurretContentClient();
    }

    public static void dynamicTurretContentClient(){
        String cascadeName = cascadeAlt ? "PH-cascade" : "cascade";
        cascade.region = Core.atlas.find("olupis-" + cascadeName, "olupis-cascade");
        cascade.uiIcon = cascade.fullIcon = Core.atlas.find("olupis-" + cascadeName + "-ui");

        cascade.localizedName = Core.bundle.get(cascade.getContentType() + ".olupis-"  + cascadeName + ".name");
        cascade.details = Core.bundle.getOrNull(cascade.getContentType() + ".olupis-" + cascadeName + ".details");

        cascadeColor = updateColor();
        cascadeEffect = updateTrail();
    }


    public static Color updateColor(){
        return cascadeAlt ? Color.brown : Color.blue;
    }

    public static Effect updateTrail(){
        if(cascadeAlt) return Fx.none;
        return new MultiEffect(Fx.disperseTrail, NyfalisFxs.hollowPointHit);
    }
}
