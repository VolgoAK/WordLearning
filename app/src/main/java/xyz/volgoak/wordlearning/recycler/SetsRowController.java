package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.entities.DataEntity;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.data.StorageContract;
import xyz.volgoak.wordlearning.update.ImageDownloader;

/**
 * Created by Volgoak on 16.08.2017.
 */

 class SetsRowController extends NewRowController{

    public static final String TAG = "SetsRowController";

    private View mRoot;
    private CardView mCardRoot;

    private TextView setNameTv;
    private TextView setDescriptionTv;
    private TextView firstLetterTv;
    private ImageButton addButton;
    private CircleImageView civ;

    private SetsRecyclerAdapter.SetStatusChanger mStatusChanger;

    public SetsRowController(View view, Context context, RecyclerAdapter adapter){
        super(view, context, adapter);

        mRoot = view;
        mCardRoot = (CardView)mRoot;
        setNameTv = (TextView) view.findViewById(R.id.tv_name_setsadapter);
        addButton = (ImageButton) view.findViewById(R.id.ibt_add_sets);
        setDescriptionTv = (TextView) view.findViewById(R.id.tv_description_setsadapter);
        civ = (CircleImageView) view.findViewById(R.id.civ_sets);
        firstLetterTv = (TextView) view.findViewById(R.id.tv_first_letter_sets);
    }

    @Override
    public void bindController(DataEntity dataEntity) {
        Set set = (Set) dataEntity;
        setNameTv.setText(set.getName());
        setDescriptionTv.setText(set.getDescription());

        int setStatus = set.getStatus();
        int drawableId = setStatus == DatabaseContract.Sets.IN_DICTIONARY
                ? R.drawable.ic_added_green_50dp : R.drawable.ic_add_blue_50dp;
        addButton.setImageResource(drawableId);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStatusChanger != null){
                    mStatusChanger.changeSetStatus(set);
                }
            }
        });

        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onControllerClick(SetsRowController.this, v, getAdapterPosition());
            }
        });

        mRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return mAdapter.onControllerLongClick(SetsRowController.this, v, getAdapterPosition());
            }
        });

        String imageUrl = set.getImageUrl();
        File imagesDir = new File(mContext.getFilesDir(), StorageContract.IMAGES_FOLDER);
        File imageFile = new File(imagesDir, imageUrl);
        Uri imageUri = Uri.fromFile(imageFile);

        if(imageFile.exists()) {
            Glide.with(mContext).load(imageUri)
                    .error(R.drawable.button_back)
                    .into(civ);
        } else {
            // TODO: 1/31/18 load new image
            Bundle failEvent = new Bundle();
            failEvent.putString("image", set.getImageUrl());
            FirebaseAnalytics.getInstance(mContext).logEvent("Image_fail", failEvent);

            ((SetsRecyclerAdapter) mAdapter).downloadImage(set.getImageUrl());

            Glide.with(mContext).load(R.drawable.button_back).into(civ);
        }
    }

    @Override
    public void setChecked(boolean checked) {

        int backGroundColor = checked ? R.color.colorAccent : R.color.semi_transparent_white;
        int ss = mAdapter.getContext().getResources()
                .getColor(backGroundColor);
        mCardRoot.setCardBackgroundColor(ss);
    }

    public void setmStatusChanger(SetsRecyclerAdapter.SetStatusChanger StatusChanger) {
        mStatusChanger = StatusChanger;
    }
}
