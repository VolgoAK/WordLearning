package xyz.volgoak.wordlearning.dagger;

import javax.inject.Singleton;

import dagger.Component;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.screens.dictionary.DictionaryFragment;
import xyz.volgoak.wordlearning.screens.set.SingleSetFragment;
import xyz.volgoak.wordlearning.screens.main.fragment.StartFragment;
import xyz.volgoak.wordlearning.screens.set.WordCardsFragment;
import xyz.volgoak.wordlearning.screens.main.fragment.WordSetsFragment;
import xyz.volgoak.wordlearning.screens.set.viewModel.SingleSetViewModel;
import xyz.volgoak.wordlearning.adapter.SetsRecyclerAdapter;
import xyz.volgoak.wordlearning.screens.main.viewModel.MainViewModel;
import com.attiladroid.data.update_managment.SetsLoaderService;
import com.attiladroid.data.update_managment.FirebaseDownloadHelper;
import com.attiladroid.data.update_managment.ImageDownloader;

import xyz.volgoak.wordlearning.screens.training.fragment.BoolTrainingFragment;
import xyz.volgoak.wordlearning.screens.training.fragment.ResultsFragment;
import xyz.volgoak.wordlearning.screens.training.fragment.TimerFragment;
import xyz.volgoak.wordlearning.screens.training.fragment.TrainingFragment;
import xyz.volgoak.wordlearning.utils.SoundsManager;

/**
 * Created by alex on 1/7/18.
 */

@Component(modules = {DbModule.class, AppModule.class})
@Singleton
public interface DbComponent {
    void inject(WordsApp app);
    void inject(DictionaryFragment fragment);
    void inject(ResultsFragment fragment);
    void inject(SingleSetFragment fragment);
    void inject(StartFragment fragment);
    void inject(WordSetsFragment fragment);
    void inject(TrainingFragment fragment);
    void inject(BoolTrainingFragment fragment);
    void inject(TimerFragment fragment);
    void inject(WordCardsFragment fragment);
    void inject(FirebaseDownloadHelper helper);

    void inject(ImageDownloader downloader);
    void inject(SetsLoaderService service);
    void inject(SetsRecyclerAdapter adapter);
    void inject(SoundsManager manager);

    void inject(SingleSetViewModel model);
    void inject(MainViewModel model);
}
