package olupis.world.entities.packets;

import arc.util.io.*;
import mindustry.io.*;
import mindustry.net.*;
import mindustry.type.*;
import olupis.world.blocks.defence.ItemUnitTurret.*;
import olupis.world.blocks.distribution.*;
import olupis.world.blocks.distribution.DeliveryTerminal.*;

public class NyfalisNetRedirectPaylodPacket extends NyfalisSyncOtherSettingsPacket{
    private byte[] DATA;
    public DeliveryTerminalBuild build;

    public NyfalisNetRedirectPaylodPacket() {
        this.DATA = NODATA;
    }

    @Override
    public void write(Writes WRITE){
        TypeIO.writeObject(WRITE, build);
    }

    public void read(Reads READ, int LENGTH) {
        this.DATA = READ.b(LENGTH);
    }

    public void handled() {
        BAIS.setBytes(this.DATA);
        this.build = (DeliveryTerminalBuild)TypeIO.readObject(READ);
    }

    public void handleServer(NetConnection con) {
        build.redirect();
    }
}
