package olupis.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.Interp.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.graphics.g2d.Draw.rect;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static olupis.content.NyfalisItemsLiquid.rustyIron;

public class NyfalisFxs extends Fx {
    BounceOut bounceOutTwo = new BounceOut(2);

    public static final Effect
        OilyFlame = new Effect(10f, e -> {
            color(Color.valueOf("912a13"), Pal.darkFlame, e.fin());

            randLenVectors(e.id, 3, 5f + e.fin() * 5f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.6f + e.fslope() * 3f);
            });

            color();

            Drawf.light(e.x, e.y, 40f * e.fslope(), Color.valueOf("806f2c"), 2f);
        }),

        LubeFlame = new Effect(10f, e -> {
            color(Color.valueOf("806f2c"), Pal.lightFlame, e.fin());

            randLenVectors(e.id, 8, 3f + e.fin() * 4f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.5f + e.fslope() * 3f);
            });

            color();

            Drawf.light(e.x, e.y, 40f * e.fslope(), Color.valueOf("ffb547"), 2f);
        }),


        hollowPointHit =  new Effect(30f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            alpha(e.fout(0.5f));
            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                Lines.circle(e.x, e.y, s.fin() * 3.5f);
            });
            randLenVectors(e.id, 5, e.finpow() * 17f, (x, y) -> Fill.rect(
                e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
                e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
                1f, 2f, e.rotation + e.fin() * 50f * e.rotation
            ));
        }).layer(Layer.bullet),

        hollowPointHitSmall =  new Effect(30f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            alpha(e.fout(0.5f));
            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                Lines.circle(e.x, e.y, s.fin() * 3.5f);
            });
            randLenVectors(e.id, 1, e.finpow() * 17f, (x, y) -> Fill.rect(
                e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
                e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
                1f, 2f, e.rotation + e.fin() * 50f * e.rotation
            ));
        }).layer(Layer.bullet),

        hitTracter = new Effect(17, e -> {
            color(Color.white);
            stroke(e.fout() * 1.5f);

             z(Layer.groundUnit -0.1f);

            randLenVectors(e.id, 6, e.foutpow() * 17f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1f);
            });
        }),

        scatterDebris =  new Effect(15f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            randLenVectors(e.id, 1, e.finpow() * 10f, (x, y) -> Fill.rect(
                e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
                e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
                1f, 2f, e.rotation + e.fin() * 50f * e.rotation
            ));
        }).layer(Layer.bullet),

        failedMake =  new Effect(30f, e -> {
            color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            alpha(e.fout(0.5f));
            e.scaled(7f, s -> {
                stroke(0.5f + s.fout());
                Lines.circle(e.x, e.y, s.fin() * 7f);
            });
            randLenVectors(e.id, 5, e.finpow() * 17f, (x, y) -> Fill.rect(
               e.x + x + Mathf.randomSeedRange((long) (e.id + e.rotation + 7), 3f * e.fin()),
               e.y + y + Mathf.randomSeedRange((long) (e.id + e.rotation + 8), 3f * e.fin()),
               1f, 2f, e.rotation + e.fin() * 50f * e.rotation
           ));
            Drawf.light(e.x, e.y, 20f, Pal.lightOrange, 0.6f * e.fout());
        }).layer(Layer.bullet),

        //TODO Wip scale is off
        highYieldExplosive = new Effect(55, 120f, e -> {

            color(Pal.darkestGray, Pal.lightOrange,  e.fout());
            e.scaled(6, i -> {
                stroke(3f * i.fout());
                Lines.circle(e.x, e.y, 3f + i.fin() * 15f);
            });

            color(Pal.lightOrange, Color.white, e.fout());
            randLenVectors(e.id, 4, 4f + 6f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f);
            });
            alpha(1);

            color(Pal.lightOrange);
            stroke(e.fout());

            randLenVectors(e.id + 1, 4, 3f + 30f * e.finpow(), (x, y) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f);
            });

            Drawf.light(e.x, e.y, 45f, Pal.lightOrange, 0.8f * e.fout());
        }),

        unitBreakdown = new Effect(100f, e -> {
            if(!(e.data instanceof Unit select) || select.type == null) return;

            float scl = e.fout(Interp.pow2Out);
            float p = Draw.scl;
            Draw.scl *= scl;

            mixcol(Pal.darkMetal, 1f);
            rect(select.type.fullIcon, select.x, select.y, select.rotation - 90f);
            Lines.stroke(e.fslope());
            Lines.square(select.x, select.y, (e.fout() * 0.9f) * select.hitSize * 1.5f, 45);
            reset();

            Draw.scl = p;
        }),

        unitDischarge = new Effect(18, e -> {
            color(NyfalisItemsLiquid.rustyIron.color, 0.7f);
            stroke(e.fout() * 2f);
            float s = 16f;
            Lines.circle(e.x, e.y, 30f + e.finpow() * s);
            Lines.square(e.x, e.y, 30f + e.finpow() * -s, 45);
            randLenVectors(e.id, 5, 3f + e.fin() * 8f, (x, y) -> {
                color( new Color().set(rustyIron.color).lerp(Pal.stoneGray, 0.7f));
                Fill.square(e.x + x, e.y + y, e.fout() + 1.5f , 45);
            });
        }),

        taurusHeal = new Effect(11, e -> {
            color(Pal.heal);
            stroke(e.fout() * 2f);
            Lines.circle(e.x, e.y, 2f + e.finpow() * (Vars.tilesize * 2));
            Lines.square(e.x, e.y, 2f + e.finpow() * (Vars.tilesize * -2), 45);
        }),
        smolPorpolKaboom = new Effect(25, e -> {
            color(Pal.sapBullet);
            e.scaled(6, i -> {
                stroke(i.fout());
                Lines.circle(e.x, e.y, 3f + i.fin() * 26.66f);
            });

            color(Color.gray);

            randLenVectors(e.id, 9, 2f + 23.33f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 1.33f + 0.5f));

            color(Pal.sapBulletBack);
            stroke(e.fout());

            randLenVectors(e.id + 1, 8, 1f + 20f * e.finpow(), (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout()));
            Drawf.light(e.x, e.y, 30f, Pal.sapBulletBack, 0.8f * e.fout());
        }),

        colouredShockwave =  new Effect(8f, 120f, e -> {
            z(Layer.blockProp);
            color(Color.darkGray, e.color, e.fin());
            stroke(e.fout() * 2f + 0.2f);
            Lines.circle(e.x, e.y, e.fin() * 28f);
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

        miniPointHit = new Effect(8f, e -> {
            color(Color.white, e.color, e.fin());
            Lines.circle(e.x, e.y, e.fin() * 6f);
        }),

        getMiniPointHit = new Effect(25f, 300f, e -> {
            if(!(e.data instanceof Position pos)) return;

            Draw.color(e.color, e.fout() / 2);
            Lines.stroke(0.75f);
            Lines.line(e.x, e.y, pos.getX(), pos.getY());
            Drawf.light(e.x, e.y, pos.getX(), pos.getY(), 20f, e.color, 0.6f * e.fout());
        }),

        shootTaurus = new Effect(14, e -> {
            color(Pal.heal);
            float w = 1f + 5 * e.fout();
            Drawf.tri(e.x, e.y, w, 8f * e.fout(), e.rotation + 45f);
            Drawf.tri(e.x, e.y, w, 8f * e.fout(), e.rotation - 45f);
            Drawf.tri(e.x, e.y, w, 17f * e.fout(), e.rotation);
            Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
        }),

        repairPinShoot =  new Effect(10, e -> {
            color(e.color);
            float w = 1.2f + 7 * e.fout();

            Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
            color(e.color);

            for(int i : Mathf.signs){
                Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
            }

            Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
        }),

        chainLightningAlt = new Effect(10f, 300f, e -> {
            if(!(e.data instanceof Position p)) return;
            float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
            Tmp.v1.set(p).sub(e.x, e.y).nor();

            float normx = Tmp.v1.x, normy = Tmp.v1.y;
            float range = 12f;
            int links = Mathf.ceil(dst / range);
            float spacing = dst / links;

            Draw.z(Layer.flyingUnitLow - 0.001f);
            Lines.stroke(Math.max(6f * e.fout(), 1.5f));
            Draw.color(Color.white, e.color, e.fin());

            Lines.beginLine();

            Lines.linePoint(e.x, e.y);

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
                Lines.linePoint(nx, ny);
            }

            Lines.endLine();
        }),

        repairPinBeam = new Effect(20f, e -> {
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
        }),

        replicatorDie = new Effect(80f, e -> {
            if(!(e.data instanceof Block block)) return;

            mixcol(NyfalisColors.contentOutline, e.color, 1f);
            alpha(e.fout());
            rect(block.fullIcon, e.x, e.y);
        }).layer(Layer.turret - 5f),

        bubbleSlow = new Effect(40, e -> {
            color(Tmp.c1.set(e.color).shiftValue(0.1f));
            stroke(e.fout() + 0.2f);
            randLenVectors(e.id, 2, e.rotation * 0.9f, (x, y) -> {
                Lines.circle(e.x + x, e.y + y, 0.5f + e.fin() * 3f);
            });
        }),

        obliteratorShockwave = new MultiEffect(colouredShockwave, fastSquareSmokeCloud)

    ;

}

