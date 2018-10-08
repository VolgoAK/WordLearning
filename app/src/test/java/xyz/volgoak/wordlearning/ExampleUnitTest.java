package xyz.volgoak.wordlearning;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.volgoak.wordlearning.adapter.choice_mode.MultiChoiceMode;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void MultiChoiceGetListTest(){
        MultiChoiceMode mode = new MultiChoiceMode();
        List<Integer> testList = new ArrayList<>();
        Collections.addAll(testList, 1, 4, 5, 7, 8, 12, 14, 18);
        for(Integer i : testList){
            mode.setChecked(i, true);
        }
        List<Integer> listFromMode = mode.getCheckedList();
        for(Integer i : listFromMode){
            System.out.println(i);
        }
        assertTrue(testList.equals(listFromMode));
    }


}