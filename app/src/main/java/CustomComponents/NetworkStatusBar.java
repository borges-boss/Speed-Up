package CustomComponents;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;

import android.util.Log;
import android.view.View;

import com.ikkeware.rambooster.R;


public class NetworkStatusBar extends View {

    //Atributos do componente
    private int progress=0;
    private int progressColor;
    private int backgroundProgressColor;

    private final static int default_progress_color=Color.rgb(255,255,255);
    private final static int default_background_progress_color=Color.rgb(0,0,0);

    private Canvas myCanvas;
    private RectF myRect;
    private Paint backgroundPaint;
    private Paint progressColorPaint;


    private int rectLeft=0,rectTop=4,rectRight=320,rectBottom=16;
    private final int progressMiniRectWidth=15, interpolation=5;//Valor expresso em DP's
    float rectCutLeft=0,rectCutRight;


    public NetworkStatusBar(Context context) {
        super(context);
    }

    public NetworkStatusBar(Context context, AttributeSet atributeSet){
        super(context,atributeSet);
        TypedArray array=context.getTheme().obtainStyledAttributes(atributeSet, R.styleable.NetworkStatusBar,0,0);
        initByAtributes(array);
        initPainters();
        myRect=new RectF(rectLeft,rectTop,rectRight,10);
        System.err.println(rectLeft+" rect right: "+rectRight+" top: "+rectTop);


    }


    public void initByAtributes(TypedArray attr){

        progress=attr.getInt(R.styleable.NetworkStatusBar_progress,0);
        progressColor=attr.getColor(R.styleable.NetworkStatusBar_progressColor,default_progress_color);
        //backgroundProgressColor=attr.getColor(R.styleable.NetworkStatusBar_backgroundProgressColor,default_background_progress_color);

    }

    public void initPainters(){
        backgroundPaint=new Paint();
        progressColorPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        //backgroundPaint.setColor(default_background_progress_color);
        progressColorPaint.setColor(progressColor);

    }


    public float pixelToDp(float pixel,Context context){

        float density=context.getResources().getDisplayMetrics().density;
        float dp=pixel/density;

        return dp;
    }

    private int dpToPixel(int dp,Context con){

        float density=con.getResources().getDisplayMetrics().density;
        float pixels= dp*density;


        return (int)pixels;
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public void setProgress(int progress){
        this.progress=progress;
        invalidate();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        int heightSize =MeasureSpec.getSize(heightMeasureSpec);
        int widthSize= MeasureSpec.getSize(widthMeasureSpec)+getPaddingRight();

        System.err.println("Component Height: "+heightMeasureSpec);
        System.err.println("Component Width: "+widthSize);

        System.err.println("Mode: "+MeasureSpec.getMode(widthMeasureSpec));

        rectRight=widthSize;

        if(MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.EXACTLY){
            rectBottom=heightSize;
        }
        else if(MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.AT_MOST){
            rectBottom=Math.min(rectBottom,heightSize);
        }

        myRect.set(rectLeft,rectTop,rectRight,rectBottom);

    }

    @Override
    protected void onDraw(Canvas canvas){

        int maxProgress=rectRight;
        int leftClipValor=dpToPixel(progressMiniRectWidth,getContext())+1;
        int rightClipValor=dpToPixel(interpolation,getContext());

        if(progress>=0 && progress<10){
            maxProgress=leftClipValor*2-1;

        }
        else if(progress>=10 && progress<15){

            maxProgress=leftClipValor*3-1;
        }
        else if(progress>=15 && progress<20){

            maxProgress=leftClipValor*3-1;
        }
        else if(progress>=20 && progress<30){

            maxProgress=leftClipValor*4-1;
        }
        else if(progress>=30 && progress<40){

            maxProgress=leftClipValor*4-1;
        }
        else if(progress>=40 && progress<50){

            maxProgress=leftClipValor*5-1;
        }
        else if(progress>=50 && progress<60){

            maxProgress=leftClipValor*5-1;
        }
        else if(progress>=60 && progress<70){

            maxProgress=leftClipValor*5-1;
        }
        else if(progress>=70 && progress<80){

            maxProgress=leftClipValor*5-1;
        }
        else if(progress>=80 && progress<90){

            maxProgress=leftClipValor*5-1;
        }
        else{
            maxProgress=rectRight;

        }

        //Calculo feito para medir a area de corte do retangulo
        rectCutLeft=rectLeft+leftClipValor;//Rect left bound clip
        rectCutRight=rectCutLeft+rightClipValor;//Rect right bound clip

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(rectCutLeft, rectTop, rectCutRight, rectBottom);
        }
        else{
            canvas.clipRect(rectCutLeft, rectTop, rectCutRight, rectBottom, Region.Op.DIFFERENCE);
        }


        while(rectCutRight<=maxProgress) {
            rectCutLeft = rectCutRight + leftClipValor;
            rectCutRight = rectCutLeft + rightClipValor ;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutRect(rectCutLeft, rectTop, rectCutRight, rectBottom);
            }
            else{
                canvas.clipRect(rectCutLeft, rectTop, rectCutRight, rectBottom, Region.Op.DIFFERENCE);
            }

        }


        //Elimina o excesso
        canvas.clipRect(rectCutLeft, rectTop, rectRight, rectBottom,Region.Op.DIFFERENCE);


        canvas.drawRect(myRect, progressColorPaint);





    }

}
