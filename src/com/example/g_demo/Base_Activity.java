package com.example.g_demo;

import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class Base_Activity extends Activity implements OnClickListener,AMapLocationListener,AMap.OnMapClickListener ,OnPoiSearchListener,OnRouteSearchListener{

	 protected MapView mMapView ;
	// Ҫ�����poi�����ؼ���
	 protected String keyWord = "";
	// ����
	protected String cityCode = "����";
	// ����ʱ������
	 private ProgressDialog progDialog = null;
	// poi���صĽ��
	 protected PoiResult poiResult; 
	// ��ǰҳ�棬��0��ʼ����
	 protected int currentPage = 0;
	// Poi��ѯ������
	 protected PoiSearch.Query query;
	// POI����
	 protected PoiSearch poiSearch;



	//����AMapLocationClient�����
		 //��λ�����
	 AMapLocationClient mLocationClient = null;
		 //��λ����/** ����mLocationOption���� */  
	 AMapLocationClientOption mLocationOption = null;
		 //��λ������
	 OnLocationChangedListener mListener = null;

		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLoction();
	}
	
	public void initLoction() {
		// TODO �Զ����ɵķ������
		mLocationClient=new AMapLocationClient(this);
		mLocationOption=new AMapLocationClientOption();
		mLocationClient.setLocationListener(this);
		//���ö�λģʽΪ�߾���ģʽ��Battery_SavingΪ�͹���ģʽ��Device_Sensors�ǽ��豸ģʽ  
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		//���ö�λ���,��λ����,Ĭ��Ϊ2000ms  
	    mLocationOption.setInterval(36000);
	    //�����Ƿ�ֻ��λһ��,Ĭ��Ϊfalse
	    mLocationOption.setOnceLocation(false);
	    //�����Ƿ񷵻ص�ַ��Ϣ��Ĭ�Ϸ��ص�ַ��Ϣ��
	    mLocationOption.setNeedAddress(true);
	    //�����Ƿ�ǿ��ˢ��WIFI��Ĭ��Ϊǿ��ˢ��
	    mLocationOption.setWifiActiveScan(true);
	    //�����Ƿ�����ģ��λ��,Ĭ��Ϊfalse��������ģ��λ��
	    mLocationOption.setMockEnable(false);
	    //����λ�ͻ��˶������ö�λ����
	    mLocationClient.setLocationOption(mLocationOption);
	    //������λ
	   mLocationClient.startLocation();
	}

	/**
	 * ��ʾ���ȿ�
	 */
	protected void showProgressDialog() {
		if (progDialog == null)
		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("��������:\n" + keyWord);
		progDialog.show();
	}
	
	/**
	 * ���ؽ��ȿ�
	 */
	protected void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * ��poi����������Ϣͨ��TTF��������Ĭ�ϵ�һ����
	 */
	protected void ReadInformation(PoiItem poiItem) {
		poiItem.getTitle();
		//���Ƭ��
		poiItem.getSnippet();
		//���Name
	    poiItem.getDistance();
	}	
	
	
	@Override
	  protected void onResume() {
	    super.onResume();;
	    //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onResume();;
	  }
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onDestroy();
	  }
	
	@Override
	protected void onPause() {
	    super.onPause();
	    //��activityִ��onPauseʱִ��mMapView.onPause ()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onPause();
	    }
	
	@Override
	public void onLocationChanged(AMapLocation arg0) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
		// TODO �Զ����ɵķ������
		
	}


}
