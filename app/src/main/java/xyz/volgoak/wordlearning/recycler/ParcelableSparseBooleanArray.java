package xyz.volgoak.wordlearning.recycler;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

/**
 * Created by Volgoak on 20.08.2017.
 */

public class ParcelableSparseBooleanArray extends SparseBooleanArray implements Parcelable {

    static final Parcelable.Creator<SparseBooleanArray> CREATOR = new Parcelable.Creator<SparseBooleanArray>(){
        @Override
        public SparseBooleanArray createFromParcel(Parcel source) {
            return new ParcelableSparseBooleanArray(source);
        }

        @Override
        public SparseBooleanArray[] newArray(int size) {
            return new SparseBooleanArray[0];
        }
    };

    public ParcelableSparseBooleanArray(){}

    public ParcelableSparseBooleanArray(Parcel source){
        int size = source.readInt();
        for(int a = 0; a < size; a++){
            put(source.readInt(),(boolean) source.readValue(null));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());
        for(int a = 0; a < size(); a++){
            dest.writeInt(keyAt(a));
            dest.writeValue(keyAt(a));
        }
    }
}
