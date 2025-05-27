package olupis.world.blocks.unit;

import arc.Events;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.type.Liquid;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.consumers.*;
import olupis.world.consumer.*;

import static mindustry.Vars.state;

public class Fabricator extends Reconstructor {
    public Liquid baseLube = Liquids.oil;
    public float lubeMultiplier = 3f;
    public @Nullable ConsumeLiquidBase lubrication;

    public Fabricator(String name){
        super(name);
    }

    @Override
    public void init() {
        super.init();
        if (lubrication == null) {
            lubrication = findConsumer(c -> c instanceof ConsumeLubricant);
        }
    }

//    @Override
//    public void setStats(){
//        super.setStats();
//
//        if(lubrication != null){
//            stats.remove(Stat.booster);
//            stats.add(Stat.booster, NyfalisStats.lubeBoosters(constructTime, lubrication.amount, lubeMultiplier, baseLube.heatCapacity, l ->  l != baseLube && l.coolant && consumesLiquid(l)));
//        }
//    }

    public class FabricatorBuild extends ReconstructorBuild{
        boolean constructing;


        @Override
        public void updateTile(){
            constructing = constructing();
            float lubeMul = updateLube();


            boolean valid = false;

            if(payload != null){
                //check if offloading
                if(!hasUpgrade(payload.unit.type)){
                    moveOutPayload();
                }else{ //update progress
                    if(moveInPayload()){
                        if(efficiency > 0){
                            valid = true;
                            progress += edelta() * state.rules.unitBuildSpeed(team) * lubeMul;
                        }

                        //upgrade the unit
                        if(progress >= constructTime){
                            payload.unit = upgrade(payload.unit.type).create(payload.unit.team());

                            if(payload.unit.isCommandable()){
                                if(commandPos != null){
                                    payload.unit.command().commandPosition(commandPos);
                                }
                                if(command != null){
                                    //this already checks if it is a valid command for the unit type
                                    payload.unit.command().command(command);
                                }
                            }

                            progress %= 1f;
                            Effect.shake(2f, 3f, this);
                            Fx.producesmoke.at(this);
                            consume();
                            Events.fire(new EventType.UnitCreateEvent(payload.unit, this));
                        }
                    }
                }
            }

            speedScl = Mathf.lerpDelta(speedScl, Mathf.num(valid), 0.05f);
            time += edelta() * speedScl * state.rules.unitBuildSpeed(team) * lubeMul;
        }
        
        public float updateLube(){
            float out = 1f;
            if(lubrication == null) return out;
            if(this.liquids == null) return out;
            if(lubrication.efficiency(this) == 0) return out;
            if(efficiency == 0) return out;
            
            if(lubrication instanceof ConsumeLiquidFilter filter  && filter.getConsumed(this) != baseLube){
//                float capacity = lubrication instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : 1f;
//                float amount = lubrication.amount * lubrication.efficiency(this);

                out += edelta() * lubeMultiplier * Math.abs(filter.getConsumed(this).heatCapacity - baseLube.heatCapacity ) * (lubrication.amount * lubrication.efficiency(this));
                //out += amount * edelta() * capacity * lubeMultiplier;

            }
            return out;
        }

        @Override
        public boolean shouldConsume(){
            return constructing && enabled;
        }

    }
}
