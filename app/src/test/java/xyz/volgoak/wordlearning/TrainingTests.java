package xyz.volgoak.wordlearning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import xyz.volgoak.wordlearning.dagger.DbModule;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.training_utils.PlayWord;
import xyz.volgoak.wordlearning.training_utils.TrainingBool;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;
import xyz.volgoak.wordlearning.update.DbUpdateManager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by alex on 2/1/18.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TrainingTests {


    DataProvider provider;

    @Before
    public void setUp() {
        System.out.println("Setup test provider");
        DbModule dbModule = new DbModule(RuntimeEnvironment.application);

        provider = new DataProvider(dbModule.getInfoDao(), dbModule.getLinkDao(),
                dbModule.getSetsDao(), dbModule.getThemeDao(), dbModule.getWordDao());

        DbUpdateManager.manageDbState(RuntimeEnvironment.application, provider);
        List<Set> sets = provider.getAllSets();
        for (Set set : sets) {
            set.setStatus(DatabaseContract.Sets.IN_DICTIONARY);
            provider.updateSetStatus(set);
        }
    }

    @Test
    public void boolTrainingTest() {
        boolTrainingCorrectTest();
        boolTrainingInCorrectTest();
        bollTrainingCountTest();
    }

    public void boolTrainingCorrectTest() {
        TrainingBool trainingBool = TrainingFabric.getBoolTraining(-1, provider);
        PlayWord pw;
        while ((pw = trainingBool.getNextPlayWord()) != null) {
            Word word = provider.getWordById(pw.getId());
            assertTrue(pw.getWord().equals(word.getWord()));

            String[] vars = pw.getVars();
            boolean answer = vars[0].equals(word.getTranslation());
            System.out.println(pw.getWord() + " " + pw.getVars()[0] + " answer " + answer);
            assertTrue(trainingBool.checkAnswer(answer));
        }
    }


    public void boolTrainingInCorrectTest() {
        long time = System.currentTimeMillis();
        TrainingBool trainingBool = TrainingFabric.getBoolTraining(-1, provider);
        PlayWord pw;
        while ((pw = trainingBool.getNextPlayWord()) != null) {
            Word word = provider.getWordById(pw.getId());
            assertTrue(pw.getWord().equals(word.getWord()));

            String[] vars = pw.getVars();
            boolean answer = vars[1].equals(word.getTranslation());
            System.out.println(pw.getWord() + " " + pw.getVars()[0] + " answer " + answer);
            assertFalse(trainingBool.checkAnswer(answer));
        }

        assertEquals(0, trainingBool.getScores());
        System.out.println("Training correct test finished in " + (System.currentTimeMillis() - time));
    }


    public void bollTrainingCountTest() {
        TrainingBool trainingBool = TrainingFabric.getBoolTraining(-1, provider);
        List<Word> words = provider.getDictionaryWords();
        int counter = 0;
        while (trainingBool.nextWord() != null) counter++;

        assertEquals(words.size() - 2, counter);
    }
}
