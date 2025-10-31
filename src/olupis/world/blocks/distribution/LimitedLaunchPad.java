package olupis.world.blocks.distribution;

import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import olupis.*;

import static mindustry.Vars.*;

public class LimitedLaunchPad extends LaunchPad {
    public DrawBlock drawer;
    public  LimitedLaunchPad(String name){
        super(name);
        drawer = new DrawDefault();
    }

    public void load() {
        super.load();
        this.drawer.load(this);
    }


    public class LimitedLaunchPadBuild extends  LaunchPadBuild{

        public void draw() {
            drawer.draw(this);
        }

        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void buildConfiguration(Table table){
            if(!state.isCampaign() || net.client()){
                deselect();
                return;
            }

            table.button(Icon.upOpen, Styles.cleari, () -> {
                NyfalisVars.sectorSelect.showSelect(state.rules.sector, other -> {
                    if(state.isCampaign() && other.planet == state.rules.sector.planet){
                        state.rules.sector.info.destination = other;
                    }
                });
                deselect();
            }).size(40f);
        }
    }

}
