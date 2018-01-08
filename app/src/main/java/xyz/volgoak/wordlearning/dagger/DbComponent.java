package xyz.volgoak.wordlearning.dagger;

import javax.inject.Singleton;

import dagger.Component;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.fragment.RedactorFragment;
import xyz.volgoak.wordlearning.fragment.ResultsFragment;
import xyz.volgoak.wordlearning.fragment.SingleSetFragment;
import xyz.volgoak.wordlearning.fragment.StartFragment;
import xyz.volgoak.wordlearning.fragment.TrainingFragment;
import xyz.volgoak.wordlearning.fragment.WordSetsFragment;
import xyz.volgoak.wordlearning.update.FirebaseDownloadHelper;

/**
 * Created by alex on 1/7/18.
 */

@Component(modules = {DbModule.class})
@Singleton
public interface DbComponent {
    void inject(WordsApp app);
    void inject(RedactorFragment fragment);
    void inject(ResultsFragment fragment);
    void inject(SingleSetFragment fragment);
    void inject(StartFragment fragment);
    void inject(WordSetsFragment fragment);
    void inject(TrainingFragment fragment);
    void inject(FirebaseDownloadHelper helper);

}
