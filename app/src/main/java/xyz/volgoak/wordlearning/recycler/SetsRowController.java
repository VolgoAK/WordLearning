package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;

/**
 * Created by Volgoak on 16.08.2017.
 */

public class SetsRowController extends RowController{
    private View mRoot;

    private static boolean isColumnsBound = false;
    private static int columnIdIndex = -1;
    private static int columnNameIndex = -1;
    private static int columnDescriptionIndex = -1;
    private static int columnStatusIndex = -1;
    private static int columnImageUrlIndex = -1;

    private TextView setNameTv;
    private TextView setDescriptionTv;
    private TextView firstLetterTv;
    private ImageButton addButton;
    private CircleImageView civ;

    private SetsRecyclerAdapter.SetStatusChanger mStatusChanger;

    public SetsRowController(View view, Context context, CursorRecyclerAdapter adapter){
        super(view, context, adapter);

        mRoot = view;
        setNameTv = (TextView) view.findViewById(R.id.tv_name_setsadapter);
        addButton = (ImageButton) view.findViewById(R.id.ibt_add_sets);
        setDescriptionTv = (TextView) view.findViewById(R.id.tv_description_setsadapter);
        civ = (CircleImageView) view.findViewById(R.id.civ_sets);
        firstLetterTv = (TextView) view.findViewById(R.id.tv_first_letter_sets);
    }

    @Override
    public void bindController(final Cursor cursor) {
        if(!isColumnsBound) bindCollumns(cursor);
        setNameTv.setText(cursor.getString(columnNameIndex));
        setDescriptionTv.setText(cursor.getString(columnDescriptionIndex));

        int setStatus = cursor.getInt(columnStatusIndex);
        int drawableId = setStatus == DatabaseContract.Sets.IN_DICTIONARY ? R.drawable.ic_added : R.drawable.ic_add;
        addButton.setImageDrawable(ContextCompat.getDrawable(mContext, drawableId));

        final long id = cursor.getLong(columnIdIndex);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStatusChanger != null){
                    mStatusChanger.changeSetStatus(id);
                }
            }
        });

        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SetsRowController", "onClick: clicked " + getAdapterPosition());
                mAdapter.onControllerClick(v, getAdapterPosition());
            }
        });
    }

    public void bindCollumns(Cursor cursor){
        columnIdIndex = cursor.getColumnIndex(DatabaseContract.Sets._ID);
        columnNameIndex = cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME);
        columnDescriptionIndex = cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_DESCRIPTION);
        columnStatusIndex = cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_STATUS);
        columnImageUrlIndex = cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_IMAGE_URL);
        isColumnsBound = true;
    }

    public void setmStatusChanger(SetsRecyclerAdapter.SetStatusChanger StatusChanger) {
        mStatusChanger = StatusChanger;
    }
}
