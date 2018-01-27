package xyz.volgoak.wordlearning;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.fragment.BoolTrainingFragment;
import xyz.volgoak.wordlearning.training_utils.PlayWord;


@Layout(R.layout.boolean_word_holder)
public class SwipeHolder {
    
    public static final String TAG = SwipeHolder.class.getSimpleName();

    @View(R.id.tv_bool_word)
    TextView tvWord;

    @View(R.id.tv_bool_word_translation)
    TextView tvTranslation;

    private PlayWord word;
    private SwipeListener swipeListener;

    public SwipeHolder (Context context, PlayWord word) {
        this.word = word;
    }

    @Resolve
    public void onResolwed() {
        Log.d(TAG, "onResolwed: ");
        tvWord.setText(word.getWord());
        tvTranslation.setText(word.getVars()[0]);
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d(TAG, "onSwipedOut");
        if(swipeListener != null) {
            swipeListener.onSwipe(false);
        }
    }

    @SwipeIn
    private void onSwipedIn(){
        Log.d(TAG, "onSwipeIn: ");
        if(swipeListener != null) {
            swipeListener.onSwipe(true);
        }
    }

    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    public interface SwipeListener {
        void onSwipe(boolean answer);
    }
}
