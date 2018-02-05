package xyz.volgoak.wordlearning;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import xyz.volgoak.wordlearning.activity.SplashActivity;
import xyz.volgoak.wordlearning.dagger.DbModule;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.entities.Dictionary;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.update.SetsLoader;
import xyz.volgoak.wordlearning.update.SetsUpdatingInfo;

import static junit.framework.Assert.assertEquals;

/**
 * Created by alex on 2/6/18.
 */

@RunWith(AndroidJUnit4.class)
public class UpdaterTest {

    @Rule
    public ActivityTestRule<SplashActivity> testRule = new ActivityTestRule<SplashActivity>(SplashActivity.class);

    DataProvider provider;

    @Before
    public void init() {
        System.out.println("Setup test provider");
        DbModule dbModule = new DbModule(testRule.getActivity().getApplication());

        provider = new DataProvider(dbModule.getInfoDao(), dbModule.getLinkDao(),
                dbModule.getSetsDao(), dbModule.getThemeDao(), dbModule.getWordDao());
    }

    @Test
    public void simpleUpdateTest() {
        Dictionary dictionary = new Dictionary();
        List<Set> sets = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Set set = new Set();
            set.setName("Set " + i);
            List<Word> words = new ArrayList<>();
            for (int a = 0; a < 20; a++) {
                Word word = new Word("word" + i + " " + a, "translation " + i + " " + a);
                words.add(word);
            }

            set.setWords(words);
            sets.add(set);
        }

        dictionary.setSets(sets);

        SetsUpdatingInfo info = SetsLoader.insertSetsIntoDb(provider, dictionary);

        assertEquals(info.getSetsAdded(), 3);
        assertEquals(info.getWordsAdded(), 60);
    }

    @Test
    public void asyncUpdateTest() {
        List<Dictionary> dictionaries = new ArrayList<>();
        for (int n = 0; n < 10; n++) {
            Dictionary dictionary = new Dictionary();
            List<Set> sets = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Set set = new Set();
                set.setName("Set " + i + " " + n);
                List<Word> words = new ArrayList<>();
                for (int a = 0; a < 20; a++) {
                    Word word = new Word("word" + n + " " + i + " " + a, "translation " + i + " " + a);
                    words.add(word);
                }

                set.setWords(words);
                sets.add(set);
            }
            dictionary.setSets(sets);
            dictionaries.add(dictionary);
        }

        final SetsUpdatingInfo info = new SetsUpdatingInfo();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (Dictionary d : dictionaries) {
            executor.execute(() -> {
                info.addInfo(SetsLoader.insertSetsIntoDb(provider, d));
                System.out.println("Completed " + d);
            });
        }

        executor.shutdown();

        try {
            System.out.println("await termination");
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            assertEquals(info.getSetsAdded(), 100);
            assertEquals(info.getWordsAdded(), 2000);
        } catch (Exception ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
    }
}

