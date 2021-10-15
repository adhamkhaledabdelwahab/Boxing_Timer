package kh.ad.boxingtimer.model;

public class Settings {
    public Settings(int ROUNDS,
                              String TRAINING_TIME,
                              String BREAK_TIME,
                              int DELAY_TIME,
                              boolean SOUND_ON,
                              int SOUND_VOLUME,
                              boolean VIBRATION_ON,
                              boolean PRELIMINARY_SOUND_ON,
                              int PRELIMINARY_TIME,
                              boolean PROXIMITY_ON,
                              boolean SHAKE_ON) {
        this.ROUNDS = ROUNDS;
        this.TRAINING_TIME = TRAINING_TIME;
        this.BREAK_TIME = BREAK_TIME;
        this.DELAY_TIME = DELAY_TIME;
        this.SOUND_ON = SOUND_ON;
        this.SOUND_VOLUME = SOUND_VOLUME;
        this.VIBRATION_ON = VIBRATION_ON;
        this.PRELIMINARY_SOUND_ON = PRELIMINARY_SOUND_ON;
        this.PRELIMINARY_TIME = PRELIMINARY_TIME;
        this.PROXIMITY_ON = PROXIMITY_ON;
        this.SHAKE_ON = SHAKE_ON;
    }

    public Settings(String favourite_Name,
                              int ROUNDS,
                              String TRAINING_TIME,
                              String BREAK_TIME,
                              int DELAY_TIME,
                              boolean SOUND_ON,
                              int SOUND_VOLUME,
                              boolean VIBRATION_ON,
                              boolean PRELIMINARY_SOUND_ON,
                              int PRELIMINARY_TIME,
                              boolean PROXIMITY_ON,
                              boolean SHAKE_ON) {
        Favourite_Name = favourite_Name;
        this.ROUNDS = ROUNDS;
        this.TRAINING_TIME = TRAINING_TIME;
        this.BREAK_TIME = BREAK_TIME;
        this.DELAY_TIME = DELAY_TIME;
        this.SOUND_ON = SOUND_ON;
        this.SOUND_VOLUME = SOUND_VOLUME;
        this.VIBRATION_ON = VIBRATION_ON;
        this.PRELIMINARY_SOUND_ON = PRELIMINARY_SOUND_ON;
        this.PRELIMINARY_TIME = PRELIMINARY_TIME;
        this.PROXIMITY_ON = PROXIMITY_ON;
        this.SHAKE_ON = SHAKE_ON;
    }

    public String getFavourite_Name() {
        return Favourite_Name;
    }

    public int getROUNDS() {
        return ROUNDS;
    }

    public String getTRAINING_TIME() {
        return TRAINING_TIME;
    }

    public String getBREAK_TIME() {
        return BREAK_TIME;
    }

    public int getDELAY_TIME() {
        return DELAY_TIME;
    }

    public boolean isSOUND_ON() {
        return SOUND_ON;
    }

    public int getSOUND_VOLUME() {
        return SOUND_VOLUME;
    }

    public boolean isVIBRATION_ON() {
        return VIBRATION_ON;
    }

    public boolean isPRELIMINARY_SOUND_ON() {
        return PRELIMINARY_SOUND_ON;
    }

    public int getPRELIMINARY_TIME() {
        return PRELIMINARY_TIME;
    }

    public boolean isPROXIMITY_ON() {
        return PROXIMITY_ON;
    }

    public boolean isSHAKE_ON() {
        return SHAKE_ON;
    }

    private String Favourite_Name;
    private final int ROUNDS;
    private final String TRAINING_TIME;
    private final String BREAK_TIME;
    private final int DELAY_TIME;
    private final boolean SOUND_ON;
    private final int SOUND_VOLUME;
    private final boolean VIBRATION_ON;
    private final boolean PRELIMINARY_SOUND_ON;
    private final int PRELIMINARY_TIME;
    private final boolean PROXIMITY_ON;
    private final boolean SHAKE_ON;
}
