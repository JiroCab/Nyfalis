package olupis.world.blocks.calyx.ineternal;

import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;
import olupis.*;
import olupis.world.*;

public interface Calyxian{
    Seq<Calyxian> tempBuilds = new Seq<>();

    default @Nullable CalyxModule module(){
        return null;
    }

    default Seq<Calyxian> getCalyxConnections(Seq<Calyxian> out){
        out.clear();
        if(Vars.state.isEditor()) return out;// dont
        if(this.module() == null) return out;
        if( this.build() == null) return out;

        for(Building bul : build().proximity){
            if(!(bul instanceof Calyxian other)) continue;

            if(build().team == bul.team && !module().links.contains(bul.pos())){
                if(other.module().graph == module().graph) continue;
                if(other.module().graph.getHeart() == null){
                    out.add(other);
                    continue;
                }

                //check if same species and merge
                if(other.module().graph.species == module().graph.species){
                    out.add(other);
                }
            }
        }

        for(int i = 0; i < module().links.size; i++){
            Tile link = Vars.world.tile(this.module().links.get(i));
            if(link != null && link.build != null && link.team() == build().team && link instanceof Calyxian c){
                out.add(c);
            }
        }
        return out;
    }

    default @Nullable Building build(){
        return null;
    }

    @Nullable default Building getHeart(){
        return null;
    }


    default void updateCalyxianModule(){
        tempBuilds.clear();
        for(Calyxian c : getCalyxConnections(tempBuilds)){
            if(c.module().graph == null) continue;
            if(c.module().graph == module().graph) continue;

            if(c.module().graph.species == module().graph.species || c.module().graph.getHeart() == null){
                c.module().graph.addGraph(module().graph);
            }
        }
    }

    default void changeSpecies (int species){
        int in  = Mathf.clamp(species ,0,  NyfalisVars.calyxSpecies);
        CalyxGraph tmp =  new CalyxGraph();

        Seq<Building> prev = module().graph.all.copy();
        module().species = in;
        for(Building building : prev){
            if((!(building instanceof Calyxian bu)))return;

            if (bu.module() != null ) bu.module().species = in;
        }
        tmp.species = module().graph.species = in;

        tmp.addGraph(module().graph);
        Log.err(tmp.all.size + " vs " + module().graph.all.size);

    }

    default void removedCalyxianModule(){
        if(module() == null) return;

        module().graph.remove(build().self());
        for(int i = 0; i < module().links.size; ++i){
            Tile other = Vars.world.tile(module().links.get(i));
            if(other != null && other.build instanceof Calyxian c && c.module() != null){
                c.module().links.removeValue(build().pos());
                Log.err(build() + "");
            }
        }
        module().links.clear();
    }

    default boolean isHeart(){
        return false;
    }


    default void calyxBuildConfiguration(Table table, boolean call){
        table.table(par -> {
            par.table(t -> {
                t.background(Styles.black6);
                var group = new ButtonGroup<ImageButton>();
                group.setMinCheckCount(0);
                int i = 0;
                t.row();
                for(int j = 0; j < NyfalisVars.calyxSpecies; j++){
                    int finalJ = j;
                    ImageButton button = t.button(NyfWorldFuckingHelper.calyxSpeciesICon(j), Styles.clearNoneTogglei, 45f, () -> {
                        calyxSpeciesConfig(finalJ);
                        if(call)build().configure(finalJ);

                        if(module() != null){
                            changeSpecies(finalJ);
                        }
                        build().deselect();
                    }).group(group).color(NyfWorldFuckingHelper.calyxSpeciesColors(finalJ)).get();
                    button.update(() -> {
                        button.setChecked(finalJ == calyxSpeciesConfig());
                        button.setColor(NyfWorldFuckingHelper.calyxSpeciesColors(finalJ));
                    });

                }
            });
        });
    }

    default int calyxSpeciesConfig(){return 0;}
    default void calyxSpeciesConfig(int species){}
    //todo handle payload pick up & change team()
}
