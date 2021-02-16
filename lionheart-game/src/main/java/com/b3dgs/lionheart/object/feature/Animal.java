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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.state.StateIdleAnimal;
import com.b3dgs.lionheart.object.state.attack.StateAttackAnimal;

/**
 * Animal feature implementation.
 */
@FeatureInterface
public final class Animal extends FeatureModel implements Routine, CollidableListener
{
    private static final double SPEED_GROUND = 2.5;
    private static final double SPEED_BOAT = 0.5;

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Rasterable playerSprite = player.getFeature(Rasterable.class);
    private final StateHandler playerState = player.getFeature(StateHandler.class);
    private final Camera camera = services.get(Camera.class);
    private final CameraTracker tracker = services.get(CameraTracker.class);

    private int offsetY;
    private double cameraHeight;
    private boolean on;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Animal(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        player.getFeature(TileCollidable.class).addListener((result, category) -> off());
        player.getFeature(Collidable.class).addListener((collidable, with, by) ->
        {
            if (with.getName().startsWith(CollisionName.LEG) && by.getName().startsWith(CollisionName.GROUND))
            {
                off();
            }
        });
    }

    /**
     * Disable player on animal locking.
     */
    private void off()
    {
        if (on)
        {
            on = false;
            camera.setIntervals(Constant.CAMERA_HORIZONTAL_MARGIN, 0);
            tracker.track(player);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (on)
        {
            double speed;
            if (camera.getX() < 8544)
            {
                speed = SPEED_GROUND;
                cameraHeight = -16;
            }
            else
            {
                speed = SPEED_BOAT;
                cameraHeight += 0.75;
                if (cameraHeight > 16)
                {
                    cameraHeight = 16;
                }
            }
            camera.setShake(0, (int) Math.floor(cameraHeight));
            camera.moveLocation(extrp, speed, 0.0);
            camera.setLocation(camera.getX(), player.getY() - 64);
            player.moveLocationX(extrp, speed);

            if (playerState.isState(StateIdleAnimal.class) || playerState.isState(StateAttackAnimal.class))
            {
                final int frame = animatable.getFrame();
                if (frame == 1 || frame == 8)
                {
                    offsetY = -1;
                }
                else if (frame == 2 || frame == 9)
                {
                    offsetY = -2;
                }
                else if (frame == 3 || frame == 10)
                {
                    offsetY = -3;
                }
                else if (frame == 4)
                {
                    offsetY = -1;
                }
                else if (frame == 5 || frame == 12)
                {
                    offsetY = 1;
                }
                else if (frame == 6 || frame == 13)
                {
                    offsetY = 1;
                }
                else if (frame == 7 || frame == 14)
                {
                    offsetY = 1;
                }
                else if (frame == 10)
                {
                    offsetY = -4;
                }
                playerSprite.setFrameOffsets(0, offsetY);
            }
        }
        transformable.setLocationX(player.getX());
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (with.getName().startsWith(CollisionName.ANIMAL) && by.getName().startsWith(Anim.LEG))
        {
            collidable.getFeature(StateHandler.class).changeState(StateIdleAnimal.class);
            collidable.getFeature(Transformable.class).teleportY(transformable.getY() + with.getOffsetY());
            collidable.getFeature(Body.class).resetGravity();
            camera.setIntervals(0, 0);
            tracker.stop();
            player.getFeature(Mirrorable.class).mirror(Mirror.NONE);
            on = true;
        }
    }
}
