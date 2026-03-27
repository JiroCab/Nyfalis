package olupis.world.blocks.calyx;

import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.world.blocks.calyx.ineternal.*;

import static olupis.NyfalisVars.nyfRule;
import static olupis.world.EnvUpdater.queue;

public class GrowingHeart extends Block{
    public double rootChance = 0.010 / 60f;
    public Seq<Block> roots = new Seq<>();

    public GrowingHeart(String name){
        super(name);
        update = solid = destructible = configurable = true;

        config(Integer.class, (GrowingHeartBuilding build, Integer i) -> {
            if(!configurable) return;
            build.calyxSpecies = i;
            if (build.module() != null){
                build.module().species = i;
                build.module().graph.species = i;
                build.module().graph.reflow(build);
            }
        });
    }

    public class GrowingHeartBuilding extends Building implements Calyxian{
        public int laziness = 0;

        @Override
        public void update(){
            super.update();
            if(laziness >= 60){
                laziness = 69420;
            } else laziness += (int)Mathf.randomSeed(1, 5);

            if(roots.any() && !NyfalisBlocks.spreadingTiles.contains(tileOn().overlay()) && Mathf.chance(rootChance * nyfRule.calyxSpreadingFactor)){
                queue(roots.random()).add(tileOn().pos());
            }
        }

        @Nullable
        public CalyxModule calyxModule;
        public int calyxSpecies;

        @Override
        public CalyxModule module(){
            return calyxModule;
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
            @Nullable Building out =  super.init(tile, team, shouldAdd, rotation);
            if(initialized) {
                //reinit calyx graph like power one bc idk
                calyxModule.init = false;
                new CalyxGraph(calyxSpecies).add(self());
            }

            return out;
        }

        @Override
        public Building create(Block block, Team team){
            calyxModule = new CalyxModule(calyxSpecies);
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
            updateCalyxianModule();
        }

        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();
            removedCalyxianModule();
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            removedCalyxianModule();
        }

        @Override
        public void changeTeam(Team next){
            Team last = this.team;
            super.changeTeam(next);

            if (last == next) return;

            if(module() != null)  {
                for(int i = 0; i < module().links.size; i++){
                    Building other = Vars.world.build(module().links.get(i));
                    if(other instanceof Calyxian cal){
                        module().links.removeIndex(i);
                        cal.module().links.removeValue(this.pos());
                        (new CalyxGraph(calyxSpecies)).remove(other);
                        --i;
                    }
                }
            }
        }

        @Override
        public void buildConfiguration(Table table){

            calyxBuildConfiguration(table);
        }

        @Override
        public int calyxSpeciesConfig(){
            return calyxSpecies;
        }
        @Override
        public void calyxSpeciesConfig(int species){
            this.calyxSpecies = species;
        }

        @Override
        public void afterPickedUp(){
            if(calyxModule != null){
                this.calyxModule = new CalyxModule(calyxSpecies);
                this.calyxModule.graph.species = calyxSpecies;
                this.calyxModule.graph.clear();
            }
        }


        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(calyxSpecies);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if(revision >= 1){
                calyxSpecies = read.i();
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
        public Building getHeart(){
            return this;
        }

        @Override
        public boolean isAlive(){
            return true;
        }

        @Override
        public boolean isHeart(){
            return true;
        }

    }
}
