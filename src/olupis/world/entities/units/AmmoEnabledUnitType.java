package olupis.world.entities.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.abilities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.ai.*;
import olupis.world.entities.entities.*;
import olupis.world.interfaces.*;

import java.util.*;

import static mindustry.Vars.*;

//Class for stuff renders ammo but necessary it's life tied to it
public class AmmoEnabledUnitType extends NyfalisUnitType{
    private transient float resupplyTime = Mathf.random(10f);
    public boolean drawAmmo = false, altResupply = false;
    public TextureRegion ammoRegion;
    public float ammoZ = -1f;
    //used by relationship for recreating it on load for unit to unit gayness
    public @Nullable Seq<MappableContent> parentTypes = null;
    public StatusEffect retreatStatus = StatusEffects.none;
    public float minRetreatAmmo = 0.25f;
    public int ammoCapacity = 150;
    public boolean setRetreat = false;
    public String ammoType = NyfUnitTeamMapper.ammoCarrier;

    public AmmoEnabledUnitType(String name){
        super(name);
    }

    public Color ammoColor(Unit unit){
        if(unit instanceof AmmoNyf ae){
            float f = Mathf.clamp(ae.ammof());
            return Tmp.c1.set(Color.black).lerp(unit.team.color, f);
        }
        return Color.black;
    }

    public void drawAmmo(Unit unit){
        if(!ammoRegion.found()) return;
        float z = !unit.isAdded() ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
        if(ammoZ > 0) z = ammoZ;
        Draw.z(z);
        applyColor(unit);

        Draw.color(ammoColor(unit));
        Draw.rect(ammoRegion, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        if(drawAmmo && ammoRegion.found() && !unit.inFogTo(Vars.player.team()))drawAmmo(unit);
        Draw.reset();
    }

    @Override
    public void load() {
        if(drawAmmo)ammoRegion = Core.atlas.find(name + "-ammo");
        super.load();
    }

    @Override
    public void display(Unit unit, Table table){
        table.table(t -> {
            t.left();
            t.add(new Image(uiIcon)).size(iconMed).scaling(Scaling.fit);
            t.labelWrap(localizedName).left().width(190f).padLeft(5);
        }).growX().left();
        table.row();

        table.table(bars -> buildBars(bars, unit)).growX();

        if(unit.controller() instanceof LogicAI ai){
            table.row();
            table.add(Blocks.microProcessor.emoji() + " " + Core.bundle.get("units.processorcontrol")).growX().wrap().left();
            if(ai.controller != null && (Core.settings.getBool("mouseposition") || Core.settings.getBool("position"))){
                table.row();
                table.add("[lightgray](" + ai.controller.tileX() + ", " + ai.controller.tileY() + ")").growX().wrap().left();
            }
            table.row();
            table.label(() -> Iconc.settings + " " + (long)unit.flag).color(Color.lightGray).growX().wrap().left();
            if(net.active() && ai.controller != null && ai.controller.lastAccessed != null){
                table.row();
                table.add(Core.bundle.format("lastaccessed", ai.controller.lastAccessed)).growX().wrap().left();
            }
        }else if(net.active() && unit.lastCommanded != null){
            table.row();
            table.add(Core.bundle.format("lastcommanded", unit.lastCommanded)).growX().wrap().left();
        }

        if(unit.controller() instanceof  InoperableAi ioa){
            table.row();
            table.table().left().growX().update(i -> {
                i.left().clear();
                if(unit.controller() instanceof SearchAndDestroyFlyingAi ai ){
                    if(ai.targetOverriden && ai.tarFire != null) i.add(Core.bundle.get("rules.fire") + Iconc.statusBurning + (Core.settings.getBool("console") ? "[lightgray] (" + Mathf.round(ai.tarFire.x / 8f) + "," + Math.round(ai.tarFire.y / 8f) + ")[]" : ""));
                }

                if(unit.controller() instanceof NyfalisMiningAi ai ){
                    if(ai.targetItem == null || unit.closestCore() == null || ai.targetItem == null){
                        i.add(Core.bundle.get("nyfalis-ai-inoperable"));
                        return;
                    }
                    TextureRegion icon = unit.closestCore().block.fullIcon;
                    if(ai.mineType >= 2 && ai.ore != null){
                        if(ai.mineType == 2) icon = ai.ore.floor().fullIcon;
                        else if(ai.mineType == 3) icon = ai.ore.block().fullIcon;
                        else if(ai.mineType == 4) icon = ai.ore.overlay().fullIcon;
                    }

                    i.image(icon).size(iconSmall).scaling(Scaling.bounded).left();
                    i.add(ai.mineType != 1 ? ai.targetItem.localizedName: unit.closestCore().block.localizedName).wrap().left();
                    if (ai.ore != null && unit.closestCore() != null && (Core.settings.getBool("mouseposition") || Core.settings.getBool("position"))) {
                        if(ai.ore == null || unit.closestCore() == null) return;
                        Tile tar = ai.mineType == 1 ? unit.closestCore().tile : ai.ore;
                        i.add("[lightgray](" + Math.round(tar.x) + ", " + Math.round(tar.y) + ") [" + Math.round(unit.dst(tar)) + "]");
                    }
                }

                if(ioa.inoperable())i.add(Core.bundle.get("nyfalis-ai-inoperable"));
            });
        };

        table.row();
    }

    public void buildBars(Table bars, Unit unit){
        bars.defaults().growX().height(20f).pad(4);

        bars.add(new Bar("stat.health", Pal.health, unit::healthf).blink(Color.white));
        bars.row();

        bars.add(new Bar(NyfUnitTeamMapper.ammoIcon(ammoType) + " "+ (Core.settings.getBool("nyfalis-debug") && (unit instanceof AmmoEnabledUnitClass ae) ? ae.ammo + "/" + ammoCapacity :  Core.bundle.get("stat.ammo")  ), NyfalisColors.ammoColour(ammoType), () -> ( unit instanceof AmmoEnabledUnitClass ae ? ae.ammof() : 0)));
        bars.row();

        for(Ability ability : unit.abilities){
            ability.displayBars(unit, bars);
        }

        if(payloadCapacity > 0 && unit instanceof Payloadc payload){
            bars.add(new Bar("stat.payloadcapacity", Pal.items, () -> payload.payloadUsed() / unit.type().payloadCapacity));
            bars.row();

            var count = new float[]{-1};
            bars.table().update(t -> {
                if(count[0] != payload.payloadUsed()){
                    payload.contentInfo(t, 8 * 2, 270);
                    count[0] = payload.payloadUsed();
                }
            }).growX().left().height(0f).pad(0f);
        }
    }

    @Override
    public void update(Unit unit){
        super.update(unit);

        if(canRetreat && retreatStatus != null && unit instanceof AmmoNyf an){
            int min = Math.round(setRetreat ? minRetreatAmmo : an.ammoCapacity() * minRetreatAmmo);
            if(min <= an.currentAmmo() && unit.isCommandable() && unit.command().command == NyfalisUnitCommands.nyfalisRetreatCommand) unit.apply(retreatStatus, Time.toSeconds);
        }
    }

    @Override
    public Unit create(Team team){
        Unit unit =  super.create(team);

        if(unit instanceof  AmmoNyf ae) ae.fillAmmo();
        return unit;
    }
}

