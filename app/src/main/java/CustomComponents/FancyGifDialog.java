package CustomComponents;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;

import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

import com.ikkeware.rambooster.R;


/**
 * Created by Shashank Singhal on 06/01/2018.
 */

public class FancyGifDialog {
    public static class Builder {
        private String title, message,htmlMessage,positiveBtnText, negativeBtnText, pBtnColor, nBtnColor;
        private Activity activity;
        private FancyGifDialogListener pListener, nListener;
        private boolean cancel;
        int gifImageResource,messageColor,titleAlign,titleColor,titleFontStyle,messageStyle,messageAlign;
        float messageSize,titleSize;
        private Context context;

        public Builder(Context context){this.context=context;}

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }


        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessageColor(int messageColor){
            this.messageColor=messageColor;
            return this;
        }

        public Builder setMessageSize(float messageSize){
            this.messageSize=messageSize;
            return this;
        }


        public Builder setMessageStyle(int messageStyle){
            this.messageStyle=messageStyle;
            return this;
        }


        public Builder setMessageAlign(int messageAlign){
            this.messageAlign=messageAlign;
            return this;
        }

        public Builder setTitleColor(int titleColor){
            this.titleColor=titleColor;
            return this;
        }

        public Builder setTitleSize(float titleSize){
            this.titleSize=titleSize;
            return this;
        }

        public Builder setTitleFontStyle(int titleFontStyle){
            this.titleFontStyle=titleFontStyle;
            return this;
        }

        public Builder setTitleAlign(int titleAlign){
            this.titleAlign=titleAlign;
            return this;
        }


        public Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public Builder setPositiveBtnBackground(String pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }


        public Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public Builder setNegativeBtnBackground(String nBtnColor) {
            this.nBtnColor = nBtnColor;
            return this;
        }

        //set Positive listener
        public Builder OnPositiveClicked(FancyGifDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        //set Negative listener
        public Builder OnNegativeClicked(FancyGifDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }



        public Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder setGifResource(int gifImageResource) {
            this.gifImageResource = gifImageResource;
            return this;
        }

        public FancyGifDialog build() {
            TextView message1, title1;
            Button nBtn, pBtn;
            ImageView gifImageView;
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(cancel);
            dialog.setContentView(R.layout.fancygifdialog);


            //getting resources
            title1 = dialog.findViewById(R.id.title);
            message1 = dialog.findViewById(R.id.message);
            nBtn = dialog.findViewById(R.id.negativeBtn);
            pBtn = dialog.findViewById(R.id.positiveBtn);
            gifImageView = dialog.findViewById(R.id.gifImageView);
            gifImageView.setImageResource(gifImageResource);

            title1.setText(title);
            message1.setText(message);


            if(messageColor!=0){
                message1.setTextColor(messageColor);
            }

            if(messageStyle!=0){
                message1.setTypeface(message1.getTypeface(),messageStyle);
            }

            if(messageSize!=0){ message1.setTextSize(messageSize); }

            if(titleSize!=0){ title1.setTextSize(titleSize); }

            if(messageAlign!=0){ message1.setTextAlignment(messageAlign);
                if(messageAlign==View.TEXT_ALIGNMENT_VIEW_START || messageAlign==View.TEXT_ALIGNMENT_TEXT_START){
                    message1.setPadding(8,message1.getPaddingTop(),message1.getPaddingRight(),message1.getPaddingBottom());}

                else if((messageAlign==View.TEXT_ALIGNMENT_VIEW_END || messageAlign==View.TEXT_ALIGNMENT_TEXT_END)){
                    message1.setPadding(message1.getPaddingLeft(),message1.getPaddingTop(),message1.getPaddingRight(),8);
                }
                else{
                    message1.setPadding(message1.getPaddingLeft(),message1.getPaddingTop(),message1.getPaddingRight(),message1.getPaddingBottom());
                }


            }

            if(titleAlign!=0){ title1.setTextAlignment(titleAlign);
                if(titleAlign==View.TEXT_ALIGNMENT_VIEW_START || titleAlign==View.TEXT_ALIGNMENT_TEXT_START){
                    title1.setPadding(5,title1.getPaddingTop(),title1.getPaddingRight(),title1.getPaddingBottom());}

                else if((titleAlign==View.TEXT_ALIGNMENT_VIEW_END || titleAlign==View.TEXT_ALIGNMENT_TEXT_END)){
                    title1.setPadding(title1.getPaddingLeft(),title1.getPaddingTop(),title1.getPaddingRight(),5);
                }
                else{
                    title1.setPadding(title1.getPaddingLeft(),title1.getPaddingTop(),title1.getPaddingRight(),title1.getPaddingBottom());
                }


            }

            if(titleColor!=0){ title1.setTextColor(titleColor); }

            if(titleFontStyle!=0){ title1.setTypeface(title1.getTypeface(), titleFontStyle); }

            if (positiveBtnText != null) {
                pBtn.setText(positiveBtnText);
                if (pBtnColor != null) {
                    GradientDrawable bgShape = (GradientDrawable) pBtn.getBackground();
                    bgShape.setColor(Color.parseColor(pBtnColor));
                }

                pBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pListener != null) pListener.OnClick();
                        dialog.dismiss();
                    }

                });
            } else {
                pBtn.setVisibility(View.GONE);
            }
            if (negativeBtnText != null) {
                nBtn.setText(negativeBtnText);
                nBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (nListener != null) nListener.OnClick();
                        dialog.dismiss();
                    }
                });
                if (nBtnColor != null) {
                    GradientDrawable bgShape = (GradientDrawable) nBtn.getBackground();
                    bgShape.setColor(Color.parseColor(nBtnColor));
                }
            } else {
                nBtn.setVisibility(View.GONE);
            }

            dialog.show();

            return new FancyGifDialog();

        }
    }

}