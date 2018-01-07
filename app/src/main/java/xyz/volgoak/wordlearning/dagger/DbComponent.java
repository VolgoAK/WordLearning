package xyz.volgoak.wordlearning.dagger;

import javax.inject.Singleton;

import dagger.Component;
import xyz.volgoak.wordlearning.WordsApp;

/**
 * Created by alex on 1/7/18.
 */

@Component(modules = {DbModule.class})
@Singleton
public interface DbComponent {
    void inject(WordsApp app);

}
