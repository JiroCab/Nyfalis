package olupis.world.entities.abilities;

import arc.graphics.g2d.*;
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
import olupis.input.*;
import olupis.world.entities.entities.*;
import olupis.world.interfaces.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.*;

public class CarrierResupplyAbility extends Ability{
    public int tier = 1;
    public float
        range = Vars.tilesize * 2f,
        beamRange = Vars.tilesize * 30,
        beamForce = 5, //where tf u going
        beamScaledForce = 10,
        resupply = 2.5f * tier;

    //x, y
    public float[][] dockPoints = {{0, 0}};
    @Nullable public AmmoNyf[] docked;


    public CarrierResupplyAbility(){
        //Empty for now
        //TODO: if ammo rule is on, they resupply all units in range
        display = false;
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);

        stroke(5f);
        color(unit.team.color, 0.3f);
        for(int i = 0; i < docked.length; i++){
            if(docked[i] == null) continue;
            if(!(docked[i] instanceof  Posc pe)) continue;
            Tmp.v2.set(unit.x + dockPoints[i][0], unit.y + dockPoints[i][1]);
            line(Tmp.v2.x, Tmp.v2.y, pe.x(), pe.y());
        }
        Draw.reset();
    }

    @Override
    public void update(Unit unit){
        if(docked == null) docked = new AmmoNyf[dockPoints.length];

        for(int i = 0; i < docked.length; i++){
            if(docked[i] == null) continue;
            AmmoNyf ae = docked[i];
            if(ae.ammof() >= 1){
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

            if(pe.within(Tmp.v2, range)){
                Fx.itemTransfer.at(unit.x, unit.y, 15f , Pal.ammo, ae);
                ae.setAmmo(Math.min(ae.currentAmmo() + (resupply * ae.ammoCapacity()), ae.ammoCapacity()));

                if(ae instanceof Statusc se){
                    se.apply(StatusEffects.disarmed, Time.toSeconds);
                    se.apply(StatusEffects.slow, Time.toSeconds * 2);
                }
            }
        }

        IntSeq queue = new IntSeq();
        for(int i = 0; i < docked.length; i++){
            if(docked[i] == null) queue.add(i);
        }

        Units.nearby(unit.team, unit.x, unit.y, beamRange, other -> {
            if(queue.size == 0) return;
            if(
                other != unit && other instanceof AmmoEnabledUnitClass ae && ae.ammof() <= 0.8f
            ){
                docked[queue.first()] = ae;
                queue.removeIndex(0);
            }
        });

    }

    public void beamUnit(Vec2 carrier, AmmoNyf tar){
        if(tar instanceof  Physicsc pc){
            float scaled =   (carrier.dst(pc))/ range;
            Tmp.v1.set(carrier).sub(pc).limit((beamForce + (scaled) * Math.abs(beamScaledForce)) * Time.delta);
            if(!(pc.within(carrier, range))) Tmp.v1.rotate(pc.angleTo(carrier));
            pc.impulseNet(Tmp.v1);

        }
    }

    public CarrierResupplyAbility(int tier){
        this.tier = tier;
        display = false;
    }
}
