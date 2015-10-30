package com.lichen.youtubedownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.os.AsyncTask;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YoutubeInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadActivity extends ActionBarActivity {
    ProgressDialog mProgressDialog;
    public final static String EXTRA_MESSAGE="video.lichen.youtubedownloader.EXTRA_MESSAGE";
    public final static String MESSAGE="video.lichen.youtubedownloader.MESSAGE";
    private ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();
    private ArrayList<String> myDwnlist = new ArrayList<String>();
    private ListView list;
    private SimpleAdapter adapter;

    private Menu menu;
    private MenuItem bt_show;
    private MenuItem bt_delete;
    private LvAdapter mAdapter;
    private int checkNum = 0; // 记录选中的条目数量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);


        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.updateOnlineConfig(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // declare the dialog as a member field of your activity

        show();
        Intent intent=getIntent();
        String message=intent.getStringExtra(ListViewActivity.MESSAGE);
        String extrMessage=intent.getStringExtra(ListViewActivity.EXTRA_MESSAGE);
// instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(this);
        // execute this when the downloader must be fired
        final DownloadTask downloadTask= new DownloadTask(this);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadTask.cancel(true);
                dialog.dismiss();
            }
        });

        mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Background download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mProgressDialog.setMessage(extrMessage + " Downloading !");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        //mProgressDialog.show();


        if (!message.isEmpty())
            downloadTask.execute(message);


        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
                dialog.dismiss();
            }
        });



        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ListViewActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ListViewActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download, menu);
        this.menu = menu;
        bt_delete = menu.findItem(R.id.delete);
        bt_delete.setVisible(false);
        return true;
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

        if (id==R.id.edit){
            bt_show = menu.findItem(R.id.edit);
            bt_delete = menu.findItem(R.id.delete);
            if(LvAdapter.flag){
                LvAdapter.flag=false;
                bt_show.setTitle("Edit");
                bt_delete.setVisible(false);
                //bt_selectall.setVisibility(View.GONE);

                for(int i=0; i<LvAdapter.getIsSelected().size();i++){
                    LvAdapter.getIsSelected().set(i, false);
                }
                mAdapter = new LvAdapter(myDwnlist, DownloadActivity.this);
                list.setAdapter(mAdapter);
            }else{
                LvAdapter.flag = true;
                bt_show.setTitle("Cancel");
                bt_delete.setVisible(true);
                //bt_selectall.setVisibility(View.VISIBLE);
                mAdapter = new LvAdapter(myDwnlist, DownloadActivity.this);
                list.setAdapter(mAdapter);
            }
            return true;
        }

        if(id==R.id.delete){
            bt_delete = menu.findItem(R.id.delete);
            if (LvAdapter.flag) {
                // 遍历list的长度，将MyAdapter中的map值全部设为true
                for (int i = 0; i < myDwnlist.size(); ) {
                    //如果是选中状态
                    if (LvAdapter.getIsSelected().get(i)) {
                        clearCacheFolder(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/youtubedownloader/" + myDwnlist.get(i)));
                        myDwnlist.remove(i);
                        mylist.remove(i);
                        checkNum++;
                        LvAdapter.getIsSelected().remove(i);

                        continue;
                    }
                    i++;
                }
                if (checkNum==0) {
                    Toast.makeText( DownloadActivity.this, "Please choose one", Toast.LENGTH_SHORT).show();
                    return true;
                }else{
                    LvAdapter.flag=false;
                    //bt_delete.setVisibility(View.GONE);
                    bt_show.setTitle("Edit");
                    for(int i=0; i<LvAdapter.getIsSelected().size();i++){
                        LvAdapter.getIsSelected().set(i, false);
                    }
                    checkNum = 0;
                    mAdapter = new LvAdapter(myDwnlist, DownloadActivity.this);
                    list.setAdapter(mAdapter);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private int clearCacheFolder(File file) {
        int deletedFiles = 0;
        if (file!= null && !file.isDirectory()) {
            try {
                file.delete();

                } catch(Exception e) {
                    e.printStackTrace();
            }
        }
        return deletedFiles;
    }
    /**
     * Created by lichen on 11/10/15.
     */
// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        VideoInfo info;
        long last;
        String url;
        String path;
        public DownloadTask(Context context) {
            this.context = context;
        }
        long count;

        @Override
        protected String doInBackground(String... sUrl) {
            count=0;
            try {
                AtomicBoolean stop = new AtomicBoolean(false);
                Runnable notify = new Runnable() {
                    @Override
                    public void run() {
                        VideoInfo i1 = info;
                        DownloadInfo i2 = i1.getInfo();

                        // notify app or save download state
                        // you can extract information from DownloadInfo info;
                        switch (i1.getState()) {
                            case EXTRACTING:
                            case EXTRACTING_DONE:
                            case DONE:
                                if (i1 instanceof YoutubeInfo) {
                                    YoutubeInfo i = (YoutubeInfo) i1;
                                    System.out.println(i1.getState() + " " + i.getVideoQuality());
                                } else if (i1 instanceof VimeoInfo) {
                                    VimeoInfo i = (VimeoInfo) i1;
                                    System.out.println(i1.getState() + " " + i.getVideoQuality());
                                } else {
                                    System.out.println("downloading unknown quality");
                                }
                                break;
                            case RETRYING:
                                System.out.println(i1.getState() + " " + i1.getDelay());
                                count++;


                                break;
                            case DOWNLOADING:
                                long now = System.currentTimeMillis();
                                if (now - 1000 > last) {
                                    last = now;

                                    String parts = "";

                                    List<DownloadInfo.Part> pp = i2.getParts();
                                    if (pp != null) {
                                        // multipart download
                                        for (DownloadInfo.Part p : pp) {
                                            if (p.getState().equals(DownloadInfo.Part.States.DOWNLOADING)) {
                                                parts += String.format("dwn load test1: "+"Part#%d(%.2f) ", p.getNumber(), p.getCount()
                                                        / (float) p.getLength());

                                                //publishProgress((int) (p.getCount()/ (float) p.getLength()));

                                            }
                                        }
                                    }

                                    System.out.println("dwn load test2: " + String.format("%s %.2f %s", i1.getState(),
                                            i2.getCount() / (float) i2.getLength(), parts));
                                    publishProgress((int) (i2.getCount() / (float) i2.getLength()*100));



                                }
                                break;
                            default:
                                break;
                        }
                    }
                };

                URL web = new URL(sUrl[0]);

                // [OPTIONAL] limit maximum quality, or do not call this function if
                // you wish maximum quality available.
                //
                // if youtube does not have video with requested quality, program
                // will raise en exception.
                VGetParser user = null;

                // create proper html parser depends on url
                user = VGet.parser(web);

                // download maximum video quality from youtube
                //user = new YouTubeQParser(YoutubeInfo.YoutubeQuality.p480);

                // download mp4 format only, fail if non exist
                // user = new YouTubeMPGParser();

                // create proper videoinfo to keep specific video information
                info = user.info(web);

                String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/youtubedownloader";
                VGet v = new VGet(info, new File(path));
                //VGet v = new VGet(info, new File(getFilesDir().toString()));

                // [OPTIONAL] call v.extract() only if you d like to get video title
                // or download url link
                // before start download. or just skip it.
                v.extract(user, stop, notify);

                System.out.println("Title: " + info.getTitle());
                System.out.println("Download URL: " + info.getInfo().getSource());

                v.download(user, stop, notify);
                //show(); use the handle message
                handler.sendEmptyMessage(0);


            } catch (RuntimeException e) {
                e.printStackTrace();

                try{
                    String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/youtubedownloader";
                    VGet v = new VGet(info, new File(path));
                    v.download();
                }catch (Exception e1) {

                    return e1.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();

            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File download done!", Toast.LENGTH_SHORT).show();
        }
    }



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what==0)
            {
                super.handleMessage(msg);
                // 收到消息后执行handler
                show();
            }
        }
    };


    // 将数据填充到ListView中
    private void show() {


        try {

            String Path = Environment.getExternalStorageDirectory().getPath().toString()+ "/youtubedownloader";
            System.out.println("Path  : " +Path );
            File FPath = new File(Path);
            if (!FPath.exists()) {
                if (!FPath.mkdir()) {
                    System.out.println("***Problem creating Youtube folder " +Path );
                }
            }

            //绑定XML中的ListView，作为Item的容器
            list = (ListView) findViewById(R.id.MyDwnListView);

            //生成动态数组，并且转载数据
            mylist.clear();
            boolean findfile=false;
            String sdCardState = Environment.getExternalStorageState();


            if( !sdCardState.equals(Environment.MEDIA_MOUNTED ) ) {
                //displayMessage("No SD Card.");
                return;
            } else {
                myDwnlist.clear();

                lookForFilesAndDirectories(new File(Path));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                //infoListView.getSelectedView().setBackgroundColor(Color.DKGRAY);

                String item = list.getItemAtPosition(position).toString();
                Map<String, Object> map = new HashMap<>(mylist.get((int) id));
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (key.toString() == "ItemText") {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String type = "video/*";
                        Uri name = Uri.parse(Environment.getExternalStorageDirectory().getPath().toString() + "/youtubedownloader/" + val.toString());
                        intent.setDataAndType(name, type);
                        startActivity(intent);
                    }
                }
                return true;
            }
        });

        list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                view.setSelected(true);
                //infoListView.getSelectedView().setBackgroundColor(Color.DKGRAY);

                String item = list.getItemAtPosition(position).toString();
                Map<String, Object> map = new HashMap<>(mylist.get((int) id));
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (key.toString() == "ItemText") {

                    /*
                        vv=(VideoView)findViewById(R.id.videoView);
                        vv.setVideoPath(getFilesDir().toString()+"/"+val.toString());
                        vv.start();
                        Toast.makeText(getApplicationContext(), "You selected : " + val.toString(), Toast.LENGTH_SHORT).show();
                    */
                        if (val.toString().contains(".flv")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            String type = "video/*";
                            Uri name = Uri.parse(Environment.getExternalStorageDirectory().getPath().toString() + "/youtubedownloader/" + val.toString());
                            intent.setDataAndType(name, type);
                            startActivity(intent);


                        } else {

                            Intent intent = new Intent(getApplicationContext(), VideoActivity.class);

                            intent.putExtra(MESSAGE, val.toString());
                            intent.putExtra(EXTRA_MESSAGE, Environment.getExternalStorageDirectory().getPath().toString() + "/youtubedownloader");


                            startActivity(intent);
                        }
                    }

                }


                //Toast.makeText(getApplicationContext(),"You selected : " + item,Toast.LENGTH_SHORT).show();
            }
        });

    }

    // lookForFilesAndDirectories() method:

    public void lookForFilesAndDirectories(File file) {
        if( file.isDirectory() ) {
            String[] filesAndDirectories = file.list();
            for( String fileOrDirectory : filesAndDirectories) {
                File f = new File(file.getAbsolutePath() + "/" + fileOrDirectory);
                lookForFilesAndDirectories(f);
            }
        } else {
            //doSomethingWithFile(f);
            myDwnlist.add(file.getName());

            //Bitmap b= createVideoThumbnail(file.getPath());

            HashMap<String, Object> map = new HashMap<String, Object>();
            //map.put("img", b);
            map.put("ItemText", file.getName());

            mylist.add(map);
            Log.i("Filelist: ", file.getName());

            if(mylist.isEmpty()) {

            } else {

                adapter = new SimpleAdapter(this, //没什么解释
                        mylist,//数据来源
                        R.layout.my_listitem,//ListItem的XML实现

                        //动态数组与ListItem对应的子项
                        new String[] {"ItemText"},

                        //ListItem的XML文件里面的两个TextView ID
                        new int[] {R.id.ItemText});
                //添加并且显示
                list.setAdapter(adapter);

            }
        }
    }



    private Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);
            retriever.setDataSource(filePath);
            //bitmap = retriever.captureFrame();
            bitmap = retriever.getFrameAtTime(1000);

        } catch(IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
    }
}