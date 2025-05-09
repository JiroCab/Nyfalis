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

import static mindustry.type.ItemStack.with;

public class FactoryPlan extends Block{
    public float time;
    public @Nullable ItemStack[] input, output;
    public @Nullable LiquidStack[] outputLiquid, inputLiquid;

    public FactoryPlan(String name, float time, ItemStack[] input, ItemStack[] output, LiquidStack[] inputLiquid, LiquidStack[] outputLiquid){
        super(name);
        this.time = time;
        this.input = input;
        this.output = output;
        this.inputLiquid = inputLiquid;
        this.outputLiquid = outputLiquid;

        health = 1;
        update = true;
        rebuildable = false;
        requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
        researchCost = with(NyfalisItemsLiquid.powerAmmoItem, 69);
    }

    public FactoryPlan(String name, float time, ItemStack[] input, ItemStack[] output){
        this(name, time,input, output, null, null);
    }

    public float time(){
        return time;
    }


    @Override
    public void load(){
        super.load();
        TextureRegion out = Core.atlas.find(name);
        if(!Core.atlas.isFound(out)) out = getDisplayed().uiIcon;
        fullIcon = uiIcon = out;
        if(region == null) region = fullIcon;
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

