package olupis.world.blocks.calyx.ineternal;

import arc.util.io.*;
import mindustry.entities.*;
import mindustry.gen.*;
import olupis.content.*;

public class CalyxGraphUpdater implements Entityc{
    protected transient boolean added;
    public transient CalyxGraph graph;
    public transient int id = EntityGroup.nextId();
    protected transient int index__all = -1;

    @Override
    public int classId(){
        return NyfUnitTeamMapper.calxyUpdater;
    }

    @Override
    public int id(){
        return this.id;
    }

    @Override
    public String toString(){
        return "CalyxGraphUpdater#" + this.id;
    }

    public CalyxGraph graph(){
        return this.graph;
    }

    @Override
    public void id(int i){
        this.id = i;
    }

    public void setIndex__all(int index__all){
        this.index__all = index__all;
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public <T> T as() {
        return (T)this;
    }

    public boolean isAdded() {
        return this.added;
    }

    @Override
    public boolean isLocal(){
        //?????
        return false;
    }

    @Override
    public boolean isRemote(){
        return false;
    }

    @Override
    public boolean serialize(){
        return false;
    }

    public static CalyxGraphUpdater create(){
        return new CalyxGraphUpdater();
    }

    @Override
    public void add(){
        if(!this.added){}
        this.index__all = Groups.all.addIndex(this);
        this.added = true;
    }


    public void afterRead() {
    }

    public void afterReadAll() {
    }

    public void beforeWrite() {
    }

    public void read(Reads read) {
        this.afterRead();
    }

    public void write(Writes write) {

    }

    public void remove(){
        if(this.added){
            Groups.all.removeIndex(this, this.index__all);
            this.index__all = -1;
            this.added = false;
        }
    }

    @Override
    public void update(){
        this.graph.update();
    }


}
