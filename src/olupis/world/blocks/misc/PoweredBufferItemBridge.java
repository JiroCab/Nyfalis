package olupis.world.blocks.misc;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.ItemBuffer;
import mindustry.world.blocks.distribution.BufferedItemBridge;

public class PoweredBufferItemBridge extends BufferedItemBridge {

    public PoweredBufferItemBridge(String name){
        super(name);
        hasPower = true;
    }

    public class PoweredBufferItemBridgeBuild extends BufferedItemBridgeBuild{
        ItemBuffer buffer = new ItemBuffer(bufferCapacity);

        @Override
        public void updateTransport(Building other){
            power.links.addUnique(other.pos());
            other.power.links.addUnique(pos());

            power.graph.addGraph(other.power.graph);

            if(efficiency <= 0.7) return; //To lazy to make this dynamic so hard cut off it is -rushie
            if(buffer.accepts() && items.total() > 0){
                buffer.accept(items.take());
            }

            Item item = buffer.poll(speed / timeScale);
            if(timer(timerAccept,  4 / timeScale * efficiency) && item != null && other.acceptItem(this, item)){
                moved = true;
                other.handleItem(this, item);
                buffer.remove();
            }
        }
    }

}
