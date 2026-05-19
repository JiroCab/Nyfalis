package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.Interp.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import olupis.world.blocks.environment.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Draw.rect;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static olupis.content.NyfalisItemsLiquid.rustyIron;

public class NyfalisFxs extends Fx {
    BounceOut bounceOutTwo = new BounceOut(2);

    public static final Effect
        //Trail Effects
        trailStrataBulletMine = new Effect(13, e -> {
            color(Color.white, e.color, e.fin());
            stroke(0.6f + e.fout() * 1.7f);
            rand.setSeed(e.id);

            for(int i = 0; i < 2; i++){
                float rot = e.rotation + 180f;
                v.trns(rot, e.fin() * 19f);
                lineAngle(e.x + v.x, e.y + v.y, rot, e.fout() * 5 + 1.5f);
            }

        }),

        // Interval Effects
        intervalGnatBullCharge = new Effect(80f, 100f, e -> {
            color(Pal.heal, e.fout());
            stroke(e.fin() * 2f);
            randLenVectors(e.id, 2 ,  3, 30f * e.fin(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout()  + (1.4f * e.fout()));
                Drawf.light(e.x + x, e.y + y, e.fout() * 3f, Pal.heal, 0.15f );
            });
            color();
            Drawf.light(e.x, e.y, e.fin() * 5f, Pal.heal, 0.2f);
        }).followParent(true).rotWithParent(true).layer(Layer.bullet),

        // StatusEffect Effects
        statusOverTuned = new Effect(20f, e -> {
        color(e.color);

        randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) -> {
            Fill.poly(e.x, e.y, 3, e.fout() * 2.3f + 0.5f);
        });
    }),

        //Environment Effects
        envOilyFlame = new Effect(60f, e -> {
                color(Color.valueOf("912a13"), Pal.darkFlame, e.fin());

                randLenVectors(e.id, 3, 5f + e.fin() * 5f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.6f + e.fslope() * 3f);
                });

                color();

                Drawf.light(e.x, e.y, 40f * e.fslope(), Color.valueOf("806f2c"), 2f);
            }),

        envLubeFlame = new Effect(60f, e -> {
            color(Color.valueOf("806f2c"), Pal.lightFlame, e.fin());

            randLenVectors(e.id, 8, 3f + e.fin() * 4f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.5f + e.fslope() * 3f);
            });

            color();

            Drawf.light(e.x, e.y, 40f * e.fslope(), Color.valueOf("ffb547"), 2f);
        }),

        envMossSpread = new Effect(25f, e -> {
            color(e.color);
            randLenVectors(e.id, e.fin(), Mathf.random(4, 7), 5f, (x, y, fin, fout) -> {
                alpha((0.5f - Math.abs(fin - 0.5f)) * 2f);
                Fill.circle(e.x + x, e.y + y, fout * fin);
            });
        }).layer(Layer.debris),

        envTransgenderTreeLeafEffect =  new Effect(450f, e ->{
            //stolen from: https://github.com/ItsKirby69/MineDusty/blob/master/src/minedusty/content/DustyEffects.java#L263
            if(Vars.world.tileWorld(e.x, e.y).block() instanceof TrasngenderTreeBlock trans){
                Tile tile = Vars.world.tileWorld(e.x, e.y);
                if(!trans.flavours.isEmpty()) color(new Color().set(e.color).lerp(trans.flavours.get((int)Mathf.randomSeedRange(tile.pos(), trans.flavours.size)), Mathf.randomSeedRange(tile.pos(), 1f)), e.fslope());
            }else  color(e.color, e.fslope());
            alpha(e.fslope() * 3f);

            float drift = -20f * e.fin() * 4f;
            randLenVectors(e.id, 3, 30f + e.finpow() * 40f, (x, y) -> {
                rect(Core.atlas.find("olupis-tree-prop3"), e.x + x + drift, e.y + y + drift, 16f, 16f, e.fin() * 360f);
            });
        }).layer(Layer.darkness + 1),

        envTransgenderTreeLeafEffectUnder = new MultiEffect(envTransgenderTreeLeafEffect).layer(Layer.power + 0.01f),

        envFlowWater = new Effect(85f, 250f, e -> {
            //stolen from: https://github.com/ItsKirby69/MineDusty/blob/master/src/minedusty/content/DustyEffects.java#L144
            float thresh = 0.6f;
            float fade = e.fin() < thresh ?
            Interp.pow3Out.apply(e.fin() / thresh) :
            1f - Interp.pow3In.apply((e.fin() - thresh) / (1f - thresh));

            fade *= 0.5f;

            color(Color.valueOf("#4f5fb8"), Color.valueOf("#fbfcffff"), e.finpow());

            rand.setSeed(e.id);
            float baseAngle = e.rotation;
            float angle = baseAngle + rand.random(-2f, 2f);
            // Keep in mind that the default position of the effect is pointing East
            float offsetX = rand.random(-15f, -14f);
            float offsetY = rand.random(-6f, 6f);
            // Offsets according to direction.
            float ox = Angles.trnsx(angle, offsetX, offsetY);
            float oy = Angles.trnsy(angle, offsetX, offsetY);

            float speed = rand.random(0.15f);
            float travel = 10f * speed;

            float rise = 36f * e.fin() * Mathf.clamp(1.75f - e.finpow(), 0f, 1f);
            float shrink = Mathf.lerp(1f, 0.5f, e.fin());

            float cx = e.x + ox + Angles.trnsx(angle, travel);
            float cy = e.y + oy + Angles.trnsy(angle, travel);
            cy += Angles.trnsy(baseAngle, rise);
            cx += Angles.trnsx(baseAngle, rise);

            float baseSize = 4.5f + rand.random(3f);
            float wid = baseSize * (2f + shrink);
            float len = baseSize * (1f * 0.7f) * shrink * Mathf.clamp(e.finpow() * 7f - 4f, 1f, 7f); //Mathf.sin(e.fin() * Mathf.PI)

            alpha(fade);
            rect(Core.atlas.find("olupis-circooler"), cx, cy, wid, len, angle);
            // lineAngle(sx, sy, angle, length);
        }).layer(35f).rotWithParent(true).followParent(true),

        envBubbleSlow = new Effect(40, e -> {
            color(Tmp.c1.set(e.color).shiftValue(0.1f));
            stroke(e.fout() + 0.2f);
            randLenVectors(e.id, 2, e.rotation * 0.9f, (x, y) -> {
                circle(e.x + x, e.y + y, 0.5f + e.fin() * 3f);
            });
        }),

        //Processing Effects
        burstSplash = new Effect(110, e -> {
            float length = 10f + e.finpow() * 30f;
            rand.setSeed(e.id);
            for(int i = 0; i < 13; i++){
                v.trns(rand.random(360f), rand.random(length));
                float sizer = rand.random(0.6f, 2.1f);

                e.scaled(e.lifetime * rand.random(0.5f, 1f), b -> {
                    color(e.color, b.fslope() * 0.93f);

                    Fill.circle(e.x + v.x, e.y + v.y, sizer + b.fslope() * 1.2f);
                });
            }
        }),

        burstSmallSplashes = new Effect(110, e -> {
            float length = 3.5f + e.finpow() * 32f;
            rand.setSeed(e.id);
            for(int i = 0; i < 17; i++){
                v.trns(rand.random(360f), rand.random(length));
                float sizer = rand.random(0.2f, 1.6f);

                e.scaled(e.lifetime * rand.random(0.5f, 1f), b -> {
                    color(e.color, b.fslope() * 0.93f);

                    Fill.circle(e.x + v.x, e.y + v.y, sizer + b.fslope() * 1.2f);
                });
            }
        }),

        fastSquareSmokeCloud = new Effect(30, e -> {
            z(Layer.blockProp);
            rand.setSeed(e.id);
            color(new Color().set(Color.black).lerp(Color.darkGray, 0.7f), Color.gray, e.fin());
            alpha(Math.max(e.fout() * 2f, 0.45f));
            randLenVectors(e.id, e.fin() , 8, 45f, (x, y, fin, fout) -> {
                //TLDR: have a minimum distance for the smoke to travel, or I think so, feel free to pr any better ideas -Rushie
                Fill.poly(e.x + (Math.max(Math.abs(x), fin + 0.7f) * Math.signum(x)), e.y + (Math.max(Math.abs(y), fin + 0.7f) * Math.signum(y)), 6, 2.4f + fout * 6f , rand.random(360f));
            });
        }),

        //Shoot Effects
        shootTaurus = new Effect(14, e -> {
            color(Pal.heal);
            float w = 1f + 5 * e.fout();
            Drawf.tri(e.x, e.y, w, 8f * e.fout(), e.rotation + 45f);
            Drawf.tri(e.x, e.y, w, 8f * e.fout(), e.rotation - 45f);
            Drawf.tri(e.x, e.y, w, 17f * e.fout(), e.rotation);
            Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
        }),

        shootRepairPin =  new Effect(10, e -> {
            color(e.color);
            float w = 1.2f + 7 * e.fout();

            Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
            color(e.color);

            for(int i : Mathf.signs){
                Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
            }

            Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
        }),

        shootChainLightning = new Effect(10f, 300f, e -> {
            if(!(e.data instanceof Position p)) return;
            float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
            Tmp.v1.set(p).sub(e.x, e.y).nor();

            float normx = Tmp.v1.x, normy = Tmp.v1.y;
            float range = 12f;
            int links = Mathf.ceil(dst / range);
            float spacing = dst / links;

            z(Layer.flyingUnitLow - 0.001f);
            stroke(Math.max(6f * e.fout(), 1.5f));
            color(Color.white, e.color, e.fin());

            beginLine();

            linePoint(e.x, e.y);

            rand.setSeed(e.id);

            for(int i = 0; i < links; i++){
                float nx, ny;
                if(i == links - 1){
                    nx = tx;
                    ny = ty;
                }else{
                    float len = (i + 1) * spacing;
                    Tmp.v1.setToRandomDirection(rand).scl(range/2);
                    nx = e.x + normx * len + Tmp.v1.x;
                    ny = e.y + normy * len + Tmp.v1.y;
                }
                Drawf.light(nx, ny, Tmp.v1.x + e.x, Tmp.v1.y + e.y, 15f, e.color, 0.15f);
                linePoint(nx, ny);
            }

            endLine();
        }),

        shootMiniMikuMikuBeam = new Effect(15f, 300f, e -> {
            if(!(e.data instanceof Position v)) return;
            color(e.color, 0.75f);
            stroke(e.fout() * 0.9f + 0.6f);
            Fx.rand.setSeed(e.id);

            e.scaled(14f, b -> {
                stroke(b.fout() * 5f);
                color(e.color);
                line(e.x, e.y, v.getX(), v.getY());
            });
        }),

        shootRepairPinBeam = new Effect(20f, e -> {
            if(!(e.data instanceof Vec2 v)) return;
            color(e.color);
            stroke(e.fout() * 0.9f + 0.6f);
            Fx.rand.setSeed(e.id);
            for(int i = 0; i < 7; i++){
                Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
            }
            e.scaled(14f, b -> {
                stroke(b.fout() * 1.5f);
                color(e.color);
                line(e.x, e.y, v.x, v.y);
            });
        }),

        shootUnitDischarge = new Effect(18, e -> {
            color(rustyIron.color, 0.7f);
            stroke(e.fout() * 2f);
            float s = 16f;
            circle(e.x, e.y, 30f + e.finpow() * s);
            square(e.x, e.y, 30f + e.finpow() * -s, 45);
            randLenVectors(e.id, 5, 3f + e.fin() * 8f, (x, y) -> {
                color( new Color().set(rustyIron.color).lerp(Pal.stoneGray, 0.7f));
                Fill.square(e.x + x, e.y + y, e.fout() + 1.5f , 45);
            });
        }),

        //Explosions Effects
        explosionHighYield = new Effect(40, 120f, e -> {
            float z = z();
            z(Layer.blockOver + 4);
            float size = 0.4f;

            color(Pal.lightOrange.cpy().add(Color.gray));
            stroke(e.fout() * 3.0F);
            rand.setSeed((long)e.id);

            for(int i = 0; i < 13; ++i) {
                float angle = rand.random(360.0F);
                float lenRand = rand.random(0.5F, 1.0F);
                lineAngle(e.x, e.y, angle, e.foutpow() * 50.0F * rand.random(1.0F, 0.6F) + 2.0F, e.finpow() * 45.0F * lenRand + 6.0F);
            }
            z(Layer.bullet);
            color(Pal.orangeSpark.cpy().add(Color.purple));
            stroke(e.fout() * 2.0F);
            float circleRad = size + e.finpow() * 40.0F;
            circle(e.x, e.y, circleRad);
            rand.setSeed((long)e.id);

            for(int i = 0; i < 5; ++i) {
                float angle = rand.random(360.0F);
                float lenRand = rand.random(0.5F, 1.0F);
                Tmp.v1.trns(angle, circleRad);

                for(int s : Mathf.signs) {
                    Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 15.0F, e.fout() * 20.0F * lenRand + 6.0F, angle + 90.0F + (float)s * 90.0F);
                }
            }
            z(z);
            Drawf.light(e.x, e.y, 45f, Pal.lightOrange, 0.8f * e.fout());
        }),

        explosionHighYieldSmoke = new Effect(80, 120f, e -> {
            color(Color.purple.cpy().add(Pal.lightOrange).a(0.7f), Pal.orangeSpark.cpy().add(Color.gray).a(0.05f), e.fin());
            randLenVectors((long)e.id, 20, 35F, (x, y) -> Fill.circle(e.x + x, e.y + y, 6.0F * Mathf.clamp(e.fin() / 0.1F) * Mathf.clamp(e.fout() / 0.1F)));
        }),

        explosionCascadeSmoke = new Effect(275f, e -> {
            float z = z();
            z(Layer.blockOver);
            color(Color.blue.cpy().add(Color.gray),0.45f);
            randLenVectors((long)e.id, 80, 90.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 12.0F * Mathf.clamp(e.fin() / 0.1F) * Mathf.clamp(e.fout() / 0.1F)));
            float size = 1.0F + e.fout() * 5.0F;
            color(Color.purple.cpy().add(Color.lightGray), Color.blue.cpy().add(Color.gray), e.fin());
            Fill.circle(e.x, e.y, size / 2.0F);
            z(z);
        }),

        explosionCascadeSun = new Effect(250f, e -> {
            float z = z();
            z(Layer.blockOver + 4);

            color(Color.blue.cpy().add(Color.gray));
            stroke(e.fout() * 3.0F);
            float circleRad1 = 6.0F + e.finpow() * 110.0F;
            circle(e.x, e.y, circleRad1);
            rand.setSeed((long)e.id);

            for(int i = 0; i < 21; ++i) {
                float angle = rand.random(360.0F);
                float lenRand = rand.random(0.5F, 1.0F);
                lineAngle(e.x, e.y, angle, e.foutpow() * 50.0F * rand.random(1.0F, 0.6F) + 2.0F, e.finpow() * 100.0F * lenRand + 6.0F);
            }

            color(Color.blue.cpy().add(Color.purple));
            stroke(e.fout() * 2.0F);
            float circleRad = 6.0F + e.finpow() * 80.0F;
            circle(e.x, e.y, circleRad);
            rand.setSeed((long)e.id);

            for(int i = 0; i < 8; ++i) {
                float angle = rand.random(360.0F);
                float lenRand = rand.random(0.5F, 1.0F);
                Tmp.v1.trns(angle, circleRad);

                for(int s : Mathf.signs) {
                    Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 15.0F, e.fout() * 20.0F * lenRand + 6.0F, angle + 90.0F + (float)s * 90.0F);
                }
            }
            z(z);
        }),

        explosionCascade = new MultiEffect(explosionCascadeSun, explosionCascadeSmoke),

        explosionScatteredDebris =  new Effect(15f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            randLenVectors(e.id, 1, e.finpow() * 10f, (x, y) -> Fill.rect(
            e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
            e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
            1f, 2f, e.rotation + e.fin() * 50f * e.rotation
            ));
        }).layer(Layer.bullet),

        explosionFailedMake =  new Effect(30f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            alpha(e.fout(0.5f));
            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                circle(e.x, e.y, s.fin() * 7f);
            });
            randLenVectors(e.id, 5, e.finpow() * 17f, (x, y) -> Fill.rect(
            e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
            e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
            1f, 2f, e.rotation + e.fin() * 50f * e.rotation
            ));

            randLenVectors(e.id, 10, e.finpow() * 15f, (x, y) -> Fill.circle(
            e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
            e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
            e.fout() * 3f
            ));
            Drawf.light(e.x, e.y, 20f, Pal.lightOrange, 0.6f * e.fout());
        }).layer(Layer.bullet),

        explosionUnitDepleted = new Effect(100f, e -> {
            if(!(e.data instanceof Unit select) || select.type == null) return;

            float scl = e.fout(Interp.pow2Out);
            float p = Draw.scl;
            Draw.scl *= scl;

            mixcol(Pal.darkMetal, 1f);
            rect(select.type.fullIcon, select.x, select.y, select.rotation - 90f);
            stroke(e.fslope());
            square(select.x, select.y, (e.fout() * 0.9f) * select.hitSize * 1.5f, 45);
            reset();

            Draw.scl = p;
        }),

        explosionReplicatorDie = new Effect(80f, e -> {
            if(!(e.data instanceof Block block)) return;

            mixcol(NyfalisColors.contentOutline, e.color, 1f);
            alpha(e.fout());
            rect(block.fullIcon, e.x, e.y);
        }).layer(Layer.turret - 5f),

        explosionColouredShockwave =  new Effect(8f, 120f, e -> {
            z(Layer.blockProp);
            color(Color.darkGray, e.color, e.fin());
            stroke(e.fout() * 2f + 0.2f);
            circle(e.x, e.y, e.fin() * 28f);
        }),

        shootSmallPorpolKaboom = new Effect(25, e -> {
            color(Pal.sapBullet);
            e.scaled(6, i -> {
                stroke(i.fout());
                circle(e.x, e.y, 3f + i.fin() * 26.66f);
            });

            color(Color.gray);

            randLenVectors(e.id, 9, 2f + 23.33f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 1.33f + 0.5f));

            color(Pal.sapBulletBack);
            stroke(e.fout());

            randLenVectors(e.id + 1, 8, 1f + 20f * e.finpow(), (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout()));
            Drawf.light(e.x, e.y, 30f, Pal.sapBulletBack, 0.8f * e.fout());
        }),

        //Hits Effects
        hitSurgeZapped = new MultiEffect(hitBeam).wrap(Pal.surgeAmmoBack),
        hitSurgeZappedBig = new MultiEffect(hitBeam, hitLaserBlast).wrap(Pal.surgeAmmoBack),

        hitHollowPoint =  new Effect(30f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            alpha(e.fout(0.5f));
            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                circle(e.x, e.y, s.fin() * 3.5f);
            });
            randLenVectors(e.id, 5, e.finpow() * 17f, (x, y) -> Fill.rect(
            e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
            e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
            1f, 2f, e.rotation + e.fin() * 50f * e.rotation
            ));
        }).layer(Layer.bullet),

        hitHollowPointSmall =  new Effect(30f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            alpha(e.fout(0.5f));
            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                circle(e.x, e.y, s.fin() * 3.5f);
            });
            randLenVectors(e.id, 1, e.finpow() * 17f, (x, y) -> Fill.rect(
            e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
            e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
            1f, 2f, e.rotation + e.fin() * 50f * e.rotation
            ));
        }).layer(Layer.bullet),

        hitTractor = new Effect(17, e -> {
            color(Color.white);
            stroke(e.fout() * 1.5f);

            z(Layer.groundUnit -0.1f);

            randLenVectors(e.id, 6, e.foutpow() * 17f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1f);
            });
        }),

        hitMiniPointDefence = new Effect(8f, e -> {
            color(Color.white, e.color, e.fin());
            circle(e.x, e.y, e.fin() * 6f);
        }),

        hitAoePointDefence = new Effect(25f, 300f, e -> {
            if(!(e.data instanceof Position pos)) return;

            color(e.color, e.fout() / 2);
            stroke(0.75f);
            line(e.x, e.y, pos.getX(), pos.getY());
            Drawf.light(e.x, e.y, pos.getX(), pos.getY(), 20f, e.color, 0.6f * e.fout());
        }),

        hitRepairPinSpark = new Effect(40, e -> {
            color(Pal.heal);
            stroke(e.fout() * 1.6f);

            randLenVectors(e.id, 18, e.finpow() * 27f, e.rotation, 360f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Drawf.square(e.x + x, e.y + y, 0);
                Drawf.square(e.x + x, e.y + y, 0);
                lineAngle(e.x + x, e.y + y, ang, e.fout() * 6 + 1f);
            });
        }),

        hitAcidRain = new Effect(10, e -> {
            TextureRegion uwu = null;
            if(e.data instanceof TextureRegion owo) uwu = owo;
            else if(e.data instanceof UnlockableContent owo) uwu = owo.fullIcon;
            if(!uwu.found()) return;


            mixcol(e.color, Mathf.range(0.35f, 0.9f));
            alpha(e.fout());
            rect(uwu, e.x, e.y);
            
        }),

        hitHighYield = new MultiEffect(explosionHighYield, explosionHighYieldSmoke).layer(Layer.bullet),

        hitObliterator = new MultiEffect(explosionColouredShockwave, fastSquareSmokeCloud),

        hitTaurusHeal = new Effect(11, e -> {
            color(Pal.heal);
            stroke(e.fout() * 2f);
            circle(e.x, e.y, 2f + e.finpow() * (Vars.tilesize * 2));
            square(e.x, e.y, 2f + e.finpow() * (Vars.tilesize * -2), 45);
        }),

        //Debug Effects
        debugEffect = new Effect(30, e -> {
        if(!(e.data instanceof String i)) return;
        WorldLabel.drawAt(i, e.x, e.y, Layer.overlayUI, WorldLabel.flagOutline, 0.4f, Align.center, Align.center);

    })
    ;

}

