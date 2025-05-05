package olupis.world.blocks.processing;

import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import olupis.*;
import olupis.world.data.*;


public class HeadacheCrafter  extends GenericCrafter{
    public Seq<FactoryPlan> plans = new Seq<>(4);

    public HeadacheCrafter(String name){
        super(name);
        configurable = true;
        acceptsItems = true;

        config(Integer.class, (HeadacheCrafterBuild build, Integer i) -> {
            if(!configurable) return;
            if(i >= plans.size || i <= -1){
                build.planSelected = 0;
                return;
            }
            build.planSelected = i;
        });


        configClear((HeadacheCrafterBuild build) -> build.planSelected = 0);

        consume(new ConsumeItemDynamic((HeadacheCrafterBuild e) -> e.planSelected != -1 ? plans.get(Math.min(e.planSelected, plans.size - 1)).input : ItemStack.empty));
        //consume(new ConsumeLiquidsDynamic((HeadacheCrafterBuild e) -> e.planSelected != -1 ? plans.get(Math.min(e.planSelected, plans.size - 1)).inputLiquid : LiquidStack.empty));
    }


    public class HeadacheCrafterBuild extends GenericCrafterBuild{
        public int planSelected;

        @Override
        public void updateTile(){
            if(plans.size <= 0) return;
            if(planSelected <= -1 || planSelected > plans.size) planSelected = 1;
            FactoryPlan plan = plans.get(planSelected);

            if(efficiency > 0){
                progress += getProgressIncrease(plan.time);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                //continuously output based on efficiency
                if(plan.inputLiquid != null && this.liquids != null){
                    float inc = getProgressIncrease(1f);
                    for(LiquidStack output : plan.outputLiquid){
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft();
            }

            dumpOutputs();
        }

        @Override
        public void craft(){
            consume();
            FactoryPlan plan = plans.get(planSelected);

            if(plan.output != null){
                for(var output : plan.output){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        @Override
        public void buildConfiguration(Table table){
            Seq <FactoryPlan> pla = plans.copy();
            if(Vars.state.isCampaign()) pla.retainAll( p-> NyfalisMain.researchedPlans.contains(p));
            Seq<UnlockableContent> units = Seq.with(pla).map(u -> u.getDisplayed());


            if(pla.size >= 1){
                ItemSelection.buildTable(HeadacheCrafter.this,
                table,
                units,
                () -> plans.get(planSelected).getDisplayed(),
                p -> {
                    int i  = plans.indexOf(f -> f.getDisplayed() == p);
                   configure(i);
                   planSelected = i;

                },
                selectionRows,
                selectionColumns);

                table.row();
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return planSelected != -1 && items.get(item) < getMaximumAccepted(item) &&
            Structs.contains(plans.get(planSelected).input, stack -> stack.item == item);
        }

        @Override
        public Object config(){
            return plans.get(planSelected);
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(planSelected);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 2)planSelected =read.i();
        }
    }


    public static class FactoryPlan{
        public float time;
        public String name;
        public @Nullable ItemStack[] input, output;
        public @Nullable LiquidStack[] outputLiquid, inputLiquid;

        public FactoryPlan(String name, float time, ItemStack[] input, ItemStack[] output, LiquidStack[] inputLiquid, LiquidStack[] outputLiquid){
            this.name = name;
            this.time = time;
            this.input = input;
            this.output = output;
            this.inputLiquid = inputLiquid;
            this.outputLiquid = outputLiquid;
            CraftingPlansSaveIO.allPlans.add(this);
        }

        public FactoryPlan(String name, float time, ItemStack[] input, ItemStack[] output){
            this(name, time,input, output, null, null);
        }

        public float time(){
            return time;
        }

        public UnlockableContent getDisplayed(){
            if(this.output != null) return  this.output[0].item;
            else return  this.outputLiquid[0].liquid;
        }

        public String getName(){
            return name;
        }
    }

}
