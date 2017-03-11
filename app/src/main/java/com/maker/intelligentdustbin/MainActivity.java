package com.maker.intelligentdustbin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //声明相关变量
    private ImageButton imageButton;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private List<Dustbin_icon> dustbinlist = new ArrayList<Dustbin_icon>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启后台服务
        final Intent intent = new Intent(this,BackgroundService.class);
        startService(intent);

        imageButton = (ImageButton) findViewById(R.id.click);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        findViews(); //获取控件
        init_dustbin_list();//初始化侧滑list
        toolbar.setTitle("Intelligent dustbin");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Toast.makeText(MainActivity.this,"open",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Toast.makeText(MainActivity.this,"close",Toast.LENGTH_SHORT).show();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //设置菜单列表
        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        SideListViewAdapter arrayAdapter = new SideListViewAdapter(MainActivity.this,R.layout.dustbin_list,dustbinlist);
        lvLeftMenu.setAdapter(arrayAdapter);
        //设置list点击监听事件
        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dustbin_icon dustbin_icon = dustbinlist.get(position);
                if (dustbin_icon.getName().equals("关于软件")) {
                    About.changeAboutPage(MainActivity.this);
                }
                else if (dustbin_icon.getName().equals("取消推送")){
                    stopService(intent);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent i = new Intent(MainActivity.this,AlarmReceiver.class);
                    PendingIntent pi =  PendingIntent.getBroadcast(MainActivity.this,0,i,0);
                    alarmManager.cancel(pi);
                    Toast.makeText(MainActivity.this,"系统推送服务已取消",Toast.LENGTH_SHORT).show();
                }else {
                    DustbinContentActivity.actionStart(MainActivity.this, dustbin_icon.getName());
                }
            }
        });

    }
    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
    }
    private void init_dustbin_list(){
        Dustbin_icon first_dustbin = new Dustbin_icon("1号垃圾桶",R.drawable.dustbin_icon1);
        dustbinlist.add(first_dustbin);
        Dustbin_icon second_dustbin = new Dustbin_icon("2号垃圾桶",R.drawable.dustbin_icon2);
        dustbinlist.add(second_dustbin);
        Dustbin_icon third_dustbin = new Dustbin_icon("3号垃圾桶",R.drawable.dustbin_icon3);
        dustbinlist.add(third_dustbin);
        Dustbin_icon fouth_dustbin = new Dustbin_icon("4号垃圾桶",R.drawable.dustbin_icon4);
        dustbinlist.add(fouth_dustbin);
        Dustbin_icon cancel = new Dustbin_icon("取消推送",R.drawable.cancel);
        dustbinlist.add(cancel);
        Dustbin_icon about = new Dustbin_icon("关于软件",R.drawable.about);
        dustbinlist.add(about);
    }
}
