package com.lichen.youtube;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoFileDetails;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by laimiux on 11/5/14.
 */
public class BasicYouTubeListItemView extends RelativeLayout {

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private CheckBox mCheckBoxView;

    // Request
    private RequestCreator mRequest;
    private int mWidth;
    private int mHeight;

    public boolean isChecked;
    public static boolean isAnyChecked=false;
    public String id;
    public String title;
    public BasicYouTubeListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImageView = (ImageView) findViewById(R.id.youtube_video_thumbnail);
        mTitleTextView = (TextView) findViewById(R.id.youtube_video_title);
        mDescriptionTextView = (TextView) findViewById(R.id.youtube_video_description);
        mCheckBoxView = (CheckBox) findViewById(R.id.youtube_video_download);
        mCheckBoxView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mCheckBoxView.isChecked() && !isAnyChecked){
                    isChecked=true;
                    isAnyChecked=true;
                }else if (!mCheckBoxView.isChecked() && !isAnyChecked){
                    isChecked=false;
                    mCheckBoxView.setChecked(false);
                }else if (mCheckBoxView.isChecked() && isAnyChecked){
                    isChecked=false;
                    isAnyChecked=true;
                    mCheckBoxView.setChecked(false);
                }else{
                    isChecked=false;
                    isAnyChecked=false;
                    mCheckBoxView.setChecked(false);
                }
            }

        });
        final Resources resources = getResources();
        mWidth = resources.getDimensionPixelSize(R.dimen.youtube_image_width);
        mHeight = resources.getDimensionPixelSize(R.dimen.youtube_image_height);

    }

    public void bindView(Picasso picasso, Video video) {

        try{
        String tit=video.getSnippet().getTitle();
        VideoFileDetails vd= (VideoFileDetails)video.getFileDetails();
            String des=video.getSnippet().getDescription();
        if (vd !=null)
            des=vd.getDurationMs() + " mins "
                + humanReadableByteCount(vd.getFileSize().longValue(), true) + " "
                + video.getSnippet().getDescription();

            mTitleTextView.setText(tit);
            mDescriptionTextView.setText(des);
            id=video.getId();
            title=tit;
            mCheckBoxView.setChecked(false);
            isAnyChecked=false;
            mCheckBoxView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (mCheckBoxView.isChecked() && !isAnyChecked){
                        isChecked=true;
                        isAnyChecked=true;
                    }else if (!mCheckBoxView.isChecked() && !isAnyChecked){
                        isChecked=false;
                        mCheckBoxView.setChecked(false);
                    }else if (mCheckBoxView.isChecked() && isAnyChecked){
                        isChecked=false;
                        isAnyChecked=true;
                        mCheckBoxView.setChecked(false);
                    }else{
                        isChecked=false;
                        isAnyChecked=false;
                        mCheckBoxView.setChecked(false);
                    }
                }

            });

        }catch(Exception e){
            e.printStackTrace();
        }




        mRequest = picasso.load(video.getSnippet().getThumbnails().getMedium().getUrl());

        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mRequest != null) {
            mRequest.resize(mWidth, mHeight).noFade().centerCrop().into(mImageView);
            mRequest = null;
        }
    }

    public CheckBox getCheckBox()
    {
        return mCheckBoxView;
    }


}
