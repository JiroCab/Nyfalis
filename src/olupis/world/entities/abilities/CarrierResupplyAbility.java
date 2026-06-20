package olupis.world.entities.abilities;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.ai.*;
import olupis.world.entities.entities.*;
import olupis.world.interfaces.*;

import java.util.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;

public class CarrierResupplyAbility extends Ability{
    public int tier = 1;
    public float
        range = Vars.tilesize * 2f,
        beamRange = Vars.tilesize * 10,
        beamForce = 5, //where tf u going
        beamScaledForce = 10,
        beamWidth  = 0.5f,
        resupply = 2.5f * tier,
        resupplyDelay = 25f,
        scanDelay = 50f

    ;

    public String
        laserSprite = "",
        laserEndSprite = "",
        laserStartSprite = ""
    ;

    public TextureRegion
        laserRegion,
        laserEndRegion,
        laserStartRegion
    ;

    public Effect resupplyEffect = Fx.itemTransfer;

    //x, y
    public float[][] dockPoints = {{0, -10}};
    @Nullable public AmmoNyf[] docked;
    IntSeq queue = new IntSeq();

    protected float scanTime, resupplyTime;

    public CarrierResupplyAbility(int tier, float beam,  float[][] docks){
        this(tier, docks);
        beamRange = beam;
    }

    public CarrierResupplyAbility(int tier, float[][] docks){
        this(tier);
        dockPoints = docks;
    }

    public CarrierResupplyAbility(int tier){
        this.tier = tier;
        display = false;
    }

    public CarrierResupplyAbility(){
        display = false;

    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);

        stroke(5f);
        color(unit.team.color, 0.3f);
        if(docked != null){
            for(int i = 0; i < docked.length; i++){
                if(docked[i] == null) continue;
                if(!(docked[i] instanceof  Posc pe)) continue;
                float[] out = getDockPoint(i, unit);

                Drawf.laser(laserRegion, laserStartRegion, laserEndRegion,
                out[0], out[1], pe.x(), pe.y(), beamWidth);
            }
        }

        stroke(1f);//idk reset doesnt fix it sometimes
        reset();
    }

    @Override
    public void update(Unit unit){
        if(docked == null) docked = new AmmoNyf[dockPoints.length];

        //Handle docked units
        for(int i = 0; i < docked.length; i++){
            if(docked[i] == null) continue;
            AmmoNyf ae = docked[i];
            if(ae.ammof() >= 1 && !validUnit(unit)){
                docked[i] = null;
                continue;
            }

            if(i >= dockPoints.length) break;
            if(!(ae instanceof  Posc pe)) continue;
            Tmp.v2.set(unit.x + dockPoints[i][0], unit.y + dockPoints[i][1]);

            if(pe.within(Tmp.v2, beamRange)) beamUnit(Tmp.v2, ae);
            else {
                docked[i] = null;
                continue;
            }

            if((resupplyTime += Time.delta) >= resupplyDelay){
                resupplyTime = 0;
                float hb = 0;
                if(ae instanceof Hitboxc hu) hb = hu.hitSize();
                if(pe.within(Tmp.v2, range + hb)){
                    resupplyEffect.at(unit.x, unit.y, 15f , Pal.ammo, ae);
                    ae.setAmmo(Math.min(ae.currentAmmo() + (resupply * ae.ammoCapacity()), ae.ammoCapacity()));

                    if(ae instanceof Statusc se){
                        se.apply(StatusEffects.disarmed, Time.toSeconds + resupplyDelay);
                        se.apply(StatusEffects.slow, (Time.toSeconds * 2) + resupplyDelay);
                    }
                }
            }
        }

        if(unit.team == null ){
            Log.err("CarrierResupplyAbility | idk how you manged but unit.team is null");
            return;
        }

        //Look for new units to dock
        if((scanTime += Time.delta) >= scanDelay)return;
        scanTime = 0;
        queue.clear();

        for(int i = 0; i < docked.length; i++) if(docked[i] == null) queue.add(i);

        //todo docks not stealing from each other
        for(int i = 0; i < queue.size; i++){
            int q = queue.get(i);
            float[] out = getDockPoint(i, unit);
            var u =Units.closest(unit.team, out[0], out[1], beamRange, other -> {
                if(queue.size == 0) return false;
                return  other != unit && validUnit(other);
            });

            if(u instanceof  AmmoNyf ae)docked[q] = ae;
        }


    }

    public void beamUnit(Vec2 carrier, AmmoNyf tar){
        if(tar instanceof  Physicsc pc){
            float scaled =   (carrier.dst(pc) + range)/ beamRange;

            Tmp.v1.set(carrier).sub(pc).limit((beamForce + (scaled) * Math.abs(beamScaledForce)) * Time.delta);
            if(!(pc.within(carrier, range))) Tmp.v1.rotate(pc.angleTo(carrier));
            pc.impulseNet(Tmp.v1);

        }
    }

    public boolean validUnit(Unit other){
        return
            other instanceof AmmoEnabledUnitClass ae
            && Objects.equals(ae.ammoType(), NyfUnitTeamMapper.ammoCarrier)
            && ae.ammof() <= 0.8f
            && ( //Unit Ai check, so it ignores anything that isn't trying to resupply 1st, (aka stop beaming moving empty units via beam)
                (other.isCommandable() && (other.command().command == NyfalisUnitCommands.nyfalisRetreatCommand || (other.command().command == UnitCommand.moveCommand && other.command().targetPos == null)))
                || (other.controller() instanceof RetreatAi)
            )
        ;
    }


    public void loadRegions(){
        if(!laserSprite.isEmpty())laserRegion = Core.atlas.find(laserSprite);
        else laserRegion = ((TractorBeamTurret)Blocks.parallax).laser;

        if(!laserEndSprite.isEmpty())laserEndRegion = Core.atlas.find(laserEndSprite);
        else laserEndRegion = ((TractorBeamTurret)Blocks.parallax).laserEnd;

        if(!laserStartSprite.isEmpty())laserStartRegion = Core.atlas.find(laserStartSprite);
        else laserStartRegion = ((TractorBeamTurret)Blocks.parallax).laserStart;

    }

    public float[] getDockPoint(int i, Unit unit){
        float fx = Angles.trnsx(unit.rotation, dockPoints[i][1], dockPoints[i][0]) + unit.x;
        float fy = Angles.trnsy(unit.rotation, dockPoints[i][1], dockPoints[i][0]) + unit.y;

        return new float[]{fx, fy};
    }

    public boolean hasEmptyPort(){
        if(docked == null) return false;
        for(int i = 0; i < dockPoints.length; i++){
            if(docked[i] != null) return false;
        }

        return  true;
    }
}
