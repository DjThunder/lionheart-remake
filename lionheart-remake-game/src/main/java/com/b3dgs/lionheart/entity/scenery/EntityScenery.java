/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.entity.scenery;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionheart.entity.EntityMover;
import com.b3dgs.lionheart.entity.player.Valdyn;

/**
 * Entity scenery base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityScenery
        extends Entity
{
    /** Border timer. */
    private final Timing borderTimer;
    /** Collide state. */
    private boolean collide;
    /** Collide old state. */
    private boolean collideOld;

    /**
     * @see Entity#Entity(SetupSurfaceRasteredGame)
     */
    protected EntityScenery(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        borderTimer = new Timing();
    }

    /**
     * Called when collision occurred.
     * 
     * @param entity The entity reference.
     */
    protected abstract void onCollide(Entity entity);

    /**
     * Called when lost collision.
     */
    protected abstract void onLostCollision();

    /**
     * Update the entity extremity position.
     * 
     * @param entity The entity reference.
     */
    private void updateExtremity(Entity entity)
    {
        if (entity instanceof Valdyn)
        {
            final Valdyn valdyn = (Valdyn) entity;
            final int width = valdyn.getWidth() / 2;
            final Rectangle collision = getCollisionBounds();
            if (valdyn.getLocationX() < collision.getMinX() - width + Valdyn.TILE_EXTREMITY_WIDTH * 2)
            {
                checkExtremity(valdyn, true);
            }
            else if (valdyn.getLocationX() > collision.getMaxX() + width - Valdyn.TILE_EXTREMITY_WIDTH * 2)
            {
                checkExtremity(valdyn, false);
            }
            else
            {
                borderTimer.stop();
            }
        }
    }

    /**
     * Check the extremity timer and apply it.
     * 
     * @param valdyn Valdyn reference.
     * @param mirror The mirror to apply.
     */
    private void checkExtremity(Valdyn valdyn, boolean mirror)
    {
        if (!borderTimer.isStarted())
        {
            borderTimer.start();
        }
        if (borderTimer.elapsed(250))
        {
            valdyn.updateExtremity(true);
        }
    }

    /*
     * Entity
     */

    @Override
    public void checkCollision(Valdyn player)
    {
        if (player.getCollisionLeg().collide(this))
        {
            hitThat(player);
        }
        if (player.getCollisionAttack().collide(this))
        {
            hitBy(player);
        }
    }

    @Override
    public void hitBy(Entity entity)
    {
        // Nothing to do
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (entity instanceof EntityMover)
        {
            final EntityMover mover = (EntityMover) entity;
            if (!mover.isJumping() && mover.getLocationY() > getLocationY())
            {
                onCollide(entity);
                mover.checkCollisionVertical(Double.valueOf(getLocationY() + getCollisionData().getOffsetY()),
                        EntityCollisionTile.GROUND);
                collide = true;
            }
        }
        updateExtremity(entity);
    }

    @Override
    public void onUpdated()
    {
        if (!collide && collideOld)
        {
            onLostCollision();
            collideOld = false;
        }
    }

    @Override
    protected void updateStates()
    {
        collideOld = collide;
        collide = false;
    }

    @Override
    protected void updateDead()
    {
        // Nothing to do
    }

    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        // Nothing to do
    }
}
