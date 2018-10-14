package tw.dora.myviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;

public class MyView extends View {

    private LinkedList<LinkedList<HashMap<String,Float>>> lines;
    private LinkedList<LinkedList<HashMap<String,Float>>> recycle;

    private GestureDetector gd;
    Paint paint = new Paint();

    private boolean clearFlag = false;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        lines = new LinkedList<>();
        recycle = new LinkedList<>();

        gd = new GestureDetector(context,new MyGestureListner());

        setBackgroundColor(Color.YELLOW);


        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(4);

    }

    private class MyGestureListner extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            Log.v("brad","onDown");
            return true; //super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
            String status = "";
            if(Math.abs(vX)>Math.abs(vY)+1000){
                if(vX>0) status = "Right";
                else  status = "Left";
            }else if (Math.abs(vY)>Math.abs(vX)+1000){
                if(vY>0) status = "Down";
                else  status = "Up";
            }
            Log.v("brad","onFling:"+vX+","+vY+":"+status);

            return super.onFling(e1, e2, vX, vY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.v("brad", "onDraw");



        for (LinkedList<HashMap<String, Float>> line : lines) {
            for (int i = 1; i < line.size(); i++) {
                    HashMap<String, Float> p0 = line.get(i - 1);
                    HashMap<String, Float> p1 = line.get(i);
                    canvas.drawLine(p0.get("x"), p0.get("y"), p1.get("x"), p1.get("y"), paint);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float ex = event.getX();
        float ey = event.getY();
        HashMap<String,Float> point = new HashMap<>();
        point.put("x",ex);point.put("y",ey);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.v("brad","down: "+ex+" x" + ey);
                LinkedList<HashMap<String,Float>> line = new LinkedList<>();
                line.add(point);
                lines.add(line);
                recycle.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.v("brad","move: "+ex+" x" + ey);
                lines.getLast().add(point);
                break;


        }

        //只要呼叫invalidate(),就會自動呼叫onDraw()
        invalidate();
        return true;//super.onTouchEvent(event);

//        return gd.onTouchEvent(event);
    }

    public void clear(){
        while(lines.size()>0){
            recycle.add(lines.getLast());
            lines.removeLast();
            invalidate();
            clearFlag = true;
        }

//        recycle = lines;
//        lines.clear();
//        invalidate();
    }

    public void undo(){
        if(lines.size()>0){
            recycle.add(lines.getLast());
            lines.removeLast();
            invalidate();
        }
    }

    public void redo(){
        if (clearFlag){
            while(recycle.size()>0) {
                lines.add(recycle.getLast());
                recycle.removeLast();

            }
            invalidate();
        }else {
            if (recycle.size() > 0) {
                lines.add(recycle.getLast());
                recycle.removeLast();
                clearFlag = false;
                invalidate();

            }
        }
    }


}
