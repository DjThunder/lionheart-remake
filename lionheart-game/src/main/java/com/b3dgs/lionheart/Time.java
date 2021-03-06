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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;

/**
 * Time referential.
 */
public class Time implements Updatable
{
    private final Tick tick = new Tick();
    private final int rate;

    /**
     * Create time.
     * 
     * @param rate The rate source.
     */
    public Time(int rate)
    {
        super();

        this.rate = rate;
        tick.start();
    }

    /**
     * Check if time is before.
     * 
     * @param time The time to check.
     * @return <code>true</code> if before, <code>false</code> else.
     */
    public boolean isBefore(long time)
    {
        return !tick.elapsedTime(rate, time);
    }

    /**
     * Check if time is after.
     * 
     * @param time The time to check.
     * @return <code>true</code> if after, <code>false</code> else.
     */
    public boolean isAfter(long time)
    {
        return tick.elapsedTime(rate, time);
    }

    /**
     * Check if time is between range.
     * 
     * @param start The start time.
     * @param end The end time.
     * @return <code>true</code> if between, <code>false</code> else.
     */
    public boolean isBetween(long start, long end)
    {
        return isAfter(start) && isBefore(end);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
    }
}
