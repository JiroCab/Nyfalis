package olupis.world.blocks.calyx;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.*;
import olupis.world.blocks.calyx.ineternal.*;
import olupis.world.entities.*;

import static olupis.NyfalisVars.nyfRule;
import static olupis.world.EnvUpdater.queue;

public class GrowingTurret extends Turret{
    public BulletType shootType;

    public boolean heartlessBlends = false;
    public TextureRegion heartedRegion;
    public TextureRegion[] speciesRegion ;
    public Seq<Block>  replacements = new Seq<>(); //todo, also the actual spreading in env updater :p
    public double rootChance = 0.010 / 60f;
    public Seq<Block>  roots = new Seq<>();

    public GrowingTurret(String name){
        super(name);

    }


    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.ammo, NyfalisStats.ammo(ObjectMap.of(this, shootType)));
    }

    public void limitRange(float margin){
        limitRange(shootType, margin);
    }

    public class GrowingTurretBuild extends TurretBuild implements Calyxian{
        @Nullable
        public CalyxModule calyxModule;
        public boolean hasHeart = false;
        public int laziness = 0;

        @Override
        public void updateTile(){
            unit.ammo(isAlive() ? unit.type().ammoCapacity : 0f);

            super.updateTile();
        }

        @Override
        public double sense(LAccess sensor){
            return switch(sensor){
                case ammo -> isAlive() ? 1 : 0f;
                case ammoCapacity -> 1;
                default -> super.sense(sensor);
            };
        }

        @Override
        public BulletType useAmmo(){
            //nothing used directly
            return shootType;
        }

        @Override
        public boolean hasAmmo(){
            return isAlive();
        }

        @Override
        public BulletType peekAmmo(){
            return shootType;
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
            laziness = 69420;
        }

        @Override
        public void update(){
            super.update();
            if(laziness >= 60){
                hasHeart = getHeart() != null;
                laziness = 69420;
            } else laziness += (int)Mathf.randomSeed(1, 5);

            if(roots.any() && !NyfalisBlocks.spreadingTiles.contains(tileOn().overlay()) && Mathf.chance(rootChance * nyfRule.calyxSpreadingFactor)){
                queue(roots.random()).add(tileOn().pos());
            }
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
        public boolean isAlive(){
            return hasHeart;
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
