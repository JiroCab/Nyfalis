package olupis.world.blocks.calyx.ineternal;

import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import olupis.world.blocks.calyx.GrowingVein.*;
import olupis.world.blocks.calyx.GrowingHeart.*;

public class CalyxGraph{
    private static final Queue<Calyxian> queue = new Queue<>();
    private static final Seq<Calyxian> outArray1 = new Seq<>();
    private static final Seq<Calyxian> outArray2 = new Seq<>();
    private static final IntSet closedSet = new IntSet();

    private  final Seq<GrowingHeartBuilding> hearts = new Seq<>(false, 16, Building.class);
    private  final Seq<Building> veins = new Seq<>(false, 16, Building.class);
    public final Seq<Building> all = new Seq<>(false, 16, Building.class);
    //floors? prob not

    private final @Nullable CalyxGraphUpdater entity;
    private final int graphID;
    private static int lastGraphID;
    public int species;

    public CalyxGraph(){
        entity =  CalyxGraphUpdater.create();
        entity.graph = this;
        graphID = lastGraphID++;
    }

    public CalyxGraph(boolean noEnitity){
        entity = null;
        graphID = lastGraphID++;
    }

    public int getID(){
        return graphID;
    }


    public void update(){

    }

    public void addGraph(CalyxGraph graph){
        if(graph == this) return;

        //merge into other graph instead.
        if(graph.all.size > all.size){
            graph.addGraph(this);
            return;
        }

        //other entity should be removed as the graph was merged
        if(graph.entity != null) graph.entity.remove();

        for(Building tile : graph.all){
            add(tile);
        }
        checkAdd();
    }

    public void  add(Building build){
        if(build == null ) return;
        if(build instanceof Calyxian module){
            if(module.module().graph.species >= 0 && module.module().graph.species != this.species ) return;

            if(module.module().graph != this || !module.module().init){
                //any old graph that is added here MUST be invalid, remove it
                if(module.module().graph != null && module.module().graph != this){
                    if(module.module().graph.entity != null) module.module().graph.entity.remove();
                }

                module.module().graph = this;
                module.module().init = true;
                all.add(build);

                if(build instanceof GrowingHeartBuilding h) module.module().graph.hearts.add(h);
                else if( build instanceof GrowingVeinBulding h) module.module().graph.veins.add(h);
            }
        }


    }

    public @Nullable Building getHeart(){
        if(hearts.isEmpty()) return null;
        return hearts.first();
    }

    public void reflow(Calyxian tile){
        queue.clear();
        queue.addLast(tile);
        closedSet.clear();
        while(queue.size > 0){
            Calyxian child = queue.removeFirst();
            add(child.build());
            checkAdd();
            for(Calyxian next : child.getCalyxConnections(outArray2)){
                if(next.build() != null && closedSet.add(next.build().pos())){
                    queue.addLast(next);
                }
            }
        }
    }

    /** Note that this does not actually remove the building from the graph;
     * it creates *new* graphs that contain the correct buildings. Doing this invalidates the graph. */
    public void remove(Building tile){

        if(!(tile instanceof Calyxian tileC)) return;
        //go through all the connections of this tile
        for(Calyxian other : tileC.getCalyxConnections(outArray1)){
            //a graph has already been assigned to this tile from a previous call, skip it
            if(other.module().graph != this) continue;

            //create graph for this branch
            CalyxGraph graph = new CalyxGraph();
            graph.checkAdd();
            graph.add(other.build());
            //add to queue for BFS
            queue.clear();
            queue.addLast(other);
            while(queue.size > 0){
                //get child from queue
                Calyxian child = queue.removeFirst();
                //add it to the new branch graph
                graph.add(child.build());
                //go through connections
                for(Calyxian next : child.getCalyxConnections(outArray2)){
                    //make sure it hasn't looped back, and that the new graph being assigned hasn't already been assigned
                    //also skip closed tiles
                    if(next.build() != tile && next.module().graph != graph){
                        graph.add(next.build());
                        queue.addLast(next);
                    }
                }
            }
            //update the graph once so direct consumers without any connected producer lose their power
            graph.update();
        }

        //implied empty graph here
        if(entity != null) entity.remove();
    }

    public void checkAdd(){
        if(entity != null) entity.add();
    }

    public void clear(){
        all.clear();
        hearts.clear();
        veins.clear();
        if(entity != null) entity.remove();
    }
    public int getId(){
        return graphID;
    }

    @Override
    public String toString(){
        return "CalyxGraph{" +
        "graphID=" + graphID +
        ", species=" + species +
        ", hearts=" + hearts +
        ", veins=" + veins +
        ", all=" + all +
        '}';
    }
}
