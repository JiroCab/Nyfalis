package olupis.input;

import mindustry.ai.UnitCommand;
import mindustry.ai.types.CommandAI;
import mindustry.entities.units.AIController;
import olupis.world.ai.*;
import olupis.world.entities.units.NyfalisUnitType;

public class NyfalisUnitCommands {

    public static UnitCommand circleCommand, healCommand, nyfalisMoveCommand, nyfalisDeployCommand, nyfalisMineCommand, nyfalisGuardCommand, nyfalisMendCommand, nyfalisChargeCommand, nyfalisDashCommand;


        public static void loadUnitCommands(){
            circleCommand = new UnitCommand("nyfalis-circle", "commandRally", u -> {
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
            healCommand = new UnitCommand("nyfalis-heal", "units", u -> new UnitHealerAi());
            nyfalisMineCommand = new UnitCommand("mine", "production", u -> new NyfalisMiningAi());
            nyfalisGuardCommand = new UnitCommand("nyfalis-guard", "units", u -> new ArmDefenderAi());
            nyfalisMendCommand = new UnitCommand("nyfalis-mend", "add", u -> {
                //No other better word for this
                var ai = new UnitHealerAi();
                ai.includeBlocks = true;
                return ai;
            });
            nyfalisMoveCommand = new UnitCommand("move", "right", u -> {
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
            }){{
                switchToMove = resetTarget = false;
                drawTarget = true;
            }};
            nyfalisChargeCommand = new UnitCommand("nyfalis-charge", "commandAttack", u -> {
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
            nyfalisDeployCommand = new UnitCommand("nyfalis-deploy", "down", u -> new DeployedAi()){{
                switchToMove = resetTarget = false;
                drawTarget = true;
            }};
            nyfalisDashCommand = new UnitCommand("nyfalis-dash", "redo", u -> new NyfalisGroundAi()){{
                    switchToMove = resetTarget = false;
                    drawTarget = true;
                }};
        }

}
