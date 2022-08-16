package com.muvi.tvplayer.interfaces;

import androidx.annotation.IntDef;

public interface PlayerEventListener {
    void onPlaybackStateChanged(int playbackState);
    void onPlayerError(int errorCode,String errorMessage);
}
