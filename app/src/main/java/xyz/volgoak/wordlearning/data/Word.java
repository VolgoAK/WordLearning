package xyz.volgoak.wordlearning.data;

import com.google.gson.annotations.Expose;

/**
 * Created by alex on 1/3/18.
 */

public class Word implements Entity{
    private long id;
    @Expose
    private String word;
    @Expose
    private String translation;
    @Expose
    private String transcription;
    private int trainedWt;
    private int trainedTw;
    private int studied;
    private int status;

    public Word(String word) {
        this.word = word;
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
}
