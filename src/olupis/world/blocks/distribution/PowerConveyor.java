package olupis.world.blocks.distribution;

import mindustry.world.meta.*;

public class PowerConveyor extends VaraintConveryor {
    public float poweredSpeed = 1f, unpoweredSpeed = 0.5f, displayedSpeedPowered = displayedSpeed;
    /*Minimum threshold of power before we're considered unpowered*/
    public float powerRequired = 20f / 60f;

    public PowerConveyor(String name){
        super(name);
        speed = unpoweredSpeed;
    }

    @Override
    public void setStats(){
        super.setStats();
//TODO FIX
        stats.add(new Stat("olupis-powerforgiveness", StatCat.power), powerRequired * -120f , StatUnit.powerSecond);
        stats.add(new Stat("olupis-itemsmovedpowered", StatCat.items), displayedSpeedPowered, StatUnit.itemsSecond);
    }

    public class PowerConveyorBuild extends ConveyorBuild {
        @Override
        public void updateTile(){
            float pwr = power.status * (block.consPower.buffered ? block.consPower.capacity : 1f);
            speed = pwr <= powerRequired ? unpoweredSpeed : poweredSpeed;

            super.updateTile();
            noSleep();
        }

        @Override
        public BlockStatus status(){
            float pwr = power.status * (block.consPower.buffered ? block.consPower.capacity : 1f);
            if (pwr >= powerRequired && pwr != 1 ) return BlockStatus.noOutput;
            if (pwr >= powerRequired ) return BlockStatus.active;
            return BlockStatus.noInput;
        }
    }
}
