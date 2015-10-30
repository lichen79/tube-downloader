package com.lichen.youtubedownloader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.api.services.youtube.model.Video;
import com.lichen.youtube.BasicYouTubeListItemView;
import com.lichen.youtube.PlayerActivity;
import com.lichen.youtube.YoutubeListView;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by laimiux on 11/2/14.
 */
public class ListViewActivity extends ActionBarActivity {

    @InjectView(R.id.youtube_list_view) YoutubeListView mYoutubeListView;
    //@InjectView(R.id.progress_indicator_view) ProgressBar mProgressBar;

    ProgressBar mProgressBar;

    private EditText searchInput;
    private Button searchSend;
    private Handler handler;
    private List<VideoItem> searchResults;

    List<String> ids;
    public final static String EXTRA_MESSAGE="video.lichen.youtubedownloader.EXTRA_MESSAGE";
    public final static String MESSAGE="video.lichen.youtubedownloader.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_list_view);


        ButterKnife.inject(this);

        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.updateOnlineConfig(this);

        /*  test new youtube parser
        new Thread(new Runnable(){
            @Override
            public void run() {
                YoutubeParse yp=new YoutubeParse ();
                yp.fetchBasic("-CmadmM5cOk");
            }
        }).start();
        */


        mYoutubeListView =(YoutubeListView)findViewById(R.id.youtube_list_view);
        mProgressBar=(ProgressBar)findViewById(R.id.progress_indicator_view);

        searchInput = (EditText)findViewById(R.id.search_input);


        handler = new Handler();

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    searchOnYoutube(v.getText().toString());
                    searchInput.clearFocus();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                    return false;
                }
                return true;
            }
        });

        searchSend= (Button)findViewById(R.id.search_send);

        searchSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                searchOnYoutube(searchInput.getText().toString());
                searchInput.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
            }

        });

        ids = new ArrayList<String>();

        /*

        ids.add("iX-QaNzd-0Y");
        ids.add("nCkpzqqog4k");
        ids.add("pB-5XG-DbAA");
        ids.add("QD7qIthSdkA");
        ids.add("GtKnRFNffsI");
        ids.add("IIA1XQnAv5s");
        ids.add("6vopR3ys8Kw");
        ids.add("uJ_1HMAGb4k");
        ids.add("MYSVMgRr6pw");
        ids.add("oWYp1xRPH5g");
        ids.add("qlGQoxzdwP4");
        ids.add("4ZHwu0uut3k");
        ids.add("b6dD-I7kJmM");
        ids.add("NDH1bGnNMjw");
        ids.add("rnqUBmd5xRo");
        ids.add("fJ5LaPyzaj0");
        ids.add("6teOmBuMxw4");
        ids.add("RBumgq5yVrA");

        */

        ids.add("5h0olbPE9yU");
        ids.add("P9Uj25xJ3NM");
        ids.add("teqc83kpEmE");
        ids.add("nd63_FlQq2w");
        ids.add("8vm0mzmFz_U");
        ids.add("eIxH3W4LtXA");
        ids.add("B-Wd0_IuTwo");
        ids.add("fShFVT2dtUw");


        searchOnYoutube("");

        searchInput.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        searchSend.requestFocus();
        /*
        + " "
                        +"B-Wd0_IuTwo"+ " "
                +"eIxH3W4LtXA"+ " "
                +"teqc83kpEmE"+ " "
                +"nd63_FlQq2w"+ " "
                +"P9Uj25xJ3NM"+ " "
                +"5h0olbPE9yU");


        // Show loader here

        mProgressBar.setVisibility(View.VISIBLE);

        mYoutubeListView.init(BuildConfig.YOUTUBE_BROWSER_DEV_KEY, ids, new YoutubeListView.OnListViewLoad() {
            @Override
            public void onLoad() {
                // Hide loader here.
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable error) {
                // Hide loader
                mProgressBar.setVisibility(View.GONE);

                Toast.makeText(ListViewActivity.this, "There was an error " + error, Toast.LENGTH_LONG).show();
            }
        });
        */


        mYoutubeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //遍历listview, find checkbox selected
                BasicYouTubeListItemView v = (BasicYouTubeListItemView) view;

                List<Integer> checkedId = new ArrayList<Integer>();
                for (int i = 0; i < parent.getChildCount(); i++) {// 遍历mGridView子控件找到被选中的checkbox的id
                    BasicYouTubeListItemView iv = (BasicYouTubeListItemView) parent.getChildAt(i);
                    if (iv.isChecked)
                        checkedId.add(iv.getId());
                }

                /*
                int c=mYoutubeListView.getChildCount();
                for (int i = 0; i < mYoutubeListView.getChildCount(); i++) {// 遍历mGridView子控件找到被选中的checkbox的id
                    BasicYouTubeListItemView biv = (BasicYouTubeListItemView) mYoutubeListView.getChildAt(i);
                    //Video video = (Video) mYoutubeListView.getItemAtPosition(i);

                    if (biv.isChecked)  {

                        CheckBox cb=biv.getCheckBox();
                        cb.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View arg0) {
                                                  }




                                              }
                        );}
                }
                */

                        final Video video = (Video) parent.getItemAtPosition(position);

                        showVideo(video);
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

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(ListViewActivity.this);
                searchResults = yc.search(keywords);
                handler.post(new Runnable(){
                    public void run(){
                        //update List<String> ids = new ArrayList<String>();
                        ids.clear();
                        YoutubeListView mYoutubeListView =(YoutubeListView)findViewById(R.id.youtube_list_view);
                        //mYoutubeListView.setAdapter(null);
                        mProgressBar=(ProgressBar)findViewById(R.id.progress_indicator_view);

                        if (searchResults!=null) {
                            for(Iterator<VideoItem> it    =    searchResults.iterator();    it.hasNext();    )    {
                                ids.add(it.next().getId());
                            }
                        }else {

                            Toast.makeText(ListViewActivity.this, "offline or network problem! ", Toast.LENGTH_LONG).show();
                        }
                        // Show loader here
                        mProgressBar.setVisibility(View.VISIBLE);



                        mYoutubeListView.init(BuildConfig.YOUTUBE_BROWSER_DEV_KEY, ids, new YoutubeListView.OnListViewLoad() {
                            @Override
                            public void onLoad() {
                                // Hide loader here.
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable error) {
                                // Hide loader
                                mProgressBar.setVisibility(View.GONE);

                                Toast.makeText(ListViewActivity.this, "There was an error " + error, Toast.LENGTH_LONG).show();
                            }
                        });

                        mYoutubeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                //(BasicYouTubeListItemView) test=view

                                final Video video = (Video) parent.getItemAtPosition(position);
                                showVideo(video);
                            }
                        });

                        mYoutubeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                /*
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle("Video")
                                        .setMessage("Are you sure you want to download this video?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                        */
                                return true;
                            }

                        });
                    }
                });
            }
        }.start();
    }


    private void showVideo(Video video) {
        PlayerActivity.showPlayer(this, BuildConfig.YOUTUBE_DEV_KEY, video.getId());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        try {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about) {

            Intent intent = new Intent(getApplicationContext(),AboutActivity.class);

            startActivity(intent);


            return true;
        }else if (id == R.id.menu_file) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();//
            path= getFilesDir().toString();

            Intent intent = new Intent(getApplicationContext(),DownloadActivity.class);

            intent.putExtra(MESSAGE, "");

            startActivity(intent);

        }else if (id == R.id.menu_download) {




            List<Integer> checkedId = new ArrayList<Integer>();


            mYoutubeListView =(YoutubeListView)findViewById(R.id.youtube_list_view);



            int c=mYoutubeListView.getChildCount();
            for (int i = 0; i < mYoutubeListView.getChildCount(); i++) {// 遍历mGridView子控件找到被选中的checkbox的id
                BasicYouTubeListItemView biv = (BasicYouTubeListItemView) mYoutubeListView.getChildAt(i);
                 //Video video = (Video) mYoutubeListView.getItemAtPosition(i);

                if (biv.isChecked)  {

                    CheckBox cb=biv.getCheckBox();
                    cb.setChecked(false);
                    biv.isChecked=false;
                    biv.isAnyChecked=false;
                    for(Iterator<VideoItem> it    =    searchResults.iterator();    it.hasNext();    )    {

                         //String t=it.next().getId();
                         if (biv.id.equals(it.next().getId())){
                             checkedId.add(biv.getId());

                             String path = Environment.getExternalStorageDirectory().getAbsolutePath();//
                             path= getFilesDir().toString();

                             Intent intent = new Intent(getApplicationContext(),DownloadActivity.class);

                             intent.putExtra(MESSAGE, "https://www.youtube.com/watch?v=" + biv.id);
                             intent.putExtra(EXTRA_MESSAGE, biv.title);

                             startActivity(intent);

                             break;
                         }


                    }
                    break;

                    //CheckBox checkBox = (CheckBox) layout.getChildAt(1);// checkBox在LinearLayout的第二个位置（从gridview布局文件得知），所以用1（位置从0开始）
                    //if (checkBox.isChecked())
                    //    checkedId.add(checkBox.getId());
                }


            }

            if (checkedId.size()==0) {


                Toast.makeText(ListViewActivity.this, "please Search to find your Video and choose one first! ", Toast.LENGTH_LONG).show();
                    //CheckBox checkBox = (CheckBox) layout.getChildAt(1);// checkBox在LinearLayout的第二个位置（从gridview布局文件得知），所以用1（位置从0开始）
                    //if (checkBox.isChecked())
                    //    checkedId.add(checkBox.getId());

            }


            return true;
        }
        }catch (Exception e) {
            e.printStackTrace();
        }



        return super.onOptionsItemSelected(item);
    }
}
