package com.ikkeware.rambooster.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.LinearInterpolator;

public class AnimationUtils {


    public void translateObjectInXAxisAnimation(float distance){

    }


    public static void translateObjectInXAxisAnimation(View view, long duration, float distance, AnimatorListenerAdapter animationListener){
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationX", distance);
        //animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(150);
        animation.addListener(animationListener);
        animation.start();
    }
    public static void translateObjectInXAxisAnimation(View view, long duration, float distance){
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationX", distance);
        //animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(150);
        animation.start();
    }
    public static void createIntroCircularTransitionAnimation(View view, long duration, Animator.AnimatorListener animatorListener){

        // get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius);
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());
        anim.addListener(animatorListener);


        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();


    }
    public static void createIntroCircularTransitionAnimation(View view, long duration){

        // get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius);
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();


    }



}
