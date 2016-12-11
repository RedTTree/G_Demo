package com.example.g_demo;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;

import Util.TTSController;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Navi extends Base_Navi{
	private static final String TAG = "NaviActivity";
	
	//ʵ��������
	 protected TTSController mTtsManager;
	 AMapNaviView mAMapNaviView;
	 AMapNavi mAMapNavi;
	 AMap amap;
	 Button start_naiv;
	 private double lat;  
	 private double lon;
	 private double endlat;  
	 private double endlon;

	 @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.naiv);
     
      //��ȡAMapNaviʵ��
       mAMapNavi = AMapNavi.getInstance(getApplicationContext());
       mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);      
       mAMapNaviView.onCreate(savedInstanceState);
       initView();
       mAMapNavi.addAMapNaviListener(this);
   	   mAMapNavi.addAMapNaviListener(mTtsManager);
       
   }
	 
	
	private void initView() {
		// TODO �Զ����ɵķ������
		start_naiv=(Button)findViewById(R.id.start_naiv);
		amap=mAMapNaviView.getMap();
	    Intent itent=getIntent();
	    lat=itent.getDoubleExtra("startlat", 1);
	    lon=itent.getDoubleExtra("startlon", 2);
	    endlat=itent.getDoubleExtra("endlat", 3);
	    endlon=itent.getDoubleExtra("endlon", 4);
	    amap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon),19));
	    start_naiv.setOnClickListener(this);
	    mTtsManager = TTSController.getInstance(getApplicationContext());
	  	mTtsManager.init();
	
	}


	@Override
	    protected void onResume() {
	        super.onResume();
	        mAMapNaviView.onResume();
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	        mAMapNaviView.onPause();

	    }

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        mAMapNaviView.onDestroy();
	        //since 1.6.0
	        //������naviview destroy��ʱ���Զ�ִ��AMapNavi.stopNavi();
	        //������ִ��
	        mAMapNavi.stopNavi();
	        mAMapNavi.destroy();
	    }
		

	@Override
	public void onCalculateRouteFailure(int arg0) {
		// TODO �Զ����ɵķ������
		 Toast.makeText(getApplicationContext(),"�滮fail"
	                ,Toast.LENGTH_LONG).show(); 
		Log.i(TAG,"navi·���滮����+"+arg0);
	}
	@Override
	public void onCalculateRouteSuccess() {
		// TODO �Զ����ɵķ������
				 Toast.makeText(getApplicationContext(),"�ɹ��滮"
			                ,Toast.LENGTH_LONG).show(); 
				AMapNaviPath naviPath = mAMapNavi.getNaviPath();
		        if (naviPath == null){
		        	 Toast.makeText(getApplicationContext(),"fail"
		 	                ,Toast.LENGTH_LONG).show(); 
		            return;
		        }
		        Log.i(TAG,"navi·���滮�ɹ�");
		        mTtsManager.startSpeaking("·���滮�ɹ�,��ʼ����");
		        try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}		
		    	mAMapNavi.startNavi(NaviType.GPS);
		    	mAMapNavi.startGPS();
	}
	
	@Override
	public void onInitNaviFailure() {
		// TODO �Զ����ɵķ������
		Log.i(TAG,"ʧ��");
	}
	@Override
	public void onInitNaviSuccess() {
		// TODO �Զ����ɵķ������
		Log.i(TAG,"navi");
		Toast.makeText(getApplicationContext(),"�ɹ���������",Toast.LENGTH_LONG).show(); 
		Boolean isSuccess= mAMapNavi.calculateWalkRoute(new NaviLatLng(lat, lon), new NaviLatLng(endlat, endlon));
		if(isSuccess==null)
			Toast.makeText(getApplicationContext(),"ʧ��",Toast.LENGTH_LONG).show(); 
	 
	}
	
	@Override
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
		mAMapNavi.calculateWalkRoute(new NaviLatLng(lat, lon), new NaviLatLng(endlat, endlon));
	}
	
}
