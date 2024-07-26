package olupis.world.entities.units;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.types.LogicAI;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Env;
import olupis.content.NyfalisFxs;
import olupis.world.ai.NyfalisMiningAi;
import olupis.world.ai.SearchAndDestroyFlyingAi;
import olupis.world.entities.packets.NyfalisUnitTimedOutPacket;

import static mindustry.Vars.*;

/*Unit that dies when it runs out of ammo, ammo Depletes over time*/
public class AmmoLifeTimeUnitType extends  NyfalisUnitType {
    /*Custom logic to remove ammo over time*/
    public  boolean ammoDepletesOverTime = true, ammoDepletesInRange = false;
    /*Custom logic to kill unit on no ammo*/
    public  boolean killOnAmmoDepletion = true;
    /*Amount to deplete per tick*/
    public float ammoDepletionAmount = 0.2f;
    public float passiveAmmoDepletion = ammoDepletionAmount;
    /*Ammo amount that will trigger death*/
    public float deathThreshold = 0.1f;
    /*mining depletes ammo*/
    public boolean miningDepletesAmmo = false;
    /*Time before depleting ammo*/
    public float ammoDepletionOffset = Time.toMinutes;
    float startTime;
    /*Being player controlled depletes ammo*/
    public boolean depleteOnInteraction = true, depleteOnInteractionUsesPassive = false;
    /*Deplete Ammo when over unit cap, Assumes ammoDepletesOverTime = true */
    public boolean overCapacityPenalty = false;
    /*Anti-spam to hard, aka setting a diminishing return for the sake of frames */
    public float penaltyMultiplier = 2f;
    /*Time out params */
    public boolean drawAmmo = false, inoperable = false;
    public TextureRegion ammoRegion;
    public Sound timedOutSound = Sounds.explosion;
    public Effect timedOutFx = NyfalisFxs.unitBreakdown;
    public float timedOutSoundPitch = 1f, timedOutSoundVolume = 0.4f, maxRange = -1;
    public Vec2 startPos;


    //TODO: Range limit them, deplete ammo when N tiles away from X & Y

    public AmmoLifeTimeUnitType(String name){
        /*let's just hope that ammo is never removed at least not removed internally */
        super(name);
        envDisabled = Env.none;
    }

    @Override
    public void display(Unit unit, Table table){
        table.table(t -> {
            t.left();
            t.add(new Image(uiIcon)).size(iconMed).scaling(Scaling.fit);
            t.labelWrap(localizedName).left().width(190f).padLeft(5);
        }).growX().left();
        table.row();

        table.table(bars -> {
            bars.defaults().growX().height(20f).pad(4);

            bars.add(new Bar("stat.health", Pal.health, unit::healthf).blink(Color.white));
            bars.row();

            if(state.rules.unitAmmo || killOnAmmoDepletion){
                bars.add(new Bar(ammoType.icon() + " " + Core.bundle.get("stat.ammo"), ammoType.barColor(), () -> (unit.ammo - deathThreshold ) / (ammoCapacity - deathThreshold) ));
                bars.row();
            }

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
        }).growX();

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

        if(unit.controller() instanceof SearchAndDestroyFlyingAi ai ){
            table.row();
            table.table().left().growX().update(i -> {
                i.left().clear();
                if(ai.inoperable){
                    i.add(Core.bundle.get("nyfalis-ai-inoperable"));
                }
            });
        }

        if(unit.controller() instanceof NyfalisMiningAi ai ){
            table.row();
            table.table().left().growX().update(i -> {
                i.left().clear();
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
            });

            if (ai.ore != null && unit.closestCore() != null && (Core.settings.getBool("mouseposition") || Core.settings.getBool("position"))) {
                table.row();
                table.table().update(i -> {
                    i.left().clear();
                    if(ai.ore == null || unit.closestCore() == null) return;
                    Tile tar = ai.mineType == 1 ? unit.closestCore().tile : ai.ore;
                    i.add("[lightgray](" + Math.round(tar.x) + ", " + Math.round(tar.y) + ") [" + Math.round(unit.dst(tar)) + "]");
                }).growX().wrap();
            }
        }
        table.row();
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);
        if(drawAmmo && ammoRegion.found() && !unit.inFogTo(Vars.player.team()))drawAmmo(unit);
        Draw.reset();
    }

    @Override
    public void load() {
        if(drawAmmo)ammoRegion = Core.atlas.find(name + "-ammo", name);
        super.load();
    }

    public void drawAmmo(Unit unit){
        float z = !unit.isAdded() ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
        Draw.z(z);
        applyColor(unit);

        Draw.color(ammoColor(unit));
        Draw.rect(ammoRegion, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();
    }

    public void drawItems(Unit unit){
        if(drawAmmo){
            //Jank otherwise it draw under the ammo
            float z = !unit.isAdded() ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
            Draw.z(z + 0.001f);
        }
        super.drawItems(unit);
    }

    public Color ammoColor(Unit unit){
        if(ammoDepletesInRange && !inRange(unit)) return Color.black;
        float f = Mathf.clamp(unit.ammof());
        return Tmp.c1.set(Color.black).lerp(unit.team.color, f + Mathf.absin(Time.time, Math.max(f * 2.5f, 1f), 1f - f));
    }

    @Override
    public void update(Unit unit){
        if (unit.ammo <= deathThreshold && killOnAmmoDepletion){
            callTimeOut(unit);
        }

        inoperable = ((unit.controller() instanceof NyfalisMiningAi ai  && (ai.targetItem == null || unit.closestCore() == null || ai.targetItem == null || ai.inoperable) )
                            || !unit.moving() && (unit.hasWeapons() && !unit.isShooting || !unit.activelyBuilding()))
                            || (unit.controller() instanceof SearchAndDestroyFlyingAi ai && ai.inoperable);
        boolean multiplier =((unit.count() > unit.cap() && unit.type.useUnitCap));

        boolean shouldDeplete = ( (startTime+ ammoDepletionOffset) <= Time.time) || (ammoDepletesInRange && !inRange(unit));
        if(inoperable || (ammoDepletesOverTime && shouldDeplete && (!overCapacityPenalty || (unit.count() > unit.cap())))){
            unit.ammo  -= ((depleteOnInteractionUsesPassive ? passiveAmmoDepletion : ammoDepletionAmount) * (multiplier || inoperable ? penaltyMultiplier : 1f));
        }

        if(miningDepletesAmmo && unit.mining()){
            unit.ammo = unit.ammo - (ammoDepletionAmount * (multiplier ? penaltyMultiplier : 1f));
        }

        if(unit.isPlayer() && depleteOnInteraction && unit.ammo >= deathThreshold +0.05f ){
            unit.ammo = unit.ammo - (ammoDepletionAmount * (multiplier ? penaltyMultiplier : 1f));
        }

        super.update(unit);
    }

    @Override
    public Unit create(Team team){
        Unit unit = constructor.get();
        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying ? 1f : 0;
        unit.heal();
        if(unit instanceof TimedKillc u){
            u.lifetime(lifetime);
        }
        startTime = Time.time;
        unit.apply(spawnStatus, spawnStatusDuration);
        startPos = new Vec2(unit.x /8f, unit.y /8f);
        return unit;
    }

    public Unit create(Team team, float unitRange, float startX, float startY ){
        Unit unit = constructor.get();
        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying ? 1f : 0;
        unit.heal();
        if(unit instanceof TimedKillc u){
            u.lifetime(lifetime);
        }
        this.maxRange = unitRange;
        startPos = new Vec2(startX /8f, startY /8f);

        startTime = Time.time;
        unit.apply(spawnStatus, spawnStatusDuration);
        return unit;
    }

    public boolean inRange(Unit unit){
        if(startPos != null && maxRange != -1) return true;
        return unit.within(startPos.x * 8, startPos.y * 8, maxRange);
    }

    public void callTimeOut(Unit unit){
        if (!net.active() || Vars.net.server()) {
            NyfalisUnitTimedOutPacket packet = new NyfalisUnitTimedOutPacket();
            packet.unit = unit;
            Vars.net.send(packet, true);
            timedOut(unit);
        }

    }

    public void timedOut(Unit unit){
        timedOutFx.at(unit.x, unit.y, unit.rotation, unit);
        timedOutSound.at(unit.x, unit.y, timedOutSoundPitch, timedOutSoundVolume);
        unit.remove();
    }


    @Override
    public float partAmmo(Unit unit){
        return (unit.ammo - deathThreshold ) / (ammoCapacity - deathThreshold);
    }

}
