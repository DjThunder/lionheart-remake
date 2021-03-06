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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Ghost2 feature implementation.
 * <ol>
 * <li>Move on player.</li>
 * </ol>
 */
@FeatureInterface
public final class Ghost2 extends FeatureModel implements Routine, Recyclable
{
    private static final int TRACK_TICK = 100;
    private static final double SPEED = 1.2;

    private final Tick tick = new Tick();
    private final Transformable target = services.get(SwordShade.class).getFeature(Transformable.class);

    private final Force current = new Force();

    private boolean phase;
    private double startY;
    private double idle;
    private boolean first;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Hurtable hurtable;
    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler handler;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Ghost2(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);
    }

    /**
     * Compute the force vector depending of the target.
     */
    private void computeVector()
    {
        final double sx = transformable.getX();
        final double sy = transformable.getY();

        double dx = target.getX();
        double dy = target.getY();

        final double ray = UtilMath.getDistance(target.getX(), target.getY(), target.getX(), target.getY());
        dx += (int) ((target.getX() - target.getOldX()) * ray);
        dy += (int) ((target.getY() - target.getOldY()) * ray);

        final double dist = Math.max(Math.abs(sx - dx), Math.abs(sy - dy));

        final double vecX = (dx - sx) / dist * SPEED;
        final double vecY = (dy - sy) / dist * SPEED;

        current.setDestination(vecX, vecY);
        current.setDirection(vecX, vecY);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        current.setVelocity(1.0);
        current.setSensibility(0.5);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        current.update(extrp);

        if (first)
        {
            first = false;
            startY = transformable.getY();
        }

        if (tick.elapsed(TRACK_TICK))
        {
            if (!phase)
            {
                computeVector();
            }
            phase = !phase;
            tick.restart();
        }
        else if (phase)
        {
            if (hurtable.isHurting())
            {
                current.zero();
            }
            transformable.moveLocation(extrp, current);
            startY = transformable.getY();
        }
        else
        {
            idle = UtilMath.wrapDouble(idle + 0.15, 0, 360);
            transformable.teleportY(startY + Math.sin(idle) * 2.0);
        }
    }

    @Override
    public void recycle()
    {
        first = true;
        phase = false;
        current.zero();
        tick.restart();
    }
}
