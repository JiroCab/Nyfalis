package olupis.world.blocks.environment;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.editor.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.content.*;

import static mindustry.Vars.tilesize;

public class FlowWaterTile extends Floor{
    public Block parent = Blocks.air;
    public Effect effect = NyfalisFxs.flowWater;
    public Sound soundEffect = Sounds.none;
    public int effectSpacing = 6;
    public TextureRegion overlay;


    public FlowWaterTile(String name){
        super(name);
        saveData = true;
        editorConfigurable = true;
        saveConfig = true;
    }

    @Override
    public void load(){
        super.load();
        overlay = Core.atlas.find("olupis-flow-overlay");
        region = parent.region;

        if(Core.atlas.has(parent + "-edge")){
            int tsize = (int)(tilesize / Draw.scl);
            edges = Core.atlas.find(parent + "-edge").split(tsize, tsize);
        }
    }


    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(Core.atlas.has(name) ? name : name + "1"), overlay };
    }

    @Override
    public void drawBase(Tile tile){

        if(parent instanceof Floor floor){
            floor.drawBase(tile);
        }

        if(tile instanceof EditorTile){
            Draw.rect(overlay, tile.worldx(), tile.worldy(), tile.extraData);
        }
    }

    @Override
    public void drawMain(Tile tile){

        if(parent instanceof Floor floor){
            floor.drawMain(tile);
        }
    }

    @Override
    public Object getConfig(Tile tile){
        return tile.extraData;
    }

    @Override
    public boolean rotatedOutput(int x, int y){
        return super.rotatedOutput(x, y);
    }


    @Override
    public void buildEditorConfig(Table t){
        t.table(b -> {
            b.margin(4f);
            b.left();
            int ls = lastConfig instanceof  Integer ii ? ii : 0;
            b.field(ls + "", s ->{
                lastConfig = Strings.parseInt(s);
            }).valid(f -> Strings.parseInt(f) >= 0 && Strings.parseInt(f) <= 360 ).color(Color.white).minWidth(200).padLeft(5f);
        }).left().width(250f).pad(3f).row();
    }


    @Override
    public void placeEnded(Tile tile, @Nullable Unit builder, int rotation, @Nullable Object config){
        //config is assumed to be an rotation
        if(config instanceof Integer i){
            tile.extraData = i;
        }
    }


    @Override
    public void editorPicked(Tile tile){
        lastConfig = tile.extraData;
    }



}
