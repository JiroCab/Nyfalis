package olupis.world.blocks.calyx;

import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import olupis.world.blocks.calyx.GrowingHeart.*;
import olupis.world.blocks.calyx.ineternal.*;
import olupis.world.blocks.defence.*;

public class GrowingCore extends PropellerCoreTurret{

    public GrowingCore(String name) {
        super(name);
    }


        @Override
        public void configs(){
            consumePowerDynamic((GrowingCoreBuild b) -> b.producingUnits() ? unitPowerCost : 0);
            config(IntSeq.class, (GrowingCoreBuild build, IntSeq s) -> {
                if(!configurable) return;
                if(s.size > 2 || s.size == 0) return;

                int mI =  s.get(0);
                if(build.currentMode != mI){
                    build.currentMode = mI < 0 || mI > modes.size ? 0 : mI;
                }

                int sI = s.get(1);
                build.calyxSpecies = sI;
                if (build.module() != null){
                    build.module().species = sI;
                    build.module().graph.species = sI;
                }
            });

            config(UnitCommand.class, (GrowingCoreBuild build, UnitCommand command) -> build.command = command);

            configClear((GrowingCoreBuild build) -> build.currentMode = 0);
        }



    public class GrowingCoreBuild extends PropellerCoreTurretBuild implements Calyxian{
        @Nullable
        public CalyxModule calyxModule;
        public int calyxSpecies = 0;


        @Override public CalyxModule module(){
            return calyxModule;
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
            @Nullable Building out =  super.init(tile, team, shouldAdd, rotation);
            if(initialized) {
                //reinit calyx graph like power one bc idk
                calyxModule.init = false;
                new CalyxGraph().add(self());
            }

            return out;
        }

        @Override
        public Building create(Block block, Team team){
            calyxModule = new CalyxModule();
            calyxModule.graph.species =  calyxModule.species = calyxSpecies;
            calyxModule.graph.add(self());

            return super.create(block, team);

        }

        @Override
        public void add(){
            super.add();
            module().graph.checkAdd();
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            if(calyxModule != null) updateCalyxianModule();
        }

        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();
            if(calyxModule != null) removedCalyxianModule();
        }

        @Override
        public void changeTeam(Team next){
            Team last = this.team;
            super.changeTeam(next);

            if (last == next) return;

            if(module() != null)  {
                for(int i = 0; i < module().links.size; i++){
                    Building other = Vars.world.build(module().links.get(i));
                    if(other != null && other instanceof Calyxian cal){
                        module().links.removeIndex(i);
                        cal.module().links.removeValue(this.pos());
                        (new CalyxGraph()).remove(other);
                        --i;
                    }
                }
            }
        }

        @Override
        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            table.row();
            table.table(t -> {
                t.defaults().center();
                t.image().color(team.color.cpy().lerp(Color.black, 0.25f).a(0.55f)).height(2f).center().growX().row();
            }).growX().row();

            calyxBuildConfiguration(table);
        }

        @Override
        public int calyxSpeciesConfig(){
            return calyxSpecies;
        }
        @Override
        public void calyxSpeciesConfig(int species){
            configure(IntSeq.with(currentMode, species));
            this.calyxSpecies = species;
        }

        @Override
        public void configMode(int mode){
            currentMode = mode;
            configure(IntSeq.with(mode, calyxSpecies));
        }

        @Override
        public void afterPickedUp(){
            if(calyxModule != null){
                this.calyxModule = new CalyxModule();
                this.calyxModule.graph.clear();
            }
        }

        @Override
        public void placed(){
            super.placed();
        }

        @Override
        public Building build(){
            return this;
        }

        @Override
        public @Nullable Building getHeart(){
            return this;
        }

        @Override
        public boolean isHeart(){
            return true;
        }

        @Override
        public byte version(){
            return 3;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(calyxSpecies);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if(revision >= 3){
                calyxSpecies = read.i();
            }
        }
    }

}
