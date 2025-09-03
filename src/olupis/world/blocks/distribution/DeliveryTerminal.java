package olupis.world.blocks.distribution;

import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.net.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import olupis.world.entities.packets.*;

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
            if(!Vars.state.hasSector()) return;
            table.button(Icon.down, Styles.clearNoneTogglei, 40f, () -> {
                if(Vars.net.client()){
                    var p = new NyfalisNetRedirectPaylodPacket();
                    p.build = this;
                    Vars.net.send(p, true);
                }else {
                    redirect();
                }
            });
        }

        public void redirect(){
            for(Sector s : Vars.state.getSector().near()) s.info.destination = Vars.state.getSector();
            deselect();
            remove();
            kill();
            toggleEffect.at(this);
        }
    }




}
