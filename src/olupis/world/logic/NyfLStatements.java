package olupis.world.logic;

import arc.func.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.logic.LExecutor.*;
import mindustry.ui.*;
import mindustry.world.*;
import olupis.*;
import olupis.content.*;
import olupis.world.*;

import static mindustry.Vars.world;
import static olupis.NyfalisVars.*;

public class NyfLStatements{
    public static LCategory nyfalian = new LCategory("nyfalian", NyfalisColors.supportGreen, Icon.planet);
    public static Seq<Prov<LStatement>> allNyf = Seq.with(
        GetCalyxTile::new,
        NyfalisSetRuleStatement::new
    );

    public static void writeNyfLogic(Object obj, StringBuilder out) {
        if (obj.getClass() == NyfalisSetRuleStatement.class) {
            out.append("nyf-setrule");
            out.append(" ");
            out.append(((NyfalisSetRuleStatement)obj).rule.name());
            out.append(" ");
            out.append(((NyfalisSetRuleStatement)obj).value);
            out.append(" ");
            out.append(((NyfalisSetRuleStatement)obj).p1);
            out.append(" ");
            out.append(((NyfalisSetRuleStatement)obj).p2);
            out.append(" ");
            out.append(((NyfalisSetRuleStatement)obj).p3);
            out.append(" ");
            out.append(((NyfalisSetRuleStatement)obj).p4);
        }else if(obj.getClass() == GetCalyxTile.class){
            out.append("nyf-getcalyxtile");
            out.append(" ");
            out.append(((GetCalyxTile)obj).result);
            out.append(" ");
            out.append(((GetCalyxTile)obj).x);
            out.append(" ");
            out.append(((GetCalyxTile)obj).y);
            out.append(" ");
            out.append(((GetCalyxTile)obj).r);
        }
    }

    public static LStatement readNyfLogic(String[] tokens, int length) {
        if (tokens[0].equals("nyf-setrule")) {
            NyfalisSetRuleStatement result = new NyfalisSetRuleStatement();
            if (length > 1) {
                result.rule = NyfLogicRule.valueOf(tokens[1]);
            }

            if (length > 2) {
                result.value = tokens[2];
            }

            if (length > 3) {
                result.p1 = tokens[3];
            }

            if (length > 4) {
                result.p2 = tokens[4];
            }

            if (length > 5) {
                result.p3 = tokens[5];
            }

            if (length > 6) {
                result.p4 = tokens[6];
            }

            result.afterRead();
            return result;
        }else if (tokens[0].equals("nyf-getcalyxtile")) {
            GetCalyxTile result = new GetCalyxTile();

            if (length > 1) {
                result.result = tokens[1];
            }

            if (length > 2) {
                result.x = tokens[2];
            }

            if (length > 3) {
                result.y = tokens[3];
            }

            if (length > 4) {
                result.r = tokens[4];
            }

            result.afterRead();
            return result;
        }
        return null;
    }

    public static class NyfLStatment extends LStatement{

        @Override
        public void build(Table table){}

        @Override
        public LInstruction build(LAssembler builder){return null;}

        @Override
        public LCategory category(){
            return nyfalian;
        }

        @Override
        public void write(StringBuilder builder){
            writeNyfLogic(this, builder);
        }
    }

    public static class GetCalyxTile extends NyfLStatment{
        public String result = "result", x = "0", y = "0", r = "1";

        @Override
        public void build(Table table){
            fields(table, result, str -> result = str);

            table.add(" = get ");

            row(table);
            fields(table, x, str -> x = str);
            table.add(", ");
            fields(table, y, str -> y = str);
            table.add( " :" );
            fields(table, r, str -> r = str);
            table.add( " tiles" );
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LInstruction build(LAssembler builder){
            return new GetCalyx(builder.var(x), builder.var(y), builder.var(result), builder.var(r));
        }
    }

    public static class GetCalyx implements LInstruction{
        public LVar x, y, r;
        public LVar dest;

        public GetCalyx(LVar x, LVar y, LVar dest, LVar r){
            this.x = x;
            this.y = y;
            this.dest = dest;
            this.r = r;
        }

        public GetCalyx(){}

        @Override
        public void run(LExecutor exec){
            Tile tile = world.tile(Mathf.round(x.numf()), Mathf.round(y.numf()));
            if(tile == null){
                dest.setobj(null);
            }else{
                dest.setobj(EnvUpdater.getInfested(x.numi(), y.numi(), r.numi()));
            }
        }
    }

    public static class NyfalisSetRuleStatement extends NyfLStatment{
        public NyfLogicRule rule = NyfLogicRule.calyxSpread;
        public String value = "1", p1 = "0", p2 = "0", p3 = "100", p4 = "100";

        @Override
        public void build(Table table){
            rebuild(table);
        }

        void rebuild(Table table){
            table.clearChildren();

            table.button(b -> {
                b.label(() -> rule.name()).growX().wrap().labelAlign(Align.center);
                b.clicked(() -> showSelect(b, NyfLogicRule.all, rule, o -> {
                    rule = o;
                    rebuild(table);
                }, 2, c -> c.width(150f)));
            }, Styles.logict, () -> {}).size(160f, 40f).margin(5f).pad(4f).color(table.color);

            switch(rule){
                default -> {
                    table.add(" = ");

                    field(table, value, s -> value = s);
                }
            }
        }

        @Override
        public boolean privileged(){
            return true;
        }

        @Override
        public LInstruction build(LAssembler builder){
            return new NyfSetRuleI(rule, builder.var(value), builder.var(p1), builder.var(p2), builder.var(p3), builder.var(p4));
        }
    }

    public static class NyfSetRuleI implements LInstruction{
        public NyfLogicRule rule = NyfLogicRule.calyxSpread;
        public LVar value, p1, p2, p3, p4;

        public NyfSetRuleI(NyfLogicRule rule, LVar value, LVar p1, LVar p2, LVar p3, LVar p4){
            this.rule = rule;
            this.value = value;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.p4 = p4;
        }

        public NyfSetRuleI(){
        }

        @Override
        public void run(LExecutor exec){
            switch(rule){
                case calyxSpread -> calyxSpreading = value.bool();
                case calyxSpreadFactor ->  calyxSpreadingFactor = value.numf();
                case calyxBuildingFactor ->  calyxBuildingFactor = value.numf();

                case damagingWeather -> damagingWeather = value.bool();
            }
        }
    }
}
