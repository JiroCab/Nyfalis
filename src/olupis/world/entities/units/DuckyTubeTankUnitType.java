package olupis.world.entities.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.world.*;
import olupis.world.entities.parts.*;

import java.util.*;

import static mindustry.Vars.*;

//Rushie was watching too much pvz and cant think of a better name
public class DuckyTubeTankUnitType extends  LeggedWaterUnit{
    public float floatRate = 0.03f;
    HashMap<Unit, Float>floatingTracker = new HashMap<>(),
                                        treadTracker = new HashMap<>(),
                                        onSolidTracker = new HashMap<>()
    ;

    public DuckyTubeTankUnitType(String name) {
        super(name);
    }


    @Override
    public void update(Unit unit){
        super.update(unit);
        if (unit.dead()){
            floatingTracker.remove(unit);
            treadTracker.remove(unit);
            onSolidTracker.remove(unit);
        }

        if(!floatingTracker.containsKey(unit)) floatingTracker.put(unit, onWater(unit) ? 1f : 0f);
        if(!treadTracker.containsKey(unit) || treadTracker.get(unit) >= Float.MAX_VALUE -5f) treadTracker.put(unit, 0f);
        if(!onSolidTracker.containsKey(unit)) onSolidTracker.put(unit, 0f);

        float prev = floatingTracker.get(unit), tar = onWater(unit) ? 1 : -1;
        floatingTracker.replace(unit, Mathf.clamp(prev + (tar * floatRate)));


        if (!unit.vel.isZero(0.001F) || Vars.net.client()) {
            float len = unit.deltaLen(), tprev = treadTracker.get(unit) ;
            //TODO: maybe make this a lerp but rushie too dumb to to figure it out atm
            treadTracker.replace(unit, tprev + len);
            drawTrailCustom(unit);
        }

        int solids = 0;
        int r = Math.max(Math.round(hitSize * 0.6f / tilesize), 1);
        for(int dx = -r; dx <= r; dx++){
            for(int dy = -r; dy <= r; dy++){
                Tile t = Vars.world.tileWorld(unit.x + dx*tilesize, unit.y + dy*tilesize);
                if(t == null ||  t.solid()){
                    solids ++;
                }

                //TODO should this apply to the player team(s)? currently PvE due to balancing
                if(crushDamage > 0 && (unit.deltaLen() >= 0.01f) && t != null && t.build != null && t.build.team != unit.team
                //damage radius is 1 tile smaller to prevent it from just touching walls as it passes
                && Math.max(Math.abs(dx), Math.abs(dy)) <= r - 1){

                    t.build.damage(unit.team, crushDamage * Time.delta * t.block().crushDamageMultiplier * state.rules.unitDamage(unit.team));
                }
            }
        }
        onSolidTracker.replace(unit, (float) solids);
    }

    public void drawTrailCustom(Unit unit){
        if(unit.isFlying()) return;

        if(!onWater(unit))for(int i : Mathf.signs){
            Tmp.v1.set(waveTrailX * i, waveTrailY).rotate(unit.rotation - 90);
            Effect.floorDustAngle(treadEffect, Tmp.v1.x + unit.x, Tmp.v1.y + unit.y, unit.rotation + 180f);
        } else if( Math.round(treadTracker.get(unit)) % 10 == 0){for(int i : Mathf.signs){
            //TODO: maybe make the bubbles bigger
            Tmp.v2.set(waveTrailX * i, waveTrailY).rotate(unit.rotation - 90);
            Effect.floorDustAngle(Fx.bubble.layer(Layer.debris), Tmp.v2.x + unit.x, Tmp.v2.y + unit.y, 0);
        }}
    }


    @Override
    public void updateMovement(Unit unit){
        super.updateMovement(unit);

        int r = Math.max(Math.round(hitSize * 0.6f / tilesize), 1), total = (r*2+1)*(r*2+1);
        speed *= Mathf.lerp(1f, crawlSlowdown, Mathf.clamp(onSolidTracker.getOrDefault(unit, 1f) / total / crawlSlowdownFrac));
    }

    @Override
    public void updatePrams (Unit unit){
        NyfPartParms.nyfparams.set(
        unit.healthf(),
        unit.team.id,
        unit.elevation(),
        partAmmo(unit),
        onWater(unit) ? 1 : 0,
        treadTracker.getOrDefault(unit, 0f),
        unit instanceof Payloadc p ? p.payloads().size : 0
        );
    }


    //honestly this whole thing is a mess, im so sorry
    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);

        for(DrawPart p : parts){
            if(p instanceof FloaterTreadsPart f){
                PixmapRegion pix = Core.atlas.getPixmap(f.treadRegion);

                for(int r = 0; r < f.treadRects.length; r++){
                    Rect treadRect =f.treadRects[r];
                    //slice is always 1 pixel wide
                    Pixmap slice = pix.crop((int)(treadRect.x + pix.width/2f), (int)(treadRect.y + pix.height/2f), 1, (int)treadRect.height);
                    int frames = treadFrames;
                    for(int i = 0; i < frames; i++){
                        int pullOffset = f.treadPullOffset;
                        Pixmap frame = new Pixmap(slice.width, slice.height);
                        for(int y = 0; y < slice.height; y++){
                            int idx = y + i;
                            if(idx >= slice.height){
                                idx -= slice.height;
                                idx += pullOffset;
                                idx = Mathf.mod(idx, slice.height);
                            }

                            frame.setRaw(0, y, slice.getRaw(0, idx));
                        }

                        packer.add(PageType.main, name + "-treads" + r + "-" + i, frame);
                    }
                }
            }
        }
    }

    @Override
    public <T extends Unit&Tankc> void drawTank(T unit){
        super.drawTank(unit);
    }
}