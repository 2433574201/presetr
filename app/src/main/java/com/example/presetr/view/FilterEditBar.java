package com.example.presetr.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.presetr.R;
import com.example.presetr.activity.DiyActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterEditBar extends ConstraintLayout implements View.OnTouchListener {

    private static final String TAG = "FilterEditBar";

    @BindView(R.id.filter_edit_bar_slider_gray)
    ImageView filterEditBarSliderGray;
    @BindView(R.id.filter_edit_bar_slider_circle)
    ImageView filterEditBarSliderCircle;
    @BindView(R.id.filter_edit_bar_progress)
    TextView filterEditBarProgress;
    @BindView(R.id.filter_edit_bar_cancel)
    ImageView filterEditBarCancel;
    @BindView(R.id.filter_edit_bar_confirm)
    ImageView filterEditBarConfirm;

    private Context mContext;
    private DiyActivity activity;
    private int progress;
    private float last_x;
    private float dis;
    private float curr_dis;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        this.progress = progress;
        filterEditBarProgress.setText(progress + "");
        filterEditBarProgress.setTranslationX(
                (filterEditBarSliderGray.getWidth() - filterEditBarSliderCircle.getWidth()) / 100.0f * progress);
        filterEditBarSliderCircle.setTranslationX(
                (filterEditBarSliderGray.getWidth() - filterEditBarSliderCircle.getWidth()) / 100.0f * progress);
        curr_dis = (filterEditBarSliderGray.getWidth() - filterEditBarSliderCircle.getWidth()) / 100.0f * progress;
    }

    public FilterEditBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        activity = (DiyActivity) context;
        View view = LayoutInflater.from(context).inflate(R.layout.filter_edit_bar, this);
        ButterKnife.bind(this, view);
        filterEditBarSliderCircle.setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        filterEditBarProgress.setText(progress + "");
        filterEditBarProgress.setTranslationX(
                (filterEditBarSliderGray.getWidth() - filterEditBarSliderCircle.getWidth()) / 100.0f * progress);
        filterEditBarSliderCircle.setTranslationX(
                (filterEditBarSliderGray.getWidth() - filterEditBarSliderCircle.getWidth()) / 100.0f * progress);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.filter_edit_bar_slider_circle) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    filterEditBarProgress.setVisibility(VISIBLE);
                    last_x = event.getRawX();
                    dis = curr_dis;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX();
                    float change_dis = x - last_x;
                    if (dis + change_dis > (filterEditBarSliderGray.getRight() - filterEditBarSliderCircle.getRight())) {
                        change_dis = filterEditBarSliderGray.getRight() - filterEditBarSliderCircle.getRight() - dis;
                    }
                    if (dis + change_dis < filterEditBarSliderGray.getLeft() - filterEditBarSliderCircle.getLeft()) {
                        change_dis = filterEditBarSliderGray.getLeft() - filterEditBarSliderCircle.getLeft() - dis;
                    }
                    filterEditBarSliderCircle.setTranslationX(dis + change_dis);
                    filterEditBarProgress.setTranslationX(dis + change_dis);
                    float s = filterEditBarSliderCircle.getLeft() + filterEditBarSliderCircle.getTranslationX() - filterEditBarSliderGray.getLeft();
                    float t = filterEditBarSliderGray.getWidth() - filterEditBarSliderCircle.getWidth() * 1.0f;
                    progress = (int) ((s / t) * 100);
                    filterEditBarProgress.setText(progress + "");
                    curr_dis = dis + change_dis;

                    activity.getFilter().setBackOld(0);
                    activity.getFilter().setIntensity(progress/100.0f);
                    activity.getDiyImage().setFilter(activity.getFilter());
                    break;
                case MotionEvent.ACTION_UP:
                    filterEditBarProgress.setVisibility(INVISIBLE);
                    break;
            }
            return true;
        }
        return false;
    }

    @OnClick({R.id.filter_edit_bar_cancel, R.id.filter_edit_bar_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.filter_edit_bar_cancel:
                activity.getFilter().setBackOld(1);
                activity.getDiyImage().setFilter(activity.getFilter());
                int last = FilterSelectLayout.PIC_POSITION;
                FilterSelectLayout.PIC_POSITION = 0;
                activity.getFilterSelectCustomLayout().getPicAdapter().notifyItemChanged(last, 1);
                activity.getFilterSelectCustomLayout().getPicAdapter().notifyItemChanged(0, 1);
                setVisibility(INVISIBLE);
                break;
            case R.id.filter_edit_bar_confirm: ;
                activity.setCur_bitmap(activity.getDiyImage().getGPUImage().getBitmapWithFilterApplied());
                activity.getDiyImage().getGPUImage().deleteImage();
                activity.getDiyImage().setImage(activity.getCur_bitmap());
                setVisibility(INVISIBLE);
                break;
        }
    }
}
