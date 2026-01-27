package olupis.world.blocks.processing;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.type.ItemStack.with;

public class FactoryPlan extends Block{
    public float time;
    public @Nullable ItemStack[] input, output;
    public @Nullable LiquidStack[] outputLiquid, inputLiquid;
    public float powerIn = 0, powerOut = 0;
    public @Nullable String overlay;
    public TextureRegion overlayRegion;

    public Seq<UnlockableContent> displayFactory = new Seq<>();

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
        if(researchCostMultiplier == 1) researchCostMultiplier = 1000;

        requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
        if(input != null && researchCost == null){
            ItemStack[] out = new ItemStack[input.length];
            for(int i = 0; i < input.length; i++) out[i] = new ItemStack(input[i].item, Math.round(input[i].amount * researchCostMultiplier));
            researchCost = out;
        }
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.health);
        stats.remove(Stat.size);
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


        if(!Core.bundle.has(getContentType() + "." + this.name + ".description")){
            StringBuilder out = new StringBuilder();
            //prob a better way to do this lol
            boolean
                inLiq = inputLiquid != null && inputLiquid != LiquidStack.empty ,
                inItm =  input != null && input != ItemStack.empty,
                outLiq = outputLiquid != null && outputLiquid != LiquidStack.empty,
                outItm = output != null && output != ItemStack.empty;

            if(inLiq || inItm){out.append("(");}
            if(inLiq) for(int i = 0; i < inputLiquid.length; i++){
                out.append(inputLiquid[i].liquid.localizedName);
                if(i != inputLiquid.length -1) out.append(" + ");
            }
            if(inItm) for(int i = 0; i < input.length; i++){
                out.append(input[i].item.localizedName);
                if(i != input.length -1) out.append(" + ");
            }
            if(inLiq || inItm){out.append(")");}
            if((inLiq || inItm) && outItm || outLiq){out.append(" = ");}
            if(outItm || outLiq){out.append("(");}

            if(outLiq) for(int i = 0; i < outputLiquid.length; i++){
                out.append(outputLiquid[i].liquid.localizedName);
                if(i != outputLiquid.length -1) out.append(" + ");
            }
            if(outItm && output.length > 0) for(int i = 0; i < output.length; i++){
                out.append(output[i].item.localizedName);
                if(i != output.length -1) out.append(" + ");
            }
            if(outItm || outLiq){out.append(")");}
            description = out.toString();

        }

        if(Objects.equals(localizedName, name))
            localizedName = Iconc.crafting +" "+ getDisplayed().localizedName + " " + Core.bundle.get("olupis.plan.name");
    }

    @Override
    protected TextureRegion[] icons(){
        UnlockableContent dis = getDisplayed();

        region = dis.isModded() ? Core.atlas.find(dis.name) : Core.atlas.find( dis.getContentType() + "-" + dis.name);
        if(!region.found()) region = Core.atlas.find(Mathf.randomBoolean(0.5f) ? "alphaaaa" :  "ranai");
        return new TextureRegion[]{region, overlayRegion};
    }

    @Override
    public void load(){
        super.load();

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

