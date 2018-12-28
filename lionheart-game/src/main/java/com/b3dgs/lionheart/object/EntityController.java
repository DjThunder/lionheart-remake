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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;

/**
 * Entity controlling implementation.
 */
final class EntityController extends FeatureModel implements Refreshable
{
    private final Force movement;
    private final Force jump;

    /**
     * Create updater.
     * 
     * @param services The services reference.
     * @param model The model reference.
     */
    public EntityController(Services services, EntityModel model)
    {
        super();

        movement = model.getMovement();
        jump = model.getJump();
    }

    @Override
    public void update(double extrp)
    {
        movement.setDirection(DirectionNone.INSTANCE);
        jump.setDirection(DirectionNone.INSTANCE);
    }
}
