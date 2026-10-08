package olupis.world.blocks.calyx;

import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.world.blocks.calyx.ineternal.*;
import olupis.world.blocks.defence.*;

import java.util.*;

import static olupis.NyfalisVars.nyfRule;
import static olupis.world.EnvUpdater.*;

public class GrowingCore extends PropellerCoreTurret{
    public double rootChance = 0.010 / 60f;
    public Seq<Block> roots = new Seq<>();

    public GrowingCore(String name) {
        super(name);
    }

    @Override
    public void configs(){
            consumePowerDynamic((GrowingCoreBuild b) -> b.producingUnits() ? unitPowerCost : 0);
            config(String.class, (GrowingCoreBuild build, String s) -> {
                if(!configurable) return;
                if(s == null || s.isEmpty()) {
                    Log.err("(nyfalis) " + build.toString() + " string is null wtf how");

                }else {
                    if(modes.contains(m -> Objects.equals(m.name, s))){
                        build.currentMode = modes.find(m -> Objects.equals(m.name, s));
                    } else if (calyxSpeciesNames.contains( m -> Objects.equals(m, s))){
                        @Nullable  String calyx = calyxSpeciesNames.find( m -> Objects.equals(m, s));
                        if(calyx != null)build.calyxSpecies = build.module().species = build.module().graph.species = calyx;
                    }
                }

            });

            config(UnitCommand.class, (GrowingCoreBuild build, UnitCommand command) -> build.command = command);

            configClear((GrowingCoreBuild build) -> build.currentMode = modes.get(0));
        }

    public class GrowingCoreBuild extends PropellerCoreTurretBuild implements Calyxian{
        @Nullable
        public CalyxModule calyxModule;
        public String calyxSpecies = "";

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
        public String calyxSpeciesConfig(){
            return calyxSpecies;
        }

        @Override
        public void calyxSpeciesConfig(String species){
            configure(species);
        }

        @Override
        public void configMode(CoreMode mode){
            configure(mode);
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
        public boolean isAlive(){
            return true;
        }

        @Override
        public byte version(){
            return 4;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(calyxSpecies);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if(revision >= 4) calyxSpecies = read.str();
            if(revision <= 3) calyxSpecies = calyxSpeciesNames.get(read.i());
        }
    }

}
