package olupis.world.ai;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.world.entities.units.*;

import static mindustry.Vars.*;

/*Custom mining Ai that Respects the ammo lifetime gimmick*/
public class NyfalisMiningAi extends AIController {
    public boolean mining = true, dynamicItems = true, inoperable = false;
    public Item targetItem;
    public Tile ore, lastOre;
    /*0 = Cant mine, 1 = core, 2 = Floor, 3 = Wall, 4 = overlay*/
    public int mineType = 0;
    public Seq<Item> dynamicMineItems = new Seq<>(), dynamicBlackList = new Seq<>(), priorityMineItems = Seq.with(NyfalisItemsLiquid.rustyIron, Items.lead, Items.copper);
    public float priorityMin = 0.6f;
    private float lastMoveX, lastMoveY;

    public void updateMineItems(Building core){
        dynamicMineItems.clear();
        for (Item priorityMineItem : priorityMineItems) {
            if (!dynamicBlackList.contains(priorityMineItem)) dynamicMineItems.addUnique(priorityMineItem);
        }
        if(priorityMineItems.allMatch(i ->{
            int max = Vars.state.rules.coreIncinerates ? core.getMaximumAccepted(i) / 20: core.getMaximumAccepted(i);
            return core.items.get(i) >= max *priorityMin;
        })){
            Vars.content.items().each(i -> {
                if(unit.type.mineTier >= i.hardness && !dynamicBlackList.contains(i)) dynamicMineItems.addUnique(i);
            });
        }

        dynamicMineItems.sort(i -> i.hardness).reverse();
    }

    @Override
    public void updateMovement(){
        Building core = unit.closestCore();

        if(!(unit.canMine()) || core == null) return;

        if(dynamicItems)updateMineItems(core);

        if(unit.type instanceof AmmoLifeTimeUnitType al && al.deathThreshold * 2f >= unit.ammo){
            if(unit.stack.amount > 0 ){
                unit.mineTile = ore = null;
                mineType = 1;
                mining = false;
                move(core, true);

                if(unit.within(core, unit.type.mineRange)){
                    if(core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0){
                        Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                    } else unit.clearItem();
                }
                return;
            }else{
                al.callTimeOut(unit);
            }
        }

        if(unit.mineTile != null && !unit.mineTile.within(unit, unit.type.mineRange)){
            unit.mineTile(null);
        }

        if(mining){
            if(timer.get(timerTarget2, 60 * 4) || targetItem == null){
                if(dynamicItems){
                    targetItem = dynamicMineItems.min(i -> indexer.hasOre(i) && unit.canMine(i), i -> core.items.get(i));
                }
                else targetItem = unit.type.mineItems.min(i -> indexer.hasOre(i) && unit.canMine(i)  && !dynamicBlackList.contains(targetItem), i -> core.items.get(i));
            }

            //core full of the target item, do nothing
            if(targetItem != null && core.acceptStack(targetItem, 1, unit) == 0){
                unit.clearItem();
                unit.mineTile = null;
                return;
            }

            if(unit.type instanceof AmmoLifeTimeUnitType al && al.deathThreshold * 1.5f >= unit.ammo){
                mining = false;
                unit.mineTile(null);
                ore = null;
            }//if inventory is full, drop it off.
            else if(unit.stack.amount >= unit.type.itemCapacity || (targetItem != null && !unit.acceptsItem(targetItem))){
                mining = false;
            }else{
                if(timer.get(timerTarget3, 60) && targetItem != null){
                        lastOre =ore = indexer.findClosestOre(unit, targetItem);
                        mineType = 0;

                        if(ore == null) return;
                        if(ore.floor().itemDrop == targetItem) mineType = 2;
                        else if (ore.block().itemDrop== targetItem) mineType = 3;
                        else if (ore.overlay().itemDrop == targetItem) mineType = 4;
                }

                if(ore != null){
                    move(ore, unit.team != state.rules.waveTeam, true);

                    if(ore.block() == Blocks.air && unit.within(ore, unit.type.mineRange)){
                        unit.mineTile = ore;
                        unit.lookAt(ore);
                    }

                    if(ore.block() != Blocks.air){
                        mining = false;
                    }
                }
            }
        }else{
            unit.mineTile = null;

            if(unit.stack.amount == 0){
                mining = true;
                return;
            }

            if(unit.within(core, Math.max(unit.type.range, unit.type.mineRange))){
                if(core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0){
                    Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                }

                unit.clearItem();
                mining = true;
            }

            mineType = 1;
            move(core, true);
        }
    }


    public void move(Position target, boolean nullDepletion){
        move(target, nullDepletion, false);
    }
    public void move(Position target, boolean nullDepletion, boolean notCore){
        if(unit.within(target, unit.type.mineRange / 2f)) return;

        if (unit.type.flying) circle(target, unit.type.range / 1.8f);
        else {
            if(!Mathf.equal(target.getX(), lastMoveX, 0.1f) || !Mathf.equal(target.getY(), lastMoveY, 0.1f)){
                //lastPathId ++;
                lastMoveX = target.getX();
                lastMoveY = target.getY();
            }
            if (Vars.controlPath.getPathPosition(unit, Tmp.v2.set(target.getX(), target.getY()), Tmp.v1, null)) {
                unit.lookAt(Tmp.v1);
                moveTo(Tmp.v1, 1f, Tmp.v2.epsilonEquals(Tmp.v1, 4.1f) ? 30f : 0f, false, null);
            } else {
                /*Prevents getting stuck on an ore that unit can't reach*/
                if(notCore && targetItem != null && !unit.moving() && timer.get(timerTarget4, 25)) dynamicBlackList.add(targetItem);
                if(nullDepletion) inoperable = true;
                unit.lookAt(unit.prefRotation());
            }
        }
    }

}
