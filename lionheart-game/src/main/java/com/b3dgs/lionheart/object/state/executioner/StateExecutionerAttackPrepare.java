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
package com.b3dgs.lionheart.object.state.executioner;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Executioner;
import com.b3dgs.lionheart.object.feature.SwordShade;

/**
 * Prepare attack state implementation.
 */
public final class StateExecutionerAttackPrepare extends State
{
    private static final int PREPARE_TICK = 20;

    private final Transformable player = model.getServices().get(SwordShade.class).getFeature(Transformable.class);
    private final Tick tick = new Tick();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateExecutionerAttackPrepare(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateExecutionerAttack1.class,
                      () -> tick.elapsed(PREPARE_TICK)
                            && is(AnimState.FINISHED)
                            && Math.abs(player.getX() - transformable.getX()) > Executioner.ATTACK2_DISTANCE_MAX);

        addTransition(StateExecutionerAttack2.class,
                      () -> tick.elapsed(PREPARE_TICK)
                            && is(AnimState.FINISHED)
                            && Double.compare(Math.abs(player.getX() - transformable.getX()),
                                              Executioner.ATTACK2_DISTANCE_MAX) <= 0);
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        tick.restart();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        tick.update(extrp);
    }
}
