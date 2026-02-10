package olupis.world.blocks.calyx;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import olupis.world.blocks.calyx.ineternal.*;
import olupis.world.blocks.defence.*;

public class GrowingCore extends PropellerCoreTurret{

    public GrowingCore(String name) {
        super(name);
    }



    public class GrowingCoreBuild extends PropellerCoreTurretBuild implements Calyxian{
        @Nullable
        public CalyxModule calyxModule;


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
        public void afterPickedUp(){
            if(calyxModule != null){
                this.calyxModule = new CalyxModule();
                this.calyxModule.graph.clear();
            }
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
            return this;
        }

        @Override
        public boolean isHeart(){
            return true;
        }
    }

}
