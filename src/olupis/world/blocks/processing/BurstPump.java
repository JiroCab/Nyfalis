package olupis.world.blocks.processing;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.util.Strings;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Pump;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class BurstPump extends Pump {
    public Interp speedCurve = Interp.pow2In;
    //public float invertedTime = 200f,
    public float pumpTime = 400, dumpScale = 1.5f;
    public Sound drillSound = Sounds.pulseBlast;
    public float drillSoundVolume = 0.4f, drillSoundPitchRand = 0.3f, pumpEffectRnd = -1f, shake = 2f, leakAmount = 2f;
    public Effect pumpEffect = Fx.steam;

    
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
                new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastPumpSpeed * 60 * e.timeScale(), 2)), () -> Pal.ammo, () -> e.warmup));
    }
    @Override
    public void drawPlace(int x,int y,int rotation,boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        float amount = 0f;
        Liquid liquidDrop = null;

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canPump(other)){
                if(liquidDrop != null && other.floor().liquidDrop != liquidDrop){
                    liquidDrop = null;
                    break;
                }
                liquidDrop = other.floor().liquidDrop;
                amount += other.floor().liquidMultiplier;
            }
        }

        if(liquidDrop != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.pumpspeed", amount * pumpAmount*60/pumpTime, 0), x, y, valid);
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
        stats.add(Stat.output, ((pumpAmount / pumpTime) + leakAmount) * 60f , StatUnit.liquidSecond);
    }

    public class BurstPumpBuild extends PumpBuild{
        //used so the lights don't fade out immediately
        public float smoothProgress = 0f;
        public float invertTime = 0f;
        public float progress, warmup, timePumped, lastPumpSpeed;


            @Override
            public void updateTile(){
                if (liquidDrop == null) return;

                //if(invertTime > 0f) invertTime -= delta() / invertedTime;
                smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (pumpTime - 20f), 0.1f);

                if (timer(timerDump, dumpTime)){
                    dumpLiquid(liquidDrop, 1.5f);
                }

                float pumpTime = getPumpTime(liquidDrop);
                smoothProgress = Mathf.lerpDelta(smoothProgress, progress/(pumpTime - 20f), 0.1f);

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
                            drillSound.at(x, y, 1f + Mathf.range(drillSoundPitchRand), drillSoundVolume);
                            pumpEffect.at(x + Mathf.range(pumpEffectRnd), y + Mathf.range(pumpEffectRnd), liquidDrop.color);
                        }
                    } else{
                          /*keep a very small amount output but keep the bursts as a main
                               so its not annoying gameplay wise */
                        liquids.add(liquidDrop, leakAmount);
                        lastPumpSpeed = leakAmount;
                    }
                }
            }
        }
}
