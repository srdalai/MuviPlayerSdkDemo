package com.muvi.tvplayer.sources;

import com.google.android.exoplayer2.source.TrackGroup;

public class TextTrackData extends TrackData {

    private final String language;

    /**
     * Empty Constructor for Off Text Track
     */
    public TextTrackData() {
        this(-1, -1, null, "Off", "Off");
    }

    public TextTrackData(int groupIndex, int trackIndex, String label, TrackGroup trackGroup) {
        this(groupIndex, trackIndex, trackGroup, label, label);
    }

    public TextTrackData(int groupIndex, int trackIndex, TrackGroup trackGroup, String label, String language) {
        super(groupIndex, trackIndex, trackGroup, label);
        this.language = language;
    }


    public String getLanguage() {
        return language;
    }
}
