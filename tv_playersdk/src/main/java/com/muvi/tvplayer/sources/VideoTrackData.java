package com.muvi.tvplayer.sources;

import com.google.android.exoplayer2.source.TrackGroup;

public class VideoTrackData extends TrackData {

    private final Integer videoQuality;
    private final int bitrateInt;
    private final String bitrate;
    private boolean isDefault;

    /**
     * Empty Constructor for Auto Video Track
     */
    public VideoTrackData() {
        this(-1, -1, null, "Auto", 0, 0, "0", false);
    }

    public VideoTrackData(int groupIndex, int trackIndex, TrackGroup trackGroup, String liveTrackLabel, int bitrateInt, String bitrate) {
        this(groupIndex, trackIndex, trackGroup, liveTrackLabel, 0, bitrateInt, bitrate, false);
    }

    public VideoTrackData(int groupIndex, int trackIndex, TrackGroup trackGroup, Integer videoQuality, int bitrateInt, String bitrate) {
        this(groupIndex, trackIndex, trackGroup, null, videoQuality, bitrateInt, bitrate, false);
    }

    public VideoTrackData(int groupIndex, int trackIndex, TrackGroup trackGroup, String label, Integer videoQuality, int bitrateInt, String bitrate) {
        this(groupIndex, trackIndex, trackGroup, label, videoQuality, bitrateInt, bitrate, false);
    }

    public VideoTrackData(int groupIndex, int trackIndex, TrackGroup trackGroup, String label, Integer videoQuality, int bitrateInt, String bitrate, boolean isDefault) {
        super(groupIndex, trackIndex, trackGroup, label);
        this.videoQuality = videoQuality;
        this.bitrateInt = bitrateInt;
        this.bitrate = bitrate;
        this.isDefault = isDefault;
    }

    public String getVideoQualityString() {
        return String.valueOf(videoQuality);
    }

    public Integer getVideoQuality() {
        return videoQuality;
    }

    public int getBitrateInt() {
        return bitrateInt;
    }

    public String getBitrate() {
        return bitrate;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getLabelWithBitrate() {
        if (super.getLabel() != null) {
            return super.getLabel();
        } else if (bitrate != null) {
            return videoQuality + "p (" + bitrate + ")";
        } else {
            return videoQuality + "p";
        }
    }

    @Override
    public String getLabel() {
        if (super.getLabel() != null) {
            return super.getLabel();
        } else {
            return videoQuality + "p";
        }
    }
}
