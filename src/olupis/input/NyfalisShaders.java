package olupis.input;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.gl.Shader;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.CacheLayer;
import olupis.*;

import static mindustry.Vars.renderer;

public class NyfalisShaders {
    public static NyfalisSurfaceShader slop, algae, floodPlane;
    public static CacheLayer slopC, algaeC, floodPlaneC;

    public static void LoadShaders(){
        if(!Vars.headless){
            slop = new NyfalisSurfaceShader("slop");
            algae = new NyfalisSurfaceShader("algae");
            floodPlane = new NyfalisSurfaceShader("floodplane"){{plane = true;}};
        }
    }

    public static void LoadCacheLayer(){
        CacheLayer.addLast(
            slopC = new CacheLayer.ShaderLayer(slop),
            algaeC = new CacheLayer.ShaderLayer(algae),
            floodPlaneC = new CacheLayer.ShaderLayer(floodPlane)
        );
    }

    public static class NyfalisSurfaceShader extends Shader{
        Texture noiseTex;
        public boolean plane = false;
        public String tex = "noise";

        public NyfalisSurfaceShader(String frag){
            super(Vars.tree.get("shaders/screenspace.vert"),Vars.tree.get("shaders/" + frag + ".frag"));
            loadNoise();
        }

        public NyfalisSurfaceShader(String vert, String frag){
            super(Vars.tree.get("shaders/" + vert + ".vert"), Vars.tree.get("shaders/" + frag + ".frag"));
            loadNoise();
        }

        public String textureName(){
            return tex;
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);
            if(plane)setUniformf("level", NyfalisMain.floodPlaneLevel);


            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }



}
