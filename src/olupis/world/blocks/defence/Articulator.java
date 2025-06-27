package olupis.world.blocks.defence;

import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.*;
import mindustry.world.meta.BlockGroup;
import olupis.world.blocks.*;

import static mindustry.Vars.world;

public class Articulator extends Block {
    public DrawBlock drawer = new DrawDefault();
    public int tier = 1;

    public Articulator(String name){
        super(name);
        rotate = rotateDraw = destructible = update = true;
        quickRotate = solid = false;
        group = BlockGroup.units;
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }


    public class ArticulatorBuild extends Building {
        public Moduleable link;
        public int lastChange = -2;

        public int tier(){
            return tier;
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        /*There's probably a better way to do this, but I can't be asked to look for it */
        public void findLink(){
            if(link != null){
                link.removeModule(this);
            }
            link = this.front() instanceof Moduleable bld  ? bld : null;
            if(link != null){
                link.updateModules(this);
            }
        }

        @Override
        public void onRemoved(){
            super.onRemoved();

            link = this.front() instanceof Moduleable bld ? bld : null;
            if(link != null){
                link.removeModule(this);
            }
        }

        @Override
        public void placed() {
            super.placed();

            link = this.front() instanceof Moduleable bld ? bld : null;
            if(link != null){
                link.updateModules(this);
            }
        }

        @Override
        public void updateTile() {
            if (lastChange != world.tileChanges) {
                lastChange = world.tileChanges;
                findLink();
            }
        }

    }
}
