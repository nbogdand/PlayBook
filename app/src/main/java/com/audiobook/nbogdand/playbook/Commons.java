package com.audiobook.nbogdand.playbook;

import android.util.Log;

public class Commons {
    private static boolean isPlaying = true;
    private static boolean serviceWasStopped = false;

    public static void setIsPlaying(boolean bool){
        isPlaying = bool;
    }

    public static boolean getIsPlaying(){
        return isPlaying;
    }

    public static void setServiceWasStopped(boolean bool){
        serviceWasStopped = bool;
    }

    public static boolean getServiceWasStopped(){
        return serviceWasStopped;
    }

}
