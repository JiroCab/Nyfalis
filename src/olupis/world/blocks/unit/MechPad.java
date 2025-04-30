package olupis.world.blocks.unit;

import arc.*;
import arc.graphics.*;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.meta.*;
import olupis.content.NyfalisUnits;

import static mindustry.Vars.*;

public class MechPad extends Block {
    public UnitType type = NyfalisUnits.scarab;
    public float unPowerThreshold = 0.3f;
    public float lowPowerThreshold = 0.7f;
    public StatusEffect lowPowerStatus = StatusEffects.slow;
    public StatusEffect unPowerStatus = StatusEffects.unmoving;

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.unitType, table -> {
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.image(type.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                b.table(info -> {
                    info.add(type.localizedName).left();
                    if(Core.settings.getBool("console")){
                        info.row();
                        info.add(type.name).left().color(Color.lightGray);
                    }
                });
                b.button("?", Styles.flatBordert, () -> ui.content.show(type)).size(40f).pad(10).right().grow().visible(() -> type.unlockedNow());
            }).growX().pad(5).row();
        });
    }

    public  MechPad(String name){
        super(name);
        update = true;
    }

    public class MechPadBuild extends Building implements ControlBlock{
        public int readUnitId = -1;
        public @Nullable Unit slave;

        @Override
        public void updateTile(){
            //unit was lost/destroyed
            if(slave != null && (slave.dead || !slave.isAdded())) slave = null;

            if(readUnitId != -1){
                slave = Groups.unit.getByID(readUnitId);
                if(slave != null || !net.client()) readUnitId = -1;
            }

            if(slave == null && Units.canCreate(team, type) && efficiency > unPowerThreshold){
                if(!net.client()){
                    slave = type.create(team);
                    slave.set(x, y);
                    slave.rotation = 90f;
                    slave.add();
                    readUnitId = slave.id;
                }
            }

            if (slave != null){
                if(efficiency < unPowerThreshold) slave.apply(unPowerStatus, 1 * Time.toSeconds);
                else if(efficiency < lowPowerThreshold) slave.apply(lowPowerStatus, 1 * Time.toSeconds);
            }
        }

        @Override
        public boolean canControl(){
            return slave != null;
        }

        @Override
        public Unit unit(){//make sure stats are correct
            return slave;
        }

        public byte version() {
            return 1;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 1)readUnitId = read.i();
        }


        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(slave == null ? -1 : slave.id);
        }

    }
}
