package olupis.world.blocks.distribution;

import arc.scene.ui.layout.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.campaign.*;

import static mindustry.Vars.content;

public class LimitedLandingPad extends LandingPad{

    public LimitedLandingPad(String name){
        super(name);
    }

    public class LimtedLandingPadBuild extends LandingPadBuild{
        @Override
        public void buildConfiguration(Table table){

            ItemSelection.buildTable(LimitedLandingPad.this, table, content.items(), () -> config, this::configure, selectionRows, selectionColumns);
            //do not the redirect lol
        }
    }
}
