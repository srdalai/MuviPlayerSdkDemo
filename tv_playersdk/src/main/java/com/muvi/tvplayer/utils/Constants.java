package com.muvi.tvplayer.utils;

public class Constants {

    /** The player does not have any media to play. */
    public static final int PLAYER_STATE_IDLE = 1;

    /**
     * The player is not able to immediately play from its current position. This state typically
     * occurs when more data needs to be loaded.
     */
    public static final int PLAYER_STATE_BUFFERING = 2;

    /**
     * The player is able to immediately play from its current position. The player will be playing if
     */
    public static final int PLAYER_STATE_READY = 3;

    /** The player has finished playing the media. */
    public static final int PLAYER_STATE_ENDED = 4;

    public static final int RENDER_INDEX_VIDEO = 0;
    public static final int RENDER_INDEX_AUDIO = 1;
    public static final int RENDER_INDEX_TEXT = 2;
}
