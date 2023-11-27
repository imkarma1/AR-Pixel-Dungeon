/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cloaking;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElectroBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.InfiniteBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.gunner.Riot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.AmmoBelt;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfReload;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.CorrosionBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.GoldenBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.NaturesBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.WindBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RocketLauncher extends MeleeWeapon {

    public static final String AC_SHOOT		= "SHOOT";
    public static final String AC_RELOAD = "RELOAD";

    public int max_round;
    public int round = 0;
    public float reload_time;
    public boolean silencer = false;
    public boolean short_barrel = false;
    public boolean long_barrel = false;
    public boolean magazine = false;
    public boolean light = false;
    public boolean heavy = false;
    public boolean flash = false;
    private static final String TXT_STATUS = "%d/%d";

    {

        defaultAction = AC_SHOOT;
        usesTargeting = true;

        image = ItemSpriteSheet.ROCKET_LAUNCHER;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 0.8f;

        tier = 5;

        gun = true;
        rocketGun = true;
        canAdvance = true;
    }

    private static final String ROUND = "round";
    private static final String MAX_ROUND = "max_round";
    private static final String RELOAD_TIME = "reload_time";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MAX_ROUND, max_round);
        bundle.put(ROUND, round);
        bundle.put(RELOAD_TIME, reload_time);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        max_round = bundle.getInt(MAX_ROUND);
        round = bundle.getInt(ROUND);
        reload_time = bundle.getFloat(RELOAD_TIME);
    }




    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped( hero )) {
            actions.add(AC_SHOOT);
            actions.add(AC_RELOAD);
        }
        return actions;
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target){
        return 0;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {

    }



    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {

            if (!isEquipped( hero )) {
                usesTargeting = false;
                GLog.w(Messages.get(this, "not_equipped"));
            } else {
                if (round <= 0) {
                    reload_time = 3f* RingOfReload.reloadMultiplier(Dungeon.hero);
                    reload();
                } else {
                    reload_time = 3f* RingOfReload.reloadMultiplier(Dungeon.hero);
                    usesTargeting = true;
                    curUser = hero;
                    curItem = this;
                    GameScene.selectCell(shooter);
                }
            }
        }
        if (action.equals(AC_RELOAD)) {
            max_round = 4;
            if (Dungeon.hero.hasTalent(Talent.LARGER_MAGAZINE)) {
            max_round += 1f * Dungeon.hero.pointsInTalent(Talent.LARGER_MAGAZINE);
        }
            if (round == max_round){
                GLog.w(Messages.get(this, "already_loaded"));
            } else {
                fullReload();
            }
        }
    }

    public void fullReload() {
        max_round = 4;
        if (Dungeon.hero.hasTalent(Talent.LARGER_MAGAZINE)) {
            max_round += 1f * Dungeon.hero.pointsInTalent(Talent.LARGER_MAGAZINE);
        }

        Buff.detach(hero, FrostBullet.class);
        Buff.detach(hero, FireBullet.class);
        Buff.detach(hero, ElectroBullet.class);

        if (hero.hasTalent(Talent.ELEMENTAL_BULLET) && round == 0) {
            int chance = Random.Int(6);
            int point = Dungeon.hero.pointsInTalent(Talent.ELEMENTAL_BULLET);
            switch (chance) {
                default:
                    break;
                case 0:
                    if (point >= 1) {
                        Buff.affect(hero, FrostBullet.class, 100f);
                    }
                    break;
                case 1:
                    if (point >= 2) {
                        Buff.affect(hero, FireBullet.class, 100f);
                    }
                    break;
                case 2:
                    if (point >= 3) {
                        Buff.affect(hero, ElectroBullet.class, 100f);
                    }
                    break;
            }
        }

        curUser.spend(reload_time*(max_round-round));
        curUser.busy();
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2, 1.1f);
        curUser.sprite.operate(curUser.pos);
        round = Math.max(max_round, round);

        GLog.i(Messages.get(this, "reloading"));

        if (Dungeon.hero.hasTalent(Talent.SAFE_RELOAD) && Dungeon.hero.buff(Talent.ReloadCooldown.class) == null) {
            Buff.affect(hero, Barrier.class).setShield(1+2*hero.pointsInTalent(Talent.SAFE_RELOAD));
            Buff.affect(hero, Talent.ReloadCooldown.class, 5f);
        }

        updateQuickslot();
    }

    public void reload() {
        max_round = 4;
        if (Dungeon.hero.hasTalent(Talent.LARGER_MAGAZINE)) {
            max_round += 1f * Dungeon.hero.pointsInTalent(Talent.LARGER_MAGAZINE);
        }

        Buff.detach(hero, FrostBullet.class);
        Buff.detach(hero, FireBullet.class);
        Buff.detach(hero, ElectroBullet.class);

        if (hero.hasTalent(Talent.ELEMENTAL_BULLET) && round == 0) {
            int chance = Random.Int(6);
            int point = Dungeon.hero.pointsInTalent(Talent.ELEMENTAL_BULLET);
            switch (chance) {
                default:
                    break;
                case 0:
                    if (point >= 1) {
                        Buff.affect(hero, FrostBullet.class, 100f);
                    }
                    break;
                case 1:
                    if (point >= 2) {
                        Buff.affect(hero, FireBullet.class, 100f);
                    }
                    break;
                case 2:
                    if (point >= 3) {
                        Buff.affect(hero, ElectroBullet.class, 100f);
                    }
                    break;
            }
        }

        curUser.spend(reload_time);
        curUser.busy();
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2, 1.1f);
        curUser.sprite.operate(curUser.pos);
        if (round < 4 + 1f * Dungeon.hero.pointsInTalent(Talent.LARGER_MAGAZINE)) {
            round ++;
        } else {
            round = Math.max(max_round, round);
        }

        GLog.i(Messages.get(this, "reloading"));

        if (Dungeon.hero.hasTalent(Talent.SAFE_RELOAD) && Dungeon.hero.buff(Talent.ReloadCooldown.class) == null) {
            Buff.affect(hero, Barrier.class).setShield(1+2*hero.pointsInTalent(Talent.SAFE_RELOAD));
            Buff.affect(hero, Talent.ReloadCooldown.class, 5f);
        }

        updateQuickslot();
    }


    public int getRound() { return this.round; }

    public void oneReload() {
        max_round = 4;
        if (Dungeon.hero.hasTalent(Talent.LARGER_MAGAZINE)) {
            max_round += 1f * Dungeon.hero.pointsInTalent(Talent.LARGER_MAGAZINE);
        }
        round ++;
        if (round > max_round) {
            round = max_round;
        }
    }

    @Override
    public String status() {
        max_round = 4;
        if (Dungeon.hero.hasTalent(Talent.LARGER_MAGAZINE)) {
            max_round += 1f * Dungeon.hero.pointsInTalent(Talent.LARGER_MAGAZINE);
        }
        return Messages.format(TXT_STATUS, round, max_round);
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq(tier, lvl);
    }

    public int min(int lvl) {
        return tier +
                lvl;
    }

    public int max(int lvl) {
        return 3 * (tier + 1) +
                lvl;
    }

    public int Bulletmin(int lvl) {
        return (tier+5) +
                lvl      +
                RingOfSharpshooting.levelDamageBonus(hero);
    }

    public int Bulletmax(int lvl) {
        return 2 * (tier+5)   +
                lvl * (tier+5) +
                RingOfSharpshooting.levelDamageBonus(hero);
    }

    @Override
    public String info() {
        String info = super.info();

        return info;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return knockBullet().targetingPos(user, dst);
    }

    private int targetPos;

    @Override
    public int damageRoll(Char owner) {
        int damage = augment.damageFactor(super.damageRoll(owner));

        if (owner instanceof Hero) {
            int exStr = ((Hero)owner).STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange( 0, exStr );
            }
        }

        return damage;
    }                           //초과 힘에 따른 추가 데미지

    @Override
    protected float baseDelay(Char owner) {
        float delay = augment.delayFactor(this.DLY);
        if (owner instanceof Hero) {
            int encumbrance = STRReq() - ((Hero)owner).STR();
            if (encumbrance > 0){
                delay *= Math.pow( 1.2, encumbrance );
            }
        }
        return delay;
    }

    public RocketLauncher.Rocket knockBullet(){
        return new RocketLauncher.Rocket();
    }
    public class Rocket extends MissileWeapon {

        {
            image = ItemSpriteSheet.ROCKET;

            hitSound = Assets.Sounds.PUFF;
            tier = 5;

            bullet = true;
            rocketGunBullet = true;
        }

        @Override
        public int buffedLvl(){
            return RocketLauncher.this.buffedLvl();
        }

        @Override
        public int damageRoll(Char owner) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            int bulletdamage = Random.NormalIntRange(Bulletmin(RocketLauncher.this.buffedLvl()),
                    Bulletmax(RocketLauncher.this.buffedLvl()));

            if (owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()) {
                bulletdamage = Math.round(bulletdamage * (1f + 0.15f * ((Hero) owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM)));
            }

            if (owner.buff(Bless.class) != null && ((Hero) owner).hasTalent(Talent.BLESSED_TALENT)) {
                bulletdamage = Math.round(bulletdamage * (1f + 0.15f * ((Hero) owner).pointsInTalent(Talent.BLESSED_TALENT)));
            }
            return bulletdamage;
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return RocketLauncher.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            if (silencer) {
                damage *= 0.75f;
            }
            if (damage >= defender.HP && hero.buff(MeleeWeapon.PrecisionShooting.class) != null && hero.buff(Charger.class).charges >= 1) {
                RocketLauncher.this.onAbilityKill(hero, defender);
            }
            SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
            WindBow bow2 = hero.belongings.getItem(WindBow.class);
            GoldenBow bow3 = hero.belongings.getItem(GoldenBow.class);
            NaturesBow bow4 = hero.belongings.getItem(NaturesBow.class);
            CorrosionBow bow5 = hero.belongings.getItem(CorrosionBow.class);
            if (RocketLauncher.this.enchantment == null
                    && Random.Int(3) < hero.pointsInTalent(Talent.SHARED_ENCHANTMENT)
                    && hero.buff(MagicImmune.class) == null
                    && bow != null
                    && bow.enchantment != null) {
                return bow.enchantment.proc(this, attacker, defender, damage);
            } else if (RocketLauncher.this.enchantment == null
                    && Random.Int(3) < hero.pointsInTalent(Talent.SHARED_ENCHANTMENT)
                    && hero.buff(MagicImmune.class) == null
                    && bow2 != null
                    && bow2.enchantment != null) {
                return bow2.enchantment.proc(this, attacker, defender, damage);
            } else if (RocketLauncher.this.enchantment == null
                    && Random.Int(3) < hero.pointsInTalent(Talent.SHARED_ENCHANTMENT)
                    && hero.buff(MagicImmune.class) == null
                    && bow3 != null
                    && bow3.enchantment != null) {
                return bow3.enchantment.proc(this, attacker, defender, damage);
            } else if (RocketLauncher.this.enchantment == null
                    && Random.Int(3) < hero.pointsInTalent(Talent.SHARED_ENCHANTMENT)
                    && hero.buff(MagicImmune.class) == null
                    && bow4 != null
                    && bow4.enchantment != null) {
                return bow4.enchantment.proc(this, attacker, defender, damage);
            } else if (RocketLauncher.this.enchantment == null
                    && Random.Int(3) < hero.pointsInTalent(Talent.SHARED_ENCHANTMENT)
                    && hero.buff(MagicImmune.class) == null
                    && bow5 != null
                    && bow5.enchantment != null) {
                return bow5.enchantment.proc(this, attacker, defender, damage);
            } else {
                return RocketLauncher.this.proc(attacker, defender, damage);
            }
        }

        @Override
        public float delayFactor(Char user) {
            if (hero.subClass == HeroSubClass.GUNSLINGER && hero.justMoved) {
                return 0;
            } else {
                if (hero.buff(Riot.riotTracker.class) != null) {
                    return RocketLauncher.this.delayFactor(user)/2f;
                } else {
                    return RocketLauncher.this.delayFactor(user);
                }
            }
        }

        @Override
        public int STRReq(int lvl) {
            return RocketLauncher.this.STRReq();
        }

        @Override
        protected void onThrow(int cell) {
            Char enemy = Actor.findChar( cell );
            ArrayList<Char> targets = new ArrayList<>();
            if (Actor.findChar(cell) != null) targets.add(Actor.findChar(cell));
            for (int i : PathFinder.NEIGHBOURS8){
                if (Actor.findChar(cell + i) != null) targets.add(Actor.findChar(cell + i));
            }
            for (Char target : targets){
                curUser.shoot(target, this);
                if (target == hero && !target.isAlive()){
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(RocketLauncher.class, "ondeath"));
                }
            }
            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
            CellEmitter.center(cell).burst(BlastParticle.FACTORY, 2);
            ArrayList<Char> affected = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS9) {
                int c = cell + n;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Dungeon.level.heroFOV[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                        CellEmitter.center(cell).burst(BlastParticle.FACTORY, 4);
                    }
                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy(c);
                        GameScene.updateMap(c);
                    }
                    Char ch = Actor.findChar(c);
                    if (ch != null) {
                        affected.add(ch);
                    }
                }
            }
            Sample.INSTANCE.play( Assets.Sounds.BLAST );
            if (hero.buff(InfiniteBullet.class) != null) {
                //round preserves
            } else if (hero.buff(Riot.riotTracker.class) != null && Random.Int(10) <= hero.pointsInTalent(Talent.ROUND_PRESERVE)-1) {
                //round preserves
            } else {
                round --;
            }
            if (hero.pointsInTalent(Talent.SILENCER) > 1){
                if (hero.pointsInTalent(Talent.SILENCER) > 2) {
                    //no aggro
                } else {
                    if (hero.buff(Cloaking.class) != null) {
                        //no aggro
                    }
                }
            } else {
                for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                    int dist = 4;
                    if (hero.hasTalent(Talent.SILENCER) && hero.buff(Cloaking.class) != null) {
                        dist *= 0.5;
                    }
                    if (mob.paralysed <= 0
                            && Dungeon.level.distance(curUser.pos, mob.pos) <= dist
                            && mob.state != mob.HUNTING
                            && !silencer) {
                        mob.beckon( curUser.pos ); }
                }
            }
            updateQuickslot();
            if (Dungeon.isChallenged(Challenges.DURABILITY)) {
                RocketLauncher.this.use();
            }
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f) );
        }

        @Override
        public void cast(final Hero user, final int dst) {
            super.cast(user, dst);
        }
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            AmmoBelt.OverHeat overHeat = hero.buff(AmmoBelt.OverHeat.class);
            if (target != null) {
                if (overHeat != null && Random.Float() < AmmoBelt.OverHeat.chance) {
                    usesTargeting = false;
                    GLog.w(Messages.get(Gun.class, "failed"));
                    curUser.spendAndNext(Actor.TICK);
                } else {
                    if (target != null) {
                        if (target == curUser.pos) {
                            reload();
                        } else {
                            knockBullet().cast(curUser, target);
                            if (hero.buff(MeleeWeapon.PrecisionShooting.class) != null &&
                                    hero.buff(MeleeWeapon.Charger.class) != null &&
                                    hero.buff(MeleeWeapon.PrecisionShooting.class).onUse &&
                                    hero.buff(MeleeWeapon.Charger.class).charges >= 1) {
                                beforeAbilityUsed(curUser, Actor.findChar(target));
                                hero.buff(MeleeWeapon.Charger.class).charges--;
                                afterAbilityUsed(curUser);
                            }
                        }
                    }
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

}