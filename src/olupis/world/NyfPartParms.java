package olupis.world;

import arc.math.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.BaseTurret.*;
import olupis.world.entities.entities.*;
import olupis.world.entities.units.*;

public class NyfPartParms {
    public static final NyfPartParms.NyfPartParams nyfparams = new NyfPartParms.NyfPartParams();

    public static class NyfPartParams{
        public int team;
        public float health,elevation, ammo, floating, treads, speedR, speed, paylCount, tarDist;

        public NyfPartParams set(float health, int team, float elevation, float ammo, float speed, float speedR, float floating, float treads, float paylCount, float tarDist){
            this.health = health;
            this.team = team;
            this.elevation = elevation;
            this.ammo = ammo;
            this.floating = floating;
            this.treads = treads;
            this.paylCount = paylCount;
            this.speed = speed;
            this.speedR = speedR;
            this.tarDist = tarDist;

            return this;
        }


        public NyfPartParams set(Unit unit){
            this.health = unit.healthf();
            this.team = unit.team.id;
            this.elevation = unit.elevation();
            this.speed =  Mathf.clamp(Math.abs(unit.vel().len2() / unit.type.speed));
            this.speedR = unit.speed();
            this.paylCount = unit instanceof OnePayloadUnitClass po ? (po.hasPayload() ? 1 : 0) : unit instanceof Payloadc p ? p.payloadUsed() / unit.type().payloadCapacity : 0;
            this.tarDist = unit.dst(unit.aimX, unit.aimY) / unit.range();

            if(unit.type instanceof  NyfalisUnitType nyf){
                this.ammo = nyf.partAmmo(unit);
                if(nyf instanceof  DuckyTubeTankUnitType dt){
                    this.floating = dt.fetchFloating(unit);
                    this.treads = dt.fetchFloating(unit);
                } else this.floating = this.treads = 0;
            }  else {
                this.floating = this.treads = 0;
                this.ammo = unit.ammo();
            }

            return this;
        }

        public NyfPartParams set(Building build, float tarDist, float ammo){
            this.health = build.healthf();
            this.team = build.team.id;
            this.elevation = this.speed = this.speedR = this.floating = this.treads = 0;

            this.paylCount = build.isPayload() ? build.getPayload().size() : 0;
            this.tarDist = tarDist;
            this.ammo = ammo;


            return this;
        }
    }

    public interface NyfPartProgress {
        NyfPartProgress
            team = p -> nyfparams.team,
            elevation = p -> nyfparams.elevation,
            ammo = p -> nyfparams.ammo,
            floating = p -> nyfparams.floating,
            treads = p -> nyfparams.treads,
            payCount = p -> nyfparams.paylCount,
            speed = p -> nyfparams.speed,
            tarDist = p -> nyfparams.tarDist
        ;

        DrawPart.PartProgress
            elevationP = p-> nyfparams.elevation,
            floatingP = p-> nyfparams.floating,
            speedP = p-> nyfparams.speed,
            treadsP = p-> nyfparams.treads,
            payCountP = p -> nyfparams.paylCount,
            tarDistP = p -> nyfparams.tarDist
        ;

        float get(NyfPartParams p);
    }

}
