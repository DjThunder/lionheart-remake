/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionheart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;

/**
 * List of available sounds fx.
 * <p>
 * Sound file name is enum name in lower case.
 * </p>
 */
public enum Sfx
{
    /** Menu select. */
    MENU_SELECT,
    /** Valdyn sword attack. */
    VALDYN_SWORD,
    /** Valdyn hurt. */
    VALDYN_HURT,
    /** Valdyn die. */
    VALDYN_DIE,
    /** Item potion little taken. */
    ITEM_POTIONLITTLE,
    /** Item potion big taken. */
    ITEM_POTIONBIG,
    /** Item taken other. */
    ITEM_TAKEN,
    /** Scenery dragon tongue. */
    SCENERY_DRAGON,
    /** Scenery turning. */
    SCENERY_TURNING,
    /** Scenery turning cube. */
    SCENERY_TURNINGCUBE,
    /** Scenery rotating platform. */
    SCENERY_ROTATINGPLATFORM,
    /** Scenery spike. */
    SCENERY_SPIKE,
    /** Scenery melting platform. */
    SCENERY_MELTINGPLATFORM,
    /** Scenery hot fire ball. */
    SCENERY_HOTFIREBALL,
    /** Scenery fire ball. */
    SCENERY_FIREBALL,
    /** Scenery geyzer. */
    SCENERY_GEYZER,
    /** Scenery geyzer platform. */
    SCENERY_GEYZERPLATFORM,
    /** Monster hurt. */
    MONSTER_HURT,
    /** Monster grasshopper. */
    MONSTER_GRASSHOPER,
    /** Monster land. */
    MONSTER_LAND,
    /** Monster spider. */
    MONSTER_SPIDER,
    /** Monster canon1 fire. */
    MONSTER_CANON1,
    /** Monster canon2 fire. */
    MONSTER_CANON2,
    /** Monster canon3 fire. */
    MONSTER_CANON3,
    /** Monster goblin rise. */
    MONSTER_GOBELIN,
    /** Monster executioner hurt. */
    MONSTER_EXECUTIONER_HURT,
    /** Monster executioner attack. */
    MONSTER_EXECUTIONER_ATTACK,
    /** Monster dragon ball. */
    MONSTER_DRAGONBALL,
    /** Monster frog. */
    MONSTER_FROG,
    /** Monster wizard. */
    MONSTER_WIZARD,
    /** Effect explode 1. */
    EFFECT_EXPLODE1,
    /** Effect explode 2. */
    EFFECT_EXPLODE2,
    /** Effect explode 3. */
    EFFECT_EXPLODE3,
    /** Projectile flower. */
    PROJECTILE_FLOWER,
    /** Projectile fly. */
    PROJECTILE_FLY,
    /** Projectile canon 2 bounce. */
    PROJECTILE_BULLET2,
    /** Boss 1 hurt. */
    BOSS1_HURT,
    /** Boss 1 bowl. */
    BOSS1_BOWL,
    /** Boss 2. */
    BOSS2,
    /** Boss 3 jump. */
    BOSS3_JUMP,
    /** Boss 3 hurt. */
    BOSS3_HURT,
    /** Boss flyer. */
    BOSS_FLYER,
    /** Boss daemon fire. */
    BOSS_DAEMON_FIRE,
    /** Boss daemon land. */
    BOSS_DAEMON_LAND,
    /** Boss norka platform. */
    BOSS_NORKA_PLATFORM,
    /** Boss norka fire. */
    BOSS_NORKA_FIRE,
    /** Boss flyer. */
    BOSS_NORKA_HURT;

    private static final int TIMEOUT_SEC = 30;
    private static final int MAX_PARALLEL_CACHE = 3;
    private static final List<Sfx> AUDIO = new ArrayList<>(Arrays.asList(VALDYN_HURT,
                                                                         VALDYN_DIE,
                                                                         VALDYN_SWORD,
                                                                         ITEM_POTIONLITTLE,
                                                                         ITEM_POTIONBIG,
                                                                         ITEM_TAKEN,
                                                                         SCENERY_DRAGON,
                                                                         SCENERY_TURNING,
                                                                         SCENERY_TURNINGCUBE,
                                                                         SCENERY_ROTATINGPLATFORM,
                                                                         SCENERY_SPIKE,
                                                                         SCENERY_MELTINGPLATFORM,
                                                                         SCENERY_FIREBALL,
                                                                         SCENERY_GEYZER,
                                                                         SCENERY_GEYZERPLATFORM,
                                                                         MONSTER_HURT,
                                                                         MONSTER_LAND,
                                                                         MONSTER_GRASSHOPER,
                                                                         MONSTER_SPIDER,
                                                                         MONSTER_CANON1,
                                                                         MONSTER_CANON2,
                                                                         MONSTER_CANON3,
                                                                         MONSTER_GOBELIN,
                                                                         MONSTER_EXECUTIONER_HURT,
                                                                         MONSTER_EXECUTIONER_ATTACK,
                                                                         MONSTER_DRAGONBALL,
                                                                         MONSTER_FROG,
                                                                         MONSTER_WIZARD,
                                                                         EFFECT_EXPLODE1,
                                                                         EFFECT_EXPLODE2,
                                                                         EFFECT_EXPLODE3,
                                                                         PROJECTILE_FLOWER,
                                                                         PROJECTILE_FLY,
                                                                         PROJECTILE_BULLET2,
                                                                         BOSS2));

    private static final CountDownLatch LATCH = new CountDownLatch(1);
    private static final List<Sfx> TO_CACHE = new ArrayList<>();

    /**
     * Cache sfx start.
     * 
     * @param sfxs The sfx to cache.
     */
    public static void cacheStart(Sfx... sfxs)
    {
        if (TO_CACHE.isEmpty())
        {
            TO_CACHE.addAll(AUDIO);
            TO_CACHE.addAll(Arrays.asList(sfxs));
            final Collection<Sfx> list = new ArrayList<>(TO_CACHE);
            new Thread(() ->
            {
                if (Settings.getInstance().getVolumeSfx() > 0)
                {
                    int i = 0;
                    for (final Sfx sfx : list)
                    {
                        if (!sfx.cached)
                        {
                            sfx.audio.setVolume(0);
                            try
                            {
                                sfx.play();
                                i++;
                                if (i > MAX_PARALLEL_CACHE)
                                {
                                    i = 0;
                                    sfx.audio.await();
                                }
                            }
                            catch (@SuppressWarnings("unused") final RejectedExecutionException exception)
                            {
                                // Skip
                            }
                        }
                    }
                }
                list.clear();
                LATCH.countDown();
            }, Sfx.class.getSimpleName()).start();
        }
    }

    /**
     * Cache sfx end.
     */
    public static void cacheEnd()
    {
        if (!TO_CACHE.isEmpty())
        {
            try
            {
                LATCH.await(TIMEOUT_SEC, TimeUnit.SECONDS);

                final int volume = Settings.getInstance().getVolumeSfx();
                if (!TO_CACHE.isEmpty() && volume > 0)
                {
                    for (final Sfx sfx : TO_CACHE)
                    {
                        try
                        {
                            sfx.audio.await();
                        }
                        catch (@SuppressWarnings("unused") final RejectedExecutionException exception)
                        {
                            // Skip
                        }
                        sfx.audio.setVolume(volume);
                    }
                    TO_CACHE.clear();
                }
            }
            catch (final InterruptedException exception)
            {
                Verbose.exception(exception);
            }
        }
    }

    /**
     * Cache sfx.
     * 
     * @param sfx The sfx reference.
     */
    public static void cache(Sfx sfx)
    {
        sfx.audio.setVolume(0);
        sfx.play();
        sfx.audio.await();
        sfx.audio.setVolume(Settings.getInstance().getVolumeSfx());
    }

    /**
     * Play a random explode sound.
     */
    public static void playRandomExplode()
    {
        final int id = UtilRandom.getRandomInteger(2);
        if (id == 0)
        {
            EFFECT_EXPLODE1.play();
        }
        else if (id == 1)
        {
            EFFECT_EXPLODE2.play();
        }
        else if (id == 2)
        {
            EFFECT_EXPLODE3.play();
        }
    }

    /** Audio handler. */
    private final Audio audio;
    /** Cached flag. */
    private boolean cached;

    /**
     * Create Sfx.
     */
    Sfx()
    {
        final Media media = Medias.create(Folder.SFX, name().toLowerCase(Locale.ENGLISH) + Extension.SFX);
        audio = AudioFactory.loadAudio(media);
    }

    /**
     * Play sound.
     */
    public void play()
    {
        if (Settings.getInstance().getVolumeSfx() > 0)
        {
            audio.play();
            cached = true;
        }
    }

    /**
     * Stop sound.
     */
    public void stop()
    {
        audio.stop();
    }
}
