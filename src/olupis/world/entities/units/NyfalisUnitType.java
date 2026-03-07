package olupis.world.entities.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.abilities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.*;
import olupis.world.ai.*;
import olupis.world.blocks.defence.*;
import olupis.world.blocks.unit.*;
import olupis.world.entities.*;
import olupis.world.entities.entities.*;

import java.util.*;

import static arc.Core.settings;
import static mindustry.Vars.*;

public class NyfalisUnitType extends UnitType {
    /*Custom RTS commands*/
    public boolean canCircleTarget = false, canHealUnits = false, canGuardUnits  = false, canMend = false, canDeploy = false, canDash = false, canCharge = false, canRetreat = false,
                            constructHideDefault = false, customMineAi = false, waveHunts = false, cantMove = false, AiCircleBomb = false, borrows = false;
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
                            payloadUpdateRequiresStatus = false,
                            pickupBlocks = true,  //Only used in LeggedPayloadUnit
                            payloadDisarms = false,
                            healingIgnoresMines = false,
                            attackRotationLock = false
    ;
    public float minVel = -1, idleCircleRaduis = rotateSpeed;
    public HashMap<Unit, Integer> velPreviousAngle = new HashMap<>();
    public Color secondaryLightColor = NyfalisColors.floodLightColor;
    public float secondaryLightRadius = lightRadius  * 2, deathRegrowChance = 0.1f;
    public StatusEffect payloadUpdateSE = StatusEffects.none, payloadDisarmSE = StatusEffects.disarmed, vLockSE;

    public TextureRegion bossRegion, borrowRegion;

    //TODO: This is a mess, mostly a proof of concept please replace

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
        
            if (customMoveCommand || cantMove){
                if(customMoveCommand){
                    commands.replace(UnitCommand.moveCommand, NyfalisUnitCommands.nyfalisMoveCommand);
                    if(defaultCommand == UnitCommand.moveCommand) defaultCommand = NyfalisUnitCommands.nyfalisMoveCommand;
                }
                else commands.remove(UnitCommand.moveCommand);
            }
            if(canDeploy)commands.add(NyfalisUnitCommands.nyfalisDeployCommand);
            if(canCircleTarget) commands.add(NyfalisUnitCommands.circleCommand);
            if(canHealUnits) commands.add(NyfalisUnitCommands.healCommand);
            if(canMend) commands.add(NyfalisUnitCommands.nyfalisMendCommand);
            if (customMineAi){
                if(commands.contains(UnitCommand.mineCommand)) commands.remove(UnitCommand.mineCommand);
                commands.add(NyfalisUnitCommands.nyfalisMineCommand);
            }
            if (canGuardUnits) commands.add(NyfalisUnitCommands.nyfalisGuardCommand);
            if (canDash)commands.add(NyfalisUnitCommands.nyfalisDashCommand);
            if (canCharge) commands.add(NyfalisUnitCommands.nyfalisChargeCommand);
            if (canBoost && alwaysBoosts) commands.remove(UnitCommand.boostCommand);
            if(canRetreat) commands.add(NyfalisUnitCommands.nyfalisRetreatCommand);
            //Move it to last
            if (commands.contains(UnitCommand.enterPayloadCommand)){
                commands.remove(UnitCommand.enterPayloadCommand);
                commands.add(UnitCommand.enterPayloadCommand);
            }



        if(generateDisplayFactory){
            var pwr = (PowerUnitTurret) Vars.content.blocks().find(b -> b instanceof PowerUnitTurret c && c.allUnitTypes().contains(this));
            if(pwr  != null)displayFactory.add(pwr);

            var cons = (ItemUnitTurret) Vars.content.blocks().find(b -> b instanceof ItemUnitTurret c && c.allUnitTypes().contains(this));
            if(cons != null && pwr == null){
                displayFactory.add(cons);
                if(cons.statArticulator != null && cons.possibleUnitTypes(true).contains(this)) displayFactory.add(cons.statArticulator);
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

        //Find a constructor
        var cons = (ItemUnitTurret) Vars.content.blocks().find(b -> b instanceof ItemUnitTurret c && c.allUnitTypes().contains(this));
        if(cons != null){
            boolean alt = cons.possibleUnitTypes(false).contains(this);
            if(timeReturn != null){
                float mul = cons.ammoTypes.values().toSeq().find(b -> b.spawnUnit == this).reloadMultiplier;
                timeReturn[0] = cons.reload * mul;
            }

           //idk
            Item modifier = cons.unitTypeToAmmo(this);
            if(!modifier.isHidden()){
                ItemStack[] cost = alt  ? cons.requiredItems : cons.requiredAlternate;
                ItemStack[] out = new ItemStack[cost.length + 1];
                for(int i = 0; i < cost.length; i++){
                    out[i] = cost[i];
                }

                out[cost.length] = new ItemStack(modifier, 1);
                return out;
            } else  return  alt  ? cons.requiredItems : cons.requiredAlternate;
        }

        //find Fabricator
        // only the 3rd real entry is considered a
        var rec = (Fabricator)content.blocks().find(b -> b instanceof Fabricator re && re.upgrades.contains(u -> u[1] == this));

        if(rec != null && rec.findConsumer(i -> i instanceof ConsumeItems) instanceof ConsumeItems ci){
            ItemSeq reqs = new ItemSeq();
            reqs.add(ci.items);

            if(prevReturn != null){
                //3rd entry is the real upgrade from, not the inputted
                var up = rec.upgrades.find(u -> u[1] == this);
                if(up.length >= 3){
                    prevReturn[0] = rec.upgrades.find(u -> u[1] == this)[3];
                    for(var stack : rec.upgrades.find(u -> u[1] == this)[0].getTotalRequirements()){
                        reqs.add(stack.item, stack.amount);
                    }
                } else prevReturn[0] =  rec.upgrades.find(u -> u[1] == this)[0];
            }
            if(timeReturn != null){
                timeReturn[0] = rec.constructTime;
            }
            return reqs.toArray();
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
            stats.add(new Stat("olupis-relevant", StatCat.optional), table -> displayFactory.each(fac -> {
                table.row();
                table.table(NyfalisColors.infoPanel, t -> {
                    boolean show = (fac instanceof Block b && b.isVisible()) || (fac instanceof  UnitType u && !u.isBanned());
                    if(!fac.unlocked() && (Vars.state.isCampaign() || !Vars.state.isPlaying())) t.image(Icon.lock.getRegion()).tooltip(fac.localizedName).size(25).pad(10f).left().scaling(Scaling.fit);
                    else {
                        if(show) t.image(fac.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).get().clicked(()-> ui.content.show(fac));
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
                }).growX().padBottom(5f).padLeft(10f).padRight(10f).row();
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
        Unit unit =  super.create(team);

        unit.elevation = flying || alwaysBoosts ? 1f : 0;
        unit.apply(spawnStatus, spawnStatusDuration);
        if(weaponsStartEmpty)unit.apply(NyfalisStatusEffects.unloaded, 60f); //is now a second bc it won't get synced properly
        return unit;
    }

    @Override
    public void draw(Unit unit){
        if(parts.size > 0) NyfPartParms.nyfparams.set(unit);

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
        borrowRegion = Core.atlas.find(name + "-borrowed", name);

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
    public void killed(Unit unit){
        super.killed(unit);
        if(deathRegrowChance > 0 &&  Mathf.randomBoolean(deathRegrowChance) &&unit.team ==  state.rules.waveTeam) NyfWorldFuckingHelper.growSprigs(unit.tileOn());
    }

    @Override
    public <T extends Unit&Legsc> void drawLegs(T unit){
        if(borrows && unit.hasEffect(deployEffect)){
            applyColor(unit);

            Color mix = Pal.darkestestGray;
            if(unit.floorOn() != null) mix.set(unit.floorOn().mapColor).lerp(Color.black, 0.45f);
            Draw.color(mix, 0.8f);
            Draw.scl(1.1f, 1.1f);
            Draw.rect(borrowRegion, unit.x, unit.y, unit.rotation - 90);

            Draw.reset();
        }else super.drawLegs(unit);
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

        if(payloadDisarms && unit instanceof  Payloadc p && p.hasPayload()){
            unit.apply(payloadDisarmSE, Time.toSeconds);
        }

        boolean circle = minVel > 0 && unit.speedMultiplier >= 0.5;
        boolean vlock = attackRotationLock && unit.hasEffect(vLockSE);

        if(circle || vlock){
            if (!velPreviousAngle.containsKey(unit)) velPreviousAngle.put(unit, Math.round(unit.vel.angle() * 100));

            if(circle){
                unit.vel.setLength2(Math.max(minVel, unit.vel.len2()));
                if(!unit.isShooting &unit.vel.len2() <= (minVel * 1.25f)){
                    int mul = unit.vel.angle() - unit.vel.angle() >= 0 ? 2 : -2;
                    unit.vel.setAngle(unit.vel.angle() - (idleCircleRaduis * mul));

                    unit.lookAt(unit.vel.angle());
            }}

            if(vlock){
                Tmp.v1.set(unit).setAngle(velPreviousAngle.get(unit) * 0.01f);
                unit.vel.setAngle(velPreviousAngle.get(unit) * 0.01f);
                unit.rotation(Tmp.v1.angle());
            }
            unit.prefRotation();
            velPreviousAngle.put(unit, Math.round(unit.vel.angle() * 100));
        }

        if(velPreviousAngle.containsKey(unit) && unit.dead()) velPreviousAngle.remove(unit);


    }


    public static boolean onWater(Unit unit){
        return unit.floorOn().isLiquid;
    }

    public boolean onDeepWater(Unit unit){
        return onWater(unit) && unit.floorOn().drownTime > 0;
    }

    public boolean canUpdatePayload(Unit unit){
        return payloadUnitsUpdate || (payloadUpdateRequiresStatus && unit.hasEffect(payloadUpdateSE));
    }

    public void updatePayload(Unit unit){

    }
}
