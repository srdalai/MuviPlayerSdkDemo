# MuviPlayer Android TV SDK
**MuviPlayer Android TV SDK** can be used for content playback in any Android TV App without the hassle of handling D-Pad controllers.
MuviPlayer is built on top of [ExoPlayer](https://exoplayer.dev/).

## Installation
In your **project** level `build.gradle` file add the Jitpack repository:

```
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
In your **app** level `build.gradle` file add the following dependencies:
```
implementation 'com.muvi.sdk:tvplayersdk:1.0.0'
```

## Usage
### 1. Instantiation
MuviPlayer will us the UI Element `MuviPlayerView`. It will be instantiated by adding it to your layout as in following snippet.
```xml
<com.muvi.tvplayer.MuviPlayerView
    android:id="@+id/muvi_player_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Then in the Activity, reference to it as shown in the snippet bellow.
```java
MuviPlayerView muviPlayerView = findViewById(R.id.muvi_player_view);
```

### 2. Calling the lifecycle events
```java
@Override
protected void onResume() {
    super.onResume();
    muviPlayerView.onResume();
}

@Override
protected void onPause() {
    super.onPause();
    muviPlayerView.onPause();
    muviPlayerView.removePlayerListener();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    muviPlayerView.onDestroy();
}
```

### 3. Setting Media Items
**MuviPlayer** supports the follwoing Media types
- [Progressive](https://exoplayer.dev/progressive.html)
- [HLS](https://exoplayer.dev/hls.html)
- [DASH](https://exoplayer.dev/dash.html)

#### a) Progressive Media Source (Non DRM)
Using the `MuviMediaItem.fromUrl()`, you can create a simple MideaItem ready for playabck.
```java
MuviMediaItem muviMediaItem = MuviMediaItem.fromUrl(videoUrl);
```
See: [Add Multiple MediaSource](#adding-multiple-resolution)

#### b) HLS Media Source
To create a HLS Media Source use `MuviMediaItem.fromUrlWithEmbeddedTracks()`. This will enable the player to use the tracks (Video, Subtitle) embedded in the stream. If you don't want to use the Embedded Tracks use the `MuviMediaItem.fromUrl()` method instead.
```java
MuviMediaItem muviMediaItem = MuviMediaItem.fromUrlWithEmbeddedTracks(videoUrl);
```


#### c) DASH Media Source (DRM Only)
Currently MuviPlayer only supports DASH Media Source with DRM Support. The DRM License should be of Widevine.
```java
MuviMediaItem muviMediaItem = MuviMediaItem.buildDrmMedia(videoUrl, licenseUrl);
```

### 4. Starting Playback
After the `MuviMediaItem` object has been created, provide it to the `MuviPlayerView`, and start playback as shown in the following code snippet.
```java
muviPlayerView.setMuviMediaItem(muviMediaItem);
muviPlayerView.initialize();
muviPlayerView.play();
```

## Advance Playback
For advance Playback, you can use the `MuviMediaItem.Builder` to customize the `MuviMediaItem`. First create the builder as shown below. Add required data to `builder`. Then `build()` the `MuviMediaItem` and set it to `MuviPlayerView`.
```java
MuviMediaItem.Builder builder = new MuviMediaItem.Builder();
...
MuviMediaItem.VideoData videoData = new MuviMediaItem.VideoData.Builder()
    .setVideoUrl(url)
    .setResolution(resolution)
    .build();
...
muviPlayerView.setMuviMediaItem(builder.build());
muviPlayerView.initialize();
muviPlayerView.play();
```
**Note:** Atleast a single `MuviMediaItem.VideoData` is required to start playback.

### Adding Multiple Resolution
Creating `MuviMediaItem.VideoData` object, and adding to `MuviMediaItem.Builder`.
```java
ArrayList<MuviMediaItem.VideoData> videoDataList = new ArrayList<>();
MuviMediaItem.VideoData videoData = new MuviMediaItem.VideoData.Builder()
    .setVideoUrl(url)
    .setResolution(resolution)
    .build();
    
videoDataList.add(videoData);
...
builder.setVideoData(videoDataList);
```
The `MuviMediaItem.Builder.setVideoData()` can take both `MuviMediaItem.VideoData` & `ArrayList<MuviMediaItem.VideoData>`. The provided Video Data will not have any effect if `MuviMediaItem.Builder.setUseEmbeddedResolutions()` is set to `true`, as the player will show the Resolutions embedded in the Stream.
**Note:** As `MuviMediaItem.Builder` can not take `MuviMediaItem.VideoData` once the `MuviMediaItem` is build, you need to add all the availbale VideoData in one go. And then call the `MuviMediaItem.Builder.setVideoData()`.

### Adding Subtitle
You can add subtitles to the `MuviMediaItem.Builder`.
Creating `MuviMediaItem.Subtitle` object, and adding to `MuviMediaItem.Builder`.
```java
MuviMediaItem.Subtitle subtitle = new MuviMediaItem.Subtitle.Builder()
    .setLanguage(language)
    .setLanguageCode(code)
    .setUrl(url)
    .build();
                        
builder.setSubtitle(subtitle);
```
The `MuviMediaItem.Builder` can also take a `ArrayList<MuviMediaItem.Subtitle>`. The provided subtitle will not have any effect if `MuviMediaItem.Builder.setUseEmbeddedSubtitles()` is set to `true`, as the player will show the Subtitles embedded in the Stream.

### Adding Intro Data
MuviPlayer supports **Skip Intro** feature.
Getting Intro Data from Muvi API
```bash
curl 'https://api.muvi.com/rest/getVideoDetails?authToken=<AUTH_TOKEN>&content_uniq_id=<MUVI_UNIQUE_ID>&stream_uniq_id=<STREAM_UNIQUE_ID>'
```
Response:
```json
{
    "code": 200,
	"status": "OK",
	"msg": "Video Details",
	...
	skip_intro: {
    	"starting_intro_time": 82,
    	"ending_intro_time": 99,
    	"skip_intro_btn_position": "brc"
	}
	...
}
```
Creating `MuviMediaItem.IntroData` object, and adding to `MuviMediaItem.Builder`.
```java
MuviMediaItem.IntroData introData = new MuviMediaItem.IntroData.Builder()
    .setStartingIntroTime(starting_intro_time)
    .setEndingIntroTime(ending_intro_time)
    .build();
                    
builder.setIntroData(introData);
```

#### Adding Thumbnail Data
Getting Thumnail Data from Muvi API
```bash
curl 'https://api.muvi.com/rest/getVideoDetails?authToken=<AUTH_TOKEN>&content_uniq_id=<MUVI_UNIQUE_ID>&stream_uniq_id=<STREAM_UNIQUE_ID>'
```
Response:
```json
{
    "code": 200,
	"status": "OK",
	"msg": "Video Details",
	...
	frame_info: {
    	"no_of_row": 6,
		"no_of_column": 20,
		"total_no_thumbs": 117,
		"thumb_interval": 10,
		"sprite_image": "https://d2sal5lpzsf102.cloudfront.net/39538/EncodedVideo/uploads/movie_stream/full_movie/880699/sprite/1643263547-sprite.jpg"
	}
	...
}
```
Creating `MuviMediaItem.ThumbnailData` object, and adding to `MuviMediaItem.Builder`.
```java
MuviMediaItem.ThumbnailData thumbnailData = new MuviMediaItem.ThumbnailData.Builder()
        .setThumbRows(no_of_row)
        .setThumbColumns(no_of_column)
        .setThumbInterval(thumb_interval)
        .setThumbSprite(sprite_image)
        .build();

        builder.setThumbnailData(thumbnailData);
```