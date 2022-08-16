package com.muvi.tvplayer.interfaces;

public interface PlayerOptionsSelectedListener {
    void onSubtitleSelected(int pos);
    void onAudioTrackSelected(int pos);
    void onVideoTrackSelected(int pos);
    void openSettings();
    void showDetails();
    void onCancelled();
}
