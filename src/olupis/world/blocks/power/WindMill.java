package olupis.world.blocks.power;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.world.consumer.*;
import olupis.world.entities.parts.*;

public class WindMill extends PowerGenerator {
    //ThermalGenerator but Attribute multiples a base number and doesn't require the attribute tiles
    public Attribute attribute = Attribute.heat;
    public float attributeMul = 0.5f, attributeMulDisplay = 10f, attributeMulStat = 0.1f;
    public final boolean displayEfficiency = true;
    public final Effect generateEffect = Fx.none;
    public final float effectChance = 0.05f;
    public float boosterMultiplier = 6.6f;
    public @Nullable ConsumeLiquidBase lubrication;

    public WindMill(String name){
        super(name);
        flags = EnumSet.of();
        envEnabled ^= Env.space;
        group = BlockGroup.power;

        drawer = new DrawMulti(
            new VariantableDrawRegion(2, "-bottom"),
            new DrawLiquidTile(){{alpha = 0.85f; padding = 2f;}},
            new VariantableDrawRegion(5),
            new DrawBlurSpin("-rotator", 0.6f * 9f){{blurThresh =  0.01f;}

                @Override
                public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
                    Draw.rect(region, plan.drawx()+ x, plan.drawy() + y, 0);
                }
            }
        );
    }

    @Override
    public void init(){
        super.init();
        if (lubrication == null) {
            lubrication = findConsumer(c -> c instanceof ConsumeLubricant);
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.affinities, attribute, floating, size * size * attributeMulStat, !displayEfficiency);
        stats.remove(generationType);
        stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(displayEfficiency && sumAttribute(attribute, x, y) != 0){
            drawPlaceText(Core.bundle.formatFloat("bar.nyfalis-windmill", sumAttribute(attribute, x, y) * attributeMulDisplay, 0), x, y, valid);
        }
    }

    @Override
    public boolean canReplace(Block other){
        if(other instanceof  Wire) return true;
        return super.canReplace(other);
    }

    public  class windMillBuild extends GeneratorBuild{
        public float sum;

        @Override
        public void updateTile(){
            productionEfficiency = (sum * attributeMul) + attribute.env() + 1f * updateLube();


            if(productionEfficiency > 0.1f && Mathf.chanceDelta(effectChance)){
                generateEffect.at(x + Mathf.range(3f), y + Mathf.range(3f));
            }
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            sum = sumAttribute(attribute, tile.x, tile.y);
        }

        @Override
        public float getPowerProduction(){
            return powerOfFive(super.getPowerProduction() * 60)/60;
        }

        //i cant be bother to actaully be smart so heres a dumb work around
        public float powerOfFive(float value){
            if(value % 5 == 0 ) return value;
            float t  = Mathf.round(value);
            while(t % 5 != 0){
                t--;
                if(t % 5 == 0) return t;
                if(t <= (value -10)) return t;
            }

            return value;
        }


        public float updateLube(){
            float out = 1f;
            if(lubrication == null) return out;
            if(this.liquids == null) return out;
            if(lubrication.efficiency(this) == 0) return out;
            if(efficiency == 0) return out;

            if(lubrication instanceof ConsumeLiquidFilter filter){
                out +=  Math.abs(filter.getConsumed(this).heatCapacity) * (lubrication.amount * lubrication.efficiency(this)) * boosterMultiplier;

            }
            return out;
        }

    }
}
