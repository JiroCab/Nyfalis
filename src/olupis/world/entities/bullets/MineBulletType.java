package olupis.world.entities.bullets;

import arc.audio.Sound;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;

import static mindustry.Vars.world;

public class MineBulletType extends BulletType{
    public Block mine;
    public Sound creationSound = Sounds.mineDeploy, creationFailureSound = Sounds.boom;

    public boolean createChance, failureShoot = true, allowKillShooter = false;
    public float createChancePercent;
    public float soundsVolume = 0.75f;

    public Effect placeEffect;


    public MineBulletType(Block mine, Effect placeEffect){
        super();
        this.mine = mine;
        this.placeEffect = placeEffect;

        absorbable = hittable = createChance = false;
        speed = createChancePercent = 0;
        collides = false;
        hitEffect = despawnEffect = shootEffect = smokeEffect = Fx.none;
        layer = Layer.debris;
        displayAmmoMultiplier = false;
    }

    public MineBulletType(Block mine, Effect placeEffect,Float createChancePercent){
        super();
        this.mine = mine;
        this.placeEffect = placeEffect;
        createChance = true;
        this.createChancePercent = createChancePercent;

        absorbable = hittable = false;
        damage = 0;
        speed = 0;
        collides = false;
        hitEffect = despawnEffect = shootEffect = smokeEffect = Fx.none;
        layer = Layer.debris;
        displayAmmoMultiplier = false; 
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        Tile tile = world.tile((int)b.x/8, (int)b.y/8);
        //just in case
        if(tile == null) return;
        boolean occupied = Groups.unit.intersect(b.x, b.y, 1, 1).contains(Flyingc::isGrounded)
                || !Build.validPlace(mine, b.team, World.toTile(b.x),  World.toTile(b.y), 0, false) ;  //Dont spawn mines at the enemy core!

        if (createChance){
            float createChanceRan = Mathf.random(0.01f,0.99f);
            float set;

            if(createChancePercent > 0.99f) set = 0.99f;
            else if (createChancePercent < 0.01f) set = 0.01f;
            else set = createChancePercent;

            if(createChanceRan <= set) placeBlock(b, tile, occupied);
            else creationFailureSound.at(b.x, b.y, 1.2f, soundsVolume);

        } else placeBlock(b, tile, occupied);
        b.remove();
    }

    public void placeBlock(Bullet b, Tile tile, boolean occupied){
        if(tile.block() == Blocks.air && !tile.floor().isLiquid){
            if(!occupied){
                tile.setNet(mine, b.team, (int) b.rotation()/90);
                placeEffect.at(b.x, b.y, mine.size);
                creationSound.at(b.x, b.y, Mathf.random(0.8f, 1.2f), soundsVolume);

            } else if(failureShoot) fireWeapon(b);
        } else if (!occupied && tile.block() instanceof ShockMine) fireWeapon(b);
    }

    public void fireWeapon(Bullet b){
        if( mine instanceof ShockMine sm && sm.bullet != null){
            //don't kill the strata for crying out loud
            for(int i = 0; i < sm.shots; i++) sm.bullet.create(allowKillShooter ? b.owner : null, b.team, b.x, b.y, (360f / sm.shots) * i + Mathf.random(sm.inaccuracy));
        }
    }
}
