package olupis.world.blocks.misc;

import arc.func.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.blocks.logic.*;
import olupis.*;
import olupis.world.logic.NyfLStatements.*;

import static mindustry.logic.LStatements.*;

public class NyfalisLogicBlock extends LogicBlock {
    /*Credit: https://github.com/TeamViscott/ModProjectViscott/blob/master/src/viscott/world/block/logic/PvLogicBlock.java*/
    public Seq<Prov<LStatement>> allStatements, privStatements;

    public NyfalisLogicBlock(String name){
        super(name);

        privStatements = Seq.with( new Prov[]{
            UnitBindStatement::new,
            UnitControlStatement::new,
            UnitRadarStatement::new,
            UnitLocateStatement::new,
            //
            NyfalisSetRuleStatement::new,
            GetCalyxTile::new,
            //TODO: Manual Call Calyx spread instruction
            //TODO: check infected/Claxied tile (out = x y lvl# growth#)
            //TODO: Sprig growth call bc idk (x y )
            //

            //moved here so they dont load after unit controls and thow ppl off
            GetBlockStatement::new,
            SetBlockStatement::new,
            SpawnUnitStatement::new,
            ApplyStatusStatement::new,
            SpawnWaveStatement::new,
            SetRuleStatement::new,
            FlushMessageStatement::new,
            CutsceneStatement::new,
            ExplosionStatement::new,
            SetRateStatement::new,
            FetchStatement::new,
            GetFlagStatement::new,
            SetFlagStatement::new

        });
        allStatements = Seq.with( new Prov[]{
                InvalidStatement::new,
                ReadStatement::new,
                WriteStatement::new,
                DrawStatement::new,
                PrintStatement::new,
                DrawFlushStatement::new,
                PrintFlushStatement::new,
                GetLinkStatement::new,
                ControlStatement::new,
                SensorStatement::new,
                RadarStatement::new,
                SetStatement::new,
                OperationStatement::new,
                WaitStatement::new,
                StopStatement::new,
                LookupStatement::new,
                PackColorStatement::new,
                EndStatement::new,
                JumpStatement::new
        });
    }

    public class NyfalisLogicBuild extends LogicBuild {

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.pencil, Styles.cleari, () -> NyfalisVars.logicDialog.show(allStatements, code, executor, privileged, code -> configure(compress(code, relativeConnections())))).size(40);
        }
    }
}
