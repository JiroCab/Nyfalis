package olupis.world.blocks.processing;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.graphics.*;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.BurstDrill;

public class BoostableBurstDrill extends BurstDrill {
    public Liquid boostLiquid = Liquids.water;
    public int topVariant = 0;
    public TextureRegion[] topRegions, topInvertedRegions;


    public BoostableBurstDrill(String name)
    {
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();


        addBar("drillspeed", (DrillBuild e) ->{
            int b = e.liquids.get(boostLiquid) > 0 ? 2 : 1;
            return new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastDrillSpeed * 60 * e.timeScale() * b, 2)), () -> Pal.ammo, () -> e.warmup);
        });
    }

    @Override
    public void load(){
        super.load();

        if(variants != 0){
            variantRegions = new TextureRegion[variants];
            for(int i = 0; i < variants; i++) variantRegions[i] = Core.atlas.find(name + (i + 1));
        }

        if(topVariant != 0){
            topRegions = new TextureRegion[topVariant];
            for(int i = 0; i < variants; i++) topRegions[i] = Core.atlas.find(name +"-top"+ (i + 1));

            topInvertedRegions = new TextureRegion[topVariant];
            for(int i = 0; i < topVariant; i++) topInvertedRegions[i] = Core.atlas.find(name + "-top-invert"+ (i + 1));


            if(topRegion == null) topRegion = topRegions[0];
            if(topInvertedRegions == null) topInvertRegion = topInvertedRegions[0];
        }
    }

    public class BoostableBurstDrillBuild extends BurstDrillBuild{

        @Override
        public void updateTile(){
            if(dominantItem == null){
                return;
            }

            if(invertTime > 0f) invertTime -= delta() / invertedTime;

            if(timer(timerDump, dumpTime)){
                dump(items.has(dominantItem) ? dominantItem : null);
            }

            float drillTime = getDrillTime(dominantItem);

            smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (drillTime - 20f), 0.1f);

            if(items.total() <= itemCapacity - dominantItems && dominantItems > 0 && efficiency > 0){
                warmup = Mathf.approachDelta(warmup, progress / drillTime, 0.01f);

                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

                timeDrilled += speedCurve.apply(progress / drillTime) * speed;

                lastDrillSpeed = 1f / drillTime * speed * dominantItems;
                progress += delta() * speed;
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, 0.01f);
                lastDrillSpeed = 0f;
                return;
            }

            if(dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity){
                int b = liquids.get(boostLiquid) > 0 ? 2 : 1;
                for(int i = 0; i < dominantItems * b; i++){
                    offload(dominantItem);
                }

                invertTime = 1f;
                progress %= drillTime;

                if(wasVisible){
                    Effect.shake(shake, shake, this);
                    drillSound.at(x, y, 1f + Mathf.range(drillSoundPitchRand), drillSoundVolume);
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
                }
            }
        }

        @Override
        public void draw(){
            if(variants <= 0)Draw.rect(region, x, y);
            else Draw.rect(variantRegions[Mathf.randomSeed(Point2.pack((int)x, (int)y), 0,variants-1)], x, y);
            drawDefaultCracks();


            if(topVariant <= 0)Draw.rect(topRegion, x, y);
            else Draw.rect(topRegions[Mathf.randomSeed(Point2.pack((int)x, (int)y), 0, topVariant -1)], x, y);

            if(invertTime > 0 && (topInvertRegion.found() || topInvertedRegions.length >= 1)){
                Draw.alpha(Interp.pow3Out.apply(invertTime));

                if(topVariant <= 0)Draw.rect(topInvertRegion, x, y);
                else Draw.rect(topInvertedRegions[Mathf.randomSeed(Point2.pack((int)x, (int)y), 0,topVariant-1)], x, y);

                Draw.color();
            }

            if(dominantItem != null && drawMineItem){
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }

            float fract = smoothProgress;
            Draw.color(arrowColor);
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < arrows; j++){
                    float arrowFract = (arrows - 1 - j);
                    float a = Mathf.clamp(fract * arrows - arrowFract);
                    Tmp.v1.trns(i * 90 + 45, j * arrowSpacing + arrowOffset);

                    //TODO maybe just use arrow alpha and draw gray on the base?
                    Draw.z(Layer.block);
                    Draw.color(baseArrowColor, arrowColor, a);
                    Draw.rect(arrowRegion, x + Tmp.v1.x, y + Tmp.v1.y, i * 90);

                    Draw.color(arrowColor);

                    if(arrowBlurRegion.found()){
                        Draw.z(Layer.blockAdditive);
                        Draw.blend(Blending.additive);
                        Draw.alpha(Mathf.pow(a, 10f));
                        Draw.rect(arrowBlurRegion, x + Tmp.v1.x, y + Tmp.v1.y, i * 90);
                        Draw.blend();
                    }
                }
            }
            Draw.color();

            if(glowRegion.found()){
                Drawf.additive(glowRegion, Tmp.c2.set(glowColor).a(Mathf.pow(fract, 3f) * glowColor.a), x, y);
            }
        }
    }
}
