package com.muvi.tvplayer;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class MuviMediaItem {

    public static final class Builder {

        private String licenseUrl = "";
        private String primaryTitle = "";
        private String secondaryTitle = "";
        private String contentPoster = "";

        private boolean isLiveContent = false;
        private boolean useEmbeddedResolutions = false;
        private boolean useEmbeddedSubtitles = false;

        private ArrayList<VideoData> videoDataList;
        private ArrayList<Subtitle> subtitleList;
        private IntroData introData;
        private ThumbnailData thumbnailData;

        public Builder setPrimaryTitle(String primaryTitle) {
            this.primaryTitle = primaryTitle;
            return this;
        }

        public Builder setSecondaryTitle(String secondaryTitle) {
            this.secondaryTitle = secondaryTitle;
            return this;
        }

        public Builder setContentPoster(String contentPoster) {
            this.contentPoster = contentPoster;
            return this;
        }

        public Builder setLicenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
            return this;
        }

        public Builder setLiveContent(boolean liveContent) {
            isLiveContent = liveContent;
            return this;
        }

        public Builder setUseEmbeddedResolutions(boolean useEmbeddedResolutions) {
            this.useEmbeddedResolutions = useEmbeddedResolutions;
            return this;
        }

        public Builder setUseEmbeddedSubtitles(boolean useEmbeddedSubtitles) {
            this.useEmbeddedSubtitles = useEmbeddedSubtitles;
            return this;
        }

        public Builder setVideoData(ArrayList<VideoData> videoDataList) {
            this.videoDataList = videoDataList;
            return this;
        }

        public Builder setVideoData(VideoData videoData) {
            ArrayList<VideoData> videoDataList = new ArrayList<>();
            videoDataList.add(videoData);
            this.videoDataList = videoDataList;
            return this;
        }

        public Builder setSubtitle(ArrayList<Subtitle> subtitleList) {
            subtitleList.add(0, new Subtitle.Builder().build());
            this.subtitleList = subtitleList;
            return this;
        }

        public Builder setSubtitle(Subtitle subtitle) {
            ArrayList<Subtitle> subtitleList = new ArrayList<>();
            subtitleList.add(new Subtitle.Builder().build());
            subtitleList.add(subtitle);
            this.subtitleList = subtitleList;
            return this;
        }

        public Builder setIntroData(@Nullable IntroData introData) {
            this.introData = introData;
            return this;
        }

        public Builder setThumbnailData(@Nullable ThumbnailData thumbnailData) {
            this.thumbnailData = thumbnailData;
            return this;
        }

        public MuviMediaItem build() {
            if (subtitleList == null) subtitleList = new ArrayList<>();
            MuviMediaItem muviMediaItem = new MuviMediaItem(this);
            validateObject(muviMediaItem);
            return muviMediaItem;
        }

        private void validateObject(MuviMediaItem muviMediaItem) {
            //Do some basic validations to check
            //if user object does not break any assumption of system
        }

    }

    private final String licenseUrl;
    private final String primaryTitle;
    private final String secondaryTitle;
    private final String contentPoster;
    private final boolean isLiveContent;
    private final boolean useEmbeddedResolutions;
    private final boolean useEmbeddedSubtitles;

    private final ArrayList<VideoData> videoDataList;
    private final ArrayList<Subtitle> subtitleList;
    private final IntroData introData;
    private final ThumbnailData thumbnailData;

    private MuviMediaItem(Builder builder) {
        this.licenseUrl = builder.licenseUrl;
        this.primaryTitle = builder.primaryTitle;
        this.secondaryTitle = builder.secondaryTitle;
        this.contentPoster = builder.contentPoster;
        this.isLiveContent = builder.isLiveContent;
        this.useEmbeddedResolutions = builder.useEmbeddedResolutions;
        this.useEmbeddedSubtitles = builder.useEmbeddedSubtitles;
        this.videoDataList = builder.videoDataList;
        this.subtitleList = builder.subtitleList;
        this.introData = builder.introData;
        this.thumbnailData = builder.thumbnailData;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public String getPrimaryTitle() {
        return primaryTitle;
    }

    public String getSecondaryTitle() {
        return secondaryTitle;
    }

    public String getContentPoster() {
        return contentPoster;
    }

    public boolean isLiveContent() {
        return isLiveContent;
    }

    public boolean isUseEmbeddedResolutions() {
        return useEmbeddedResolutions;
    }

    public boolean isUseEmbeddedSubtitles() {
        return useEmbeddedSubtitles;
    }

    public ArrayList<VideoData> getVideoData() {
        return videoDataList;
    }

    public ArrayList<Subtitle> getSubtitles() {
        return subtitleList;
    }

    public IntroData getIntroData() {
        return introData;
    }

    public ThumbnailData getThumbnailData() {
        return thumbnailData;
    }

    public ArrayList<String> getVideoResolutions() {
        ArrayList<String> resolutions = new ArrayList<>();
        if (videoDataList == null) return resolutions;
        for (MuviMediaItem.VideoData videoData: videoDataList) {
            resolutions.add(videoData.getResolution());
        }
        return resolutions;
    }

    public ArrayList<String> getSubLanguages() {
        ArrayList<String> subtitles = new ArrayList<>();
        if (subtitleList == null) return subtitles;
        for (MuviMediaItem.Subtitle sub: subtitleList) {
            subtitles.add(sub.getLanguage());
        }
        return subtitles;
    }

    //Helper methods
    public static MuviMediaItem fromUrl(String videoUrl) {
        return new Builder().setVideoData(new VideoData.Builder()
                        .setVideoUrl(videoUrl)
                        .build())
                .build();
    }

    public static MuviMediaItem fromUrlWithEmbeddedTracks(String videoUrl) {
        return new Builder()
                .setVideoData(new VideoData.Builder()
                        .setVideoUrl(videoUrl)
                        .build())
                .setUseEmbeddedResolutions(true)
                .setUseEmbeddedSubtitles(true)
                .build();
    }

    public static MuviMediaItem buildLiveMedia(String videoUrl) {
        if (!videoUrl.contains(".m3u8")) {
            throw new AssertionError("Required a .m3u8 stream");
        }
        return new Builder()
                .setVideoData(new VideoData.Builder()
                        .setVideoUrl(videoUrl)
                        .build())
                .setUseEmbeddedResolutions(true)
                .setUseEmbeddedSubtitles(true)
                .setLiveContent(true)
                .build();
    }

    public static MuviMediaItem buildDrmMedia(String videoUrl, String licenseUrl) {
        return new Builder()
                .setVideoData(new VideoData.Builder()
                        .setVideoUrl(videoUrl)
                        .build())
                .setLicenseUrl(licenseUrl)
                .setUseEmbeddedResolutions(true)
                .setUseEmbeddedSubtitles(true)
                .build();
    }

    public boolean isSubtitlesEnabled() {
        return false;
    }

    public boolean isSkipIntroEnabled() {
        return introData != null;
    }

    public boolean isThumbnailPreviewEnabled() {
        return thumbnailData != null;
    }

    public static final class VideoData {

        public static final class Builder {

            private String resolution = "Default";
            private String videoUrl = "";

            public Builder setResolution(String resolution) {
                this.resolution = resolution;
                return this;
            }

            public Builder setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
                return this;
            }

            public VideoData build() {
                return new VideoData(this);
            }
        }

        private final String resolution;
        private final String videoUrl;

        public VideoData(VideoData.Builder builder) {
            this.resolution = builder.resolution;
            this.videoUrl = builder.videoUrl;
        }

        public String getResolution() {
            return resolution;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

    }

    public static final class Subtitle {

        public static final class Builder {

            private String language = "Off";
            private String languageCode = "";
            private String url = "";

            public Builder setLanguage(String language) {
                this.language = language;
                return this;
            }

            public Builder setLanguageCode(String languageCode) {
                this.languageCode = languageCode;
                return this;
            }

            public Builder setUrl(String url) {
                this.url = url;
                return this;
            }

            public Subtitle build() {
                return new Subtitle(this);
            }
        }

        private final String language;
        private final String languageCode;
        private final String url;

        private Subtitle(Builder builder) {
            this.language = builder.language;
            this.languageCode = builder.languageCode;
            this.url = builder.url;
        }

        public String getLanguage() {
            return language;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public String getUrl() {
            return url;
        }
    }

    public static final class IntroData {

        public static final class Builder {

            private int startingIntroTime = 0;
            private int endingIntroTime = 0;


            public Builder setStartingIntroTime(int startingIntroTime) {
                this.startingIntroTime = startingIntroTime;
                return this;
            }

            public Builder setEndingIntroTime(int endingIntroTime) {
                this.endingIntroTime = endingIntroTime;
                return this;
            }

            public IntroData build() {
                if (this.endingIntroTime == 0) {
                    try {
                        throw new RuntimeException("Ending time can not be zero(0). Disabling Skip Intro");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (this.startingIntroTime > this.endingIntroTime) {
                    try {
                        throw new RuntimeException("Starting time can not exceed ending time. Disabling Skip Intro");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return new IntroData(this);
            }
        }

        private final int startingIntroTime;
        private final int endingIntroTime;

        public IntroData(Builder builder) {
            this.startingIntroTime = builder.startingIntroTime;
            this.endingIntroTime = builder.endingIntroTime;
        }

        public int getStartingIntroTime() {
            return startingIntroTime;
        }

        public int getEndingIntroTime() {
            return endingIntroTime;
        }
    }

    public static final class ThumbnailData {

        public static final class Builder {

            private int thumbRows = 0;
            private int thumbColumns = 0;
            private int thumbInterval = 0;
            private String thumbSprite = "";

            public Builder setThumbRows(int thumbRows) {
                this.thumbRows = thumbRows;
                return this;
            }

            public Builder setThumbColumns(int thumbColumns) {
                this.thumbColumns = thumbColumns;
                return this;
            }

            public Builder setThumbInterval(int thumbInterval) {
                this.thumbInterval = thumbInterval;
                return this;
            }

            public Builder setThumbSprite(String thumbSprite) {
                this.thumbSprite = thumbSprite;
                return this;
            }

            public ThumbnailData build() {
                if (thumbRows == 0) return null;
                if (thumbColumns == 0) return null;
                if (thumbInterval == 0) return null;
                if (thumbSprite.isEmpty()) return null;
                try {
                    Uri.parse(thumbSprite);
                } catch (Exception e) {
                    return null;
                }
                return new ThumbnailData(this);
            }
        }

        private final int thumbRows;
        private final int thumbColumns;
        private final int thumbInterval;
        private final String thumbSprite;

        private ThumbnailData(Builder builder) {
            this.thumbRows = builder.thumbRows;
            this.thumbColumns = builder.thumbColumns;
            this.thumbInterval = builder.thumbInterval;
            this.thumbSprite = builder.thumbSprite;
        }

        public int getThumbRows() {
            return thumbRows;
        }

        public int getThumbColumns() {
            return thumbColumns;
        }

        public int getThumbInterval() {
            return thumbInterval;
        }

        public int getThumbIntervalMillis() {
            return thumbInterval * 1000;
        }

        public String getThumbSprite() {
            return thumbSprite;
        }
    }
}
