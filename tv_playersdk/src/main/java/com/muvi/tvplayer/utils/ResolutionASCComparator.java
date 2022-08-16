package com.muvi.tvplayer.utils;

import com.muvi.tvplayer.sources.VideoTrackData;

import java.util.Comparator;

public class ResolutionASCComparator implements Comparator<VideoTrackData> {
    @Override
    public int compare(VideoTrackData trackData1, VideoTrackData trackData2) {
        return trackData1.getVideoQuality() - trackData2.getVideoQuality();
    }
}
