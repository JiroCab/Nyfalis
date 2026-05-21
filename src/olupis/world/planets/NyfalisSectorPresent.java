package olupis.world.planets;

import arc.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;

import static mindustry.Vars.ui;

public class NyfalisSectorPresent extends SectorPreset{

    public NyfalisSectorPresent(String name, Planet planet, int sector){
        super(name, null, planet, sector);
    }


    @Override
    public void displayExtra(Table table){
        super.displayExtra(table);

        if(Vars.state.isCampaign() || ui.planet.isShown()){

            table.table(t -> t.button(Core.bundle.get("waves.preview"), Icon.eyeSmall, () -> {
                //just hide them all
                ui.content.hide();
                ui.research.hide();
                ui.database.hide();
                if(!ui.planet.isShown()) ui.planet.show();

                //I cant figure out pan for both so no
                ui.planet.viewPlanet(this.planet, false);
                ui.planet.lookAt(this.sector);
            }).width(175f)).growX();
        }
    }
}
