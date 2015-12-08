package com.example.sony.smarteyeglass.extension.advancedlayouts;

/**
 * Created by uekikaori on 15/11/28.
 */
public final class State {

    /** Contains the counter value shown in the UI. */
    private int count;
    private int swipe;

    /** Used to toggle if an icon will be visible in the bitmap UI. */
    //private boolean iconImage;

    /**
     * Creates a new instance.
     */
    public State() {
        reset();
    }

    /**
     * Resets the state.
     */
    public void reset() {
        count = 0;
        swipe = 0;
        //iconImage = true;
    }

    /**
     * Updates the state. This method increments the counter and toggles the
     * icon.
     */
    public void update() {
        ++count;
        ++swipe;
        //iconImage = !iconImage;
    }

    /**
     * Returns the value of the counter.
     *
     * @return The counter.
     */
    public int getCount() {
        return count;
    }
    public int getSwipe(){
        return swipe;
    }

    /**
     * Returns the resource ID of the icon.
     *
     * @return The resource ID of the icon.
     */
    public int getBackImageId(int state){
        switch (state){
            case 0:
                return R.drawable.ui_cal;
            case 1:
                return R.drawable.ui_eiyou;
            case 2:
                return R.drawable.ui_allergy;
            default:
                return R.drawable.ui_cal;
        }
    }

    public int getCaloriesImageId(int state){
        if(state == 0){
            return R.drawable.ui_cal_normal;
        }else{
            return R.drawable.ui_cal_fat;
        }
    }

    public int getCalorieImageId(int state) {
        switch (state){
            case 0:
                return R.drawable.ui_cal_normal;
            case 10:
                return R.drawable.ui_cal_normal_m1;
            case 20:
                return R.drawable.ui_cal_normal_m2;
            case 30:
                return R.drawable.ui_cal_normal_m3;
            case 40:
                return R.drawable.ui_cal_normal_m4;
            case 50:
                return R.drawable.ui_cal_normal_m5;
            case 60:
                return R.drawable.ui_cal_normal_m6;
            case 70:
                return R.drawable.ui_cal_normal_m7;
            case 80:
                return R.drawable.ui_cal_normal_m8;
            case 90:
                return R.drawable.ui_cal_normal_m9;
            case 100:
                return R.drawable.ui_cal_normal_m10;
            case 110:
                return R.drawable.ui_cal_fat_m1;
            case 120:
                return R.drawable.ui_cal_fat_m2;
            case 130:
                return R.drawable.ui_cal_fat_m3;
            case 140:
                return R.drawable.ui_cal_fat_m4;
            case 150:
                return R.drawable.ui_cal_fat_m5;
            case 160:
                return R.drawable.ui_cal_fat_m6;
            case 170:
                return R.drawable.ui_cal_fat_m7;
            case 180:
                return R.drawable.ui_cal_fat_m8;
            case 190:
                return R.drawable.ui_cal_fat_m9;
            case 200:
                return R.drawable.ui_cal_fat_m10;
            default:
                return R.drawable.ui_cal_fat_m10;
        }

    }
    public int getNutrition1ImageId(int state) {
        switch (state){
            case 0:
                return R.drawable.ui_eiyou_m1_1;
            case 1:
                return R.drawable.ui_eiyou_m1_1;
            case 2:
                return R.drawable.ui_eiyou_m1_2;
            case 3:
                return R.drawable.ui_eiyou_m1_3;
            case 4:
                return R.drawable.ui_eiyou_m1_4;
            case 5:
                return R.drawable.ui_eiyou_m1_5;
            case 6:
                return R.drawable.ui_eiyou_m1_6;
            case 7:
                return R.drawable.ui_eiyou_m1_7;
            case 8:
                return R.drawable.ui_eiyou_m1_8;
            case 9:
                return R.drawable.ui_eiyou_m1_9;
            case 10:
                return R.drawable.ui_eiyou_m1_10;
            default:
                return R.drawable.ui_eiyou_m1_10;
        }
    }
    public int getNutrition2ImageId(int state) {
        switch (state){
            case 0:
                return R.drawable.ui_eiyou_m2_1;
            case 1:
                return R.drawable.ui_eiyou_m2_1;
            case 2:
                return R.drawable.ui_eiyou_m2_2;
            case 3:
                return R.drawable.ui_eiyou_m2_3;
            case 4:
                return R.drawable.ui_eiyou_m2_4;
            case 5:
                return R.drawable.ui_eiyou_m2_5;
            case 6:
                return R.drawable.ui_eiyou_m2_6;
            case 7:
                return R.drawable.ui_eiyou_m2_7;
            case 8:
                return R.drawable.ui_eiyou_m2_8;
            case 9:
                return R.drawable.ui_eiyou_m2_9;
            case 10:
                return R.drawable.ui_eiyou_m2_10;
            default:
                return R.drawable.ui_eiyou_m2_10;
        }
    }
    public int getNutrition3ImageId(int state) {
        switch (state){
            case 0:
                return R.drawable.ui_eiyou_m3_1;
            case 1:
                return R.drawable.ui_eiyou_m3_1;
            case 2:
                return R.drawable.ui_eiyou_m3_2;
            case 3:
                return R.drawable.ui_eiyou_m3_3;
            case 4:
                return R.drawable.ui_eiyou_m3_4;
            case 5:
                return R.drawable.ui_eiyou_m3_5;
            case 6:
                return R.drawable.ui_eiyou_m3_6;
            case 7:
                return R.drawable.ui_eiyou_m3_7;
            case 8:
                return R.drawable.ui_eiyou_m3_8;
            case 9:
                return R.drawable.ui_eiyou_m3_9;
            case 10:
                return R.drawable.ui_eiyou_m3_10;
            default:
                return R.drawable.ui_eiyou_m3_10;
        }
    }
    public int getNutrition4ImageId(int state) {
        switch (state){
            case 0:
                return R.drawable.ui_eiyou_m4_1;
            case 1:
                return R.drawable.ui_eiyou_m4_1;
            case 2:
                return R.drawable.ui_eiyou_m4_2;
            case 3:
                return R.drawable.ui_eiyou_m4_3;
            case 4:
                return R.drawable.ui_eiyou_m4_4;
            case 5:
                return R.drawable.ui_eiyou_m4_5;
            case 6:
                return R.drawable.ui_eiyou_m4_6;
            case 7:
                return R.drawable.ui_eiyou_m4_7;
            case 8:
                return R.drawable.ui_eiyou_m4_8;
            case 9:
                return R.drawable.ui_eiyou_m4_9;
            case 10:
                return R.drawable.ui_eiyou_m4_10;
            default:
                return R.drawable.ui_eiyou_m4_10;
        }
    }
    public int getNutrition5ImageId(int state) {
        switch (state){
            case 0:
                return R.drawable.ui_eiyou_m5_1;
            case 1:
                return R.drawable.ui_eiyou_m5_1;
            case 2:
                return R.drawable.ui_eiyou_m5_2;
            case 3:
                return R.drawable.ui_eiyou_m5_3;
            case 4:
                return R.drawable.ui_eiyou_m5_4;
            case 5:
                return R.drawable.ui_eiyou_m5_5;
            case 6:
                return R.drawable.ui_eiyou_m5_6;
            case 7:
                return R.drawable.ui_eiyou_m5_7;
            case 8:
                return R.drawable.ui_eiyou_m5_8;
            case 9:
                return R.drawable.ui_eiyou_m5_9;
            case 10:
                return R.drawable.ui_eiyou_m5_10;
            default:
                return R.drawable.ui_eiyou_m5_10;
        }
    }
}