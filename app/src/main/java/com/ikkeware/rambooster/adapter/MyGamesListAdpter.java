package com.ikkeware.rambooster.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.ikkeware.rambooster.model.AppDetail;
import com.ikkeware.rambooster.R;
import com.ikkeware.rambooster.utils.DataEventUtils;

import java.util.List;

import static com.ikkeware.rambooster.model.AppTheme.getCurrentApplicationTheme;
import static com.ikkeware.rambooster.utils.AnimationUtils.createIntroCircularTransitionAnimation;
import static com.ikkeware.rambooster.utils.AnimationUtils.translateObjectInXAxisAnimation;
import static com.ikkeware.rambooster.utils.DatabaseUtils.deleteApplication;
import static com.ikkeware.rambooster.utils.DatabaseUtils.getIdByApplicationPackage;
import static com.ikkeware.rambooster.utils.Utils.shortenText;

public class MyGamesListAdpter extends RecyclerView.Adapter<MyGamesListAdpter.MyViewHolder> {

    private List<AppDetail> appDetails;
    public String textColor="#2D2C2C";
    private int currentTheme=0;
    private DataEventUtils onItemDeleted;

    Context context;

    public MyGamesListAdpter(List<AppDetail> appDetails, Context context, DataEventUtils onItemDeleted) {
        this.appDetails = appDetails;
        this.context=context;
        this.onItemDeleted=onItemDeleted;
        currentTheme=getCurrentApplicationTheme();



    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtAppName;
        ImageButton btnPlay, btnSettings;
        ImageView imgAppIcon;
        LinearLayout containerDelete;
        ImageView imgDelete;
        ConstraintLayout itemRowContainer;
        FrameLayout separator;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtAppName=  itemView.findViewById(R.id.txtAppNameSelect);
            btnPlay=itemView.findViewById(R.id.btnPlay);
            btnSettings=itemView.findViewById(R.id.btnSettings);
            imgAppIcon=itemView.findViewById(R.id.imgAppIconSelect);
            containerDelete=itemView.findViewById(R.id.containerDelete);
            imgDelete=itemView.findViewById(R.id.imgDelete);
            itemRowContainer=itemView.findViewById(R.id.itemRowContainer);
            separator=itemView.findViewById(R.id.separator);

            itemView.setOnTouchListener(new OnSwipeTouchListener(itemView.getContext(), itemView){
                boolean canSwipeLeft=false;

                @Override
                void onSwipeRight() {
                    translateObjectInXAxisAnimation(itemRowContainer,200,110f,new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            btnPlay.setVisibility(View.GONE);
                            btnSettings.setVisibility(View.GONE);
                            separator.setVisibility(View.GONE);
                            containerDelete.setVisibility(View.VISIBLE);
                            itemRowContainer.setTranslationX(107f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            containerDelete.setElevation(10f);
                            containerDelete.setBackgroundColor((getCurrentApplicationTheme()==R.style.LightAppTheme)?
                                    itemView.getContext().getColor(R.color.darkActionBarColor): itemView.getContext().getColor(android.R.color.white));
                            createIntroCircularTransitionAnimation(containerDelete, 250, new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    super.onAnimationCancel(animation);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    containerDelete.setBackgroundColor(Color.parseColor(
                                            (getCurrentApplicationTheme()==R.style.LightAppTheme)?"#F8F8F8":"#2D2C2C"));
                                    createIntroCircularTransitionAnimation(containerDelete,200);

                                }
                            });


                            //containerDelete.setBackgroundColor(Color.parseColor("#F8F8F8"));

                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            canSwipeLeft=true;
                            Animation fadeOut=AnimationUtils.loadAnimation(itemView.getContext(),R.anim.fade_out);
                            fadeOut.setFillAfter(true);
                            fadeOut.setDuration(100);

                            btnPlay.startAnimation(fadeOut);
                            btnSettings.startAnimation(fadeOut);
                            separator.setVisibility(View.GONE);


                        }
                    });
                }

                @Override
                void onSwipeLeft() {
                    if(itemRowContainer.getTranslationX()>0)
                        translateObjectInXAxisAnimation(itemRowContainer,200,itemRowContainer.getTranslationX()-110f,new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationCancel(Animator animation) {
                                super.onAnimationCancel(animation);
                                btnPlay.setVisibility(View.VISIBLE);
                                btnSettings.setVisibility(View.VISIBLE);
                                itemView.findViewById(R.id.separator).setVisibility(View.VISIBLE);
                                containerDelete.setVisibility(View.GONE);
                                itemRowContainer.setTranslationX(0);
                                canSwipeLeft=false;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                                //containerDelete.setBackgroundColor(Color.parseColor("#F8F8F8"));

                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                Animation fadeIn=AnimationUtils.loadAnimation(itemView.getContext(),R.anim.fade_in);
                                fadeIn.setFillAfter(true);
                                fadeIn.setDuration(100);
                                containerDelete.setElevation(0);
                                containerDelete.setBackgroundColor((getCurrentApplicationTheme()==R.style.LightAppTheme)?
                                        itemView.getContext().getColor(R.color.darkActionBarColor): itemView.getContext().getColor(android.R.color.white));
                                containerDelete.setVisibility(View.GONE);
                                btnPlay.startAnimation(fadeIn);
                                btnSettings.startAnimation(fadeIn);
                                itemView.findViewById(R.id.separator).setVisibility(View.VISIBLE);



                            }
                        });
                    canSwipeLeft=false;
                }

                @Override
                void onSwipeTop() {

                }

                @Override
                void onSwipeBottom() {
                }
            });


        }

    }

    @NonNull
    @Override
    public MyGamesListAdpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        FrameLayout row = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_activity_recycler_view_row, parent, false);

        return new MyGamesListAdpter.MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        holder.txtAppName.setText(shortenText(appDetails.get(position).getAppName(),28));


        if(getCurrentApplicationTheme()==R.style.LightAppTheme) {
            holder.txtAppName.setTextColor(Color.parseColor(textColor));
            holder.imgDelete.setImageTintList(ColorStateList.valueOf(Color.parseColor("#A227FF")));
            holder.containerDelete.setBackgroundColor(Color.parseColor("#F8F8F8"));
            holder.itemRowContainer.setBackgroundColor(Color.WHITE);
        }
        else{
            holder.txtAppName.setTextColor(Color.parseColor(textColor));
            holder.imgDelete.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            holder.containerDelete.setBackgroundColor(Color.parseColor("#2D2C2C"));
            holder.itemRowContainer.setBackgroundColor(Color.parseColor("#3B3B3B"));
        }

        //Icon
        holder.imgAppIcon.setImageDrawable(appDetails.get(position).getAppIconImage());
        if(holder.imgAppIcon.getVisibility()==View.INVISIBLE){holder.imgAppIcon.setVisibility(View.VISIBLE);}

        //Button play
        holder.btnPlay.setOnClickListener(appDetails.get(position).getListener());
        if(holder.btnPlay.getVisibility()==View.INVISIBLE){holder.btnPlay.setVisibility(View.VISIBLE);}

        holder.btnSettings.setOnClickListener(appDetails.get(position).getAutotaskListener());

        if(holder.itemRowContainer.getTranslationX()>0){
            holder.itemRowContainer.setTranslationX(holder.itemRowContainer.getTranslationX()-holder.itemRowContainer.getTranslationX());
        }

        if(holder.separator.getVisibility()==View.GONE){
            holder.separator.setVisibility(View.VISIBLE);
        }



        holder.containerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Change the color of all components to animate
                holder.itemRowContainer.setBackgroundColor((getCurrentApplicationTheme()==R.style.LightAppTheme)?
                        holder.itemRowContainer.getContext().getColor(R.color.darkActionBarColor): holder.itemRowContainer.getContext().getColor(android.R.color.white));

                holder.txtAppName.setTextColor((getCurrentApplicationTheme()==R.style.LightAppTheme)?
                        holder.itemRowContainer.getContext().getColor(R.color.darkActionBarColor): holder.itemRowContainer.getContext().getColor(android.R.color.white));

                holder.imgAppIcon.setVisibility(View.INVISIBLE);

                holder.btnPlay.setVisibility(View.INVISIBLE);

                createIntroCircularTransitionAnimation(holder.itemRowContainer, 500, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        appDetails.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        appDetails.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());

                        holder.containerDelete.setVisibility(View.GONE);
                        //Has to set visibility to GONE because of Recycler View bug
                        //Container still appeared when item deleted and theme changed

                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);


                    }
                });
                deleteApplication(getIdByApplicationPackage(appDetails.get(position).getPackName(),context),context);
                onItemDeleted.onItemDeleted();

            }
        });


    }
    private static void setBottomMargin(View view, int bottomMargin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin);
            view.requestLayout();
        }
    }




    @Override
    public int getItemCount() {
        return appDetails.size();
    }


}


class OnSwipeTouchListener implements View.OnTouchListener {


    private final GestureDetector gestureDetector;
    Context context;
    OnSwipeTouchListener(Context ctx, View mainView) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        mainView.setOnTouchListener(this);
        context = ctx;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return gestureDetector.onTouchEvent(event);
    }
    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
    void onSwipeRight() {
        this.onSwipe.swipeRight();
    }
    void onSwipeLeft() {
        this.onSwipe.swipeLeft();
    }
    void onSwipeTop() {
        this.onSwipe.swipeTop();
    }
    void onSwipeBottom() {
        this.onSwipe.swipeBottom();
    }
    interface onSwipeListener {
        void swipeRight();
        void swipeTop();
        void swipeBottom();
        void swipeLeft();
    }
    onSwipeListener onSwipe;
}






