package com.muvi.tvplayer.utils;

import com.muvi.tvplayer.sources.VideoTrackData;

import java.util.Comparator;

public class ResolutionDSCComparator implements Comparator<VideoTrackData> {
    @Override
    public int compare(VideoTrackData trackData1, VideoTrackData trackData2) {
        int diff;
        diff = trackData2.getVideoQuality() - trackData1.getVideoQuality();
        if (diff == 0) {
            diff = trackData2.getBitrate().compareTo(trackData1.getBitrate());      //Not recommended
        }
        return diff;
    }
}
