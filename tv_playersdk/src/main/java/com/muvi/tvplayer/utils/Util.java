package com.muvi.tvplayer.utils;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.List;

public class Util {

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static int convertSpToPx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static boolean supportsResolution(String height) {
        int heightInt = Integer.parseInt(height);
        if (heightInt <= 720) {
            return true;
        } else {
            try {
                List<MediaCodecInfo> mediaCodecInfoList = MediaCodecUtil.getDecoderInfos(MimeTypes.VIDEO_H264,true,false);
                for (MediaCodecInfo mediaCodecInfo : mediaCodecInfoList) {
                    assert mediaCodecInfo.capabilities != null;
                    android.media.MediaCodecInfo.VideoCapabilities videoCapabilities = mediaCodecInfo.capabilities.getVideoCapabilities();
                    Log.d("TAG", "Size supported -- " + heightInt + " -- " + videoCapabilities.isSizeSupported(getWidthForHeight(heightInt), heightInt));
                    return videoCapabilities.isSizeSupported(getWidthForHeight(heightInt), heightInt);
                }
            } catch (MediaCodecUtil.DecoderQueryException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static int getWidthForHeight(int height) {
        switch (height) {
            case 1080:
                return 1920;
            case 1440:
                return 2560;
            case 2160:
                return 3840;
            default:
                return 0;
        }
    }
}
