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

import java.util.OptionalInt;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Spike configuration.
 */
public final class SpikeConfig
{
    /** Config node name. */
    public static final String NODE_SPIKE = "spike";
    /** Delay attribute name. */
    public static final String ATT_DELAY = "delay";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static SpikeConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_SPIKE));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static SpikeConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new SpikeConfig(root);
    }

    /** Delay start. */
    private final OptionalInt delay;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    private SpikeConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        delay = root.readIntegerOptional(ATT_DELAY);
    }

    /**
     * Get the start delay.
     * 
     * @return The start delay.
     */
    public OptionalInt getDelay()
    {
        return delay;
    }
}
