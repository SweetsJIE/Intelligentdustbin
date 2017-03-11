package com.maker.intelligentdustbin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sweets on 17/2/18.
 * 此处GPS数据采用软件手动修正，Web端和APP端的地图不同
 * 同一个经纬度不同地点
 */

public class DustbinContentActivity extends Activity {

    private ImageButton back;
    private ListView listView;
    private List<String> list = new ArrayList<String>();
    private MapView mapview = null;
    private AMap aMap;
    //设置图钉选项
    private MarkerOptions options = new MarkerOptions();
    private AMapLocation amapLocation;
    public static final int GET_INFORMATION = 0;
    //list适配器
    private ArrayAdapter<String> adapter = null;
    private String id, able, disable, backup, others, longitude_buf, latitude_buf;
    //double latitude=2302.494,longitude=11321.678;
    double latitude, longitude;
    boolean change_flag = true;

    //启动DustbinContentActivity活动
    public static void actionStart(Context context, String text) {
        Intent intent = new Intent(context, DustbinContentActivity.class);
        intent.putExtra("text", text);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dustbin_content);

        back = (ImageButton) findViewById(R.id.BackButton);
        listView = (ListView) findViewById(R.id.dustbin_use);
        mapview = (MapView) findViewById(R.id.map_view);

        //获取数据
        getInformation();

        //获取Intent传入内容
        String DustbinContent = getIntent().getStringExtra("text");
        //刷新DustbinContentFragment界面
        DustbinContentFragment dustbinContentFragment = (DustbinContentFragment) getFragmentManager().findFragmentById(R.id.dustbin_content_fragment);
        dustbinContentFragment.refresh(DustbinContent);

        //测试使用线程
//        if (!DustbinContent.equals("1号垃圾桶")) {
//            init_dustbin_use("null", "null", "null", "null", "                " +
//                    "                                   " +
//                    "                                     ");
//            refreshData(null,null,null,null);
//            refreshAddress(null);
//            timer.cancel();
//        }

        //返回键监听事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //4个桶的状态list
        init_dustbin_use("null", "null", "null", "null", "                " +
                "                                   " +
                "                                     ");
        adapter = new ArrayAdapter<String>(DustbinContentActivity.this, android.R.layout.simple_expandable_list_item_1, list);
        listView.setAdapter(adapter);

        //地图定位
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapview.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapview.getMap();
        }
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //将地图移动到定位点
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(dealPosition(latitude) - 0.00259666, dealPosition(longitude) + 0.005337)));
        //添加图钉
        options.position(new LatLng(dealPosition(latitude) - 0.00259666, dealPosition(longitude) + 0.005337));
        aMap.addMarker(options);
        //高德逆地理编码
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();

                Log.i("address", "change");

                refreshAddress(address);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系


        //double latitude = Double.parseDouble(latitude_buf);
        //double longitude = Double.parseDouble(longitude_buf);
        //LatLonPoint lp =new LatLonPoint(latitude,longitude);
        LatLonPoint lp = new LatLonPoint(dealPosition(latitude) - 0.00259666, dealPosition(longitude) + 0.005337);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

        //设置线程启动时间间隔
        timer.schedule(refreshDataTask, 0, 4000);
    }


    //刷新地址数据
    private final void refreshAddress(String address) {
        list.set(4, "地点：" + address);
        adapter.notifyDataSetChanged();
    }

    //刷新垃圾桶容量数据
    private final void refreshData(String bucket1, String bucket2, String bucket3, String bucket4) {
        list.set(0, "可回收压缩桶已经使用：             " + bucket1 + "%         ");
        list.set(1, "可回收不可压缩桶已经使用：     " + bucket2 + "%         ");
        list.set(2, "其他桶已经使用：                         " + bucket3 + "%         ");
        list.set(3, "备用桶已经使用：                         " + bucket4 + "%         ");
        adapter.notifyDataSetChanged();
    }

    private void init_dustbin_use(String bucket1, String bucket2, String bucket3, String bucket4, String address) {
        list.add("可回收压缩桶已经使用：             " + bucket1 + "%         ");
        list.add("可回收不可压缩桶已经使用：     " + bucket2 + "%         ");
        list.add("其他桶已经使用：                         " + bucket3 + "%         ");
        list.add("备用桶已经使用：                         " + bucket4 + "%         ");
        list.add("地址：" + address.toString());
    }

    //定时器设置
    Timer timer = new Timer();
    public TimerTask refreshDataTask = new TimerTask() {
        @Override
        public void run() {
            getInformation();
        }
    };

    //WebService线程
    private void getInformation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //组装反向地理编码的接口地址
                    StringBuilder url = new StringBuilder();
                    url.append("http://jasperwong.cn:8082/SmartBicycle_Server/trash/search?id=1");
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url.toString());
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity entity = httpResponse.getEntity();

                    String response = EntityUtils.toString(entity);
                    response = response.substring(6);

                    JSONObject jsonObject = new JSONObject(response);
                    id = jsonObject.getString("id");
                    able = jsonObject.getString("able");
                    disable = jsonObject.getString("disable");
                    backup = jsonObject.getString("backup");
                    others = jsonObject.getString("others");
                    longitude_buf = jsonObject.getString("longitude");
                    latitude_buf = jsonObject.getString("latitude");
                    //Log.i("longitude_buf",longitude_buf);
                    //Log.i("latitude_buf",latitude_buf);
                    //if (change_flag){
                    latitude = Double.parseDouble(latitude_buf);
                    longitude = Double.parseDouble(longitude_buf);
//                        String buf = Double.toString(dealPosition(latitude));
//                        Log.i("Position1",buf);
//                        buf = Double.toString(longitude);
//                        Log.i("Position2",buf);
                    //}
//                    Log.i("TAG", "id" + id+"\n"+"able" + able+"\n"+"disable" + disable+
//                            "\n"+"backup" + backup+"\n"+"others" + others+
//                            "\n"+"longitude" + longitude+"\n"+"latitude" + latitude+"\n");
                    Message message = new Message();
                    message.what = GET_INFORMATION;
                    //message.obj = address;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //异步消息处理
    public android.os.Handler handler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_INFORMATION:
                    refreshData(able, disable, backup, others);
                    break;
                default:
                    break;
            }
        }

    };

    private double dealPosition(double Position) {
        double Positon_Degree, Position_Cent, Positon_Sec, Position_buf;
        Positon_Degree = (int) (Position / 100);
        Position = Position - Positon_Degree * 100;
        Position_Cent = (int) Position;
        Positon_Sec = Position - Position_Cent;
        Position_buf = (double) (Positon_Degree + (Position_Cent / 60) + (Positon_Sec / 60));
        String buf = Double.toString(Position_buf);
//        Log.i("Positon_Degree",buf);
//        buf = Double.toString(Position_Cent);
//        Log.i("Position_Cent",buf);
//        buf = Double.toString(Positon_Sec);
//        Log.i("Positon_Sec",buf);
        //buf = Double.toString(Position);
        Log.i("Position", buf);
        return (Positon_Degree + (Position_Cent / 60) + (Positon_Sec / 60));
    }


//    1.更新数据 url格式:jasperwong.cn:8082/SmartBicycle_Server/trash/upload?id=1&able1=1&disable1=1&backup1=1&others1=1&longitude=1&latitude=1 成功更新返回"success"
//            2.拉去数据 url格式:jasperwong.cn:8082/SmartBicycle_Server/trash/search?id=1


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }
}
