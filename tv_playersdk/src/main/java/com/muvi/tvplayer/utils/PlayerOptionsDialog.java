package com.muvi.tvplayer.utils;

import android.util.Log;
import android.util.Pair;

import androidx.fragment.app.DialogFragment;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.util.Assertions;
import com.muvi.tvplayer.MuviMediaItem;

import java.util.ArrayList;
import java.util.List;

public class PlayerOptionsDialog extends DialogFragment {

    public static final String VIDEO_FORMAT_RESOLUTION = "video_format_resolution";
    public static final String VIDEO_FORMAT_BIT_RATE = "video_format_bit_rate";

    boolean showVideo, showAudio, showText;

    //internalVideo, internalText
    public static PlayerOptionsDialog createForEmbeddedTracks(DefaultTrackSelector trackSelector) {
        PlayerOptionsDialog dialog = new PlayerOptionsDialog();
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo =
                Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        dialog.init(mappedTrackInfo, parameters, true, true);
        return dialog;
    }

    //externalVideo, externalText
    public static PlayerOptionsDialog createForExternalTracks(DefaultTrackSelector trackSelector, List<MuviMediaItem.VideoData> videoDataList) {
        PlayerOptionsDialog dialog = new PlayerOptionsDialog();
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo =
                Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        dialog.init(mappedTrackInfo, parameters, false, false);
        return dialog;
    }

    //internalVideo, externalText
    public static PlayerOptionsDialog createForEmbeddedVideo(DefaultTrackSelector trackSelector, List<MuviMediaItem.Subtitle> subtitleList) {
        PlayerOptionsDialog dialog = new PlayerOptionsDialog();
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo =
                Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        dialog.init(mappedTrackInfo, parameters, true, false);
        return dialog;
    }

    //externalVideo, internalText
    public static PlayerOptionsDialog createForEmbeddedText(
            DefaultTrackSelector trackSelector,
            List<MuviMediaItem.VideoData> videoDataList,
            List<MuviMediaItem.Subtitle> subtitleList
    ) {
        PlayerOptionsDialog dialog = new PlayerOptionsDialog();
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo =
                Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        dialog.init(mappedTrackInfo, parameters, false, true);
        return dialog;
    }

    private void init(
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
            DefaultTrackSelector.Parameters parameters,
            boolean showVideo, boolean showText
    ) {
        getEmbeddedAudioTracks(mappedTrackInfo);
        if (showVideo)
            getEmbeddedVideoTracks(mappedTrackInfo);
        if (showText)
            getEmbeddedTextTracks(mappedTrackInfo);
    }


    private void getEmbeddedVideoTracks(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {

    }

    private void getEmbeddedAudioTracks(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {

    }

    private void getEmbeddedTextTracks(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {

    }
}
