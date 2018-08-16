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

import com.attiladroid.data.update_managment.DbUpdateManager;

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

        DbUpdateManager.INSTANCE.manageDbState(RuntimeEnvironment.application, provider);
        List<Set> sets = provider.getAllSets();
        for (Set set : sets) {
            set.setStatus(DatabaseContract.Sets.IN_DICTIONARY);
            provider.updateSetStatus(set);
        }
    }

    @Test
    public void boolTrainingTest() {
        boolTrainingCorrectTest();
        boolTrainingWrongTest();
    }

    @Test
    public void boolTrainingCorrectTest() {
        /*TrainingBool trainingBool = TrainingFabric.getBoolTraining(-1, provider);
        PlayWord pw = trainingBool.getInitialWords().get(1);
        trainingBool.checkAnswer(false);
        int i = 0;
        while (i < 300) {
            PlayWord next = trainingBool.nextWord();
            Word word = provider.getWordById(pw.getId());
            assertTrue(pw.getWord().equals(word.getWord()));

            String[] vars = pw.getVars();
            boolean answer = vars[0].equals(word.getTranslation());
            System.out.println(pw.getWord() + " " + pw.getVars()[0] + " answer " + answer);
            assertTrue(trainingBool.checkAnswer(answer));
            pw = next;
            i++;
        }*/
    }

    @Test
    public void boolTrainingWrongTest() {
        /*long time = System.currentTimeMillis();
        TrainingBool trainingBool = TrainingFabric.getBoolTraining(-1, provider);
        PlayWord pw = trainingBool.getInitialWords().get(1);
        trainingBool.checkAnswer(false);

        int i = 0;
        while (i < 200) {
            PlayWord next = trainingBool.nextWord();
            Word word = provider.getWordById(pw.getId());
            assertTrue(pw.getWord().equals(word.getWord()));

            String[] vars = pw.getVars();
            boolean answer = vars[1].equals(word.getTranslation());
            System.out.println(pw.getWord() + " " + pw.getVars()[0] + " answer " + answer);
            assertFalse(trainingBool.checkAnswer(answer));
            pw = next;
            i++;
        }

        assertEquals(0, trainingBool.getScores());
        System.out.println("Training wrong test finished in " + (System.currentTimeMillis() - time));*/
    }

}
