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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Bounce bullet on hit ground.
 */
@FeatureInterface
public final class BulletBounceOnGround extends FeatureModel implements Routine, Recyclable, TileCollidableListener
{
    private static final String NODE = "bulletBounceOnGround";
    private static final String ATT_SFX = "sfx";
    private static final String ATT_COUNT = "count";

    private static final double BOUNCE_MAX = 3.5;
    private static final int BOUNCE_DELAY_TICK = 4;

    /**
     * Get horizontal side.
     * 
     * @param result The result reference.
     * @return The side value.
     */
    private static int getSideX(CollisionResult result)
    {
        final int sideX;
        if (result.contains(CollisionName.LEFT))
        {
            sideX = -1;
        }
        else if (result.contains(CollisionName.RIGHT))
        {
            sideX = 1;
        }
        else
        {
            sideX = 0;
        }
        return sideX;
    }

    private final Viewer viewer = services.get(Viewer.class);
    private final Tick tick = new Tick();
    private final Animation idle;
    private final Sfx sfx;
    private final int count;

    private Rasterable rasterable;
    private Force jump;
    private double bounceX;
    private int bounced;

    @FeatureGet private Hurtable hurtable;
    @FeatureGet private Body body;
    @FeatureGet private Launchable launchable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BulletBounceOnGround(Services services, Setup setup)
    {
        super(services, setup);

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
        sfx = Sfx.valueOf(setup.getString(ATT_SFX, NODE));
        count = setup.getIntegerDefault(0, ATT_COUNT, NODE);
    }

    /**
     * Load raster data.
     * 
     * @param raster The raster folder.
     */
    public void loadRaster(String raster)
    {
        if (Settings.getInstance().getRasterObject())
        {
            rasterable.setRaster(false, Medias.create(raster, Constant.RASTER_FILE_WATER), transformable.getHeight());
        }
    }

    /**
     * Load init move.
     * 
     * @param vx The horizontal direction.
     */
    public void load(double vx)
    {
        model.getMovement().setDirection(vx, 0.0);
        model.getMovement().setDestination(vx, 0.0);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if ((count == 0 || bounced < count)
            && category.getName().contains(CollisionName.LEG)
            && tick.elapsed(BOUNCE_DELAY_TICK))
        {
            if (result.containsY(CollisionName.GROUND)
                || result.containsY(CollisionName.SLOPE)
                || result.containsY(CollisionName.INCLINE)
                || result.containsY(CollisionName.BLOCK))
            {
                final double bounce = UtilMath.clamp(Math.abs(transformable.getOldY() - transformable.getY()) * 0.75,
                                                     0.0,
                                                     BOUNCE_MAX);
                if (bounce > 0.5 && viewer.isViewable(transformable, 0, 0))
                {
                    sfx.play();
                }
                body.resetGravity();
                tick.restart();
                tileCollidable.apply(result);
                transformable.teleportY(transformable.getY() + 2.0);

                final int sideX = getSideX(result);
                if (result.containsY(CollisionName.SLOPE))
                {
                    bounceX += 0.5 * sideX;
                    jump.setDestination(bounceX, 0.0);
                }
                if (result.containsY(CollisionName.INCLINE))
                {
                    bounceX += 0.75 * sideX;
                    jump.setDestination(bounceX, 0.0);
                }
                bounceX = UtilMath.clamp(bounceX, -3, 3);
                jump.setDirection(bounceX, bounce);

                bounced++;
            }
        }
        else if (category.getName().startsWith(CollisionName.KNEE))
        {
            final int side;
            if (transformable.getX() > transformable.getOldX())
            {
                side = -2;
            }
            else
            {
                side = 1;
            }
            tileCollidable.apply(result);
            transformable.teleportX(transformable.getX() + 1.0 * side);

            final Force direction = launchable.getDirection();
            final double vx = direction.getDirectionHorizontal();
            direction.setDirection(-vx, direction.getDirectionVertical());
            direction.setDestination(-vx, 0.0);
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        jump = model.getJump();
        jump.setVelocity(0.1);
        jump.setSensibility(0.5);
        jump.setDestination(0.0, 0.0);
        jump.setDirection(0.0, 0.0);

        rasterable = new RasterableModel(services, new SetupSurfaceRastered(setup.getMedia()))
        {
            @Override
            public int getRasterIndex(double y)
            {
                return (int) (transformable.getHeight()
                              - UtilMath.clamp(transformable.getY()
                                               + UtilMath.getRounded(transformable.getHeight(), 16)
                                               - 32,
                                               0,
                                               transformable.getHeight()));
            }
        };
        rasterable.prepare(provider);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        rasterable.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        if (rasterable.getRasterIndex(0) > 0)
        {
            rasterable.render(g);
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        tick.restart();
        tick.set(BOUNCE_DELAY_TICK);
        bounceX = 0.0;
        bounced = 0;
    }

}
