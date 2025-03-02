package olupis.world.entities.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.meta.*;
import olupis.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.ai.*;
import olupis.world.blocks.defence.*;
import olupis.world.blocks.unit.*;
import olupis.world.entities.*;
import olupis.world.entities.parts.*;

import java.util.AbstractMap.*;
import java.util.*;
import java.util.Map.*;

import static arc.Core.settings;
import static mindustry.Vars.*;

public class NyfalisUnitType extends UnitType {
    /*Custom RTS commands*/
    public boolean canCircleTarget = false, canHealUnits = false, canGuardUnits  = false, canMend = false, canDeploy = false, canDash = false, canCharge = false,
                           constructHideDefault = false, customMineAi = false, waveHunts = false, cantMove = false, AiCircleBomb = false;
    /*Makes (legged) units boost automatically regardless of Ai*/
    public boolean alwaysBoostOnSolid = false;
    /*Replace Move Command to a custom one*/
    public boolean customMoveCommand = false;
    /*Face targets when idle/not moving, assumes `customMoveCommand` = true  */
    public boolean idleFaceTargets = false;
    /*forces the unit to be landed on deploy*/
    public boolean deployLands = false, alwaysBoosts = false, deployHasEffect = false, inverseLanding = false;
    public StatusEffect deployEffect = StatusEffects.none;
    public float deployEffectTime = 20f;
    /*Reload cooldown on spawn (gant cheese fix)*/
    public boolean weaponsStartEmpty = false;
    /*Effects that a unit spawns with*/
    public StatusEffect spawnStatus = StatusEffects.none;
    public float spawnStatusDuration = 60f * 5f;
    public Seq<UnlockableContent> displayFactory = new Seq<>();
    /*secondary light  parameters*/
    public boolean emitSecondaryLight = false,
                            generateDisplayFactory = true,
                            payloadUnitsUpdate = false,
                            pickupBlocks = true; //Only used in LeggedPayloadUnit
    public Color secondaryLightColor = NyfalisColors.floodLightColor;
    public float secondaryLightRadius = lightRadius  * 2;

    public TextureRegion bossRegion;

    //TODO: This is a mess, mostly a proof of concept please replace
    public Map<Unit, Entry<Seq<Payload>, Seq<Weapon>>> weaponTracker = new HashMap<>();

    public NyfalisUnitType(String name){
        super(name);
        outlineColor = NyfalisColors.contentOutline;
        ammoType = new ItemAmmoType(NyfalisItemsLiquid.rustyIron);
        researchCostMultiplier = 0f;
        generateIcons = true;
        if(customMoveCommand) defaultCommand = NyfalisUnitCommands.nyfalisMoveCommand;
    }

    @Override
    public void init(){
        super.init();

        if(NyfalisMain.incompatible) return;
        Seq<UnitCommand> cmds = Seq.with(commands);
            if (customMoveCommand || cantMove){
                cmds.remove(UnitCommand.moveCommand);
                if(customMoveCommand)cmds.add(NyfalisUnitCommands.nyfalisMoveCommand);
            }
            if(canDeploy)cmds.add(NyfalisUnitCommands.nyfalisDeployCommand);
            if(canCircleTarget) cmds.add(NyfalisUnitCommands.circleCommand);
            if(canHealUnits) cmds.add(NyfalisUnitCommands.healCommand);
            if(canMend) cmds.add(NyfalisUnitCommands.nyfalisMendCommand);
            if (customMineAi){
                if(cmds.contains(UnitCommand.mineCommand)) cmds.remove(UnitCommand.mineCommand);
                cmds.add(NyfalisUnitCommands.nyfalisMineCommand);
            }
            if (canGuardUnits) cmds.add(NyfalisUnitCommands.nyfalisGuardCommand);
            if (canDash)cmds.add(NyfalisUnitCommands.nyfalisDashCommand);
            if (canCharge) cmds.add(NyfalisUnitCommands.nyfalisChargeCommand);
            if (canBoost && alwaysBoosts) cmds.remove(UnitCommand.boostCommand);
        commands = cmds.toArray();

        if(generateDisplayFactory){
            var pwr = (PowerUnitTurret) Vars.content.blocks().find(b -> b instanceof PowerUnitTurret c && c.allUnitTypes().contains(this));
            if(pwr  != null)displayFactory.add(pwr);

            var cons = (ItemUnitTurret) Vars.content.blocks().find(b -> b instanceof ItemUnitTurret c && c.allUnitTypes().contains(this));
            if(cons != null && pwr == null){
                displayFactory.add(cons);
                if(cons.statArticulator != null && cons.possibleUnitTypes(false).contains(this)) displayFactory.add(cons.statArticulator);
            }

            var rec = (Reconstructor)content.blocks().find(b -> b instanceof Reconstructor re && re.upgrades.contains(u -> u[1] == this));
            if(rec != null) displayFactory.add(rec);

            var ufac = (UnitFactory)content.blocks().find(u -> u instanceof UnitFactory uf && uf.plans.contains(p -> p.unit == this));
            if(ufac != null) displayFactory.add(ufac);

            var aby = content.units().find(u -> u.abilities.contains(a -> a instanceof UnitSpawnAbility s && s.unit == this));
            if(aby != null) displayFactory.add(aby);

            var pad = (MechPad)content.blocks().find(b -> b instanceof MechPad p && p.type == this);
            if(pad != null) displayFactory.add(pad);

            var core = (CoreBlock)content.blocks().find(b -> (b instanceof CoreBlock c && c.unitType == this) || (b instanceof  PropellerCoreBlock p && p.spawns == this));
            if(core != null){
                displayFactory.add(core);
            }

        }

    }

    @Override
    public @Nullable ItemStack[] getRequirements(@Nullable UnitType[] prevReturn, @Nullable float[] timeReturn){
       var cons = (ItemUnitTurret) Vars.content.blocks().find(b -> b instanceof ItemUnitTurret c && c.allUnitTypes().contains(this));

       if(cons != null){
           boolean alt = cons.possibleUnitTypes(false).contains(this);
           if(prevReturn != null &&cons.possibleUnitTypes(true).contains(this)){
               //prevReturn[0] = rec.upgrades.find(u -> u[1] == this)[0];
           }
           if(timeReturn != null){
               float mul = cons.ammoTypes.values().toSeq().find(b -> b.spawnUnit == this).reloadMultiplier;
               timeReturn[0] = cons.reload * mul;
           }

           //can't be bothered to add the modifier item - rushie
           return alt  ? cons.requiredItems : cons.requiredAlternate;
       }

       return super.getRequirements(prevReturn, timeReturn);
    };

    @Override
    public void display(Unit unit, Table table){
        super.display(unit, table);
        if(unit.controller() instanceof NyfalisMiningAi ai && ai.targetItem != null) table.table(t -> {
            table.row();
            t.table(i ->  {
                i.image(ai.ore != null ? ai.ore.overlay().fullIcon : ai.targetItem.fullIcon).scaling(Scaling.bounded).left().pad(5);
                i.add(ai.targetItem.localizedName).pad(5).wrap().center();
            }).left();
            if (ai.ore != null && (Core.settings.getBool("mouseposition") || Core.settings.getBool("position"))) {
                t.row();
                t.add("[lightgray](" + Math.round(ai.ore.x) + ", " + Math.round(ai.ore.y) + ")").growX().wrap().center();
            }
        }).grow();
    }

    @Override
    public void setStats(){
        super.setStats();

        /*We have a weird tech tree, this just makes it easier for the player's end*/
        if(displayFactory.size >= 1){
            stats.add(Stat.input, table -> displayFactory.each(fac -> {
                table.row();
                table.table(Styles.grayPanel, t -> {
                    boolean show = (fac instanceof Block b && b.isVisible()) || (fac instanceof  UnitType u && !u.isBanned());
                    if(!fac.unlocked() && (Vars.state.isCampaign() || !Vars.state.isPlaying())) t.image(Icon.lock.getRegion()).tooltip(fac.localizedName).size(25).pad(10f).left().scaling(Scaling.fit);
                    else {
                        if(show) t.image(fac.fullIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        else t.image(Icon.cancel.getRegion()).color(Pal.remove).size(40).pad(10f).left().scaling(Scaling.fit);
                        t.table(info -> {
                            info.add(fac.localizedName).left();
                            if (Core.settings.getBool("console")) {
                                info.row();
                                info.add(fac.name).left().color(Color.lightGray);
                            }
                        });
                        t.button("?", Styles.flatBordert, () -> ui.content.show(fac)).size(40f).pad(10).right().grow().visible(fac::unlockedNow);
                    }
                }).growX().pad(5).row();
            }));
        }

        if(weapons.any()){
            stats.remove(Stat.weapons);
            stats.add(Stat.weapons, NyfalisStats.weapons(this, weapons));
        }

        if(settings.getBool("nyfalis-debug")){
            stats.add(new Stat("olupis-id", StatCat.function), this.id);
        }

    }

    @Override
    public Unit create(Team team){
        Unit unit = constructor.get();

        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying || alwaysBoosts ? 1f : 0;
        unit.heal();
        if(unit instanceof TimedKillc u){
            u.lifetime(lifetime);
        }
        unit.apply(spawnStatus, spawnStatusDuration);
        if(weaponsStartEmpty)unit.apply(NyfalisStatusEffects.unloaded, 60f); //is now a second bc it won't get synced properly
        return unit;
    }

    @Override
    public void draw(Unit unit){
        if(parts.size > 0) updatePrams(unit);

        super.draw(unit);
    }

    @Override
    public void drawBody(Unit unit){
        applyColor(unit);
        TextureRegion e = !unit.hasEffect(StatusEffects.boss) || bossRegion == null ? region : bossRegion;
        Draw.rect(e, unit.x, unit.y, unit.rotation - 90);

        Draw.reset();
    }

    @Override
    public void load() {
        super.load();
        bossRegion = Core.atlas.find(name + "-boss", name);
    }

    public float partAmmo(Unit unit){
        return unit.ammo/ ammoCapacity;
    }

    @Override
    public void drawLight(Unit unit){
        if(lightRadius > 0) Drawf.light(unit.x, unit.y, lightRadius, lightColor, lightOpacity);
        if(secondaryLightRadius > 0) Drawf.light(unit.x, unit.y, secondaryLightRadius, secondaryLightColor, secondaryLightColor.a);
    }

    @Override
    public void update(Unit unit){
        super.update(unit);

        if(deployHasEffect && (!deployLands || unit.isGrounded())) unit.apply(deployEffect, deployEffectTime);
        if(unit.type instanceof  NyfalisUnitType nyf && nyf.canDeploy) {
            if (!unit.isPlayer() && !(unit.controller() instanceof LogicAI)) {
                boolean deployed = (unit.isCommandable() && unit.command().command == NyfalisUnitCommands.nyfalisDeployCommand);
                if(deployed && unit.isGrounded())unit.apply(deployEffect, deployEffectTime);

                //let the ai handle boosting
                if (!deployed && alwaysBoosts) {
                    unit.unapply(deployEffect);
                } else if (deployLands) unit.updateBoosting(!(deployed && unit.canLand()));
            }
        }

        if(alwaysBoostOnSolid && canBoost && (unit.controller() instanceof CommandAI c && c.command != UnitCommand.boostCommand)){
            unit.updateBoosting(unit.onSolid());
        }

        if(payloadUnitsUpdate) updatePayload(unit);
    }

    public void updatePrams(Unit unit){
        NyfPartParms.nyfparams.set(unit.healthf(), unit.team.id, unit.elevation(), partAmmo(unit));
    }

    public boolean onWater(Unit unit){
        return unit.floorOn().isLiquid;
    }

    public boolean onDeepWater(Unit unit){
        return onWater(unit) && unit.floorOn().drownTime > 0;
    }

    public void updatePayload(Unit unit){
        //AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH

        if(unit.dead) weaponTracker.remove(unit);
        if(!(unit instanceof Payloadc c)) return;
        else if(c.payloads().isEmpty()){
            weaponTracker.remove(unit);
            return;
        }

        if(!weaponTracker.containsKey(unit))weaponTracker.put(unit, new SimpleEntry<>(c.payloads().copy(), new Seq<>()));

        Seq<Payload> load = weaponTracker.get(unit).getKey();
        Seq<Weapon> wep = weaponTracker.get(unit).getValue();

        //Prevents crash for alternating weapons (ie dagger style weapons)
        if(!load.containsAll(c.payloads(), false) || load.size != c.payloads().size || wep.size == 0){
            load.set(c.payloads().copy());
            wep.clear();

            for(Payload p : c.payloads()){
                if(!(p instanceof UnitPayload up) || !up.unit.hasWeapons()) continue;
                Unit u = up.unit;
                if(u.mounts().length >= 1){
                    for(WeaponMount mount : u.mounts()){
                        if(mount.weapon.bullet.killShooter) continue; //DONOT
                        Weapon w = mount.weapon.copy();

                        //Flat reload nerf since we can go beyond the unit cap and this is to "help" w/ balancing
                        w.reload *=2f;

                        //compensate for them not needing to alternate
                        if(w.alternate &&mount.side) w.shoot.firstShotDelay = Math.max(w.shoot.firstShotDelay, 1) * (w.reload * 0.5f);
                        w.rotateSpeed = Math.max(w.rotateSpeed, 20);
                        w.rotate = true;
                        w.alternate = false;

                        //TODO: Fireports?
                        w.shootX =  w.x = w.shootY = w.y = 0;
                        wep.add(w);
                    }
                }
            }

        }

        int[] m ={-1};
        for(Payload p : load){
            if(p instanceof UnitPayload up && up.unit.hasWeapons()){
                //we update the unit as well so we can update ability
                Unit u = up.unit;
                u.isShooting(true);
                u.x(unit.x);
                u.y(unit.y);
                u.type.update(u);
                u.rotation(unit.rotation);

                if(u.mounts().length >= 1){
                    WeaponMount[] mounts = u.mounts();

                    for(WeaponMount mount : mounts){
                        if(mount.target != null){
                            mount.rotation = mount.targetRotation;
                            mount.shoot = true;
                        }

                        m[0]++;
                        wep.get(m[0]).update(unit, mount);
                    }
                }
                u.update();
            }
        }
    }
}
