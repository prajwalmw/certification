package com.google.developers.mojimaster2.data;

/**
 * Database naming schema, {@see app/schemas/smiley.json} for more information.
 */
public final class DataSmileyName {

    public static final String TABLE_NAME = "smiley";

    /**
     * Column name for unicode and used as primary.
     */
    public static final String COL_UNICODE = "unicode";

    /**
     * Column name for Smiley description.
     */
    public static final String COL_NAME = "name";

    /**
     * Column name for emoji character.
     */
    public static final String COL_EMOJI = "emoji";

}
