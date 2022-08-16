package com.muvi.tvplayer.sources;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.source.TrackGroup;

public abstract class TrackData {

    private final int groupIndex;
    private final int trackIndex;
    private final TrackGroup trackGroup;
    @Nullable private final String label;

    public TrackData(int groupIndex, int trackIndex, TrackGroup trackGroup, @Nullable String label) {
        this.groupIndex = groupIndex;
        this.trackIndex = trackIndex;
        this.trackGroup = trackGroup;
        this.label = label;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public TrackGroup getTrackGroup() {
        return trackGroup;
    }

    @Nullable
    public String getLabel() {
        return label;
    }
}
