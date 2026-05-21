package olupis.world.blocks.turret;

import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import olupis.world.*;

import static mindustry.Vars.*;

public class AirPriorityItemTurret extends NyfalisItemTurret {
    public float discoveryTime = 60f * 60f * 1f;
    public float illuminateTime = 30f;
    public boolean slowFogOfWar = false, supportsModes = true;
    public static TextureRegionDrawable[] icon;

    public AirPriorityItemTurret(String name){
        super(name);

        if(supportsModes){
            configurable = copyConfig = saveConfig = clearOnDoubleTap =  true;
            config(Byte.class, (AirPriorityTurretItemBuild build, Byte b )-> build.attackMode = b);
            configClear((AirPriorityTurretItemBuild build) -> build.attackMode = 0);
        }
        icon = new TextureRegionDrawable[]{Icon.modeAttack, Icon.planeOutline, Icon.turret};
    }

    @Override
    public void load(){
        super.load();
        icon = new TextureRegionDrawable[]{Icon.modeAttack, Icon.planeOutline, Icon.turret};
    }

    public class AirPriorityTurretItemBuild extends ItemTurretBuild{

        public float progressFog;
        public float progressLight;
        public float lastRadius = 0f;
        public float smoothEfficiency = 1f;
        //{both, airOnly, GroundOnly}
        public int attackMode = 0;


        @Override
        public void placed(){
            super.placed();
            attackMode = 0;
        }

        @Override
        public float fogRadius(){
            if(!slowFogOfWar) return super.fogRadius();
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
            if(slowFogOfWar && state.rules.fog) Drawf.dashCircle(x, y, fogRadius() * tilesize, Pal.metalGrayDark);
            super.drawSelect();
        }


        @Override
        public void write(Writes write){
            super.write(write);

            if(slowFogOfWar) write.f(progressFog);
            if(supportsModes) write.i(attackMode);
        }

        @Override
        public void drawLight(){
            boolean check = (!hasPower || power.status >= 0.5f) && (hasAmmo());
            if(emitLight){
                progressLight = Mathf.lerpDelta(progressLight, check ? lightRadius : 0, this.delta() / illuminateTime);
                if(progressLight >= 0) Drawf.light(x, y, progressLight, lightColor, lightColor.a);
            }
            super.drawLight();
        }

        @Override
        public byte version(){
            return 5;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 3 && slowFogOfWar) progressFog = read.f();
            if(supportsModes){
                if(revision == 4) attackMode = read.b();
                if(revision >= 5) attackMode = read.i();
            }
        }

        @Override
        protected void findTarget(){
            float range = range();

            if(targetAir && (!targetGround || attackMode == 1)){
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
            }else{
                if(attackMode != 2) target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
                //hit air 1st before doing ground
                if(target == null) target = Units.bestTarget(team, x, y, range, e -> !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> targetGround && buildingFilter.get(b), unitSort);
            }

            if(target == null && canHeal()){
                target = Units.findAllyTile(team, x, y, range, b -> b.damaged() && b != this);
            }
        }


        @Override
        public void buildConfiguration(Table table){
            table.table(par -> {
                Runnable[] rebuild = {null};
                rebuild[0] = () -> {
                    par.clear();
                    par.table(t -> {
                        t.background(Styles.black6);
                        var group = new ButtonGroup<ImageButton>();
                        group.setMinCheckCount(0);

                        for(byte i = 0; i < 3; i++){
                            byte ii = i;
                            ImageButton button = t.button(icon[ii], Styles.clearNoneTogglei, 45f, () -> {
                                Call.tileConfig(Vars.player, this, ii);
                                rebuild[0].run();
                            }).scaling(Scaling.bounded).group(group).get();

                            button.update(() -> button.setChecked(attackMode == ii));
                        }

                    });
                };
                rebuild[0].run();
            });
        }

        @Override
        public Object config(){
            return attackMode;
        }

        @Override
        public void draw(){
            super.draw();

            NyfWorldFuckingHelper.renderConfigIndicator(this, icon[attackMode].getRegion());
        }


    }
}