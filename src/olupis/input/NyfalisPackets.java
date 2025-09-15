package olupis.input;

import mindustry.net.*;
import olupis.world.entities.packets.*;

public class NyfalisPackets {
    public static void LoadPackets(){

        Net.registerPacket(NyfalisUnitTimedOutPacket::new);

        Net.registerPacket(NyfalisDebugPackets::new);
        Net.registerPacket(ConstructorCheatConfigPacket::new);
        Net.registerPacket(NyfalisNetRedirectPaylodPacket::new);

    }


}
