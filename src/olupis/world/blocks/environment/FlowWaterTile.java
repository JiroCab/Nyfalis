package olupis.world.blocks.environment;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.editor.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.content.*;

import java.util.*;

public class FlowWaterTile extends Floor{
    public @Nullable Block parent;
    public Effect effect = NyfalisFxs.flowWater;
    public Sound soundEffect = Sounds.none;
    public int effectSpacing = 6;
    public TextureRegion arrow;



    public FlowWaterTile(String name){
        super(name);
        saveData = true;
        editorConfigurable = true;
        saveConfig = true;
    }


    @Override
    public void load(){
        super.load();
        arrow = Core.atlas.find("olupis-flow-overlay");
    }

    @Override
    public TextureRegion[] icons(){
        region = parent.variants == 0 ? Core.atlas.find(parent.name) : Core.atlas.find(  parent.name + "1");
        return new TextureRegion[]{region, arrow};
    }

    @Override
    public void drawBase(Tile tile){
        parent.drawBase(tile);

        if(tile instanceof EditorTile){
            Draw.rect(arrow, tile.worldx(), tile.worldy(), tile.extraData);
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
            Runnable[] rebuild = {null};
            rebuild[0] = () -> {
                b.clear();
                b.margin(4f);
                b.left();
                int ls = lastConfig instanceof Integer ii ? ii : 0;

                Image ic = new Image(Icon.right);
                b.add(ic).update(a -> {
                    int ui = lastConfig instanceof Integer ii ? ii : 0;
                    a.setRotation(ui);
                });

                b.add(" | ").pad(5f);

                b.button(Icon.undo, () -> {
                    int o = ls + 90;
                    if(o >= 360) o = Math.abs(360 - o);
                    lastConfig = o;

                    rebuild[0].run();
                });

                b.button(Icon.redo, () -> {
                    int o = ls - 90;
                    if(o < 0) o = o + 360;
                    lastConfig = o;

                    rebuild[0].run();
                });

                b.field(ls + "", s -> {
                    lastConfig = Strings.parseInt(s);
                }).valid(f -> Strings.parseInt(f) >= 0 && Strings.parseInt(f) <= 360).color(Color.white).minWidth(25).padLeft(5f);

            };
            rebuild[0].run();
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
