package com.muvi.tvplayer;

import android.net.ConnectivityManager;

import androidx.annotation.IntDef;

import com.muvi.tvplayer.interfaces.OnScrubListener;
import com.muvi.tvplayer.interfaces.PlayerEventListener;
import com.muvi.tvplayer.interfaces.SourceChangeListener;

public class MuviPlayerListener implements PlayerEventListener, OnScrubListener, SourceChangeListener {

    @IntDef({STATE_IDLE, STATE_BUFFERING, STATE_READY, STATE_ENDED})
    public @interface State {}
    /**
     * The player is idle, meaning it holds only limited resources. The player must be prepared before it will play the media.
     */
    public static final int STATE_IDLE = 1;
    /**
     * The player is not able to immediately play the media, but is doing work toward being able to do
     * so. This state typically occurs when the player needs to buffer more data before playback can
     * start.
     */
    public static final int STATE_BUFFERING = 2;
    /**
     * The player is able to immediately play from its current position. The player will be playing if
     * playWhenReady is true, and paused otherwise.
     */
    public static final int STATE_READY = 3;
    /** The player has finished playing the media. */
    public static final int STATE_ENDED = 4;

    @IntDef(
            open = true,
            value = {
                    ERROR_CODE_UNSPECIFIED,
                    ERROR_CODE_REMOTE_ERROR,
                    ERROR_CODE_BEHIND_LIVE_WINDOW,
                    ERROR_CODE_TIMEOUT,
                    ERROR_CODE_FAILED_RUNTIME_CHECK,
                    ERROR_CODE_IO_UNSPECIFIED,
                    ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                    ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
                    ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE,
                    ERROR_CODE_IO_BAD_HTTP_STATUS,
                    ERROR_CODE_IO_FILE_NOT_FOUND,
                    ERROR_CODE_IO_NO_PERMISSION,
                    ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED,
                    ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE,
                    ERROR_CODE_PARSING_CONTAINER_MALFORMED,
                    ERROR_CODE_PARSING_MANIFEST_MALFORMED,
                    ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED,
                    ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED,
                    ERROR_CODE_DECODER_INIT_FAILED,
                    ERROR_CODE_DECODER_QUERY_FAILED,
                    ERROR_CODE_DECODING_FAILED,
                    ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES,
                    ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
                    ERROR_CODE_AUDIO_TRACK_INIT_FAILED,
                    ERROR_CODE_AUDIO_TRACK_WRITE_FAILED,
                    ERROR_CODE_DRM_UNSPECIFIED,
                    ERROR_CODE_DRM_SCHEME_UNSUPPORTED,
                    ERROR_CODE_DRM_PROVISIONING_FAILED,
                    ERROR_CODE_DRM_CONTENT_ERROR,
                    ERROR_CODE_DRM_LICENSE_ACQUISITION_FAILED,
                    ERROR_CODE_DRM_DISALLOWED_OPERATION,
                    ERROR_CODE_DRM_SYSTEM_ERROR,
                    ERROR_CODE_DRM_DEVICE_REVOKED,
                    ERROR_CODE_DRM_LICENSE_EXPIRED
            })
    public @interface ErrorCode {}

    // Miscellaneous errors (1xxx). Same as of ExoPlayer.

    /** Caused by an error whose cause could not be identified. */
    public static final int ERROR_CODE_UNSPECIFIED = 1000;
    /**
     * Caused by an unidentified error in a remote Player, which is a Player that runs on a different
     * host or process.
     */
    public static final int ERROR_CODE_REMOTE_ERROR = 1001;
    /** Caused by the loading position falling behind the sliding window of available live content. */
    public static final int ERROR_CODE_BEHIND_LIVE_WINDOW = 1002;
    /** Caused by a generic timeout. */
    public static final int ERROR_CODE_TIMEOUT = 1003;
    /**
     * Caused by a failed runtime check.
     *
     * <p>This can happen when the application fails to comply with the player's API requirements (for
     * example, by passing invalid arguments), or when the player reaches an invalid state.
     */
    public static final int ERROR_CODE_FAILED_RUNTIME_CHECK = 1004;

    // Input/Output errors (2xxx).

    /** Caused by an Input/Output error which could not be identified. */
    public static final int ERROR_CODE_IO_UNSPECIFIED = 2000;
    /**
     * Caused by a network connection failure.
     *
     * <p>The following is a non-exhaustive list of possible reasons:
     *
     * <ul>
     *   <li>There is no network connectivity (you can check this by querying {@link
     *       ConnectivityManager#getActiveNetwork}).
     *   <li>The URL's domain is misspelled or does not exist.
     *   <li>The target host is unreachable.
     *   <li>The server unexpectedly closes the connection.
     * </ul>
     */
    public static final int ERROR_CODE_IO_NETWORK_CONNECTION_FAILED = 2001;
    /** Caused by a network timeout, meaning the server is taking too long to fulfill a request. */
    public static final int ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT = 2002;
    /**
     * Caused by a server returning a resource with an invalid "Content-Type" HTTP header value.
     *
     * <p>For example, this can happen when the player is expecting a piece of media, but the server
     * returns a paywall HTML page, with content type "text/html".
     */
    public static final int ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE = 2003;
    /** Caused by an HTTP server returning an unexpected HTTP response status code. */
    public static final int ERROR_CODE_IO_BAD_HTTP_STATUS = 2004;
    /** Caused by a non-existent file. */
    public static final int ERROR_CODE_IO_FILE_NOT_FOUND = 2005;
    /**
     * Caused by lack of permission to perform an IO operation. For example, lack of permission to
     * access internet or external storage.
     */
    public static final int ERROR_CODE_IO_NO_PERMISSION = 2006;
    /**
     * Caused by the player trying to access cleartext HTTP traffic (meaning http:// rather than
     * https://) when the app's Network Security Configuration does not permit it.
     *
     * <p>See <a href="https://exoplayer.dev/issues/cleartext-not-permitted">this corresponding
     * troubleshooting topic</a>.
     */
    public static final int ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED = 2007;
    /** Caused by reading data out of the data bound. */
    public static final int ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE = 2008;

    // Content parsing errors (3xxx).

    /** Caused by a parsing error associated with a media container format bitstream. */
    public static final int ERROR_CODE_PARSING_CONTAINER_MALFORMED = 3001;
    /**
     * Caused by a parsing error associated with a media manifest. Examples of a media manifest are a
     * DASH or a SmoothStreaming manifest, or an HLS playlist.
     */
    public static final int ERROR_CODE_PARSING_MANIFEST_MALFORMED = 3002;
    /**
     * Caused by attempting to extract a file with an unsupported media container format, or an
     * unsupported media container feature.
     */
    public static final int ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED = 3003;
    /**
     * Caused by an unsupported feature in a media manifest. Examples of a media manifest are a DASH
     * or a SmoothStreaming manifest, or an HLS playlist.
     */
    public static final int ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED = 3004;

    // Decoding errors (4xxx).

    /** Caused by a decoder initialization failure. */
    public static final int ERROR_CODE_DECODER_INIT_FAILED = 4001;
    /** Caused by a decoder query failure. */
    public static final int ERROR_CODE_DECODER_QUERY_FAILED = 4002;
    /** Caused by a failure while trying to decode media samples. */
    public static final int ERROR_CODE_DECODING_FAILED = 4003;
    /** Caused by trying to decode content whose format exceeds the capabilities of the device. */
    public static final int ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES = 4004;
    /** Caused by trying to decode content whose format is not supported. */
    public static final int ERROR_CODE_DECODING_FORMAT_UNSUPPORTED = 4005;

    // AudioTrack errors (5xxx).

    /** Caused by an AudioTrack initialization failure. */
    public static final int ERROR_CODE_AUDIO_TRACK_INIT_FAILED = 5001;
    /** Caused by an AudioTrack write operation failure. */
    public static final int ERROR_CODE_AUDIO_TRACK_WRITE_FAILED = 5002;

    // DRM errors (6xxx).

    /** Caused by an unspecified error related to DRM protection. */
    public static final int ERROR_CODE_DRM_UNSPECIFIED = 6000;
    /**
     * Caused by a chosen DRM protection scheme not being supported by the device. Examples of DRM
     * protection schemes are ClearKey and Widevine.
     */
    public static final int ERROR_CODE_DRM_SCHEME_UNSUPPORTED = 6001;
    /** Caused by a failure while provisioning the device. */
    public static final int ERROR_CODE_DRM_PROVISIONING_FAILED = 6002;
    /**
     * Caused by attempting to play incompatible DRM-protected content.
     *
     * <p>For example, this can happen when attempting to play a DRM protected stream using a scheme
     * (like Widevine) for which there is no corresponding license acquisition data (like a pssh box).
     */
    public static final int ERROR_CODE_DRM_CONTENT_ERROR = 6003;
    /** Caused by a failure while trying to obtain a license. */
    public static final int ERROR_CODE_DRM_LICENSE_ACQUISITION_FAILED = 6004;
    /** Caused by an operation being disallowed by a license policy. */
    public static final int ERROR_CODE_DRM_DISALLOWED_OPERATION = 6005;
    /** Caused by an error in the DRM system. */
    public static final int ERROR_CODE_DRM_SYSTEM_ERROR = 6006;
    /** Caused by the device having revoked DRM privileges. */
    public static final int ERROR_CODE_DRM_DEVICE_REVOKED = 6007;
    /** Caused by an expired DRM license being loaded into an open DRM session. */
    public static final int ERROR_CODE_DRM_LICENSE_EXPIRED = 6008;

    /**
     * Player implementations that want to surface custom errors can use error codes greater than this
     * value, so as to avoid collision with other error codes defined in this class.
     */
    public static final int CUSTOM_ERROR_CODE_BASE = 1000000;

    @Override
    public void onPlaybackStateChanged(@State int playbackState) {
    }

    @Override
    public void onPlayerError(@ErrorCode int errorCode, String errorMessage) {

    }

    @Override
    public void onScrubStart(long position) {

    }

    @Override
    public void onScrubMove(long position) {

    }

    @Override
    public void onScrubStop(long position, boolean canceled) {

    }

    @Override
    public void onSubtitleChanged(int position) {

    }

    @Override
    public void onAudioTrackChanged(int position) {

    }

    @Override
    public void onVideoTrackChanged(int position) {

    }
}
