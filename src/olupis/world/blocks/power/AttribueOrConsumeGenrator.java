package olupis.world.blocks.power;

import mindustry.type.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;
import olupis.content.*;

public class AttribueOrConsumeGenrator extends ConsumeGenerator{
    public Attribute attribute = Attribute.heat;
    public Liquid passiveGen = NyfalisItemsLiquid.steam;
    /*ammount given per tile*/
    public float passiveGenAmmount = (size/ 2f) / 60f;
    public AttribueOrConsumeGenrator(String name) {
        super(name);
    }

    public class  AttribueOrConsumeGenratorBuild extends  ConsumeGeneratorBuild{
        public float sum;

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            sum = sumAttribute(attribute, tile.x, tile.y);
        }

        @Override
        public void updateTile(){

            super.updateTile();
            float maxPump = Math.min(liquidCapacity - liquids.get(passiveGen), passiveGenAmmount * sum);
            liquids.add(passiveGen, maxPump);
        }


    }


}
