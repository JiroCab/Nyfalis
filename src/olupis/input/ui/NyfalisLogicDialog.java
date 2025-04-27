package olupis.input.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.actions.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.logic.LExecutor.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import java.util.*;

import static mindustry.Vars.*;
import static mindustry.logic.LCanvas.tooltip;
import static mindustry.logic.LogicDialog.*;

public class NyfalisLogicDialog extends BaseDialog {
    /*Credits: https://github.com/TeamViscott/ModProjectViscott/blob/master/src/viscott/world/ui/PvLogicDialog.java*/
    public NyfalisCanvas canvas;
    Cons<String> consumer = s -> {};
    boolean privileged;
    @Nullable
    LExecutor executor;

    Seq<Prov<LStatement>> statements;

    public NyfalisLogicDialog(){
        super("nyfalis-logic");

        clearChildren();

        canvas = new NyfalisCanvas(statements);
        shouldPause = true;

        addCloseListener();

        shown(this::setup);
        hidden(() -> consumer.get(canvas.save()));
        onResize(() -> {
            setup();
            canvas.rebuild();
        });

            add(canvas).grow().name("Pcanvas");

        row();

        add(buttons).growX().name("Pcanvas");
    }

    private void setup(){
        buttons.clearChildren();
        buttons.defaults().size(160f, 64f);
        buttons.button("@back", Icon.left, this::hide).name("back");

        buttons.button("@edit", Icon.edit, () -> {
            BaseDialog dialog = new BaseDialog("@editor.export");
            dialog.cont.pane(p -> {
                p.margin(10f);
                p.table(Tex.button, t -> {
                    TextButton.TextButtonStyle style = Styles.flatt;
                    t.defaults().size(280f, 60f).left();

                    t.button("@schematic.copy", Icon.copy, style, () -> {
                        dialog.hide();
                        Core.app.setClipboardText(canvas.save());
                    }).marginLeft(12f);
                    t.row();
                    t.button("@schematic.copy.import", Icon.download, style, () -> {
                        dialog.hide();
                        try{
                            canvas.allStatements = statements;
                            canvas.load(Core.app.getClipboardText().replace("\r\n", "\n"));
                        }catch(Throwable e){
                            ui.showException(e);
                        }
                    }).marginLeft(12f).disabled(b -> Core.app.getClipboardText() == null);
                });
            });

            dialog.addCloseButton();
            dialog.show();
        }).name("edit");

        if(Core.graphics.isPortrait()) buttons.row();

        buttons.button("@variables", Icon.menu, () -> {
            BaseDialog dialog = new BaseDialog("@variables");
            dialog.hidden(() -> {
                if(!wasPaused && !net.active()){
                    state.set(GameState.State.paused);
                }
            });

            dialog.shown(() -> {
                if(!wasPaused && !net.active()){
                    state.set(GameState.State.playing);
                }
            });

            dialog.cont.pane(p -> {
                p.margin(10f).marginRight(16f);
                p.table(Tex.button, t -> {
                    t.defaults().fillX().height(45f);
                    for(var s : executor.vars){
                        if(s.constant) continue;

                        Color varColor = Pal.gray;
                        float stub = 8f, mul = 0.5f, pad = 4;

                        t.add(new Image(Tex.whiteui, varColor.cpy().mul(mul))).width(stub);
                        t.stack(new Image(Tex.whiteui, varColor), new Label(" " + s.name + " ", Styles.outlineLabel){{
                            setColor(Pal.accent);
                        }}).padRight(pad);

                        t.add(new Image(Tex.whiteui, Pal.gray.cpy().mul(mul))).width(stub);
                        t.table(Tex.pane, out -> {
                            float period = 15f;
                            float[] counter = {-1f};
                            Label label = out.add("").style(Styles.outlineLabel).padLeft(4).padRight(4).width(140f).wrap().get();
                            label.update(() -> {
                                if(counter[0] < 0 || (counter[0] += Time.delta) >= period){
                                    String text = s.isobj ? LExecutor.PrintI.toString(s.objval) : Math.abs(s.numval - (long)s.numval) < 0.00001 ? (long)s.numval + "" : s.numval + "";
                                    if(!label.textEquals(text)){
                                        label.setText(text);
                                        if(counter[0] >= 0f){
                                            label.actions(Actions.color(Pal.accent), Actions.color(Color.white, 0.2f));
                                        }
                                    }
                                    counter[0] = 0f;
                                }
                            });
                            label.act(1f);
                        }).padRight(pad);

                        t.add(new Image(Tex.whiteui, typeColor(s, new Color()).mul(mul))).update(i -> i.setColor(typeColor(s, i.color).mul(mul))).width(stub);

                        t.stack(new Image(Tex.whiteui, typeColor(s, new Color())){{
                            update(() -> setColor(typeColor(s, color)));
                        }}, new Label(() -> " " + typeName(s) + " "){{
                            setStyle(Styles.outlineLabel);
                        }});

                        t.row();

                        t.add().growX().colspan(6).height(4).row();
                    }
                });
            });

            dialog.addCloseButton();
            dialog.show();
        }).name("variables").disabled(b -> executor == null || executor.vars.length == 0);

        buttons.button("@add", Icon.add, () -> {
            BaseDialog dialog = new BaseDialog("@add");
            dialog.cont.table(table -> {
                table.background(Tex.button);
                table.pane(t -> {
                    for(Prov<LStatement> prov : statements){
                        LStatement example = prov.get();
                        if(example instanceof LStatements.InvalidStatement || example.hidden() || (example.privileged() && !privileged) || (example.nonPrivileged() && privileged)) continue;

                        LCategory category = example.category();
                        Table cat = t.find(category.name);
                        if(cat == null){
                            t.table(s -> {
                                if(category.icon != null){
                                    s.image(category.icon, Pal.darkishGray).left().size(15f).padRight(10f);
                                }
                                s.add(category.localized()).color(Pal.darkishGray).left().tooltip(category.description());
                                s.image(Tex.whiteui, Pal.darkishGray).left().height(5f).growX().padLeft(10f);
                            }).growX().pad(5f).padTop(10f);

                            t.row();

                            cat = t.table(c -> {
                                c.top().left();
                            }).name(category.name).top().left().growX().fillY().get();
                            t.row();
                        }

                        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
                        style.fontColor = category.color;
                        style.font = Fonts.outline;

                        cat.button(example.name(), style, () -> {
                            canvas.add(prov.get());
                            dialog.hide();
                        }).size(130f, 50f).self(c -> tooltip(c, "lst." + example.name())).top().left();

                        if(cat.getChildren().size % 3 == 0) cat.row();
                    }
                }).grow();
            }).fill().maxHeight(Core.graphics.getHeight() * 0.8f);
            dialog.addCloseButton();
            dialog.show();
        }).disabled(t -> canvas.statements.getChildren().size >= LExecutor.maxInstructions);
    }

    @Override
    public void hide(){
        super.hide();
        canvas.resetDialog();
    }

    public void show(Seq<Prov<LStatement>> statements, String code, LExecutor executor, boolean privileged, Cons<String> modified){
        this.statements = statements;
        this.executor = executor;
        this.privileged = privileged;
        canvas.statements.clearChildren();
        canvas.rebuild();
        //canvas.privileged = privileged;
        try{
            canvas.load(code);
        }catch(Throwable t){
            Log.err(t);
            canvas.load("");
        }
        this.consumer = result -> {
            if(!result.equals(code)){
                modified.get(result);
            }
        };

        show();
    }

    public class NyfalisCanvas extends LCanvas{
        /*Stolen from: https://github.com/TeamViscott/ModProjectViscott/blob/master/src/viscott/types/logic/PvCanvas.java*/
        public Seq<Prov<LStatement>> allStatements;

        public NyfalisCanvas(Seq<Prov<LStatement>> statements) {
            super();
            allStatements = statements;
            resetDialog();
        }
        @Override
        public String save(){
            Seq<LStatement> st = statements.getChildren().<StatementElem>as().map(s -> s.st);
            st.each(LStatement::saveUI);

            return NyfalisAssembler.write(st);
        }

        public void resetDialog() {
            Reflect.set(LCanvas.class,"canvas", Vars.ui.logic.canvas);
        }
        public void setDialog() {
            Reflect.set(LCanvas.class,"canvas", this);
        }

        @Override
        public void load(String asm){
            setDialog();
            NyfalisLogicParser.jumps.clear();

            Seq<LStatement> statements = NyfalisAssembler.NyfalisRead(asm, false);
            statements.truncate(LExecutor.maxInstructions);
            this.statements.clearChildren();
            for(LStatement st : statements){
                add(st);
            }

            for(LStatement st : statements){
                st.setupUI();
            }

            this.statements.layout();
        }
    }

    /* https://github.com/TeamViscott/ModProjectViscott/blob/master/src/viscott/types/logic/PvAssembler.java */
    public static class NyfalisAssembler extends LAssembler{
        public static Seq<Prov<LStatement>> allStatement;
        public static Seq<LStatement> readLogic(String text, Seq<Prov<LStatement>> allStatements){
            //don't waste time parsing null/empty text
            allStatement = allStatements;
            if(text == null || text.isEmpty()) return new Seq<>();
            return new NyfalisLogicParser(text, allStatements).parse();
        }

        public static NyfalisAssembler NyfalisAssemble(String data, boolean privileged){
            NyfalisAssembler asm = new NyfalisAssembler();

            Seq<LStatement> st = new NyfalisLogicParser(data,allStatement).parse();

            asm.instructions = st.map(l -> l.build(asm)).retainAll(Objects::nonNull).toArray(LInstruction.class);
            return asm;
        }

        public static Seq<LStatement> NyfalisRead(String text, boolean privileged){
            //don't waste time parsing null/empty text
            if(text == null || text.isEmpty()) return new Seq<>();
            return new NyfalisLogicParser(text, allStatement).parse();
        }
    }

    /* https://github.com/TeamViscott/ModProjectViscott/blob/master/src/viscott/types/logic/PvParser.java */
    public static class NyfalisLogicParser{
        private static final String[] tokens = new String[16];
        private static final int maxJumps = 500;
        private static final StringMap opNameChanges = StringMap.of(
                "atan2", "angle",
                "dst", "len"
        );

        private static final Seq<NyfalisLogicParser.JumpIndex> jumps = new Seq<>();
        private static final ObjectIntMap<String> jumpLocations = new ObjectIntMap<>();

        Seq<LStatement> statements = new Seq<>();
        static HashMap<String,LStatement> connector = new HashMap<>();
        public static String[] allToken;
        char[] chars;
        int pos, line, tok;
        boolean privileged;

        Seq<Prov<LStatement>> allStatements;

        public static void addLoad(String ent,LStatement statement)
        {
            connector.put(ent,statement);
        }

        public NyfalisLogicParser(String text, Seq<Prov<LStatement>> allStatements){
            this.privileged = true;
            this.allStatements = allStatements;
            this.chars = text.toCharArray();
        }

        void comment(){
            //read until \n or eof
            while(pos < chars.length && chars[pos++] != '\n');
        }

        void error(String message){
            throw new RuntimeException("Invalid code. " + message);
        }

        String string(){
            int from = pos;

            while(++pos < chars.length){
                var c = chars[pos];
                if(c == '\n'){
                    error("Missing closing quote \" before end of line.");
                }else if(c == '"'){
                    break;
                }
            }

            if(pos >= chars.length || chars[pos] != '"') error("Missing closing quote \" before end of file.");

            return new String(chars, from, ++pos - from);
        }

        String token(){
            int from = pos;

            while(pos < chars.length){
                char c = chars[pos];
                if(c == '\n' || c == ' ' || c == '#' || c == '\t' || c == ';') break;
                pos ++;
            }

            return new String(chars, from, pos - from);
        }

        /** Apply changes after reading a list of tokens. */
        void checkRead(){
            if(tokens[0].equals("op")){
                //legacy name change
                tokens[1] = opNameChanges.get(tokens[1], tokens[1]);
            }
        }

        /** Reads the next statement until EOL/EOF. */
        void statement(){
            boolean expectNext = false;
            tok = 0;

            while(pos < chars.length){
                char c = chars[pos];
                if(tok >= tokens.length) error("Line too long; may only contain " + tokens.length + " tokens");

                //reached end of line, bail out.
                if(c == '\n' || c == ';') break;

                if(expectNext && c != ' ' && c != '#' && c != '\t'){
                    error("Expected space after string/token.");
                }

                expectNext = false;

                if(c == '#'){
                    comment();
                    break;
                }else if(c == '"'){
                    tokens[tok ++] = string();
                    expectNext = true;
                }else if(c != ' ' && c != '\t'){
                    tokens[tok ++] = token();
                    expectNext = true;
                }else{
                    pos ++;
                }
            }

            //only process lines with at least 1 token
            if(tok > 0){
                checkRead();

                //store jump location, always ends with colon
                if(tok == 1 && tokens[0].charAt(tokens[0].length() - 1) == ':'){
                    if(jumpLocations.size >= maxJumps){
                        error("Too many jump locations. Max jumps: " + maxJumps);
                    }
                    jumpLocations.put(tokens[0].substring(0, tokens[0].length() - 1), line);
                }else{
                    boolean wasJump;
                    String jumpLoc = null;
                    //clean up jump position before parsing
                    if(wasJump = (tokens[0].equals("jump") && tok > 1 && !Strings.canParseInt(tokens[1]))){
                        jumpLoc = tokens[1];
                        tokens[1] = "-1";
                    }

                    for(int i = 1; i < tok; i++){
                        if(tokens[i].equals("@configure")) tokens[i] = "@config";
                        if(tokens[i].equals("configure")) tokens[i] = "config";
                    }

                    LStatement st;

                    try{
                        st = LogicIO.read(tokens,tok);
                        if (st == null)
                            st = readStatement(tokens, tok);
                    }catch(Exception e){
                        //replace invalid statements
                        st = new LStatements.InvalidStatement();
                    }

                    //discard misplaced privileged instructions
                    if(!privileged && st != null && st.privileged()){
                        st = new LStatements.InvalidStatement();
                    }

                    /*Prevents pasted unit code*/
                    if(privileged && st != null){
                        if(st instanceof LStatements.UnitBindStatement) st = new LStatements.InvalidStatement();
                        if(st instanceof LStatements.UnitControlStatement) st = new LStatements.InvalidStatement();
                        if(st instanceof LStatements.UnitRadarStatement) st = new LStatements.InvalidStatement();
                        if(st instanceof LStatements.UnitLocateStatement) st = new LStatements.InvalidStatement();
                    }

                    //store jumps that use labels
                    if(st instanceof LStatements.JumpStatement jump && wasJump){
                        jumps.add(new NyfalisLogicParser.JumpIndex(jump, jumpLoc));
                    }

                    if(st != null){
                        statements.add(st);
                    }else{
                        //attempt parsing using custom parser if a match is found; this is for mods
                        if(LAssembler.customParsers.containsKey(tokens[0])){
                            statements.add(LAssembler.customParsers.get(tokens[0]).get(tokens));
                        }else{
                            //unparseable statement
                            statements.add(new LStatements.InvalidStatement());
                        }
                    }
                    line ++;
                }
            }
        }

        LStatement readStatement(String[] tokens,int length) throws InstantiationException, IllegalAccessException {
            for(String s : connector.keySet()) {
                if (s.equals(tokens[0])) {
                    LStatement temp = connector.get(s).copy();
                    allToken = tokens;
                    temp.afterRead();
                    return temp;
                }
            }
            return null;
        }

        Seq<LStatement> parse(){
            jumps.clear();
            jumpLocations.clear();

            while(pos < chars.length && line < LExecutor.maxInstructions){
                switch(chars[pos]){
                    case '\n', ';', ' ' -> pos ++; //skip newlines and spaces
                    case '\r' -> pos += 2; //skip the newline after the \r
                    default -> statement();
                }
            }

            //load destination indices
            for(var i : jumps){
                if(!jumpLocations.containsKey(i.location)){
                    error("Undefined jump location: \"" + i.location + "\". Make sure the jump label exists and is typed correctly.");
                }
                i.jump.destIndex = jumpLocations.get(i.location, -1);
            }

            return statements;
        }

        static class JumpIndex{
            LStatements.JumpStatement jump;
            String location;

            public JumpIndex(LStatements.JumpStatement jump, String location){
                this.jump = jump;
                this.location = location;
            }
        }
    }
}
