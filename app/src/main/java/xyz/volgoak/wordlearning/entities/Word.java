package xyz.volgoak.wordlearning.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import xyz.volgoak.wordlearning.data.DatabaseContract.Words;

/**
 * Created by alex on 1/3/18.
 */

@Entity(tableName = Words.TABLE_NAME)
public class Word implements DataEntity {

    @ColumnInfo(name = Words._ID)
    @PrimaryKey(autoGenerate = true)
    private long id;
    @Expose
    @ColumnInfo(name = Words.COLUMN_WORD)
    private String word;
    @Expose
    @ColumnInfo(name = Words.COLUMN_TRANSLATION)
    private String translation;
    @Expose
    @ColumnInfo(name = Words.COLUMN_TRANSCRIPTION)
    private String transcription;
    @ColumnInfo(name = Words.COLUMN_TRAINED_WT)
    private int trainedWt;
    @ColumnInfo(name = Words.COLUMN_TRAINED_TW)
    private int trainedTw;
    @ColumnInfo(name = Words.COLUMN_STUDIED)
    private int studied;
    @ColumnInfo(name = Words.COLUMN_STATUS)
    private int status;

    @Ignore
    public Word(String word) {
        this.word = word;
    }

    @Ignore
    public Word(String word, String translation) {
        this.word = word;
        this.translation = translation;
        this.status = Words.IN_DICTIONARY;
    }

    public Word(long id, String word, String translation, String transcription, int trainedWt,
                int trainedTw, int studied, int status) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.transcription = transcription;
        this.trainedWt = trainedWt;
        this.trainedTw = trainedTw;
        this.studied = studied;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public int getTrainedWt() {
        return trainedWt;
    }

    public void setTrainedWt(int trainedWt) {
        this.trainedWt = trainedWt;
    }

    public int getTrainedTw() {
        return trainedTw;
    }

    public void setTrainedTw(int trainedTw) {
        this.trainedTw = trainedTw;
    }

    public int getStudied() {
        return studied;
    }

    public void setStudied(int studied) {
        this.studied = studied;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void resetProgress() {
        trainedTw = 0;
        trainedWt = 0;
        studied = 0;
    }
}
