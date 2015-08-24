package com.ckt.francis.navigationmap.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.ckt.francis.navigationmap.R;
import com.ckt.francis.navigationmap.util.JsonParser;
import com.ckt.francis.navigationmap.util.Utils;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends Activity {
    private static final String MESSAGE = "_message";
    // 语音识别对象
    private SpeechRecognizer mAsr;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private RecognizerDialog mDialog = null;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 定位的核心类:LocationClient
    private LocationClient mLocClient;
    private boolean isFirstLoc = true;
    private MyLocationListenner myListener = null;
    private String mMessage = null;

    private static final String APP_FOLDER_NAME = "BNSDKDemo";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private String authinfo = null;
    private String mSDCardPath = null;
    private BNRoutePlanNode sNode = null;
    private BNRoutePlanNode eNode = null;
    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            if (result != null) {
                List<PoiInfo> poiInfos = result.getAllPoi();
                LatLng eLatLng = Utils.bd09_To_Gcj02(new LatLng(poiInfos.get(0).location.latitude, poiInfos.get(0).location.longitude));
                eNode = new BNRoutePlanNode(eLatLng.longitude, eLatLng.latitude,
                        poiInfos.get(0).name, null);
            } else {
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
            }
            if (BaiduNaviManager.isNaviInited()) {
                routeplanToNavi();
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
            Toast.makeText(MainActivity.this, result + "1", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d("test", "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                Log.d("test", "recognizer result：" + result.getResultString());
//                String text ;
//                if("cloud".equalsIgnoreCase(mEngineType)){
//                    //text = JsonParser.parseGrammarResult(result.getResultString());
//                }else {
//                   // text = JsonParser.parseLocalGrammarResult(result.getResultString());
//                }
            } else {
                Log.d("test", "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip("onError Code："	+ error.getErrorCode());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent _intent = getIntent();
        if (_intent != null) {
            mMessage = _intent.getStringExtra(MESSAGE);
        }
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            mMessage = intent.getStringExtra(MESSAGE);
        }
        init();
    }

    private void init() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));

        mLocClient = new LocationClient(this);
        // 定位的回调接口
        myListener = new MyLocationListenner();
        mLocClient.registerLocationListener(myListener);

        // 设置mLocationClient数据,如是否打开GPS,使用LocationClientOption类.
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        // 设置发起定位请求的间隔. < 1000,则为app主动请求定位,>=1000,则为定时定位模式
        option.setScanSpan(100);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        //option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        //option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        mLocClient.setLocOption(option);
        //初始化poi搜索核心类
        mPoiSearch = PoiSearch.newInstance();
        //注册poi搜索回调接口
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        if (initDirs()) {
            initNavi();
        }
        initListener();

        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mAsr = SpeechRecognizer.createRecognizer(this, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mAsr.setParameter(SpeechConstant.DOMAIN, "iat");
        mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin ");
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。
        mAsr.startListening(mRecognizerListener);

        //1.创建RecognizerDialog对象
        mDialog = new RecognizerDialog(this, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解,必须添加以下参数设置,设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(mRecognizerDialogListener);
        //4.显示dialog,接收语音输入

    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        mMapView.onResume();

        mLocClient.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocClient.stop();
        mPoiSearch.destroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private boolean initDirs() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            mSDCardPath = Environment.getExternalStorageDirectory().toString();
        }
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        BaiduNaviManager.getInstance().setNativeLibraryPath(mSDCardPath + "/BaiduNaviSDK_SO");
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        //Toast.makeText(MainActivity.this,authinfo,Toast.LENGTH_LONG).show();
                    }

                    public void initSuccess() {
                        // Toast.makeText(MainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        if (mPoiSearch != null && mMessage != null) {
                            mPoiSearch.searchInCity((new PoiCitySearchOption())
                                    .city("深圳市")
                                    .keyword(mMessage));
                        }
                    }

                    public void initStart() {
                    }

                    public void initFailed() {
                    }
                }, null /*mTTSCallback*/);
    }

    private void initListener() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
/*                if (BaiduNaviManager.isNaviInited()) {
                    routeplanToNavi();
                }*/

                if(mDialog != null){
                    mDialog.show();
                }
            }
        });
        findViewById(R.id.buttons).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isFirstLoc = true;
                mLocClient.requestLocation();
            }
        });
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.WGS84);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.GCJ02);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09_MC);
    }

    private void routeplanToNavi() {
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
         定位成功之后的回调接口
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 获取定位精度
                    .direction(100) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude()) // 获取纬度坐标
                    .longitude(location.getLongitude())// 获取精度坐标
                    .build();
            mBaiduMap.setMyLocationData(locData);
            //坐标转换
            LatLng sLatLng = Utils.bd09_To_Gcj02(new LatLng(location.getLatitude(), location.getLongitude()));
            sNode = new BNRoutePlanNode(sLatLng.longitude, sLatLng.latitude, "", null);
            //sNode = new BNRoutePlanNode(sLatLng.longitude,sLatLng.longitude,"",null);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u, 1000);
            }
        }
    }

    /*
        启动导航的监听器
     */
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(MainActivity.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, sNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {

        }
    }

    private void showTip(String plainDescription) {
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        if (BaiduNaviManager.isNaviInited()) {
            routeplanToNavi();
        }
        if (mPoiSearch != null && resultBuffer.toString() != null) {
            mPoiSearch.searchInCity((new PoiCitySearchOption())
                    .city("深圳市")
                    .keyword(resultBuffer.toString()));
        }
        Toast.makeText(this,"" + resultBuffer.toString(),Toast.LENGTH_LONG).show();
    }
}
