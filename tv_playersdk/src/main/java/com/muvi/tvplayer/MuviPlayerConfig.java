package com.muvi.tvplayer;

public final class MuviPlayerConfig {

    public static final class Builder {

        private String preferredResolution;
        private String preferredAudio;
        private String preferredSubtitle;

        /**
         * Set Preferred Resolution in (e.g. 240, 360, 480 etc., without "p")
         *
         * @param preferredResolution
         * @return Builder
         */
        public Builder setPreferredResolution(String preferredResolution) {
            this.preferredResolution = preferredResolution;
            return this;
        }

        public Builder setPreferredAudio(String preferredAudio) {
            this.preferredAudio = preferredAudio;
            return this;
        }

        public Builder setPreferredSubLanguage(String preferredSubtitle) {
            this.preferredSubtitle = preferredSubtitle;
            return this;
        }

        public MuviPlayerConfig build() {
            return new MuviPlayerConfig(this);
        }
    }

    private String preferredResolution;
    private String preferredAudio;
    private String preferredSubtitle;

    public MuviPlayerConfig(Builder builder) {
        preferredResolution = builder.preferredResolution;
        preferredAudio = builder.preferredAudio;
        preferredSubtitle = builder.preferredSubtitle;
    }

    public String getPreferredResolution() {
        return preferredResolution;
    }

    public void setPreferredResolution(String preferredResolution) {
        this.preferredResolution = preferredResolution;
    }

    public String getPreferredAudio() {
        return preferredAudio;
    }

    public void setPreferredAudio(String preferredAudio) {
        this.preferredAudio = preferredAudio;
    }

    public String getPreferredSubtitle() {
        return preferredSubtitle;
    }

    public void setPreferredSubtitle(String preferredSubtitle) {
        this.preferredSubtitle = preferredSubtitle;
    }
}
