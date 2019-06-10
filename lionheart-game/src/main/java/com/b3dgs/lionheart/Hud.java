/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteDigit;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Represents the player stats display.
 * <ul>
 * <li>Health (fill, lost)</li>
 * <li>Talisment (icon, count)</li>
 * <li>Life (icon, count)</li>
 * </ul>
 */
public final class Hud implements Resource, Updatable, Renderable
{
    private static final String IMG_HEART = "health.png";
    private static final String IMG_HUD = "hud.png";
    private static final String IMG_NUMBERS = "numbers.png";

    private static final int HEALTH_MAX = 8;
    private static final int HEALTH_X = 1;
    private static final int HEALTH_Y = 1;

    private static final int TALISMENT_X = 57;
    private static final int TALISMENT_Y = 1;

    private static final int LIFE_X = 244;
    private static final int LIFE_Y = 1;

    private final Image heartSurface = Drawable.loadImage(Medias.create(Folder.SPRITES, IMG_HEART));
    private final ImageBuffer hudSurface = Graphics.getImageBuffer(Medias.create(Folder.SPRITES, IMG_HUD));
    private final ImageBuffer number = Graphics.getImageBuffer(Medias.create(Folder.SPRITES, IMG_NUMBERS));

    private final SpriteTiled talisment = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteTiled life = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteDigit numberTalisment = Drawable.loadSpriteDigit(number, 8, 16, 2);
    private final SpriteDigit numberLife = Drawable.loadSpriteDigit(number, 8, 16, 2);

    private final SpriteTiled[] hearts = new SpriteTiled[HEALTH_MAX];

    private Stats stats;

    /**
     * Set the featurable to display.
     * 
     * @param featurable The featurable reference.
     */
    public void setFeaturable(Featurable featurable)
    {
        if (featurable.hasFeature(Stats.class))
        {
            stats = featurable.getFeature(Stats.class);
        }
        else
        {
            stats = null;
        }
    }

    /**
     * Load health and set location.
     */
    private void loadHealth()
    {
        for (int i = 0; i < HEALTH_MAX; i++)
        {
            hearts[i] = Drawable.loadSpriteTiled(heartSurface.getSurface(), 8, 8);
            hearts[i].setTile(2);
            hearts[i].setLocation(HEALTH_X + i % (HEALTH_MAX / 2) * HEALTH_MAX,
                                  HEALTH_Y + Math.floor(i / (HEALTH_MAX / 2.0)) * HEALTH_MAX);
        }
    }

    /**
     * Load talisment location.
     */
    private void loadTalisment()
    {
        talisment.setTile(0);
        talisment.setLocation(TALISMENT_X, TALISMENT_Y + 2);
        numberTalisment.setLocation(TALISMENT_X + talisment.getTileWidth(), TALISMENT_Y + 1);
    }

    /**
     * Load life location.
     */
    private void loadLife()
    {
        life.setTile(6);
        life.setLocation(LIFE_X, LIFE_Y);
        numberLife.setLocation(LIFE_X + life.getTileWidth() + 2, LIFE_Y + 1);
    }

    /**
     * Update heart count with current health.
     */
    private void updateHeart()
    {
        for (int i = 0; i < HEALTH_MAX; i++)
        {
            if (i < stats.getHealth()) // Remaining hearts
            {
                hearts[i].setTile(0);
            }
            else if (i < stats.getHealthMax()) // Lost hearts
            {
                hearts[i].setTile(1);
            }
            else // Max hearts
            {
                hearts[i].setTile(2);
            }
        }
    }

    @Override
    public void load()
    {
        heartSurface.load();
        heartSurface.prepare();

        loadHealth();

        hudSurface.prepare();
        number.prepare();

        loadTalisment();
        loadLife();

    }

    @Override
    public boolean isLoaded()
    {
        return heartSurface.isLoaded() && talisment.isLoaded() && life.isLoaded();
    }

    @Override
    public void dispose()
    {
        heartSurface.dispose();
        hudSurface.dispose();
        number.dispose();
    }

    @Override
    public void update(double extrp)
    {
        if (stats != null)
        {
            updateHeart();

            numberTalisment.setValue(stats.getTalisment());
            numberLife.setValue(stats.getLife());
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (stats != null)
        {
            for (final SpriteTiled heart : hearts)
            {
                heart.render(g);
            }

            talisment.render(g);
            numberTalisment.render(g);

            life.render(g);
            numberLife.render(g);
        }
    }
}
