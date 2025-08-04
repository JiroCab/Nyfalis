package olupis.world.entities.parts;

import mindustry.entities.part.DrawPart;

public class NyfPartParms {
    public static final NyfPartParms.NyfPartParams nyfparams = new NyfPartParms.NyfPartParams();

    public static class NyfPartParams{
        public int team, paylCount;
        public float health,elevation, ammo, floating, treads, speedR, speed;

        public NyfPartParams set(float health, int team, float elevation, float ammo, float speed, float speedR, float floating, float treads, int paylCount){
            this.health = health;
            this.team = team;
            this.elevation = elevation;
            this.ammo = ammo;
            this.floating = floating;
            this.treads = treads;
            this.paylCount = paylCount;
            this.speed = speed;
            this.speedR = speedR;

            return this;
        }

        public NyfPartParams set(float health, int team, float elevation, float ammo, float speed, float speedR){
            return set(health, team, elevation, ammo, speed, speedR, 0, 0, 0);
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
            speed = p -> nyfparams.speed;
        ;

        DrawPart.PartProgress
                elevationP = p-> nyfparams.elevation,
                floatingP = p-> nyfparams.floating,
                speedP = p-> nyfparams.speed,
                treadsP = p-> nyfparams.treads
        ;

        float get(NyfPartParams p);
    }

}
