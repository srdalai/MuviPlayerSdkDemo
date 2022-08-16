package com.muvi.tvplayer;

import static android.media.AudioManager.ACTION_HDMI_AUDIO_PLUG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.DebugTextViewHelper;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.common.collect.ImmutableList;
import com.muvi.tvplayer.interfaces.PlayerOptionsSelectedListener;
import com.muvi.tvplayer.sources.AudioTrackData;
import com.muvi.tvplayer.sources.TextTrackData;
import com.muvi.tvplayer.sources.VideoTrackData;
import com.muvi.tvplayer.utils.GlideThumbnailTransformation;
import com.muvi.tvplayer.utils.PlayerHelper;
import com.muvi.tvplayer.utils.ResolutionDSCComparator;
import com.muvi.tvplayer.utils.Util;
import com.muvi.tvplayer.views.NewOptionsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MuviPlayerView extends ConstraintLayout implements PlayerOptionsSelectedListener {

    private static final String TAG = MuviPlayerView.class.getSimpleName();

    Context context;

    PlayerView playerView;
    LinearLayout options_linear;
    ImageView thumb_image_view;
    FrameLayout thumb_frame, thumb_image_frame, playbackInfoFrame;
    DefaultTimeBar defaultTimeBar;
    LinearLayout primary_player_controls, live_indicator_view;
    TextView content_title_text, content_sub_title_text, skip_intro_text, tvPlaybackInfo;
    ProgressBar progressBar;
    LinearProgressIndicator live_progress;

    MuviMediaItem muviMediaItem;
    MuviPlayerConfig playerConfig;

    ExoPlayer player;
    DefaultTrackSelector mTrackSelector;
    PlaybackStateListener playbackStateListener;

    private int currentWindow = 0;
    private long playbackPosition = 0;

    public int curSubtitleTrack = 0;
    public int curAudioTrack = 0;
    public int curVideoTrack = 0;

    TrackGroupArray videoTrackGroups;
    TrackGroup curVideoTrackGroup;
    List<VideoTrackData> videoTracks = new ArrayList<>();
    ArrayList<String> videoTrackLabels = new ArrayList<>();

    TrackGroupArray audioTrackGroups;
    TrackGroup curAudioTrackGroup;
    List<AudioTrackData> audioTracks = new ArrayList<>();
    ArrayList<String> audioTrackLabels = new ArrayList<>();

    TrackGroupArray subTrackGroups;
    TrackGroup curSubTrackGroup;
    List<TextTrackData> subTracks = new ArrayList<>();
    ArrayList<String> subTrackLabels = new ArrayList<>();

    boolean isSeeking = false;
    boolean showLoader;

    long single_skip_time = 10000;      //in Milli Secs

    private Timer timer;
    private TimerTask timerTask;

    private final Handler threadHandler = new Handler();

    Handler handler = new Handler();
    AudioManager.OnAudioFocusChangeListener afChangeListener;

    boolean isFirstTime = true, skipIntroFirst = true;

    int skip_intro_start_time = 0;
    int skip_intro_end_time = 0;

    //private MediaSessionCompat mediaSession;
    //private MediaSessionConnector mediaSessionConnector;

    int seekInterval;     //in milli seconds

    MuviPlayerListener listener;
    private ImaAdsLoader adsLoader;
    String adTagUri = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=";

    private final BroadcastReceiver hdmiPlugEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "HDMI Plug Event Changed");
            // Get extra data included in the Intent
            
        }
    };

    public MuviPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MuviPlayerView, 0, 0);

        setFocusable(true);
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.muvi_player_layout, this);

        try {

            showLoader = a.getBoolean(R.styleable.MuviPlayerView_showLoader, true);
        } finally {
            a.recycle();
        }

        // Create an AdsLoader.
        adsLoader = new ImaAdsLoader.Builder(/* context= */ getContext()).build();

        intiViews(rootView);
        getContext().registerReceiver(hdmiPlugEventReceiver, new IntentFilter(ACTION_HDMI_AUDIO_PLUG));
    }

    public void setPlayerConfig(MuviPlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
    }

    public void setMuviMediaItem(MuviMediaItem muviMediaItem) {
        //Validate
        this.muviMediaItem = muviMediaItem;
    }

    public void setPlayerListener(MuviPlayerListener listener) {
        this.listener = listener;
    }

    public void removePlayerListener() {
        this.listener = null;
    }

    public void initialize() {
        if (muviMediaItem == null) {
            throw new RuntimeException("MuviMediaItem not set. Call MuviPlayerView.setMuviMediaItem()");
        }
        playbackStateListener = new PlaybackStateListener();

        content_title_text.setText(muviMediaItem.getPrimaryTitle());
        content_sub_title_text.setText(muviMediaItem.getSecondaryTitle());

        thumb_image_frame.setVisibility(View.GONE);

        setListeners();
        setPreviewSeekbarBar();
        setSkipIntro();
        initializePlayer();
        configureSubtitleView();
        setupAudioFocusChangeListener();
    }

    private void updateUIItems() {
        int subTracksCount = muviMediaItem.isUseEmbeddedSubtitles() ? subTrackLabels.size() : muviMediaItem.getSubLanguages().size();
        int resolutionCount = muviMediaItem.isUseEmbeddedResolutions() ? videoTrackLabels.size() : muviMediaItem.getVideoResolutions().size();
        int audioTracksCount = audioTracks.size();
        if (subTracksCount > 0 || resolutionCount > 1 || audioTracksCount > 1) {
            options_linear.setVisibility(VISIBLE);
        } else {
            options_linear.setVisibility(GONE);
        }
    }

    private void intiViews(View rootView) {
        playerView = rootView.findViewById(R.id.playerView);

        options_linear = rootView.findViewById(R.id.options_linear);

        thumb_image_view = rootView.findViewById(R.id.thumb_image_view);
        thumb_frame = rootView.findViewById(R.id.thumb_frame);
        thumb_image_frame = rootView.findViewById(R.id.thumb_image_frame);

        defaultTimeBar = rootView.findViewById(R.id.exo_progress);

        primary_player_controls = rootView.findViewById(R.id.primary_player_controls);

        content_title_text = rootView.findViewById(R.id.content_title_text);
        content_sub_title_text = rootView.findViewById(R.id.content_sub_title_text);
        skip_intro_text = rootView.findViewById(R.id.skip_intro_text);
        live_indicator_view = rootView.findViewById(R.id.live_indicator_view);

        progressBar = rootView.findViewById(R.id.progress_bar);
        live_progress = rootView.findViewById(R.id.live_progress);
        playbackInfoFrame = rootView.findViewById(R.id.playbackInfoFrame);
        tvPlaybackInfo = rootView.findViewById(R.id.tvPlaybackInfo);
    }

    private void setListeners() {

        options_linear.setOnClickListener(v -> {
            showOptionsDialog();
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            mTrackSelector,
                            /* onDismissListener= */ dismissedDialog -> {}/*isShowingTrackSelectionDialog = false*/);
            //trackSelectionDialog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), /* tag= */ null);
        });

        playerView.setControllerVisibilityListener(visibility -> {
            if (visibility == VISIBLE) {
                skipIntroVisibility();
            } else {
                skip_intro_text.setVisibility(View.GONE);
                thumb_image_frame.setVisibility(View.GONE);
            }
            setSubtitleMargin();
        });
    }

    private void showOptionsDialog() {
        player.pause();
        ArrayList<String> subTracks = muviMediaItem.isUseEmbeddedSubtitles() ? subTrackLabels : muviMediaItem.getSubLanguages();
        ArrayList<String> videoTracks = muviMediaItem.isUseEmbeddedResolutions() ? videoTrackLabels : muviMediaItem.getVideoResolutions();
        FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        /*PlayerOptionsFragment playerOptionsFragment = PlayerOptionsFragment.newInstance(
                subTracks, curSubtitleTrack,
                audioTrackLabels, curAudioTrack,
                videoTracks, curVideoTrack
        );

        playerOptionsFragment.setPlayerOptionsSelectedListener(this);
        playerOptionsFragment.show(fm, "fragment_player_options");*/
        NewOptionsFragment newOptionsFragment = NewOptionsFragment.newInstance(
                subTracks, curSubtitleTrack,
                audioTrackLabels, curAudioTrack,
                videoTracks, curVideoTrack
        );

        newOptionsFragment.setPlayerOptionsSelectedListener(this);
        newOptionsFragment.show(fm, "fragment_player_options");
    }

    private void setSkipIntro() {
        if (!muviMediaItem.isSkipIntroEnabled()) return;
        skip_intro_start_time = muviMediaItem.getIntroData().getStartingIntroTime() - 1;  //Minus 1 for latency bias
        skip_intro_end_time = muviMediaItem.getIntroData().getEndingIntroTime();

        skip_intro_text.setOnClickListener((v) -> {
            skip_intro_text.setVisibility(View.GONE);
            playerView.showController();
            player.seekTo(TimeUnit.SECONDS.toMillis(skip_intro_end_time));
        });
    }

    private void setPreviewSeekbarBar() {
        if (muviMediaItem.isLiveContent()) {
            defaultTimeBar.setVisibility(GONE);
            findViewById(R.id.time_linear).setVisibility(GONE);
            return;
        }
        if (muviMediaItem.isThumbnailPreviewEnabled()) {
            seekInterval = muviMediaItem.getThumbnailData().getThumbIntervalMillis();
        } else {
            seekInterval = PlayerHelper.getSeekInterval(playerCurrentPosition());
        }

        defaultTimeBar.setKeyTimeIncrement(seekInterval);

        defaultTimeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(@NonNull TimeBar timeBar, long position) {
                if (listener != null) listener.onScrubStart(position);
                stopTimer();
                isSeeking = true;
                skipIntroFirst = true;
                if (muviMediaItem.isThumbnailPreviewEnabled()) {
                    thumb_image_frame.setVisibility(VISIBLE);
                    player.pause();
                    //Added to fix thumb frame overlapping Issue
                    //playerView.getSubtitleView().setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrubMove(@NonNull TimeBar timeBar, long position) {
                if (listener != null) listener.onScrubMove(position);
                if (muviMediaItem.isThumbnailPreviewEnabled()) {
                    int progress = (int) position;
                    Log.d(TAG, "Scrub Move to " + progress / 1000);
                    Glide.with(context)
                            .load(muviMediaItem.getThumbnailData().getThumbSprite())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .transform(
                                    new GlideThumbnailTransformation(
                                        position,
                                        muviMediaItem.getThumbnailData().getThumbRows(),
                                        muviMediaItem.getThumbnailData().getThumbColumns(),
                                        seekInterval
                                    )
                            )
                            .into(thumb_image_view);

                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) thumb_image_frame.getLayoutParams();
                    int thumbFrameWidth = thumb_frame.getWidth();
                    double playedRatio = (double) progress / playerDurationMillis();
                    int startMargin = (int) (thumbFrameWidth * playedRatio);
                    int viewHalfWidth = (int) Util.convertDpToPixel(context, 100);
                    double biasMultiplier = 1 - (playedRatio * 2);
                    int biasWidth = (int) (viewHalfWidth * biasMultiplier);
                    int finaStartMargin = startMargin - viewHalfWidth + biasWidth;
                    layoutParams.setMarginStart(finaStartMargin);
                    thumb_image_frame.setLayoutParams(layoutParams);
                    Log.d(TAG, "Bias Multiplier => " + biasMultiplier);
                    Log.d(TAG, "Bias Width => " + biasWidth);
                    Log.d(TAG, "Final Start Margin => " + finaStartMargin);
                }
            }

            @Override
            public void onScrubStop(@NonNull TimeBar timeBar, long position, boolean canceled) {
                if (listener != null) listener.onScrubStop(position, canceled);
                isSeeking = false;
                if (!muviMediaItem.isThumbnailPreviewEnabled()) {
                    player.play();
                }
                startTimer();
            }
        });

        defaultTimeBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                thumb_image_frame.setVisibility(View.GONE);
                //Added to fix thumb frame overlapping Issue
                //playerView.getSubtitleView().setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializePlayer() {
        if (player == null) {
            // Set up the factory for media sources, passing the ads loader and ad view providers.
            DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(getContext());

            MediaSource.Factory mediaSourceFactory =
                    new DefaultMediaSourceFactory(dataSourceFactory)
                            .setLocalAdInsertionComponents(unusedAdTagUri -> adsLoader, playerView);

            mTrackSelector = new DefaultTrackSelector(this.getContext());
            mTrackSelector.setParameters(mTrackSelector.buildUponParameters()
                    .setMaxVideoSize(3840, 2160)
                    .setPreferredAudioLanguages("en")
                    .setTunnelingEnabled(true)
            );

            player = new ExoPlayer.Builder(this.getContext())
                    .setMediaSourceFactory(mediaSourceFactory)
                    .setTrackSelector(mTrackSelector)
                    .build();

        }

        setPlayerListeners();

        playerView.setPlayer(player);
        adsLoader.setPlayer(player);

        if (muviMediaItem.getSubtitles().size() > 1) {
            curSubtitleTrack = 1;
        }
        player.setMediaItem(buildMediaItem());
        player.prepare();

        /*mediaSession = new MediaSessionCompat(this.getContext(), "sample");
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);*/

        if (getResources().getBoolean(R.bool.muvi_player_show_debug_info)) {
            TextView debug_text = findViewById(R.id.debug_text);
            DebugTextViewHelper debugTextViewHelper = new DebugTextViewHelper(player, debug_text);
            debugTextViewHelper.start();
            debug_text.setVisibility(VISIBLE);
        }

        if (muviMediaItem.isLiveContent()) {
            //TODO do required change
            //playerView.hideController();
            //defaultTimeBar.setFocusable(false);
            live_indicator_view.setVisibility(VISIBLE);
        }
    }

    StringBuilder infoBuilder = new StringBuilder();
    private void configurePlaybackInfoView(Tracks tracks) {
        infoBuilder = new StringBuilder();

        StringBuilder videoInfo = new StringBuilder();
        StringBuilder audioInfo = new StringBuilder();
        StringBuilder subInfo = new StringBuilder();

        for (Tracks.Group groupInfo : tracks.getGroups()) {
            // Group level information.
            @C.TrackType int trackType = groupInfo.getType();
            boolean trackInGroupIsSelected = groupInfo.isSelected();
            boolean trackInGroupIsSupported = groupInfo.isSupported();
            TrackGroup group = groupInfo.getMediaTrackGroup();
            for (int i = 0; i < group.length; i++) {
                // Individual track information.
                boolean isSupported = groupInfo.isTrackSupported(i);
                boolean isSelected = groupInfo.isTrackSelected(i);
                Format trackFormat = group.getFormat(i);
                //Log.d(TAG, "TracksInfo ->\nTrack Type - " + trackType + "; " + trackFormat + "; isSupported - " + isSupported);
                switch (trackType) {
                    case C.TRACK_TYPE_AUDIO:
                        audioInfo.append("\t").append(PlayerHelper.getAudioTrackLabel(getContext(), trackFormat));
                        break;
                    case C.TRACK_TYPE_VIDEO:
                        videoInfo.append("\t").append(trackFormat.width).append("x").append(trackFormat.height);
                        break;
                    case C.TRACK_TYPE_TEXT:
                        subInfo.append("\t").append(PlayerHelper.getTextTrackLabel(getContext(), trackFormat));
                        break;
                    default:
                }
            }
        }

        if (videoInfo.length() > 0) {
            infoBuilder.append("\n\nResolutions:");
            infoBuilder.append(videoInfo);
        }
        if (audioInfo.length() > 0) {
            infoBuilder.append("\n\nAudios:");
            infoBuilder.append(audioInfo);
        }
        if (subInfo.length() > 0) {
            infoBuilder.append("\n\nSubtitles:");
            infoBuilder.append(subInfo);
        }

        tvPlaybackInfo.setText(infoBuilder.toString());
    }

    private void setPlayerListeners() {
        player.addListener(playbackStateListener);
        if (BuildConfig.DEBUG) {
            player.addAnalyticsListener(new EventLogger());
        }
    }

    private void getEmbeddedTracks() {
        if (muviMediaItem.isUseEmbeddedResolutions())
            getEmbeddedVideoTracks();
        getEmbeddedAudioTracks();
        if (muviMediaItem.isUseEmbeddedSubtitles())
            getEmbeddedTextTracks();

        updateUIItems();
    }

    private void getEmbeddedVideoTracks() {
        //Preparing Data
        videoTracks.clear();
        videoTrackLabels.clear();
        //Checking Player Config to set the default video track
        int preferredResolution = 0;
        if (playerConfig != null && playerConfig.getPreferredResolution() != null) {
            preferredResolution = Integer.parseInt(playerConfig.getPreferredResolution());
        }

        //Getting Tracks
        List<Integer> resolutionArray = new ArrayList<>();
        int trackCount = 0;
        int groupIndex = 0;
        for (Tracks.Group groupInfo : player.getCurrentTracks().getGroups()) {
            // Group level information.
            @C.TrackType int trackType = groupInfo.getType();
            TrackGroup group = groupInfo.getMediaTrackGroup();
            for (int index = 0; index < group.length; index++) {
                // Individual track information.
                boolean isSupported = groupInfo.isTrackSupported(index, getResources().getBoolean(R.bool.muvi_player_allow_exceeds_capabilities));
                boolean isSelected = groupInfo.isTrackSelected(index);
                Format trackFormat = group.getFormat(index);
                if (trackType == C.TRACK_TYPE_VIDEO) {
                    //Log.d("Formats", "Video - " + Format.toLogString(trackFormat));
                    if (isSupported) {      //Comment this to disabled checking for track support
                        if (muviMediaItem.isLiveContent()) {
                            videoTracks.add(
                                    new VideoTrackData(groupIndex,
                                        index,
                                        group,
                                        PlayerHelper.getLiveTrackLabel(getContext(), trackFormat),
                                        trackFormat.bitrate,
                                        PlayerHelper.getVideoBitrate(getContext(), trackFormat)
                                    )
                            );
                        } else {
                            int resolutionInt = PlayerHelper.getVideoResolution(getContext(), trackFormat);
                            VideoTrackData trackData = new VideoTrackData(groupIndex,
                                    index,
                                    group,
                                    resolutionInt,
                                    trackFormat.bitrate,
                                    PlayerHelper.getVideoBitrate(getContext(), trackFormat)
                            );
                            if (!resolutionArray.contains(resolutionInt)) {
                                //Has multiple tracks of same resolution
                                if (resolutionInt == preferredResolution) {
                                    trackData.setDefault(true);
                                }
                            }
                            resolutionArray.add(resolutionInt);
                            videoTracks.add(trackData);
                        }
                    }
                }
            }
            if (trackType == C.TRACK_TYPE_VIDEO) groupIndex++;
        }
        if (videoTracks.size() > 0) {
            changeEmbeddedVideoTrack(true);
        }
    }

    //TODO Changes as described at https://developer.android.com/training/tv/playback/audio-capabilities#intercept-audio-changes
    private void getEmbeddedAudioTracks() {
        audioTracks.clear();
        audioTrackLabels.clear();

        int trackCount = 0;
        int groupIndex = 0;
        for (Tracks.Group groupInfo : player.getCurrentTracks().getGroups()) {
            // Group level information.
            @C.TrackType int trackType = groupInfo.getType();
            TrackGroup group = groupInfo.getMediaTrackGroup();
            for (int index = 0; index < group.length; index++) {
                // Individual track information.
                boolean isSupported = groupInfo.isTrackSupported(index);
                boolean isSelected = groupInfo.isTrackSelected(index);
                Format trackFormat = group.getFormat(index);
                if (trackType == C.TRACK_TYPE_AUDIO) {
                    //Log.d("Audio Track", "Format - " + Format.toLogString(trackFormat) + ", isSupported=" + isSupported);
                    if (isSupported) {  //trackFormat.id != null && trackFormat.language != null && trackFormat.sampleMimeType != null
                        String label = PlayerHelper.getAudioTrackLabel(getContext(), trackFormat);
                        audioTrackLabels.add(label/* + " (" + groupIndex + " , " + index + ")"*/);
                        audioTracks.add(new AudioTrackData(group, groupIndex, index, label, trackFormat.language));
                        if (isSelected) {
                            curAudioTrack = trackCount;
                            curVideoTrackGroup = group;
                        }
                        trackCount++;
                    }
                }
            }
            if (trackType == C.TRACK_TYPE_AUDIO) groupIndex++;
        }
        //Log.d("Audio Track", "Size - " + audioTracks.size());
    }

    private void getEmbeddedTextTracks() {
        subTracks.clear();
        subTrackLabels.clear();

        int trackCount = 0;
        int groupIndex = 0;
        for (Tracks.Group groupInfo : player.getCurrentTracks().getGroups()) {
            // Group level information.
            @C.TrackType int trackType = groupInfo.getType();
            TrackGroup group = groupInfo.getMediaTrackGroup();
            for (int index = 0; index < group.length; index++) {
                // Individual track information.
                boolean isSupported = groupInfo.isTrackSupported(index);
                boolean isSelected = groupInfo.isTrackSelected(index);
                Format trackFormat = group.getFormat(index);
                if (trackType == C.TRACK_TYPE_TEXT) {
                    //Log.d("Formats", "Text - " + Format.toLogString(trackFormat));
                    if (isSupported) {  //trackFormat.id != null && trackFormat.language != null
                        String label = PlayerHelper.getTextTrackLabel(getContext(), trackFormat);
                        subTrackLabels.add(label);
                        subTracks.add(new TextTrackData(groupIndex, index, label, group));
                        trackCount++;
                        if (isSelected) {
                            curSubtitleTrack = trackCount;
                            curSubTrackGroup = group;
                        }
                    }
                }
            }
            if (trackType == C.TRACK_TYPE_TEXT) groupIndex++;
        }
        if (subTracks.size() > 0) {
            //Adding Off to sub
            subTrackLabels.add(0, "Off");
            subTracks.add(0, new TextTrackData());
        }
    }

    private void skipIntroVisibility() {
        if (!muviMediaItem.isSkipIntroEnabled()) return;
        int currentPosition = playerCurrentPosition();
        if (currentPosition > skip_intro_start_time && currentPosition < skip_intro_end_time) {
            skip_intro_text.setVisibility(VISIBLE);
        } else {
            skip_intro_text.setVisibility(View.GONE);
        }
    }

    private void configureSubtitleView() {
        int defaultSubtitleColor = Color.parseColor("#ffffff");
        int outlineColor = Color.parseColor("#000000");
        Typeface subtitleTypeface = ResourcesCompat.getFont(getContext(), R.font.regular_font);
        CaptionStyleCompat style =
                new CaptionStyleCompat(defaultSubtitleColor,
                        Color.TRANSPARENT, Color.TRANSPARENT,
                        CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                        outlineColor, subtitleTypeface);
        playerView.getSubtitleView().setStyle(style);
        playerView.getSubtitleView().setViewType(SubtitleView.VIEW_TYPE_CANVAS);
        //playerView.getSubtitleView().setFractionalTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * 1f);
        playerView.getSubtitleView().setBottomPaddingFraction(SubtitleView.DEFAULT_BOTTOM_PADDING_FRACTION * 20f);

        playerView.getSubtitleView().setApplyEmbeddedStyles(true);
        playerView.getSubtitleView().setApplyEmbeddedFontSizes(true);
        playerView.getSubtitleView().setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        setSubtitleMargin();
    }

    private void setSubtitleMargin() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) playerView.getSubtitleView().getLayoutParams();
        lp.bottomMargin = Util.convertDpToPixel(getContext(), playerView.isControllerVisible() ? 120 : 40);
        playerView.getSubtitleView().setLayoutParams(lp);
    }

    private boolean playerControlsFocused() {
        return options_linear.isFocused() ||
                skip_intro_text.isFocused();
    }

    private void pausePlayerOnHomePressed() {
        if (player != null) {
            player.pause();
        }
        stopTimer();
    }

    private void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    private void stopTimer() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                threadHandler.post(() -> {
                    if (player != null) {
                        int currentPosition = playerCurrentPosition();

                        if (!isSeeking && currentPosition > 0) {

                            //Compare Current Position with Skip Intro timing to show Skip Intro Button
                            if (muviMediaItem.isSkipIntroEnabled() && currentPosition > skip_intro_start_time && currentPosition < skip_intro_end_time) {
                                if (skipIntroFirst) {
                                    skip_intro_text.setVisibility(VISIBLE);
                                    skip_intro_text.requestFocus();
                                    skipIntroFirst = false;
                                }
                            } else {
                                skip_intro_text.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onSubtitleSelected(int pos) {
        if (listener != null) listener.onSubtitleChanged(pos);
        //Log.d(TAG, "New Subtitle Position => " + pos);
        curSubtitleTrack = pos;
        if (muviMediaItem.isUseEmbeddedSubtitles()) {
            changeEmbeddedTextTrack();
            play();
        } else {
            updateMediaItem();
        }
    }

    @Override
    public void onAudioTrackSelected(int pos) {
        if (listener != null) listener.onAudioTrackChanged(pos);
        //Log.d(TAG, "New Audio Position => " + pos);
        curAudioTrack = pos;
        changeAudioTrack();
        play();
    }

    @Override
    public void onVideoTrackSelected(int pos) {
        if (listener != null) listener.onVideoTrackChanged(pos);
        //Log.d(TAG, "New Subtitle Position => " + pos);
        curVideoTrack = pos;
        if (muviMediaItem.isUseEmbeddedResolutions()) {
            changeEmbeddedVideoTrack(false);
        } else {
            updateMediaItem();

            String resolutionStr = muviMediaItem.getVideoData().get(curVideoTrack).getResolution(); //TODO Check to remove p from 1080p
            playerConfig.setPreferredResolution(resolutionStr);
        }
    }

    @Override
    public void openSettings() {
    }

    @Override
    public void showDetails() {
        playbackInfoFrame.setVisibility(VISIBLE);
    }

    @Override
    public void onCancelled() {
        //Log.d(TAG, "Dialog Cancelled");
        player.play();
        defaultTimeBar.requestFocus();
    }

    private void changeEmbeddedVideoTrack(boolean modify) {
        if (modify) {
            Collections.sort(videoTracks, new ResolutionDSCComparator());
            videoTracks.add(0, new VideoTrackData());
        }
        videoTrackLabels.clear();
        for (int i = 0; i < videoTracks.size(); i++) {
            VideoTrackData videoTrackData = videoTracks.get(i);
            videoTrackLabels.add(videoTrackData.getLabel());
            if (videoTrackData.isDefault()) {
                curVideoTrack = i;
            }
        }

        //Setting Preferred Video Resolution
        if (playerConfig != null)
            playerConfig.setPreferredResolution(curVideoTrack == 0 ? null : videoTracks.get(curVideoTrack).getVideoQualityString());

        DefaultTrackSelector.Parameters.Builder parametersBuilder = mTrackSelector.buildUponParameters();
        if (curVideoTrack == 0) {       //For Auto
            parametersBuilder.clearOverride(curVideoTrackGroup);

        } else {
            VideoTrackData trackData = videoTracks.get(curVideoTrack);
            curVideoTrackGroup = trackData.getTrackGroup();

            TrackSelectionOverride overrides = new TrackSelectionOverride(curVideoTrackGroup, ImmutableList.of(trackData.getTrackIndex()));
            parametersBuilder.setOverrideForType(overrides);
        }
        mTrackSelector.setParameters(parametersBuilder);
        play();
    }

    private void changeAudioTrack() {
        DefaultTrackSelector.Parameters.Builder parametersBuilder = mTrackSelector.buildUponParameters();
        AudioTrackData trackData = audioTracks.get(curAudioTrack);
        curAudioTrackGroup = trackData.getTrackGroup();

        TrackSelectionOverride overrides = new TrackSelectionOverride(curAudioTrackGroup, ImmutableList.of(trackData.getTrackIndex()));
        parametersBuilder.setOverrideForType(overrides);
        mTrackSelector.setParameters(parametersBuilder);
    }

    private void changeEmbeddedTextTrack() {
        DefaultTrackSelector.Parameters.Builder parametersBuilder = mTrackSelector.buildUponParameters();
        if (curSubtitleTrack == 0) {
            TrackSelectionOverride overrides = new TrackSelectionOverride(curSubTrackGroup, ImmutableList.of());
            parametersBuilder.addOverride(overrides);

        } else {
            TextTrackData trackData = subTracks.get(curSubtitleTrack);
            curSubTrackGroup = trackData.getTrackGroup();

            TrackSelectionOverride overrides = new TrackSelectionOverride(curSubTrackGroup, ImmutableList.of(trackData.getTrackIndex()));
            parametersBuilder.setOverrideForType(overrides);
        }
        mTrackSelector.setParameters(parametersBuilder);
        configureSubtitleView();
    }

    private void updateMediaItem() {
        if (player != null) {
            long curPos = player.getCurrentPosition();
            player.setMediaItem(buildMediaItem());
            player.prepare();
            player.seekTo(curPos - 1000);
            player.play();
            configureSubtitleView();
        }
    }

    //Public Methods for external use; always check for null
    public void play() {
        if (player != null) {
            player.play();
        }
    }

    public void play(int resumePos) {
        if (player != null) {
            player.seekTo(TimeUnit.SECONDS.toMillis(resumePos));
            play();
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    private void rewind() {
        if (player != null) {
            long curPos = player.getCurrentPosition();
            long newPos = curPos - single_skip_time;
            if (newPos > 0) {
                player.seekTo(newPos);
            }
        }
    }

    private void fastForward() {
        if (player != null) {
            long curPos = player.getCurrentPosition();
            long newPos = curPos + single_skip_time;
            if (newPos < player.getDuration()) {
                player.seekTo(newPos);
            }
        }
    }

    public int playerCurrentPosition() {
        if (player != null)
            return ((int) player.getCurrentPosition() / 1000);
        else
            return 0;
    }

    public int playerCurrentPositionMillis() {
        if (player != null)
            return ((int) player.getCurrentPosition());
        else
            return 0;
    }

    public int playerDuration() {
        if (player != null) {
            return ((int) player.getDuration() / 1000);
        } else {
            return 0;
        }
    }

    public int playerDurationMillis() {
        if (player != null) {
            return ((int) player.getDuration());
        } else {
            return 0;
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentMediaItemIndex();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
        }
    }

    public boolean isPlaying() {
        if (player != null) {
            return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
        }
        return false;
    }

    public boolean isSkipIntroEnabled() {
        if (muviMediaItem == null) return false;
        return muviMediaItem.isSkipIntroEnabled();
    }

    public void requestSkipIntroFocus() {
        skip_intro_text.setVisibility(VISIBLE);
        skip_intro_text.requestFocus();
    }

    public void setSkipIntroVisibility(int visibility) {
        skip_intro_text.setVisibility(visibility);
    }

    private MediaItem buildMediaItem() {

        MediaItem.Builder builder = new MediaItem.Builder();
        //TODO change to preserve selected embed resolution in the event where external subtitle is changed, which resets the media item
        if (muviMediaItem.isUseEmbeddedResolutions()) {
            builder.setUri(muviMediaItem.getVideoData().get(0).getVideoUrl());
        } else {
            builder.setUri(muviMediaItem.getVideoData().get(curVideoTrack).getVideoUrl());
        }

        if (muviMediaItem.getLicenseUrl() != null && !muviMediaItem.getLicenseUrl().isEmpty()) {
            builder.setDrmConfiguration(new MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(muviMediaItem.getLicenseUrl())
                    .setMultiSession(true)
                    .build());
        }

        if (!muviMediaItem.isUseEmbeddedSubtitles() && muviMediaItem.getSubtitles().size() > 1) {
            if (curSubtitleTrack == 0) {    //Off
                builder.setSubtitleConfigurations(ImmutableList.of());
            } else {
                try {
                    MuviMediaItem.Subtitle subtitle = muviMediaItem.getSubtitles().get(curSubtitleTrack);
                    MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitle.getUrl()))
                            .setMimeType(MimeTypes.TEXT_VTT)
                            .setLanguage(subtitle.getLanguageCode())
                            .setLabel(subtitle.getLanguage())
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build();

                    builder.setSubtitleConfigurations(ImmutableList.of(subtitleConfig));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //builder.setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(Uri.parse(adTagUri)).build());
        return builder.build();
    }

    private void showProgressBar() {
        if (progressBar != null && showLoader) {
            progressBar.setVisibility(VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null && showLoader) {
            progressBar.setVisibility(GONE);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //Log.d(TAG, "Key Code => " + event.getKeyCode());
        //Log.d(TAG, "Key Action => " + event.getAction());
        /*if (muviMediaItem.isLiveContent()) {
            if (liveAcceptableEvents(event)) {
                return super.dispatchKeyEvent(event);
            } else {
                return true;
            }
        } else */if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (playerView.isControllerVisible()) {
                playerView.hideController();
                return true;
            } else if (playbackInfoFrame.getVisibility() == View.VISIBLE) {
                playbackInfoFrame.setVisibility(View.GONE);
                return true;
            } else {
                return super.dispatchKeyEvent(event);
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (defaultTimeBar.hasFocus()) {
                    player.setPlayWhenReady(!player.isPlaying());
                    thumb_image_frame.setVisibility(View.GONE);
                    return true;
                }
                if (skip_intro_text.isFocused()) {
                    return super.dispatchKeyEvent(event);
                } else if (!playerView.isControllerVisible()) {
                    playerView.showController();
                    findViewById(R.id.exo_progress).requestFocus();
                    return false;
                } else if (defaultTimeBar.isFocused()) {
                    player.setPlayWhenReady(!player.isPlaying());
                    return false;
                } else {
                    return super.dispatchKeyEvent(event);
                }
            } else {
                return super.dispatchKeyEvent(event);
            }

        } else if (dPadClicked(event)/* && !muviMediaItem.isLiveContent()*/) {
            playerView.showController();
            return super.dispatchKeyEvent(event);
        } else if (isMediaButtonEvents(event) && !muviMediaItem.isLiveContent()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                handleMediaButtonEvents(event);
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private boolean dPadClicked(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    private boolean liveAcceptableEvents(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_BACK ||
                event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_STOP;
    }

    private boolean endAutoPlayAcceptableEvents(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_BACK ||
                event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER ||
                event.getKeyCode() == KeyEvent.KEYCODE_ENTER ||
                event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT ||
                event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    private boolean isMediaButtonEvents(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_STOP ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_REWIND ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS ||
                event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT;
    }

    private void handleMediaButtonEvents(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (player != null && !player.isPlaying()) {
                    player.play();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (player != null && player.isPlaying()) {
                    player.pause();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (player != null) {
                    player.setPlayWhenReady(!player.getPlayWhenReady());
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                if (player != null) {
                    player.stop();
                    playerView.hideController();
                    releasePlayer();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                rewind();
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                fastForward();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                /*if (isMediaQueuePrepared && hasNextMedia && nextMediaData != null) {
                    playQueuedMedia(nextMediaData, nextMediaPos);
                }*/
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                /*if (isMediaQueuePrepared && hasPrevMedia && prevMediaData != null) {
                    playQueuedMedia(prevMediaData, prevMediaPos);
                }*/
                break;
        }
    }

    private void setupAudioFocusChangeListener() {
        afChangeListener = focusChange -> {
            Log.d(TAG, "Audio Focus Status => " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Permanent loss of audio focus
                // Pause playback immediately
                if (player != null) player.pause();
                // Wait 30 seconds before stopping playback
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        releasePlayer();
                    }
                }, TimeUnit.SECONDS.toMillis(30));
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume, keep playing
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
            }
        };
    }

    public void requestAudioFocus() {
        AudioManager audioManager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start playback
            if (player != null) player.play();
        }
    }

    public void abandonAudioFocus() {
        AudioManager audioManager = (AudioManager) this.getContext().getSystemService(Context.AUDIO_SERVICE);
        // Abandon audio focus when playback complete
        audioManager.abandonAudioFocus(afChangeListener);
    }

    public void onResume() {
        Log.d(TAG, "onResume Called on Player");
        /*if (mediaSession != null) {
            mediaSession.setActive(true);
        }*/
        requestAudioFocus();
    }

    public void onPause() {
        Log.d(TAG, "onPause Called on Player");
        pausePlayerOnHomePressed();
        /*if (mediaSession != null) {
            mediaSession.setActive(false);
        }*/
        abandonAudioFocus();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy Called on Player");
        removePlayerListener();
        releasePlayer();
        getContext().unregisterReceiver(hdmiPlugEventReceiver);
    }

    private class PlaybackStateListener implements Player.Listener {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (listener != null) listener.onPlaybackStateChanged(playbackState);
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    showProgressBar();
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    hideProgressBar();
                    /*if (player.getPlayWhenReady()) {
                        thumb_image_view.setVisibility(View.GONE);
                    }*/
                    if (isFirstTime) {
                        isFirstTime = false;
                        startTimer();
                        getEmbeddedTracks();
                    }
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    hideProgressBar();
                    //TODO ad playing or finish listener
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            //Log.d(TAG, "Changed state to " + stateString);
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
            Log.e(TAG, "Error Code -> " + error.errorCode + " - " + error.getErrorCodeName());
            if (listener != null) listener.onPlayerError(error.errorCode, error.getMessage());
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                Log.d(TAG, "Running Behind Live Window. Re-initializing Player");
                player.seekToDefaultPosition();
                player.prepare();
            } else if (error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED) {
                Toast.makeText(context, "Format not Supported", Toast.LENGTH_SHORT).show();
            } else {
                //throw new RuntimeException(error);
            }
        }

        @Override
        public void onVideoSizeChanged(@NonNull VideoSize videoSize) {
            Player.Listener.super.onVideoSizeChanged(videoSize);
            Log.d(TAG, "Video Size Changed to -> " + videoSize.height + "p");
            //Modify Auto Track for video tracks ["Auto" -> "Auto (1080p)"] when Video Size is Changed
            if (muviMediaItem.isUseEmbeddedResolutions() && videoTrackLabels.size() > 0) {
                String label = "Auto";
                if (curVideoTrack == 0) {
                    label = "Auto (" + videoSize.height + "p)";
                }
                videoTrackLabels.set(0, label);
                //TODO If adapter is showing notify of the resolution change to reflect
            }
        }

        @Override
        public void onTracksChanged(@NonNull Tracks tracks) {
            Player.Listener.super.onTracksChanged(tracks);
            configurePlaybackInfoView(tracks);
        }

        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
            Player.Listener.super.onTimelineChanged(timeline, reason);
            //Code to Handle Live Playback
            if (muviMediaItem.isLiveContent()) {
                long currentLiveOffset = player.getDuration() - player.getCurrentPosition();
                /*live_progress.setMax((int) player.getDuration());
                live_progress.setProgress((int) player.getCurrentPosition());*/
                Log.d(TAG, "Current Live Offset -> " + currentLiveOffset);
                live_progress.setMax(100);
                live_progress.setProgress(99);
            }
            //Code to get Embedded Sprite URL from DASH Stream e.g: "https://dash.akamaized.net/akamai/bbb_30fps/bbb_with_tiled_thumbnails.mpd"
            /*DashManifest manifest = (DashManifest) timeline.getWindow(0, new Timeline.Window()).manifest;
            if (manifest == null) {
                return;
            }
            for (int i = 0; i < manifest.getPeriodCount(); i++) {
                Period period = manifest.getPeriod(i);
                long periodDurationUs = manifest.getPeriodDurationUs(i);
                for (int j = 0; j < period.adaptationSets.size(); j++) {
                    AdaptationSet adaptationSet = period.adaptationSets.get(j);
                    for (int k = 0; k < adaptationSet.representations.size(); k++) {
                        Representation representation = adaptationSet.representations.get(k);
                        String mimeType = representation.format.containerMimeType;
                        Log.d(TAG, "MimeTypes -> " + mimeType);
                        if ("image/jpeg".equals(mimeType)) {
                            Representation.MultiSegmentRepresentation multiSegmentRepresentation = (Representation.MultiSegmentRepresentation) representation;
                            for (int l = 1; l < multiSegmentRepresentation.getSegmentCount(periodDurationUs); l++) {
                                Log.d(TAG, "sprite_uri -> " + multiSegmentRepresentation.getSegmentUrl(l).resolveUri(representation.baseUrls.get(0).url));
                            }
                            return;
                        }
                    }
                }
            }*/
        }
    }
}
