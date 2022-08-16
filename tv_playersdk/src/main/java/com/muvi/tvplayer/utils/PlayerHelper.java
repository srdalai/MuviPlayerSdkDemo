package com.muvi.tvplayer.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.ui.TrackNameProvider;
import com.google.android.exoplayer2.util.Assertions;

import java.util.concurrent.TimeUnit;

public class PlayerHelper {

    //Calculating thumb interval based on video length
    public static int getSeekInterval(int videoDurationSecs) {
        if (videoDurationSecs < TimeUnit.MINUTES.toSeconds(10)) {     //10 minutes
            return 10 * 1000;
        } else if (videoDurationSecs < TimeUnit.HOURS.toSeconds(1)) {     //1 hour
            return 20 * 1000;
        } else if (videoDurationSecs < TimeUnit.HOURS.toSeconds(3)) {
            return 30 * 1000;
        } else {
            return 40 * 1000;
        }
    }

    public static int getVideoResolution(Context ctx, Format format) {
        String trackName = PlayerHelper.getTrackName(ctx, format);
        Log.d("Video Track Name", trackName);/*1920 × 1080, 4.73 Mbps*/
        String resolution = trackName.split(",")[0].trim();
        //String bit_rate = trackName.split(",")[1].trim();
        String[] resolutionParts = resolution.split("×");
        String quality = "0";
        if (resolutionParts.length > 1) {
            quality = resolutionParts[1].trim();
        }
        return Integer.parseInt(quality);
    }

    public static String getVideoBitrate(Context ctx, Format format) {
        String trackName = PlayerHelper.getTrackName(ctx, format);
        Log.d("Video Track Name", trackName);/*1920 × 1080, 4.73 Mbps*/
        //String resolution = trackName.split(",")[0].trim();
        //String quality = resolution.split("×")[1].trim();
        String[] trackNameParts = trackName.split(",");
        if (trackNameParts.length > 1) {
            return trackNameParts[1].trim();
        }
        return "";
    }

    public static String getAudioTrackLabel(Context ctx, Format format) {
        String languageOrLabel;
        if (format.label != null && !format.label.equalsIgnoreCase("unknown"))  {
            languageOrLabel = format.label;
        } else {
            String trackName = PlayerHelper.getTrackName(ctx, format);
            Log.d("Audio Track Name", trackName);/*English, Stereo*/
            String[] trackParts = trackName.split(",");
            languageOrLabel = trackParts[0].trim();
            /*if (trackParts.length == 2) {
                languageOrLabel = languageOrLabel + " (" + trackParts[1].trim()+ ")";
                return languageOrLabel;
            }*/
        }

        /*if (format.channelCount == 2) {
            languageOrLabel = languageOrLabel + " (Stereo)";
        }*/
        if (format.channelCount == 6) {
            languageOrLabel = languageOrLabel + " (5.1)";
        }
        if (format.channelCount == 8) {
            languageOrLabel = languageOrLabel + " (7.1)";
        }
        return languageOrLabel;
    }

    public static String getTextTrackLabel(Context ctx, Format format) {
        if (format.label != null && !format.label.equalsIgnoreCase("unknown")) return format.label;
        String trackName = PlayerHelper.getTrackName(ctx, format);
        Log.d("Text Track Name", trackName);/*English*/
        String languageOrLabel = trackName.split(",")[0].trim();
        return languageOrLabel;
    }

    public static String getLiveTrackLabel(Context ctx, Format format) {
        String trackName = PlayerHelper.getTrackName(ctx, format);
        Log.d("Live Track Name", trackName);/*4.73 Mbps*/
        return trackName;
    }

    public static String getTrackName(Context ctx, Format format) {
        TrackNameProvider trackNameProvider = new DefaultTrackNameProvider(ctx.getResources());
        Assertions.checkNotNull(trackNameProvider);
        String trackName = trackNameProvider.getTrackName(format);
        //Log.d("Track Name", trackName);
        return trackName;
    }
}
