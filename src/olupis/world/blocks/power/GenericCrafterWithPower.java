package olupis.world.blocks.power;

import arc.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

public class GenericCrafterWithPower extends GenericCrafter{
    /** The amount of power produced per tick in case of an efficiency of 1.0, which represents 100%. */
    public float powerProduction = 0;
    public float powerProductionBoosted = 1;
    public float minBoosterAmount = 0.2f;
    public Stat generationType = Stat.basePowerGeneration;
    //time before power drops to 0, slowly dropping in between
    public float powerDownTime =  0.001f;


    public GenericCrafterWithPower(String name) {
        super(name);

        consumesPower = false;
        outputsPower = hasPower = true;
    }

    @Override
    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower){
            addBar("power", (GenericCrafterWithPowerBuild entity) -> new Bar(() ->
            Core.bundle.format("bar.poweroutput",
            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
            () -> Pal.powerBar,
            entity::bufferedEfficiency));
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
    }

    public class  GenericCrafterWithPowerBuild extends  GenericCrafterBuild{
       public float peekEff;

        @Override
        public void updateTile(){
            super.updateTile();

            if(efficiency < peekEff){
                peekEff -= powerDownTime;
                if(peekEff <= 0) peekEff = 0;
            }
        }

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction() * bufferedEfficiency() : 0f;
        }

        public float powerProduction(){
            return liquids.currentAmount()  >= minBoosterAmount ? powerProductionBoosted : powerProduction;
        }

        public float bufferedEfficiency(){
            if(efficiency < peekEff) return peekEff;

            peekEff = efficiency;
            return efficiency;
        }
    }
}
