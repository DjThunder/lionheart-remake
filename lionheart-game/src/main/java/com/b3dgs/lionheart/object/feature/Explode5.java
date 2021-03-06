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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Explode5 feature implementation.
 * <ol>
 * <li>Trigger 5 explodes.</li>
 * </ol>
 */
@FeatureInterface
public final class Explode5 extends FeatureModel implements Routine, Recyclable
{
    private static final int[][] OFFSET =
    {
        {
            0, 0, 0
        },
        {
            -16, 16, 9
        },
        {
            16, 16, 18
        },
        {
            16, -16, 27
        },
        {
            -16, -16, 36
        }
    };

    private final Tick tick = new Tick();
    private final Spawner spawner = services.get(Spawner.class);

    private int phase;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Explode5(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (phase == 0)
        {
            for (int i = 0; i < OFFSET.length; i++)
            {
                final int index = i;
                tick.addAction(() -> spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Explode.xml"),
                                                   transformable.getX() + OFFSET[index][0],
                                                   transformable.getY() + OFFSET[index][1]),
                               OFFSET[index][2]);
            }
            phase++;
            tick.restart();
        }
        else if (phase == 1 && tick.elapsed(OFFSET[OFFSET.length - 1][2] + OFFSET[1][2]))
        {
            identifiable.destroy();
        }
    }

    @Override
    public void recycle()
    {
        phase = 0;
        tick.restart();
    }
}
