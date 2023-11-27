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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.items.spells.InventorySpell;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Cartridge extends InventorySpell {

    {
        image = ItemSpriteSheet.CARTRIDGE;
        stackable = true;

        unique = true;
        bones = false;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item instanceof Gun;
    }

    @Override
    protected void onItemSelected(Item item) {

//        Item result = changeItem(item);
//
//        if (result == null){
//            //This shouldn't ever trigger
//            GLog.n( Messages.get(this, "nothing") );
//            curItem.collect( curUser.belongings.backpack );
//        } else {
//            if (item.isEquipped(Dungeon.hero)){
//                item.cursed = false; //to allow it to be unequipped
//                ((EquipableItem)item).doUnequip(Dungeon.hero, false);
//                ((EquipableItem)result).doEquip(Dungeon.hero);
//            } else {
//                item.detach(Dungeon.hero.belongings.backpack);
//                if (!result.collect()){
//                    Dungeon.level.drop(result, curUser.pos).sprite.drop();
//                }
//            }
//            if (result.isIdentified()){
//                Catalog.setSeen(result.getClass());
//            }
//            hero.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
//            GLog.p( Messages.get(this, "load") );
//        }

    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return quantity * 40;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe{
        {
            inputs =  new Class[]{LiquidMetal.class};
            inQuantity = new int[]{40};

            cost = 3;

            output = Cartridge.class;
            outQuantity = 1;
        }
    }}
