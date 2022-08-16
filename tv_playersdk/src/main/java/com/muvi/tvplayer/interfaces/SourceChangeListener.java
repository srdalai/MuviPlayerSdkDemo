package com.muvi.tvplayer.interfaces;

public interface SourceChangeListener {
    void onSubtitleChanged(int position);
    void onAudioTrackChanged(int position);
    void onVideoTrackChanged(int position);
}
