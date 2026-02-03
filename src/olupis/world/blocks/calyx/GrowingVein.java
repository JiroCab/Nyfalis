package olupis.world.blocks.calyx;

import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import olupis.world.blocks.calyx.ineternal.*;

public class GrowingVein extends Block{
    public GrowingVein(String name) {
        super(name);
        update = true;
    }
    //todo draw when no connection

    public class GrowingVeinBulding extends Building implements Calyxian{
        @Nullable
        public CalyxModule calyxModule;

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
                new PowerGraph().add(self());
            }

            return out;
        }

        @Override
        public Building create(Block block, Team team){
            calyxModule = new CalyxModule();
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
        public void placed(){
            super.placed();
            module().graph.reflow(this);
        }

        @Override
        public Building build(){
            return this;
        }

        @Override
        public @Nullable Building getHeart(){
            if(module() == null || module().graph == null) return null;
            return module().graph.getHeart();
        }
    }
}
