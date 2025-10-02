package olupis.world.blocks.processing;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import olupis.content.*;

import java.util.*;

import static mindustry.type.ItemStack.with;

public class FactoryPlan extends Block{
    public float time;
    public @Nullable ItemStack[] input, output;
    public @Nullable LiquidStack[] outputLiquid, inputLiquid;
    public float powerIn = 0, powerOut = 0;
    public @Nullable String overlay;
    public TextureRegion overlayRegion, plan;

    public FactoryPlan(String name, String overlay, float time, ItemStack[] input, @Nullable ItemStack[] output, LiquidStack[] inputLiquid, @Nullable LiquidStack[] outputLiquid, float powerIn, float powerOut){
        super(name);
        this.time = time;
        this.input = input;
        this.output = output;
        this.inputLiquid = inputLiquid;
        this.outputLiquid = outputLiquid;
        this.powerIn = powerIn;
        this.powerOut = powerOut;
        this.overlay = overlay;

        health = 1;
        variants = 1;
        update = true;
        rebuildable = false;
        requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
        researchCost = with(NyfalisItemsLiquid.powerAmmoItem, 69);
        if(this.outputLiquid == null)this.outputLiquid = LiquidStack.empty;
        if(this.output == null)this.output = ItemStack.empty;
    }

    public FactoryPlan(String name, float time, ItemStack[] input, @Nullable ItemStack[] output, LiquidStack[] inputLiquid, @Nullable LiquidStack[] outputLiquid, float powerIn, float powerOut){
        this(name, "", time,input, output, inputLiquid, outputLiquid, powerIn, powerOut);
    }

    public FactoryPlan(String name, float time, ItemStack[] input, @Nullable ItemStack[] output, LiquidStack[] inputLiquid, @Nullable LiquidStack[] outputLiquid){
        this(name, "", time,input, output, inputLiquid, outputLiquid, 0, 0);
    }
    public FactoryPlan(String name, String overlay, float time, ItemStack[] input, @Nullable ItemStack[] output, LiquidStack[] inputLiquid, @Nullable LiquidStack[] outputLiquid){
        this(name, overlay, time,input, output, inputLiquid, outputLiquid, 0, 0);
    }
    public FactoryPlan(String name, String overlay, float time, ItemStack[] input, @Nullable ItemStack[] output){
        this(name, overlay, time,input, output, null, null, 0, 0);
    }

    public FactoryPlan(String name, String overlay, float time, ItemStack[] input, @Nullable ItemStack[] output, float powerIn, float powerOut){
        this(name, overlay, time,input, output, null, null, powerIn, powerOut);
    }

    public FactoryPlan(String name){
        super(name);
        this.time = -Float.MAX_VALUE;
        generateIcons = true;
    }

    public float time(){
        return time;
    }


    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[] {plan, overlayRegion};
    }

    @Override
    public void load(){
        super.load();

        description = Core.bundle.get("block.olupis-factory-plan-description");
        if(Objects.equals(localizedName, name))localizedName = localizedName.replace("olupis-", Iconc.crafting + " ");

        overlayRegion =  Core.atlas.find("olupis-plan-overlay");
        plan = Core.atlas.find(getDisplayed().name);
    }

    public UnlockableContent getDisplayed(){
        if(this.output != null) return  this.output[0].item;
        else return  this.outputLiquid[0].liquid;
    }

    public String getName(){
        return name;
    }

    public class teamResearchBuild extends Building{
        @Override
        public void update() {
            if (!Vars.net.client())
                kill();
        }


        @Override
        public void killed() {
            if(!Vars.net.client()) {
                if(Vars.state.rules.isBanned(block)) Vars.state.rules.bannedBlocks.remove(block);
                else Vars.state.rules.bannedBlocks.add(block);
            }

            super.killed();
        }
    }
}

