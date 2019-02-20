package com.audiobook.nbogdand.playbook;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.audiobook.nbogdand.playbook.Services.AudioService;

public class CommonBindingsUtils {


    @BindingAdapter("minus30Click")
    public static void setMinus30Click(final ImageButton imageButton,final Context context){

        final ObjectAnimator buttonAnimator = setUpAnimation(imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAnimator.start();

                Intent minus30s = new Intent(context,AudioService.class);
                minus30s.setAction(Constants.NOTIFY_MINUS);
                context.startService(minus30s);

            }
        });

    }

    @BindingAdapter(value = {"activity","applicationContext"}, requireAll = true)
    public static void setActivity(final ImageButton imageButton, final AppCompatActivity activity,
                                     final Context applicationContext){

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,0.5f,0.8f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,0.5f,0.8f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA,0f,1f);
        final ObjectAnimator buttonAnimator = ObjectAnimator.ofPropertyValuesHolder(imageButton,scaleX,scaleY,alpha);
        buttonAnimator.setDuration(350);
        buttonAnimator.setInterpolator(new OvershootInterpolator());


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAnimator.start();

                PlaySongViewModel playSongViewModel = ViewModelProviders.of(activity).get(PlaySongViewModel.class);
                playSongViewModel.pauseSong(applicationContext);
            }
        });

    }

    @BindingAdapter("plus30Click")
    public static void setPlus30Click(final ImageButton imageButton, final Context context){

        final ObjectAnimator buttonAnimator = setUpAnimation(imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAnimator.start();

                Intent plus30s = new Intent(context,AudioService.class);
                plus30s.setAction(Constants.NOTIFY_PLUS);
                context.startService(plus30s);
            }
        });


    }

    @BindingAdapter("changeDrawable")
    public static void setChangeDrawable(final ImageView imageView,Drawable drawable){

        if(Commons.getIsPlaying()){
            imageView.setImageDrawable(drawable);
        }else{
            imageView.setImageDrawable(drawable);
        }

    }


    @BindingAdapter("volumeDownClick")
    public static void setVolumeDownClick(ImageView imageView,final Context context){

        final ObjectAnimator animator = setUpAnimation(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.start();

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1,0);

            }
        });

    }

    @BindingAdapter("volumePlusClick")
    public static void setVolumePlusClick(ImageView imageView,final Context context){

        final ObjectAnimator animator = setUpAnimation(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.start();

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1,0);

            }
        });

    }


    public static ObjectAnimator setUpAnimation(View view){
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,0.5f,1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,0.5f,1f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA,0f,1f);
        final ObjectAnimator buttonAnimator = ObjectAnimator.ofPropertyValuesHolder(view,scaleX,scaleY,alpha);
        buttonAnimator.setDuration(350);
        buttonAnimator.setInterpolator(new OvershootInterpolator());

        return buttonAnimator;
    }


}
