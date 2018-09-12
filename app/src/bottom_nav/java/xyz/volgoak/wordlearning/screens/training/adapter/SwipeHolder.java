package xyz.volgoak.wordlearning.screens.training.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Observable;

import xyz.volgoak.wordlearning.eventbus.IntegerEvent;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.screens.training.helpers.PlayWord;


@Layout(R.layout.boolean_word_holder)
@NonReusable
public class SwipeHolder {

    public static final String TAG = SwipeHolder.class.getSimpleName();
    private static int starImageRes = R.drawable.ic_star_24dp;
    private static int noStarImageRes = R.drawable.ic_star_border_24dp;

    @View(R.id.tv_bool_word)
    TextView tvWord;
    @View(R.id.tv_bool_word_translation)
    TextView tvTranslation;
    @View(R.id.iv_star_one_bool)
    ImageView ivStarOne;
    @View(R.id.iv_star_two_bool)
    ImageView ivStarTwo;
    @View(R.id.iv_star_three_bool)
    ImageView ivStarThree;

    private PlayWord word;
    private SwipeListener swipeListener;
    private Observable starsObservable;

    public SwipeHolder(PlayWord word) {
        this.word = word;
        EventBus.getDefault().register(this);
    }

    @Resolve
    public void onResolwed() {
        tvWord.setText(word.getWord());
        tvTranslation.setText(word.getVars()[0]);
        IntegerEvent starEvent = EventBus.getDefault().getStickyEvent(IntegerEvent.class);
        if (starEvent != null) {
            onStarEvent(starEvent);
        }
    }

    @SwipeOut
    private void onSwipedOut() {
        if (swipeListener != null) {
            swipeListener.onSwipe(false);
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @SwipeIn
    private void onSwipedIn() {
        if (swipeListener != null) {
            swipeListener.onSwipe(true);
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void onStarEvent(IntegerEvent starEvent) {
        ivStarOne.setImageResource(starEvent.value >= 1 ? starImageRes : noStarImageRes);
        ivStarTwo.setImageResource(starEvent.value >= 2 ? starImageRes : noStarImageRes);
        ivStarThree.setImageResource(starEvent.value >= 3 ? starImageRes : noStarImageRes);
    }


    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    public interface SwipeListener {
        void onSwipe(boolean answer);
    }
}
