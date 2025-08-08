package olupis.world.blocks.power;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.power.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.input.*;

import static mindustry.Vars.headless;

public class Wire extends Battery implements Autotiler {
    public @Nullable Block  bridgeReplacement;
    public TextureRegion edgeRegion;

    public Wire(String name){
        super(name);
        consumesPower = outputsPower = drawDisabled = allowDiagonal = solid = false;
        underBullets = replaceable = conveyorPlacement = update =  true;
        priority = TargetPriority.transport;
        group = BlockGroup.power;
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("power", PowerNode.makePowerBalance());
        addBar("batteries", PowerNode.makeBatteryBalance());
    }


    @Override
    public void init(){
        super.init();

        if(bridgeReplacement == null || !(bridgeReplacement instanceof BeamNode)) bridgeReplacement = NyfalisBlocks.wireBridge;
        checkNewDrawDefault();
    }

    void checkNewDrawDefault(){
        if(drawer == null){
            drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-preview"));
        }
    }

    @Override
    public void load(){
        edgeRegion = Core.atlas.find(name + "-edge");
        checkNewDrawDefault();

        super.load();
        drawer.load(this);
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        if(bridgeReplacement == null ) return;

        NyfalisPlacement.calculateBridges(plans, (BeamNode) bridgeReplacement);
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return tile.build instanceof WireBuild;
    }


    public  class  WireBuild extends BatteryBuild{
        public int blendprox;

        @Override
        public void draw(){
            Draw.rect(this.block.region, this.x, this.y, this.drawrot());

            for(int i = 0; i < 4; i++){
                if((blendprox & (1 << i)) == 0){
                    Draw.rect(edgeRegion, x, y, (rotation - i) * 90);
                }
            }

            drawTeamTop();
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            if(!headless){
                blendprox = 0;

                for(int i = 0; i < 4; i++){
                    if(nearby(Mathf.mod(rotation - i, 4)) instanceof WireBuild || nearby(Mathf.mod(rotation - i, 4)) instanceof BeamNode.BeamNodeBuild){
                        blendprox |= (1 << i);
                    }
                }
            }
        }


        @Override
        public BlockStatus status(){
            if(Mathf.equal(power.status, 0f, 0.001f)) return BlockStatus.noInput;
            if(Mathf.equal(power.status, 1f, 0.001f)) return BlockStatus.active;
            return BlockStatus.noOutput;
        }

    }
}
