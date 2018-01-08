package xyz.volgoak.wordlearning.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xyz.volgoak.wordlearning.update.ImageDownloader;

/**
 * Created by alex on 1/8/18.
 */

@Module
public class DownloaderModule {

    @Provides
    @Singleton
    public ImageDownloader getImageDownloader() {
        return new ImageDownloader();
    }
}
