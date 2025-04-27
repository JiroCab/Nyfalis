package olupis.world.blocks.distribution;

import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;

public class DeliveryTerminal extends Block{
    public Effect toggleEffect = Fx.none;

    public DeliveryTerminal(String name){
        super(name);
        configurable  = true;
        update = true;
        solid = true;

    }

    public class DeliveryTerminalBuild extends Building{

        @Override
        public void buildConfiguration(Table table){
            if(!Vars.state.hasSector() || Vars.net.client()) return;
            table.button(Icon.down, Styles.clearNoneTogglei, 40f, () -> {
                toggleEffect.at(this);
                for(Sector s : Vars.state.getSector().near()) s.info.destination = Vars.state.getSector();
                Vars.state.getSector().near().forEach(s -> {s.info.destination = Vars.state.getSector();});
                deselect();
                remove();
            });
        }
    }




}
