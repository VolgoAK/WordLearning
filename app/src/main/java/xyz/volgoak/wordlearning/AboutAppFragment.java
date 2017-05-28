package xyz.volgoak.wordlearning;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAppFragment extends Fragment {


    public AboutAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_about_app, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView aboutTextView = (TextView) getView().findViewById(R.id.tv_about_aboutfrag);
        aboutTextView.setText(Html.fromHtml(getString(R.string.about_html)));
    }
}
