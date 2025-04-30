package olupis.world.entities.entities;

import arc.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import olupis.content.*;
import olupis.world.entities.units.*;

public class LeggedPayloadUnitClass extends LegsUnit implements Payloadc{
    public Seq<Payload> payloads;


    public static LeggedPayloadUnitClass create() {
        return new LeggedPayloadUnitClass();
    }

    protected LeggedPayloadUnitClass(){
        super();
        this.payloads = new Seq<>();
    }

    @Override
    public int classId(){
        return NyfUnitMapper.LeggedPayload;
    }


    @Override
    public boolean dropBlock(BuildPayload payload){
        Building tile = payload.build;
        int tx = World.toTile(this.x - tile.block.offset);
        int ty = World.toTile(this.y - tile.block.offset);
        Tile on = Vars.world.tile(tx, ty);
        if (on != null && Build.validPlace(tile.block, tile.team, tx, ty, tile.rotation, false)) {
            payload.place(on, tile.rotation);
            Events.fire(new EventType.PayloadDropEvent(this, tile));
            if (this.getControllerName() != null) {
                payload.build.lastAccessed = this.getControllerName();
            }

            Fx.unitDrop.at(tile);
            on.block().placeEffect.at(on.drawx(), on.drawy(), (float)on.block().size);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Seq<Payload> payloads(){
        return this.payloads;
    }

    @Override
    public boolean canPickup(Building build){
        if(type instanceof NyfalisUnitType n && !n.pickupBlocks)  return false;
        return this.payloadUsed() + (float)(build.block.size * build.block.size * 8 * 8) <= this.type.payloadCapacity + 0.001F && build.canPickup() && build.team == this.team;
    }

    @Override
    public boolean canPickup(Unit unit){
        return this.type.pickupUnits && this.payloadUsed() + unit.hitSize * unit.hitSize <= this.type.payloadCapacity + 0.001F && unit.team == this.team() && unit.isAI();
    }

    @Override
    public boolean canPickupPayload(Payload pay){
        return this.payloadUsed() + pay.size() * pay.size() <= this.type.payloadCapacity + 0.001F && (this.type.pickupUnits || (!(pay instanceof UnitPayload) && type instanceof NyfalisUnitType n && !n.pickupBlocks));
    }

    @Override
    public boolean dropLastPayload(){
        if (this.payloads.isEmpty()) {
            return false;
        } else {
            Payload load = (Payload)this.payloads.peek();
            if (this.tryDropPayload(load)) {
                this.payloads.pop();
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean dropUnit(UnitPayload payload){
        Unit u = payload.unit;
        if (u.canPass(this.tileX(), this.tileY()) && Units.count(this.x, this.y, u.physicSize(), (o) -> o.isGrounded()) <= 1) {
            Fx.unitDrop.at(this);
            if (Vars.net.client()) {
                return true;
            } else {
                u.set(this);
                u.trns(Tmp.v1.rnd(Mathf.random(2.0F)));
                u.rotation(this.rotation);
                u.id = EntityGroup.nextId();
                if (!u.isAdded()) {
                    u.team.data().updateCount(u.type, -1);
                }

                u.add();
                u.unloaded();
                Events.fire(new EventType.PayloadDropEvent(this, u));
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean hasPayload(){
        return this.payloads.size > 0;
    }

    @Override
    public boolean tryDropPayload(Payload payload){
        Tile on = this.tileOn();
        if (Vars.net.client() && payload instanceof UnitPayload) {
            UnitPayload u = (UnitPayload)payload;
            Vars.netClient.clearRemovedEntity(u.unit.id);
        }
        if (on != null && on.build != null && on.build.acceptPayload(on.build, payload)) {
            Fx.unitDrop.at(on.build);
            on.build.handlePayload(on.build, payload);
            return true;
        } else if (payload instanceof BuildPayload) {
            BuildPayload b = (BuildPayload)payload;
            return this.dropBlock(b);
        } else if (payload instanceof UnitPayload) {
            UnitPayload p = (UnitPayload)payload;
            return this.dropUnit(p);
        } else {
            return false;
        }
    }

    @Override
    public float payloadUsed() {
        return this.payloads.sumf((p) -> p.size() * p.size());
    }

    @Override
    public void addPayload(Payload load){
        this.payloads.add(load);
    }

    @Override
    public void contentInfo(Table table, float itemSize, float width) {
        table.clear();
        table.top().left();
        float pad = 0.0F;
        float items = (float)this.payloads.size;
        if (itemSize * items + pad * items > width) {
            pad = (width - itemSize * items) / items;
        }

        for(Payload p : this.payloads) {
            table.image(p.icon()).size(itemSize).padRight(pad);
        }
    }

    @Override
    public void payloads(Seq<Payload> seq){
        this.payloads = payloads;
    }

    @Override
    public void pickup(Building tile){
        tile.pickedUp();
        tile.tile.remove();
        tile.afterPickedUp();
        this.addPayload(new BuildPayload(tile));
        Fx.unitPickup.at(tile);
        Events.fire(new EventType.PickupEvent(this, tile));
    }

    @Override
    public void pickup(Unit unit){
        unit.remove();
        this.addPayload(new UnitPayload(unit));
        Fx.unitPickup.at(unit);
        if (Vars.net.client()) {
            Vars.netClient.clearRemovedEntity(unit.id);
        }

        Events.fire(new EventType.PickupEvent(this, unit));
    }

    @Override
    public void writeSync(Writes write) {
        super.writeSync(write);
        write.i(this.payloads.size);

        for(int INDEX = 0; INDEX < this.payloads.size; ++INDEX) {
            TypeIO.writePayload(write, (Payload)this.payloads.get(INDEX));
        }
    }

    @Override
    public void read(Reads read) {
        super.read(read);

        boolean readpay = read.bool();
        if(!readpay)return;
        int payloads_LENGTH = read.i();
        this.payloads.clear();

        for(int INDEX = 0; INDEX < payloads_LENGTH; ++INDEX) {
            Payload payloads_ITEM = TypeIO.readPayload(read);
            if (payloads_ITEM != null) {
                this.payloads.add(payloads_ITEM);
            }
        }
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        write.bool(true);

        write.i(this.payloads.size);

        for(int INDEX = 0; INDEX < this.payloads.size; ++INDEX) {
            TypeIO.writePayload(write, (Payload)this.payloads.get(INDEX));
        }
    }
}
