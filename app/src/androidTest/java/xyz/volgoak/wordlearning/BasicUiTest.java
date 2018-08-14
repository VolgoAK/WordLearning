package xyz.volgoak.wordlearning;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.volgoak.wordlearning.screens.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by alex on 2/18/18.
 */

@RunWith(AndroidJUnit4.class)
public class BasicUiTest {

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void dictionaryTest() {
        onView(withId(R.id.cv_redactor_main)).perform(click());
        onView(withId(R.id.rv_redactor)).check(matches(isDisplayed()));
    }
}
