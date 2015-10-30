package com.lichen.youtubedownloader;

import android.app.Application;

import com.google.api.services.youtube.YouTube;
import com.lichen.youtube.YouTubeProvider;
import com.lichen.youtube.YouTubeUtil;

/**
 * Created by laimiux on 11/2/14.
 */
public class YoutubeExampleApplication extends Application implements YouTubeProvider {
    private YouTube mYouTube;

    @Override
    public void onCreate() {
        super.onCreate();

        // For more customization, look into this method and use it's code as starter.
        mYouTube = YouTubeUtil.createDefaultYouTube("youtube-example-application");
    }


    @Override
    public YouTube getYouTube() {
        return mYouTube;
    }
}
