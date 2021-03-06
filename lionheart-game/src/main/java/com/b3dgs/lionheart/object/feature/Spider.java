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

import java.util.Optional;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.io.DeviceControllerVoid;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateIdle;
import com.b3dgs.lionheart.object.state.StateJumpSpider;
import com.b3dgs.lionheart.object.state.StatePatrol;
import com.b3dgs.lionheart.object.state.StatePatrolCeil;

/**
 * Spider feature implementation.
 * <ol>
 * <li>Follow player until defined distance.</li>
 * <li>Fall on player proximity if ceil.</li>
 * </ol>
 */
@FeatureInterface
public final class Spider extends FeatureModel implements Routine, Recyclable
{
    private static final int TRACKED_DISTANCE = 80;
    private static final int FALL_DISTANCE = 16;
    private static final double TRACK_SPEED = 0.5;

    private final Transformable track = services.get(SwordShade.class).getFeature(Transformable.class);

    private int distance;
    private double move;
    private boolean tracked;
    private boolean enabled;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Body body;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Spider(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration reference.
     */
    public void load(Optional<SpiderConfig> config)
    {
        if (!config.isPresent() || config.get().getFollow())
        {
            track();
        }
        else
        {
            track(0);
        }
    }

    /**
     * Enable player tracking.
     */
    public void track()
    {
        track(TRACKED_DISTANCE);
    }

    /**
     * Enable player tracking with custom distance.
     * 
     * @param distance The track distance (negative for always).
     */
    public void track(int distance)
    {
        this.distance = distance;
        enabled = true;
        model.setInput(new DeviceControllerVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return move;
            }
        });
        stateHandler.changeState(StateIdle.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        body.setGravity(6.5);
        body.setGravityMax(6.5);
    }

    @Override
    public void update(double extrp)
    {
        if (distance < 0
            || UtilMath.getDistance(track, transformable) < distance
            || stateHandler.isState(StatePatrolCeil.class)
               && Math.abs(track.getX() - transformable.getX()) < FALL_DISTANCE)
        {
            if (stateHandler.isState(StatePatrolCeil.class))
            {
                stateHandler.changeState(StateFall.class);
                body.setGravity(4.5);
                body.setGravityMax(4.5);
            }
            else if (enabled && !tracked)
            {
                tracked = true;
                body.setGravity(6.5);
                body.setGravityMax(6.5);
                stateHandler.changeState(StateJumpSpider.class);

                if (distance > 0)
                {
                    Sfx.MONSTER_SPIDER.play();
                }
            }
        }

        if (tracked && stateHandler.isState(StatePatrol.class))
        {
            if (track.getX() > transformable.getX())
            {
                move = TRACK_SPEED;
                mirrorable.mirror(Mirror.NONE);
            }
            else if (track.getX() < transformable.getX())
            {
                move = -TRACK_SPEED;
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
        }
    }

    @Override
    public void recycle()
    {
        move = 0.0;
        enabled = false;
        tracked = false;
    }
}
