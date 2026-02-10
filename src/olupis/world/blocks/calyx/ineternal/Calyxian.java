package olupis.world.blocks.calyx.ineternal;

import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;

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
                if(other.module().graph.species == module().species) out.add(other);
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
            if(c.module() != null && (c.module().graph.species == module().graph.species || c.module().graph.getHeart() == null)){
                c.module().graph.addGraph(module().graph);
            }
        }
    }

    default void removedCalyxianModule(){
        if(module() != null){
            module().graph.removeAll(build().self());


            for(int i = 0; i < module().links.size; ++i){
                Tile other = Vars.world.tile(module().links.get(i));
                if(other != null && other.build instanceof Calyxian c && c.module() != null){
                    c.module().links.removeValue(build().pos());
                }
            }
            module().links.clear();
        }
    }

    default boolean isHeart(){
        return false;
    }

    //todo handle payload pick up & change team()
}
