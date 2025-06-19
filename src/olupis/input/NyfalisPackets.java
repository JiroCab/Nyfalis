package olupis.input;

import mindustry.Vars;
import mindustry.net.Net;
import olupis.content.NyfalisTurrets;
import olupis.world.entities.packets.*;

import static mindustry.Vars.*;

public class NyfalisPackets {
    public static void LoadPackets(){

        Net.registerPacket(NyfalisUnitTimedOutPacket::new);

        Net.registerPacket(NyfalisSyncOtherSettingsPacket::new);
        Net.registerPacket(NyfalisDebugPackets::new);
        Net.registerPacket(ConstructorCheatConfigPacket::new);

        /*Too lazy to make a new class lmao*/
        if(headless) return;
        netServer.addPacketHandler("olupis-getsettings", (p, s) ->{
            NyfalisSyncOtherSettingsPacket packet = new NyfalisSyncOtherSettingsPacket();
            packet.cascadeBread = NyfalisTurrets.cascadeAlt;
            Vars.net.send(packet, true);
        });


    }


}
