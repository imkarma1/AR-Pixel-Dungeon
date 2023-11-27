package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.rocketlauncher;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RL extends Gun {

    {
        max_round = 2;
        round = max_round;
        reload_time = 4f;
        explode = true;
    }

    @Override
    public int bulletMin(int lvl) {
        return 2*super.bulletMin(lvl);
    }

    @Override
    public int bulletMax(int lvl) {
        return 6 * (tier+2) +
                lvl * (tier+2) +
                RingOfSharpshooting.levelDamageBonus(hero);
    }

    @Override
    public Gun.Bullet knockBullet(){
        return new RLBullet();
    }

    public class RLBullet extends Gun.Bullet {
        {
            image = ItemSpriteSheet.ROCKET;
        }
    }

}
