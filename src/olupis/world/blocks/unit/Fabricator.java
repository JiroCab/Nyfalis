package olupis.world.blocks.unit;

import arc.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitAssembler.*;
import mindustry.world.consumers.*;
import olupis.world.blocks.defence.Articulator.*;
import olupis.world.consumer.*;

import static mindustry.Vars.state;

public class Fabricator extends Reconstructor {
    public Liquid baseLube = Liquids.oil;
    public float lubeMultiplier = 3f;
    public @Nullable ConsumeLiquidBase lubrication;
    public boolean hasAlternate = true;
    protected @Nullable ConsumePayloadDynamic consPayload;
    public Block module;
    public static Seq<PayloadStack> savedRequirements[] ;

    //TODO t4 module logic (payload input & block attach menats)
    public Fabricator(String name){
        super(name);
    }

    @Override
    public void init() {
        consume(consPayload = new ConsumePayloadDynamic(FabricatorBuild::currentRequirements));
        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));

        super.init();
        if (lubrication == null) {
            lubrication = findConsumer(c -> c instanceof ConsumeLubricant);
        }

        savedRequirements = new Seq[upgrades.size];
        for(int i = 0; i < savedRequirements.length; i++){
            if(upgrades.get(i).length >= 3){
                savedRequirements[i] = new Seq<>();
                for(int j = 2; j < upgrades.get(i).length ; j++){
                    savedRequirements[i].add(PayloadStack.with(upgrades.get(i)[j], 1));
                }
            } else savedRequirements[i] = PayloadStack.list();
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
        public boolean constructing;

        public boolean useAlternate = false;
        public Seq<ArticulatorBuild> modules = new Seq<>();
        public PayloadSeq blocks = new PayloadSeq();

        @Override
        public void display(Table table){
            super.display(table);
            table.row();
        }

        @Override
        public void updateTile(){
            constructing = constructing();
            float lubeMul = updateLube();


            boolean valid = false;

            if(payload != null){
                //check if offloading
                if(!hasUpgrade(payload.unit.type)){
                    moveOutPayload();
                } else if (blocks.contains(payload.unit.type)){
                    if(moveInPayload()){
                        Tmp.v4.set(x, y);
                        Fx.payloadDeposit.at(payload.x(), payload.y(), rotation, new YeetData(Tmp.v4.cpy(), payload.content()));
                    }
                }else{ //update progress
                    if(moveInPayload()){
                        if(efficiency > 0){
                            valid = true;
                            progress += edelta() * state.rules.unitBuildSpeed(team) * lubeMul *   consPayload.efficiency(this);
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
                            blocks.clear();
                        }
                    }
                }
            }

            speedScl = Mathf.lerpDelta(speedScl, Mathf.num(valid), 0.05f);
            time += edelta() * speedScl * state.rules.unitBuildSpeed(team) * lubeMul;

            checkTier();
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

        public void checkTier(){
            if(!hasAlternate) return;
            boolean check =  modules.size > 0;
            if(check != useAlternate) progress = 0;
            useAlternate = check;
        }

        public UnitType[] currentPlan(){
            if(payload == null) return null;
            return upgrades.find( u -> u[0] == payload.unit.type);
        }

        public Seq<PayloadStack> currentRequirements(){
            if(payload == null || !upgrades.contains(currentPlan())) return PayloadStack.list();
            return savedRequirements[upgrades.indexOf(currentPlan())];
        }

        @Override
        public boolean hasUpgrade(UnitType type){
            UnitType[] p = upgrades.find(u -> u[0] == type);
            if(p != null && p.length > 2 && !hasAlternate) return false;

            UnitType t = upgrade(type);
            return t != null && (t.unlockedNowHost() || team.isAI()) && !type.isBanned();
        }

        @Override
        public PayloadSeq getPayloads(){
            return blocks;
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            if(currentRequirements().find( b-> b.item == payload.content()) != null){
                blocks.add(payload.content(), 1);
                return;
            }
            super.handlePayload(source, payload);
        }


        @Override
        public boolean acceptPayload(Building source, Payload payload){
            if(currentRequirements().find( b-> b.item == payload.content()) != null && !blocks.contains( payload.content())) return true;
            return super.acceptPayload(source, payload);
        }

        @Override
        public boolean constructing(){
            return super.constructing() && consPayload.efficiency(this) > 0 ;
        }

        @Override
        public boolean shouldConsume(){
            return constructing && enabled && consPayload.efficiency(this) > 0 ;
        }
    }
}
