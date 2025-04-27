package olupis.world.blocks.processing;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.BurstDrill;

public class BoostableBurstDrill extends BurstDrill {
    public Liquid boostLiquid = Liquids.water;
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

    public class BoostableBurstDrillBuild extends BurstDrillBuild
    {

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
    }
}
