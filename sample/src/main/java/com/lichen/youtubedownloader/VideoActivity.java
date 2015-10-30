package com.lichen.youtubedownloader;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class VideoActivity extends ActionBarActivity {
    private VideoView mVideoView;

    private MediaController mController;
    MediaMetadataRetriever mMetadataRetriever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //取消顶部标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setOrientation(LinearLayout.HORIZONTAL);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();
        Intent intent=getIntent();
        String message=intent.getStringExtra(DownloadActivity.MESSAGE);
        String extra_message=intent.getStringExtra(DownloadActivity.EXTRA_MESSAGE);
        mVideoView = (VideoView) findViewById(R.id.myplaysurface);

        mMetadataRetriever = new MediaMetadataRetriever();


        startPlayback(extra_message+"/"+message);
        //Intent intent1=new Intent();

        //intent1.setType(extra_message+"/*");
        //intent1.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent1, "Video File to Play"), 0);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    public void startPlayback(String videoPath)
    {
        //mMetadataRetriever.setDataSource(videoPath);

        Uri uri = Uri.parse(videoPath);
        mVideoView.setVideoURI(uri);

        mController = new MediaController(this, false);
        mVideoView.setMediaController(mController);
        mVideoView.requestFocus();
        mVideoView.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_video, menu);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                Uri sourceUri = data.getData();
                String source = getPath(sourceUri);

                startPlayback(source);
            }
        }
    }

    public String getPath(Uri uri)
    {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor == null)
        {
            return uri.getPath();
        } else
        {
            cursor.moveToFirst();

            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

            return cursor.getString(idx);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
