package olupis.world.blocks.turret;

import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.ui.*;
import olupis.world.*;
import olupis.world.blocks.turret.LineOfSightItemTurret.*;
import olupis.world.blocks.turret.NyfalisItemTurret.*;

import static olupis.world.NyfWorldFuckingHelper.renderConfigIndicator;

public class LineOfSightLiquidTurret extends  NyfalisLiquidTurret{
    public boolean angleCheck = false;

    public LineOfSightLiquidTurret(String name) {
        super(name);
        configurable = copyConfig = saveConfig = clearOnDoubleTap =  true;
        config(Boolean.class, (LineOfSightLiquidTurretBuild build, Boolean b )-> build.ignoreLOS = b);
        configClear((LineOfSightLiquidTurretBuild build) -> build.ignoreLOS = false);
    }

    public class LineOfSightLiquidTurretBuild extends NyfalisLiquidTurretBuild{
        public boolean ignoreLOS = false;

        @Override
        public void placed(){
            super.placed();
            ignoreLOS = false;
        }

        @Override
        protected Posc findEnemy(float range){
            if(!angleCheck) return super.findEnemy(range);
            if(ignoreLOS) return super.findEnemy(range);

            if(targetAir && !targetGround){
                return Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() &&  unitFilter.get(e) && !NyfWorldFuckingHelper.rayCheck(this, e, b -> b.solid), unitSort);
            }else{
                var ammo = peekAmmo();
                boolean buildings = targetGround && targetBlocks && (ammo == null || ammo.targetBlocks), missiles = ammo == null || ammo.targetMissiles;
                return Units.bestTarget(team, x, y, range,
                e -> !NyfWorldFuckingHelper.rayCheck(this, e, b -> b.solid) && !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround) && (missiles || !(e instanceof TimedKillc)),
                b -> buildings && buildingFilter.get(b), unitSort);
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
                        TextureRegionDrawable icon = !ignoreLOS ? Icon.line : Icon.eyeSmall;
                        ImageButton button = t.button(icon, Styles.clearNoneTogglei, 45f, () ->{
                            Call.tileConfig(Vars.player, this, !ignoreLOS);
                            rebuild[0].run();
                        }).scaling(Scaling.bounded).group(group).get();

                        button.update(() -> button.setChecked(ignoreLOS));
                    });
                };
                rebuild[0].run();
            });
        }

        @Override
        public Object config(){
            return ignoreLOS;
        }

        @Override
        public byte version(){
            return 3;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(ignoreLOS);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(version() >= 3){
                ignoreLOS = read.bool();
            }
        }

        @Override
        public void draw(){
            super.draw();
            renderConfigIndicator(this, ignoreLOS ? Icon.eyeSmall.getRegion(): Icon.line.getRegion() );
        }
    }
}
