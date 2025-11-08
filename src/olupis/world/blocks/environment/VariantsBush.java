package olupis.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Prop;

// A Bush prop (like seaBush) with variants.
public class VariantsBush extends Prop{
    public TextureRegion[] botRegions, topRegions;
    public TextureRegion centerRegion;

    public int lobesMin = 7, lobesMax = 7;
    public float botAngle = 60f, origin = 0.1f;
    public float sclMin = 30f, sclMax = 50f, magMin = 5f, magMax = 15f, timeRange = 40f, spread = 0f;

    static Rand rand = new Rand();

    public VariantsBush(String name){
        super(name);
        variants = 0;
        customShadow = true;
    }

    @Override
    public void load(){
        super.load();

        if(variants > 0){
            botRegions = new TextureRegion[variants];
            topRegions = new TextureRegion[variants];

            for(int i = 0; i < variants; i++){
                topRegions[i] = Core.atlas.find(name + (i + 1));
                botRegions[i] = Core.atlas.find(name + "-bot" + (i + 1));
            }
        }
        centerRegion = Core.atlas.find(name + "-center");
    }

    @Override
    public void drawBase(Tile tile){
        Draw.z(layer);
        rand.setSeed(tile.pos());
        float offset = rand.random(180f);
        int lobes = rand.random(lobesMin, lobesMax);
        int variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1));
        for(int i = 0; i < lobes; i++){
            float ba =  i / (float)lobes * 360f + offset + rand.range(spread), angle = ba + Mathf.sin(Time.time + rand.random(0, timeRange), rand.random(sclMin, sclMax), rand.random(magMin, magMax));
            float w = region.width * region.scl(), h = region.height * region.scl();
            var region = Angles.angleDist(ba, 225f) <= botAngle ? botRegions[variant] : topRegions[variant];

            Draw.rect(region,
                tile.worldx() - Angles.trnsx(angle, origin) + w*0.5f, tile.worldy() - Angles.trnsy(angle, origin),
                w, h,
                origin*4f, h/2f,
                angle
            );
        }

        if(centerRegion.found()){
            Draw.rect(centerRegion, tile.worldx(), tile.worldy());
        }
    }
}
