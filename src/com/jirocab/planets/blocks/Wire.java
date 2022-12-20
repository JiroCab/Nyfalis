package com.jirocab.planets.blocks;

import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.util.Nullable;
import com.jirocab.planets.content.OlupisBlocks;
import com.jirocab.planets.input.OlupisPlacement;
import mindustry.entities.units.BuildPlan;
import mindustry.world.Block;
import mindustry.world.blocks.power.*;

public class Wire extends Battery {
    public @Nullable Block junctionReplacement, bridgeReplacement;

    public Wire(String name){
        super(name);
        itemCapacity = 0;
        conductivePower = true;
    }

    @Override
    public void init(){
        super.init();

        if(bridgeReplacement == null || !(bridgeReplacement instanceof PowerBlock)) bridgeReplacement = OlupisBlocks.wireBridge;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        if(bridgeReplacement == null) return;

        OlupisPlacement.calculateBridges(plans, (BeamNode) bridgeReplacement);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("power", PowerNode.makePowerBalance());
        addBar("batteries", PowerNode.makeBatteryBalance());
    }

    public  class  WireBuild extends BatteryBuild{
        @Override
        public void draw(){
            Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            drawTeamTop();
        }
    }
}
