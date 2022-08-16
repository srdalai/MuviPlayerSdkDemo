package com.muvi.tvsdk;

import static android.media.AudioManager.ACTION_HDMI_AUDIO_PLUG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioRouting;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.muvi.tvplayer.MuviMediaItem;
import com.muvi.tvplayer.MuviPlayerConfig;
import com.muvi.tvplayer.MuviPlayerListener;
import com.muvi.tvplayer.MuviPlayerView;
import com.muvi.tvplayer.utils.PlayerHelper;
import com.muvi.tvplayer.utils.Util;
import com.muvi.tvplayer.views.NewOptionsFragment;
import com.theoplayer.android.api.THEOplayerView;
import com.theoplayer.android.api.event.EventListener;
import com.theoplayer.android.api.event.player.PlayEvent;
import com.theoplayer.android.api.event.player.PlayerEventTypes;
import com.theoplayer.android.api.source.SourceDescription;
import com.theoplayer.android.api.source.addescription.THEOplayerAdDescription;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DemoPlayerActivity extends AppCompatActivity {

    private static final String TAG = DemoPlayerActivity.class.getSimpleName();

    MuviPlayerView muviPlayerView;
    THEOplayerView tpv;
    EventListener<PlayEvent> eventListener;

    ArrayList<MuviMediaItem.VideoData> videoDataList = new ArrayList<>();
    ArrayList<MuviMediaItem.Subtitle> subtitleList = new ArrayList<>();
    MuviMediaItem.IntroData introData;
    MuviMediaItem.ThumbnailData thumbnailData;

    //https://simplysouth.muvi.com/rest/getVideoDetails?authToken=1e793998500e2183849920a1987b7a16&content_uniq_id=eab1528ef46c808864104d11968df6bb&stream_uniq_id=50d63c4cb1fb0fc3d18d726cd0a857fa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_player);

        //Muvi Player Setup
        muviPlayerView = findViewById(R.id.muvi_player_view);

        //getVideoDetails();
        parseIntentExtra();

        //Theo Player Setup
        /*tpv = findViewById(R.id.theoplayer_view);
        SourceDescription sourceDescription = SourceDescription.Builder
                .sourceDescription(dashVideoUrl)
                .ads(
                        THEOplayerAdDescription.Builder.adDescription("https://cdn.theoplayer.com/demos/preroll.xml")
                                .timeOffset("10")
                                .skipOffset("3")
                                .build())
                .poster("http://cdn.theoplayer.com/video/big_buck_bunny/poster.jpg")
                .build();
        tpv.getPlayer().setSource(sourceDescription);

        eventListener = new EventListener<PlayEvent>() {
            @Override
            public void handleEvent(PlayEvent event) {
                System.out.println(event.getCurrentTime());
            }
        };
        tpv.getPlayer().addEventListener(PlayerEventTypes.PLAY, eventListener);*/

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {
                @Override
                public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                    super.onAudioDevicesAdded(addedDevices);
                    for (AudioDeviceInfo deviceInfo : addedDevices) {
                        Log.d("AudioDeviceCallback", "Device Added - " + Arrays.toString(deviceInfo.getChannelCounts()));
                    }
                }

                @Override
                public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                    super.onAudioDevicesRemoved(removedDevices);
                    for (AudioDeviceInfo deviceInfo : removedDevices) {
                        Log.d("AudioDeviceCallback", "Device Removed" + Arrays.toString(deviceInfo.getChannelCounts()));
                    }
                }
            }, new Handler());
        }
    }

    private void parseIntentExtra() {
        MediaObject mediaObject = getIntent().getParcelableExtra("media_object");
        if (mediaObject != null) {
            MuviMediaItem muviMediaItem;
            if (mediaObject.getDrmLicenseUri() != null) {
                Log.d(TAG, "Playing DRM Content");
                muviMediaItem = MuviMediaItem.buildDrmMedia(mediaObject.getUri(), mediaObject.getDrmLicenseUri());
            } else {
                Log.d(TAG, "Playing Non-DRM Content");
                if (mediaObject.isLive()) {
                    muviMediaItem = MuviMediaItem.buildLiveMedia(Objects.requireNonNull(mediaObject.getUri()));
                } else {
                    muviMediaItem = MuviMediaItem.fromUrlWithEmbeddedTracks(mediaObject.getUri());
                }
            }
            muviPlayerView.setMuviMediaItem(muviMediaItem);

            initiatePlayback();
        } else {
            Toast.makeText(this, "No Media", Toast.LENGTH_SHORT).show();
        }
    }

    private void getVideoDetails() {
        HttpUrl.Builder builder = HttpUrl.parse("https://api.muvi.com/rest/getVideoDetails").newBuilder();
        builder.addQueryParameter("authToken", "92b9a7907748d038f3277feafcf07506");
        //Hulk
        /*builder.addQueryParameter("content_uniq_id", "551c0c01be07a9d302206bd5505c1143");
        builder.addQueryParameter("stream_uniq_id", "63fafc041e2c665af906f73ac78bcb9f");*/

        //Young Sheldon
        /*builder.addQueryParameter("content_uniq_id", "24dd53f894142731f2b3b6e5c20f0e5a");
        builder.addQueryParameter("stream_uniq_id", "9f4e5e3d831cd6d7aee25da4c499c391");*/

        //FRIENDS - Marshmallow (40 Subs)
        builder.addQueryParameter("content_uniq_id", "6461779b546e676c70fed35bc63fb144");
        builder.addQueryParameter("stream_uniq_id", "f97ba05d751c48183fb6fc6f45f7a61b");

        String url = builder.build().toString();
        Request request = new Request.Builder().url(url).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                handleVideoResponse(response.body().string());
            }
        });
    }

    private void handleVideoResponse(String responseStr) {
        videoDataList.clear();
        subtitleList.clear();
        try {
            JSONObject responseObj = new JSONObject(responseStr);
            JSONArray resolutionArray = responseObj.getJSONArray("videoDetails");
            for (int i = 0; i < resolutionArray.length(); i++) {
                JSONObject videoObj = resolutionArray.getJSONObject(i);
                String resolution = videoObj.optString("resolution", "null");
                if (!resolution.equalsIgnoreCase("best") && !resolution.equalsIgnoreCase("null")) {
                    resolution = resolution + "p";
                }
                String url = videoObj.optString("url", "");
                MuviMediaItem.VideoData videoData = new MuviMediaItem.VideoData.Builder()
                        .setVideoUrl(url)
                        .setResolution(resolution)
                        .build();
                videoDataList.add(videoData);
            }

            JSONArray subArray = responseObj.getJSONArray("subTitle");
            for (int i = 0; i < subArray.length(); i++) {
                JSONObject subObj = subArray.getJSONObject(i);
                String url = subObj.optString("url", "");
                String code = subObj.optString("code", "");
                String language = subObj.optString("language", "");
                MuviMediaItem.Subtitle subtitle = new MuviMediaItem.Subtitle.Builder()
                        .setLanguage(language)
                        .setLanguageCode(code)
                        .setUrl(url)
                        .build();
                subtitleList.add(subtitle);
            }

            JSONObject introObj = responseObj.getJSONObject("skip_intro");
            int starting_intro_time = introObj.optInt("starting_intro_time", 0);
            int ending_intro_time = introObj.optInt("ending_intro_time", 0);
            introData = new MuviMediaItem.IntroData.Builder()
                    .setStartingIntroTime(starting_intro_time)
                    .setEndingIntroTime(ending_intro_time)
                    .build();


            JSONObject frameObj = responseObj.getJSONObject("frame_info");
            int no_of_row = frameObj.optInt("no_of_row", 0);
            int no_of_column = frameObj.optInt("no_of_column", 0);
            int thumb_interval = frameObj.optInt("thumb_interval", 0);
            String sprite_image = frameObj.optString("sprite_image", "");
            thumbnailData = new MuviMediaItem.ThumbnailData.Builder()
                    .setThumbRows(no_of_row)
                    .setThumbColumns(no_of_column)
                    .setThumbInterval(thumb_interval)
                    .setThumbSprite(sprite_image)
                    .build();

        } catch (Exception e) {

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initiatePlayback();
            }
        });
    }

    private void initiatePlayback() {
        //Util.supportsResolution("1440");
        //Generic Playback
        /*MuviMediaItem.Builder builder = new MuviMediaItem.Builder();
        builder.setVideoData(new MuviMediaItem.VideoData.Builder()
                        .setVideoUrl(dashVideoUrl)
                        .build()
                )
                .setIntroData(new MuviMediaItem.IntroData.Builder()
                        .setStartingIntroTime(10)
                        .setEndingIntroTime(50)
                        .build()
                ).setUseEmbeddedResolutions(true)
                .setUseEmbeddedSubtitles(true);
        muviPlayerView.setMuviMediaItem(builder.build());*/

        //Live Playback
        /*MuviMediaItem muviMediaItem = MuviMediaItem.buildLiveMedia(liveVideoUrl);
        muviPlayerView.setMuviMediaItem(muviMediaItem);
        muviPlayerView.setMuviMediaItem(muviMediaItem);*/

        //Muvi Content Playback
        /*MuviMediaItem.Builder builder = new MuviMediaItem.Builder();
        builder.setPrimaryTitle("Content Title")
                .setSecondaryTitle("Episode Details")
                .setVideoData(videoDataList);
        if (subtitleList.size() > 0) {
            builder.setSubtitle(subtitleList);
        }
        if (introData != null) {
            builder.setIntroData(introData);
        }
        if (thumbnailData != null) {
            builder.setThumbnailData(thumbnailData);
        }
        muviPlayerView.setMuviMediaItem(builder.build());*/

        //muviPlayerView.setPlayerConfig(new MuviPlayerConfig.Builder().setPreferredResolution("720").build());
        muviPlayerView.initialize();
        muviPlayerView.play();

        PlayerHelper playerHelper;
        muviPlayerView.setPlayerListener(new MuviPlayerListener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                String stateString;
                switch (playbackState) {
                    case MuviPlayerListener.STATE_IDLE:
                        stateString = "ExoPlayer.STATE_IDLE      -";
                        break;
                    case MuviPlayerListener.STATE_BUFFERING:
                        stateString = "ExoPlayer.STATE_BUFFERING -";
                        break;
                    case MuviPlayerListener.STATE_READY:
                        stateString = "ExoPlayer.STATE_READY     -";
                        break;
                    case MuviPlayerListener.STATE_ENDED:
                        stateString = "ExoPlayer.STATE_ENDED     -";
                        finishAfterTransition();
                        break;
                    default:
                        stateString = "UNKNOWN_STATE             -";
                        break;
                }
                Log.d(TAG, "Changed state to " + stateString);
            }

            @Override
            public void onVideoTrackChanged(int position) {
                super.onVideoTrackChanged(position);
            }

            @Override
            public void onPlayerError(int errorCode, String errorMessage) {
                super.onPlayerError(errorCode, errorMessage);
                if (errorCode == ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    //Running Behind Live Window. Wait as the player will correct itself
                } else {
                    Toast.makeText(DemoPlayerActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    finishAfterTransition();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume Called");
        super.onResume();
        if (muviPlayerView != null) {
            muviPlayerView.onResume();
        }
        if (tpv != null) {
            tpv.onResume();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause Called");
        super.onPause();
        if (muviPlayerView != null) {
            muviPlayerView.onPause();
        }
        if (tpv != null) {
            tpv.getPlayer().removeEventListener(PlayerEventTypes.PLAY, eventListener);
        }
        if (tpv != null) {
            tpv.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy Called");
        super.onDestroy();
        if (muviPlayerView != null) {
            muviPlayerView.onDestroy();
        }
        if (tpv != null) {
            tpv.onDestroy();
        }
    }
}