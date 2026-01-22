package olupis.input;

import arc.*;
import arc.func.*;
import arc.input.*;
import arc.struct.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.Binding;
import olupis.content.*;
import olupis.world.ai.*;
import olupis.world.entities.units.*;

import javax.naming.*;

public class NyfalisUnitCommands {
    public static Seq<UnitCommand> hasAppend = new Seq<>();

    public static NyfUnitCommand
        circleCommand, healCommand, nyfalisMoveCommand, nyfalisDeployCommand, nyfalisMineCommand, nyfalisGuardCommand,
        nyfalisMendCommand, nyfalisChargeCommand, nyfalisDashCommand, nyfalisRetreatCommand;


        public static void loadUnitCommands(){
            circleCommand = new NyfUnitCommand("nyfalis-circle", "commandRally", u -> {
                if(!u.type().flying){
                    var ai = new NyfalisGroundAi();
                    ai.shouldCircle = true;
                    return ai;
                }else{
                    var ai = new AgressiveFlyingAi();
                    ai.shouldCircle = true;
                    return ai;
                }
            }){{
                switchToMove = resetTarget = false;
                drawTarget = true;
            }};
            healCommand = new NyfUnitCommand("nyfalis-heal", "units", u -> new UnitHealerAi());
            nyfalisMineCommand = new NyfUnitCommand("mine", "production", u -> new NyfalisMiningAi()){
                @Override
                public String localized(){
                    return mineCommand.localized();
                }
            };
            nyfalisGuardCommand = new NyfUnitCommand("nyfalis-guard", "units", u -> new ArmDefenderAi());
            nyfalisMendCommand = new NyfUnitCommand("nyfalis-mend", "add", u -> {
                //No other better word for this
                var ai = new UnitHealerAi();
                ai.includeBlocks = true;
                return ai;
            });
            nyfalisMoveCommand = new NyfUnitCommand("move", "right", u -> {
                if(u.isGrounded()){
                    return new NyfalisGroundAi();
                }
                return new AIController(){
                    @Override
                    public void updateUnit(){
                        if(unit.controller() instanceof CommandAI ai){
                            if(u.type instanceof NyfalisUnitType nyf && nyf.canDeploy && unit.isGrounded()) return;
                            ai.defaultBehavior();
                        }
                        super.updateUnit();
                        if(u.type instanceof NyfalisUnitType nyf){
                            if(nyf.alwaysBoosts) unit.updateBoosting(true);
                        }
                    }
                };
            }){
                @Override
                public String localized(){
                    return moveCommand.localized();
                }
                {
                    switchToMove = resetTarget = false;
                    drawTarget = true;
            }};
            nyfalisChargeCommand = new NyfUnitCommand("nyfalis-charge", "commandAttack", u -> {
                if(u.type instanceof NyfalisUnitType nyf && nyf.alwaysBoosts){
                    return new DeployedAi();
                }else{
                    var ai = new NyfalisGroundAi();
                    ai.shouldCharge = true;
                    return ai;
                }
            }){{
                switchToMove = resetTarget = false;
                drawTarget = true;
            }};
            nyfalisDeployCommand = new NyfUnitCommand("nyfalis-deploy", "down", u -> new DeployedAi()){{
                switchToMove = resetTarget = false;
                drawTarget = true;
            }};
            nyfalisDashCommand = new NyfUnitCommand("nyfalis-dash", "redo", u -> new NyfalisGroundAi()){{
                    switchToMove = resetTarget = false;
                    drawTarget = true;
                }};
            nyfalisRetreatCommand = new NyfUnitCommand("nyfalis-retreat",  "rotate", u -> new RetreatAi()){{
                drawTarget = true;
                switchToMove = resetTarget = false;
            }};
        }


        public static class NyfUnitCommand extends  UnitCommand{

            public NyfUnitCommand(String name, String icon, Func<Unit, AIController> controller){
                super(name, icon, controller);
                if(Core.bundle.has("command." + name + ".append")) hasAppend.add(this);
            }

            public NyfUnitCommand(String name, String icon, KeyBind keybind, Func<Unit, AIController> controller){
                super(name, icon, keybind, controller);
                if(Core.bundle.has("command." + name + ".append")) hasAppend.add(this);
            }

            @Override
            public String localized(){
                if(!Vars.headless && Core.bundle.has("command." + name + ".append")){
                    if(Core.input.keyDown(Binding.boost)) return Core.bundle.get("command." + name).replace("*", "") + "\n[#" + Pal.stat.toString() + "]" + (Core.bundle.get("command." + name + ".append"));
                }

                return Core.bundle.get("command." + name);
            }
        }
}
