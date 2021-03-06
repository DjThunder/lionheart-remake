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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.Sfx;

/**
 * HotFireBall feature implementation.
 * <ol>
 * <li>Fire on timer.</li>
 * </ol>
 */
@FeatureInterface
public final class HotFireBall extends FeatureModel implements Routine, Recyclable
{
    private static final int BALL_DELAY_TICK = 8;

    private final Tick tick = new Tick();
    private final Tick series = new Tick();
    private final Viewer viewer = services.get(Viewer.class);

    private HotFireBallConfig config;
    private int current;

    @FeatureGet private Launcher launcher;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public HotFireBall(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration.
     */
    public void load(HotFireBallConfig config)
    {
        this.config = config;
        launcher.setLevel(config.getLevel());
        tick.set(config.getDelay() / 2);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        rasterable.setVisibility(false);
        launcher.addListener(l -> l.getDirection().setDestination(config.getVx(), config.getVy()));
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (config != null && tick.elapsed(config.getDelay()))
        {
            series.update(extrp);
            if (series.elapsed(BALL_DELAY_TICK))
            {
                if (current == 0 && viewer.isViewable(transformable, 0, 0))
                {
                    Sfx.SCENERY_HOTFIREBALL.play();
                }
                current++;
                if (current > config.getCount())
                {
                    current = 0;
                    tick.restart();
                }

                launcher.fire();
                series.restart();
            }
        }
    }

    @Override
    public void recycle()
    {
        tick.restart();
        series.restart();
    }
}
