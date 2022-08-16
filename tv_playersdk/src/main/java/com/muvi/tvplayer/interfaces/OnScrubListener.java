package com.muvi.tvplayer.interfaces;

/** Listener for scrubbing events. */
public interface OnScrubListener {

    /**
     * Called when the user starts moving the scrubber.
     *
     * @param position The scrub position in milliseconds.
     */
    void onScrubStart(long position);

    /**
     * Called when the user moves the scrubber.
     *
     * @param position The scrub position in milliseconds.
     */
    void onScrubMove(long position);

    /**
     * Called when the user stops moving the scrubber.
     *
     * @param position The scrub position in milliseconds.
     * @param canceled Whether scrubbing was canceled.
     */
    void onScrubStop(long position, boolean canceled);
}
