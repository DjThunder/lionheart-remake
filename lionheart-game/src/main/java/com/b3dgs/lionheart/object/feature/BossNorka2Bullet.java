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
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Norka 2 Bullet.
 */
@FeatureInterface
public final class BossNorka2Bullet extends FeatureModel implements Recyclable, CollidableListener
{
    private boolean reverted;

    @FeatureGet private Hurtable hurtable;
    @FeatureGet private Launchable launchable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossNorka2Bullet(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (reverted
            && with.getName().startsWith(Anim.ATTACK)
            && by.getName().startsWith(Anim.BODY)
            && collidable.hasFeature(BossNorka2.class))
        {
            collidable.getFeature(Hurtable.class).hurt();
            hurtable.kill(true);
        }
        if (!reverted && with.getName().startsWith(Anim.BODY) && by.getName().startsWith(Anim.ATTACK_FALL))
        {
            final double sh = launchable.getDirection().getDirectionHorizontal();
            final double sv = launchable.getDirection().getDirectionVertical();
            launchable.getDirection().setDirection(-sh, sv);
            launchable.getDirection().setDestination(-sh, sv);
            reverted = true;
        }
    }

    @Override
    public void recycle()
    {
        reverted = false;
    }
}
