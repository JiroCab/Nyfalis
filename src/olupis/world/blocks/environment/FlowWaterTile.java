package olupis.world.blocks.environment;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.editor.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.content.*;

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

//        if(Core.atlas.has(parent.name + "-edge")){
//            int tsize = (int)(tilesize / Draw.scl);
//            edges = Core.atlas.find(parent.name + "-edge").split(tsize, tsize);
//        }
    }


    @Override
    public TextureRegion[] icons(){
        String out = Core.atlas.has(name) ? name : name + "1";
        if(parent != null){
            if(parent.isModded()){
                out = parent.getContentType() + "-" + parent;
                if(Core.atlas.has(out)) out = out + "1";
            }else out = parent.name;
        }

        return new TextureRegion[]{Core.atlas.find(out), overlay };
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
            b.clear();
            b.margin(4f);
            b.left();
            int ls = lastConfig instanceof  Integer ii ? ii : 0;
            b.field(ls + "", s ->{
                lastConfig = Strings.parseInt(s);
            }).valid(f -> Strings.parseInt(f) >= 0 && Strings.parseInt(f) <= 360 ).color(Color.white).minWidth(100).padLeft(5f);
            Image ic = new Image(Icon.right);
            b.add(ic).update(a ->{
                int ui = lastConfig instanceof  Integer ii ? ii : 0;
                a.setRotation(ui);
            });
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
