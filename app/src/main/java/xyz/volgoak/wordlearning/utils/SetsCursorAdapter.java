package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;

/**
 * Created by Volgoak on 18.04.2017.
 */

public class SetsCursorAdapter extends SimpleCursorAdapter{

    private static final String[] FROM = new String[]{DatabaseContract.Sets.COLUMN_NAME};
    private static final int[] TO = new int[]{R.id.tv_name_setsadapter};
    private static final int LAYOUT = R.layout.sets_cursor_adapter;

    private Context mContext;

    public SetsCursorAdapter(Context context, Cursor cursor){
        super(context, LAYOUT, cursor, FROM, TO, 0);
        mContext = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if(holder == null){
            holder = new ViewHolder();
            holder.setNameTv = (TextView) view.findViewById(R.id.tv_name_setsadapter);
            view.setTag(holder);
        }

        holder.setNameTv.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME)));
    }

    private class ViewHolder{
        TextView setNameTv;
    }
}
