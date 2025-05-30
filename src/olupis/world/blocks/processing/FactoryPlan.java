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

    public FactoryPlan(String name, float time, ItemStack[] input, @Nullable ItemStack[] output, LiquidStack[] inputLiquid, @Nullable LiquidStack[] outputLiquid){
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

    public FactoryPlan(String name, float time, ItemStack[] input, @Nullable ItemStack[] output){
        this(name, time,input, output, null, null);
    }

    public FactoryPlan(String name){
        super(name);
        this.time = -Float.MAX_VALUE;
    }

    public float time(){
        return time;
    }


    @Override
    public void load(){
        super.load();

        description = Core.bundle.get("block.olupis-factory-plan-description");
        if(Objects.equals(localizedName, name))localizedName = localizedName.replace("olupis-", Iconc.crafting + " ");

        if(time == -Float.MAX_VALUE) return;
        TextureRegion out = Core.atlas.find(name);
        if(!Core.atlas.isFound(out)) out = getDisplayed().uiIcon;
        fullIcon = uiIcon = out;
        if(!Core.atlas.isFound(region)) region = fullIcon;

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

