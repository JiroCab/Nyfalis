package olupis.world.blocks.power;

import arc.Core;
import arc.math.Mathf;
import arc.struct.EnumSet;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class WindMill extends PowerGenerator {
    //ThermalGenerator but Attribute multiples a base number and doesn't require the attribute tiles
    public Attribute attribute = Attribute.heat;
    public float displayEfficiencyScale = 1f;
    public float attributeMul = 0.5f;
    public final boolean displayEfficiency = true;
    public final Effect generateEffect = Fx.none;
    public final float effectChance = 0.05f;
    public float boosterMultiplier = 1f;

    public WindMill(String name){
        super(name);
        flags = EnumSet.of();
        envEnabled ^= Env.space;
        group = BlockGroup.power;

        drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawLiquidTile(Liquids.oil, 2f){{alpha = 0.8f;}},
            new DrawDefault(),
            new DrawBlurSpin("-rotator", 0.6f * 9f){{blurThresh =  0.01f;}}
        );
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.affinities, attribute, floating, size * size * displayEfficiencyScale, !displayEfficiency);
        stats.remove(generationType);
        stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(displayEfficiency && sumAttribute(attribute, x, y) != 0){
            drawPlaceText(Core.bundle.formatFloat("bar.efficiency", sumAttribute(attribute, x, y) * 100, 1), x, y, valid);
        }
        if (Core.settings.getBool("nyfalis-debug")) drawPlaceText(Core.bundle.formatFloat("bar.efficiency", ((attribute.env() + sumAttribute(attribute, x, y)) * powerProduction + powerProduction ) , 1),  x , y - 2, valid);
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
            productionEfficiency = (sum * attributeMul) + attribute.env() + 1f;
            if(boosterMultiplier > 1f){
                productionEfficiency *= Mathf.lerp(1f, boosterMultiplier, optionalEfficiency);
            }


            if(productionEfficiency > 0.1f && Mathf.chanceDelta(effectChance)){
                generateEffect.at(x + Mathf.range(3f), y + Mathf.range(3f));
            }
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            sum = sumAttribute(attribute, tile.x, tile.y);
        }

    }
}
