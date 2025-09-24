package olupis.input;

import arc.struct.*;
import mindustry.*;
import mindustry.mod.*;
import olupis.content.*;
import olupis.input.ui.*;
import olupis.world.*;
import rhino.*;
// stolen mostly from Zhttps://github.com/SMOLKEYS/yellow-java/blob/master/src/yellow/JSLink.java
public class NyfalisClassMap{
    static boolean loaded = false;
    static ImporterTopLevel scope = null;

    //I have no idea how this is working lmao -rushie
    public static void load(String s){
        if(!loaded){
            scope = (ImporterTopLevel) Vars.mods.getScripts().scope;
        }

        Seq<NativeJavaPackage> out = new Seq<>();
        out.add(new NativeJavaPackage(s, Vars.mods.mainLoader()));
        out.add(new NativeJavaPackage(NyfalisUnits.class.getPackage().getName(), Vars.mods.mainLoader()));
        out.add(new NativeJavaPackage(NyfalisPackets.class.getPackage().getName(), Vars.mods.mainLoader()));
        out.add(new NativeJavaPackage(LimitedLauncherSelect.class.getPackage().getName(), Vars.mods.mainLoader()));
        out.add(new NativeJavaPackage(EnvUpdater.class.getPackage().getName(), Vars.mods.mainLoader()));
        for(NativeJavaPackage pkg : out){
            pkg.setParentScope(scope);
            scope.importPackage(pkg);
        }

    }

}


