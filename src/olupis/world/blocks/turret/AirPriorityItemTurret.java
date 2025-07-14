package olupis.world.blocks.turret;

import arc.math.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

public class AirPriorityItemTurret extends NyfalisItemTurret {
    public float discoveryTime = 60f * 60f * 1f;
    public float illuminateTime = 30f;
    public boolean slowFogOfWar = false;

    public AirPriorityItemTurret(String name){
        super(name);
    }

    public class AirPriorityTurretItemBuild extends ItemTurretBuild{

        public float progressFog;
        public float progressLight;
        public float lastRadius = 0f;
        public float smoothEfficiency = 1f;

        @Override
        public float fogRadius(){
            if(!slowFogOfWar)return super.fogRadius();
            return fogRadius * progressFog * smoothEfficiency;
        }

        @Override
        public void updateTile(){
            if(slowFogOfWar && state.rules.fog){
                smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, 1, 0.05f);

                if(Math.abs(fogRadius() - lastRadius) >= 0.5f){
                    Vars.fogControl.forceUpdate(team, this);
                    lastRadius = fogRadius();
                }

                progressFog += this.delta() / discoveryTime;
                progressFog = Mathf.clamp(progressFog);

            }
            super.updateTile();
        }

        @Override
        public void drawSelect(){
            if(slowFogOfWar && state.rules.fog)Drawf.dashCircle(x, y, fogRadius() * tilesize, Pal.metalGrayDark);
            super.drawSelect();
        }


        @Override
        public void write(Writes write){
            super.write(write);

            if(slowFogOfWar)write.f(progressFog);
        }

        @Override
        public void drawLight() {
            boolean check = (!hasPower || power.status >= 0.5f) && (hasAmmo());
            if(emitLight){
                progressLight = Mathf.lerpDelta(progressLight, check ? lightRadius : 0, this.delta() / illuminateTime);
                if(progressLight >= 0)Drawf.light(x, y, progressLight, lightColor, lightColor.a);
            }
            super.drawLight();
        }

        @Override
        public byte version(){
            return 3;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 3) if(slowFogOfWar)progressFog = read.f();
        }

        @Override
        protected void findTarget(){
            float range = range();

            if(targetAir && !targetGround){
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
            }else{
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
                //hit air 1st before doing ground
                if(target == null) target = Units.bestTarget(team, x, y, range, e -> !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> targetGround && buildingFilter.get(b), unitSort);
            }

            if(target == null && canHeal()){
                target = Units.findAllyTile(team, x, y, range, b -> b.damaged() && b != this);
            }
        }
    }
}