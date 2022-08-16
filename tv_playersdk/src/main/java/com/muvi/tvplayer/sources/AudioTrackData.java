package com.muvi.tvplayer.sources;

import com.google.android.exoplayer2.source.TrackGroup;

public class AudioTrackData extends TrackData {

    private final String language;
    private final int channelCount;

    public AudioTrackData(TrackGroup trackGroup, int groupIndex, int trackIndex, String label) {
        this(trackGroup, groupIndex, trackIndex, label, label);
    }

    public AudioTrackData(TrackGroup trackGroup, int groupIndex, int trackIndex, String label, String language) {
        this(trackGroup, groupIndex, trackIndex, label, language, 2);
    }

    public AudioTrackData(TrackGroup trackGroup, int groupIndex, int trackIndex, String label, String language, int channelCount) {
        super(groupIndex, trackIndex, trackGroup, label);
        this.language = language;
        this.channelCount = channelCount;
    }

    public String getLanguage() {
        return language;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public String getLabelDescriptiveLabel() {
        String languageOrLabel = super.getLabel();

        /*if (channelCount == 2) {
            languageOrLabel = languageOrLabel + " (Stereo)";
        }*/
        if (channelCount == 6) {
            languageOrLabel = languageOrLabel + " (5.1)";
        }
        if (channelCount == 8) {
            languageOrLabel = languageOrLabel + " (7.1)";
        }
        return languageOrLabel;
    }
}
