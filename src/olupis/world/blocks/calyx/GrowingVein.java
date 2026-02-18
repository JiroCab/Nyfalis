package olupis.world.blocks.calyx;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import olupis.world.*;
import olupis.world.blocks.calyx.ineternal.*;

public class GrowingVein extends Block{
    public Color heartlessColour = Color.black;
    public boolean heartlessBlends = false;
    public TextureRegion heartedRegion;
    public TextureRegion[] speciesRegion ;

    public GrowingVein(String name) {
        super(name);
        update = true;

    }

    @Override
    public void load(){
        super.load();
        heartedRegion = Core.atlas.find(name + "-heart");
    }

    public class GrowingVeinBulding extends Building implements Calyxian{
        @Nullable public CalyxModule calyxModule;

        @Override
        public void draw(){
            if(getHeart() == null){
                Draw.color(heartlessColour);
                if(heartedRegion.found()) Draw.rect(heartedRegion, this.x, this.y, this.drawrot());
                if(!heartlessBlends) Draw.reset();
            } else {
                Draw.color(NyfWorldFuckingHelper.calyxSpeciesColors(calyxModule.graph.species));
            }
            super.draw();
            Draw.reset();
        }

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

            //randomized which one it adds itself too
            Seq<Building> pro = proximity.copy();
            pro.sort( i -> Mathf.randomSeed(pos()));
            for(Building building : pro){
                if(!(building instanceof  Calyxian b)) continue;
                b.module().graph.add(this);

            }
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
