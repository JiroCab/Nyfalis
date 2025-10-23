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
    public TextureRegion overlayRegion;

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
        generateIcons = true;

        requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
        researchCost = with(NyfalisItemsLiquid.powerAmmoItem, 69);
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

        time = -Float.MAX_VALUE;
        generateIcons = true;
    }

    public float time(){
        return time;
    }

    @Override
    public void init(){
        super.init();

        if(outputLiquid == null)
            outputLiquid = LiquidStack.empty;
        if(output == null)
            output = ItemStack.empty;
    }

    @Override
    protected TextureRegion[] icons(){
        UnlockableContent dis = getDisplayed();

        region = dis.isModded() ? Core.atlas.find(dis.name) : Core.atlas.find( dis.getContentType() + "-" + dis.name);
        return new TextureRegion[]{region, overlayRegion};
    }

    @Override
    public void load(){
        super.load();

        description = Core.bundle.get("block.olupis-factory-plan-description");
        if(Objects.equals(localizedName, name))
            localizedName = Iconc.crafting +" "+ getDisplayed().localizedName + " " + Core.bundle.get("plan");

        overlayRegion =  Core.atlas.find("olupis-plan-overlay");
    }

    public UnlockableContent getDisplayed(){
        if(output != null)
            return output[0].item;

        return outputLiquid[0].liquid;
    }

    public String getName(){
        return name;
    }

    public class teamResearchBuild extends Building{
        @Override
        public void update(){
            if(!Vars.net.client())
                kill();
        }


        @Override
        public void killed(){
            if(!Vars.net.client()){
                if(Vars.state.rules.isBanned(block))
                    Vars.state.rules.bannedBlocks.remove(block);
                else Vars.state.rules.bannedBlocks.add(block);
            }

            super.killed();
        }
    }
}

