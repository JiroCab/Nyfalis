package olupis.world.blocks.processing;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;
import olupis.*;
import olupis.content.*;

import static mindustry.Vars.*;
import static olupis.content.NyfalisAttributeWeather.flood;

public class BurstPump extends Pump {
    public Interp speedCurve = Interp.pow2In;
    //public float invertedTime = 200f,
    public float pumpTime = 400, dumpScale = 1.5f;
    public Sound drillSound = NyfalisSounds.dunk;
    public float
        burstSoundVolume = 0.4f,
        burstSoundPitchRandMin = 0.1f,
        burstSoundPitchRandMax = 0.4f,
        pumpEffectRnd = -1f,
        shake = 2f,
        leakAmount = 2f,
        skimAmount = 0.1f;
    public Effect pumpEffect = new MultiEffect(Fx.shockwaveSmaller, NyfalisFxs.burstSplash, Fx.mineImpactWave);


    /** Multipliers of drill speed for each item. Defaults to 1. */
    public ObjectFloatMap<Liquid> pumpMultipliers = new ObjectFloatMap<>();

    public BurstPump(String name){
        super(name);
    }

    public float getPumpTime(Liquid liquid){return pumpTime / pumpMultipliers.get(liquid, 1f);}

    @Override
    public TextureRegion[] icons(){
        if(topRegion.found()) return new TextureRegion[]{region, topRegion};
        else return new TextureRegion[]{region};
    }

    @Override
    public void setBars(){
        super.setBars();

        //replace dynamic output bar with own custom bar
        addLiquidBar((PumpBuild build) -> build.liquidDrop);
        addBar("drillspeed", (BurstPump.BurstPumpBuild e) ->
                new Bar(() -> Strings.fixed(pumpAmount * e.amount, 0)  + Iconc.liquid +( e.amount == 0 ? "" :  " | " + Strings.fixed(((1 - e.warmup) * pumpTime + 60)  / 60, 0)), () -> Pal.ammo, () -> e.warmup));
        addBar("boost", (BurstPump.BurstPumpBuild e) ->
        new Bar(() -> (
            Strings.fixed(e.amount * leakAmount * 60, 0) +
            (e.attrsum >= 0? " + " + Strings.fixed(e.attrsum * 60 * skimAmount * NyfalisVars.floodPlaneLevel , 0) + "\u26C8"  + " " + StatUnit.perSecond.localized()  : "")) ,
        () -> Pal.sapBulletBack, () -> e.attrsum > 0 ? NyfalisVars.floodPlaneLevel: 1f));
    }

    @Override
    protected boolean canPump(Tile tile){
        return  canRegularPump(tile) || canSeep(tile);
    }

    public boolean canRegularPump(Tile tile){
        return super.canPump(tile);
    }

    public boolean canSeep(Tile tile){
        return tile.floor().attributes.get(flood) >= 0;
    }

    @Override
    public void drawPlace(int x,int y,int rotation,boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        float amount = 0f, skim = 0f;
        @Nullable Liquid liquidDrop = tile.floor().liquidDrop;


        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canSeep(other)) skim +=  other.floor().attributes.get(flood) * skimAmount;
            if(canRegularPump(other)){
                if(other.floor().liquidDrop == null){
                    continue;
                }else if(liquidDrop != null && other.floor().liquidDrop != liquidDrop){
                    liquidDrop = null;
                    skim = 0;
                    break;
                }else{
                    liquidDrop = other.floor().liquidDrop;
                    amount += other.floor().liquidMultiplier;
                }
            }
        }

        if(liquidDrop != null){
            String
                rainTxt = skim > 0 ? Strings.fixed(skim * 60, 0 ) + "\u26C8"   : "",
                pumpTxt = amount > 0 ? Strings.fixed(amount * pumpAmount,0 ) +Iconc.liquid+ " + " +  (Strings.fixed(amount * leakAmount * 60f, 0)) : "",
                addTxt = skim > 0 && amount > 0 ? " + " : ""
            ;
            float width = drawPlaceText(
                Core.bundle.format("bar.pumpspeed", pumpTxt + addTxt +rainTxt)
                , x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            float ratio = (float)liquidDrop.fullIcon.width / liquidDrop.fullIcon.height;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(liquidDrop.fullIcon, dx, dy - 1, s * ratio, s);
            Draw.reset();
            Draw.rect(liquidDrop.fullIcon, dx, dy, s * ratio, s);
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.output);
        stats.add(Stat.output, Strings.fixed(pumpAmount * size* size,0)+ Iconc.liquid + " + " +Strings.fixed(leakAmount * size  * size * 60, 0) + " "+  StatUnit.liquidSecond.localized());
    }

    public class BurstPumpBuild extends PumpBuild{
        //used so the lights don't fade out immediately
        public float smoothProgress = 0f;
        public float attrsum = 0f;
        public float progress, warmup, timePumped, lastPumpSpeed;


        @Override
        public void pickedUp(){
            attrsum = 0;
            super.pickedUp();
        }

        @Override
        public void onProximityUpdate(){
            this.noSleep();
            attrsum = 0f;
            amount = 0f;
            liquidDrop = null;

            for(Tile other : tile.getLinkedTiles(tempTiles)){
                if(canSeep(other)) attrsum +=  other.floor().attributes.get(flood);
                if(canPump(other)){
                    liquidDrop = other.floor().liquidDrop;
                    amount += other.floor().liquidMultiplier;
                }
            }

            attrsum = sumAttribute(flood, tile.x, tile.y);
        }

        @Override
        public void updateTile(){
            if (liquidDrop == null) return;

            if (timer(timerDump, dumpTime)){
                dumpLiquid(liquidDrop, 1.5f);
            }

            float pumpTime = getPumpTime(liquidDrop);
            smoothProgress = Mathf.lerpDelta(smoothProgress, progress/(pumpTime - 20f), 0.1f);

            if(amount > 0 ){
                if(liquids.currentAmount() < liquidCapacity && efficiency > 0 ){
                    warmup = Mathf.approachDelta(warmup, progress/pumpTime, 0.01f);
                    float speed = efficiency;

                    timePumped += speedCurve.apply(progress/pumpTime) * speed;
                    lastPumpSpeed = 1f / pumpTime * speed;
                    progress += delta() * speed;
                } else  {
                    warmup = Mathf.approachDelta(warmup, 0f, 0.01f);
                    return;
                }
            }
            if (liquids.currentAmount() < liquidCapacity){
                if(progress >= pumpTime ){
                    float emptySpaceLiquid = liquidCapacity - liquids.get(liquidDrop);
                    //float maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop) + (liquidCapacity / 2.5f), amount * pumpAmount * edelta());
                    liquids.add(liquidDrop,Math.min(pumpAmount,emptySpaceLiquid));
                    //invertedTime is not used anywhere
                    //invertedTime = 1f;
                    progress %= pumpTime;
                    if(wasVisible){
                        Effect.shake(shake, shake, this);
                        drillSound.at(x, y, Mathf.range(burstSoundPitchRandMin, burstSoundPitchRandMax), burstSoundVolume);
                        pumpEffect.at(x + Mathf.range(pumpEffectRnd), y + Mathf.range(pumpEffectRnd), Tmp.c1.set(liquidDrop.color).lerp(Color.white, 0.35f));
                    }
                }

                float maxPump = amount * leakAmount * edelta();
                if(attrsum >= 0) maxPump += edelta() * ((attrsum * skimAmount * NyfalisVars.floodPlaneLevel));

                maxPump= Math.min(liquidCapacity - liquids.get(liquidDrop), maxPump);

                liquids.add(liquidDrop, maxPump);
                lastPumpSpeed += (leakAmount * edelta() * amount);



            }
        }

        //TODO: Add the arrows like impact drills and maybe a piston(s) to draw()
    }
}
