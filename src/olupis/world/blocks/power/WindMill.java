package olupis.world.blocks.power;

import arc.*;
import arc.math.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.world.entities.parts.*;

public class WindMill extends PowerGenerator {
    //ThermalGenerator but Attribute multiples a base number and doesn't require the attribute tiles
    public Attribute attribute = Attribute.heat;
    public float attributeMul = 0.5f, attributeMulDisplay = 10f, attributeMulStat = 0.1f;
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
            new VariantableDrawRegion(2, "-bottom"),
            new DrawLiquidTile(Liquids.oil, 2f){{alpha = 0.8f;}},
            new VariantableDrawRegion(5),
            new DrawBlurSpin("-rotator", 0.6f * 9f){{blurThresh =  0.01f;}}
        );
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
