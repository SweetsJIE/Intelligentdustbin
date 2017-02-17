package com.maker.intelligentdustbin;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //声明相关变量
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private String[] lvs = {"首页", "1号垃圾桶", "2号垃圾桶", "3号垃圾桶","4号垃圾桶"};
   // private ArrayAdapter arrayAdapter;
    private List<Dustbin_icon> dustbinlist = new ArrayList<Dustbin_icon>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews(); //获取控件
        init_dustbin_list();
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
                Toast.makeText(MainActivity.this,dustbin_icon.getName(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
    }
    private void init_dustbin_list(){
        Dustbin_icon main_page = new Dustbin_icon("主页",R.drawable.main_page);
        dustbinlist.add(main_page);
        Dustbin_icon first_dustbin = new Dustbin_icon("1号垃圾桶",R.drawable.dustbin_icon1);
        dustbinlist.add(first_dustbin);
        Dustbin_icon second_dustbin = new Dustbin_icon("2号垃圾桶",R.drawable.dustbin_icon2);
        dustbinlist.add(second_dustbin);
        Dustbin_icon third_dustbin = new Dustbin_icon("3号垃圾桶",R.drawable.dustbin_icon3);
        dustbinlist.add(third_dustbin);
        Dustbin_icon fouth_dustbin = new Dustbin_icon("4号垃圾桶",R.drawable.dustbin_icon4);
        dustbinlist.add(fouth_dustbin);
        Dustbin_icon about = new Dustbin_icon("关于",R.drawable.about);
        dustbinlist.add(about);
    }
}
