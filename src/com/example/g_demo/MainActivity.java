package com.example.g_demo;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;

import Util.JsonParser;
import Util.SRController;
import Util.TTSController;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Base_Activity {

	private static final String TAG = "MainActivity"; 
	private double lat;  
    private double lon;
    private double prelat;  
    private double prelon;
    private boolean change=false;
	private double endlat;  
	private double endlon;
    private LatLonPoint lp ;
    private LatLonPoint endlp;
    private Boolean isFlag=true;
    AMap amap;

    //˫��
    long[] mHits = new long[2];
//////////////////////////////////////////////�����������//////////////// 
    
    private Boolean isShowDialog=true;
	private Toast mToast;
 // ������д����
 	private SpeechRecognizer mIat;
 	// ������дUI
 	private RecognizerDialog mIatDialog;
 // ��������
 	private String mEngineType = SpeechConstant.TYPE_CLOUD;
 	int ret = 0; // �������÷���ֵ
	// ��HashMap�洢��д���
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	/**
	 * ��ʼ����������
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("��ʼ��ʧ�ܣ������룺" + code);
			}
		}
	};

	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// �˻ص���ʾ��sdk�ڲ�¼�����Ѿ�׼�����ˣ��û����Կ�ʼ��������
			showTip("��ʼ˵��");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips��
			// �����룺10118(��û��˵��)��������¼����Ȩ�ޱ�������Ҫ��ʾ�û���Ӧ�õ�¼��Ȩ�ޡ�
			// ���ʹ�ñ��ع��ܣ���ǣ���Ҫ��ʾ�û�������ǵ�¼��Ȩ�ޡ�
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			// �˻ص���ʾ����⵽��������β�˵㣬�Ѿ�����ʶ����̣����ٽ�����������
			showTip("����˵��");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results);

			if (isLast) {
				// TODO ���Ľ��
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("��ǰ����˵����������С��" + volume);
			Log.d(TAG, "������Ƶ���ݣ�"+data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// ���´������ڻ�ȡ���ƶ˵ĻỰid����ҵ�����ʱ���Ựid�ṩ������֧����Ա�������ڲ�ѯ�Ự��־����λ����ԭ��
			// ��ʹ�ñ����������ỰidΪnull
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// ��ȡjson����е�sn�ֶ�
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

		poitext.setText(resultBuffer.toString());
		mTtsManager.startSpeaking("����Ϊ"+resultBuffer.toString()+"��Ҫ��ʼ��˫����ʼ������ť");
		
	}
	/**
	 * ��дUI������
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/**
		 * ʶ��ص�����.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};
	
///////////////////////////////////////////////////////////////////////////

    
	private AMapLocationClient mLocationClient = null;
	 //��ť
	private EditText poitext;
	private Button star_Recognizer;
	private Button start_poi;
    private Button start_naiv;
   // private Button next;
    private TextView site;
	//ʵ��������
	 protected TTSController mTtsManager;
	// protected SRController mSRCManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMapView=(MapView)findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);	
		String text = MainActivity.this.getString(R.string.app_id);
        SpeechUtility.createUtility(MainActivity.this, "appid=" + text);
		initView();	
		
		
	//////////////////////////////////////////////////////////////////
		// ��ʼ��ʶ����UIʶ�����
				// ʹ��SpeechRecognizer���󣬿ɸ��ݻص���Ϣ�Զ�����棻
				mIat = SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);
				
				// ��ʼ����дDialog�����ֻʹ����UI��д���ܣ����贴��SpeechRecognizer
				// ʹ��UI��д���ܣ������sdk�ļ�Ŀ¼�µ�notice.txt,���ò����ļ���ͼƬ��Դ
				mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
	////////////////////////////////////////////////////////////////
				
	}
	
	

	private void initView() {
		// TODO �Զ����ɵķ������
		//��ʼ��
		
		poitext=(EditText)findViewById(R.id.keyWord);
		star_Recognizer=(Button)findViewById(R.id.recognizerButton);
		start_naiv=(Button)findViewById(R.id.start_naiv);
		start_poi=(Button)findViewById(R.id.searchButton);
		//next=(Button)findViewById(R.id.nextButton);
		site=(TextView)findViewById(R.id.site);
		//��õ�ͼ����
		amap=mMapView.getMap();
		//��������
		star_Recognizer.setOnClickListener(this);
		start_naiv.setOnClickListener(this);
		start_poi.setOnClickListener(this);
		//next.setOnClickListener(this);
		mLocationClient=super.mLocationClient;
		mLocationClient.startLocation();
		 //��ʼ������
		mTtsManager = TTSController.getInstance(getApplicationContext());
		mTtsManager.init();
		//mSRCManager=SRController.getInstance(getApplicationContext());
		//mSRCManager.init();
        //�ȳ�ʼ�� ��ֹendlp����NAVI�е�ʱ��Ϊ�� ��������
        endlp=new LatLonPoint(0,0);
	}

	private void searchButton() {
		// TODO �Զ����ɵķ������
		if(TextUtils.isEmpty(poitext.getText())){
			mTtsManager.startSpeaking("δ��������,�޷���ʼ����");
			Toast.makeText(MainActivity.this, "����������", Toast.LENGTH_SHORT).show();
		}
		else{
			keyWord=poitext.getText().toString();
			doSearchQuery();
		}
	}
	
	/**
    **��һҳ
    *
	private void nextButton() {
		// TODO �Զ����ɵķ������
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;
				query.setPageNum(currentPage);// ���ò��һҳ
				poiSearch.searchPOIAsyn();
			} else {
				Toast.makeText(MainActivity.this, "û��", Toast.LENGTH_SHORT).show();
			}
		}
	}
	**/
	
	private void doSearchQuery() {
		// TODO �Զ����ɵķ������
		showProgressDialog();// ��ʾ���ȿ�
		currentPage = 0;
		// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query = new PoiSearch.Query(keyWord, "", cityCode);
		//����ÿҳ��෵�ض�����poiitem
		query.setPageSize(20);
		// ���ò��һҳ
		query.setPageNum(currentPage);
		
		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 5000, true));
			// ������������Ϊ��lp��ΪԲ�ģ�����Χ5000�׷�Χ
			poiSearch.searchPOIAsyn();// �첽����
		}
	}

	@Override
	public void onPoiSearched(PoiResult result, int rcode) {
		// TODO �Զ����ɵķ������
		dissmissProgressDialog();
		mTtsManager.startSpeaking("��ʼ����"); 
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		if (rcode == 1000) {
			if (result != null && result.getQuery() != null) {
				if (result.getQuery().equals(query)) {
					poiResult = result;
					//poi����
					List<PoiItem> poiItems = poiResult.getPois();
					mTtsManager.startSpeaking("������"+poiItems.size()
			        +"������������Ĭ��Ŀ�ĵ�Ϊ"+poiItems.get(0).getTitle()+"��������"
					+"����λ��Ϊ"+poiItems.get(0).getSnippet()+"��������"
					+"����Ϊ"+poiItems.get(0).getDistance()+"�� ��������"
			        +"���Ҫ��ʼ�����밴�Ҳ�");
					if (poiItems != null && poiItems.size() > 0) {
						amap.clear();// ����֮ǰ��ͼ��
						//poiͼ��
						PoiOverlay poiOverlay = new PoiOverlay(amap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
						endlp=poiItems.get(0).getLatLonPoint();
						}  else {
						Toast.makeText(MainActivity.this, "�޽��", Toast.LENGTH_SHORT).show();
				  }
				 }
				}
			}
		else {
			Toast.makeText(MainActivity.this, "����", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onLocationChanged(AMapLocation  amapLocation) {
		  if (amapLocation != null) {
	            if (amapLocation.getErrorCode() == 0) {
	            	  Log.i(TAG,"��λ�ɹ�");
	            	 lat = amapLocation.getLatitude();  
	                 lon = amapLocation.getLongitude();
	                 if(prelat!=lat||prelon!=lon){
	                	 change=true;
	                	 prelat=lat;
	                	 prelon=lon;
	                 }
	                 if(change){
	                	 mTtsManager.startSpeaking("��ǰλ��Ϊ"+amapLocation.getCity()+"��"
                                 +amapLocation.getAoiName());
	                	 change=false;
	                 }
	                 
	                 lp = new LatLonPoint(lat, lon);
	                 cityCode=amapLocation.getCity();
	        		 amap.clear();
	                 amap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 19));  
	                 MarkerOptions markerOptions = new MarkerOptions();  
	                 markerOptions.position(new LatLng(lat, lon));  
	                 markerOptions.title("��ǰλ��");  
	                 markerOptions.visible(true);  
	                 BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ma_pass));  
	                 markerOptions.icon(bitmapDescriptor);  
	                 amap.addMarker(markerOptions); 
	                 site.setText("  "+amapLocation.getCity()+"  "
                                      +amapLocation.getAoiName()+"  "
                                      +amapLocation.getAdCode());
	            }
	            else{
	            	  Log.e("AmapError", "location Error, ErrCode:"
	                          + amapLocation.getErrorCode() + ", errInfo:"
	                          + amapLocation.getErrorInfo());

	                  Toast.makeText(getApplicationContext(), "��λʧ��"+ amapLocation.getErrorCode(), Toast.LENGTH_LONG).show();           
	            }
	            }
		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
		
		System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
		mHits[mHits.length - 1] = SystemClock.uptimeMillis();
		//˫���¼���ʱ����500ms
		if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            //˫�������Ĳ���
			//do 
			doubleClick(v); 
		}
		else
		{
			switch(v.getId()){
			case R.id.start_naiv:
				mTtsManager.startSpeaking("��ʼ������ť");
				break;
			case R.id.recognizerButton:
				mTtsManager.startSpeaking("�������밴ť");
				break;
			case R.id.searchButton:
				mTtsManager.startSpeaking("�� ʼ������ť");
				break;
			/*case R.id.nextButton:
				nextButton();
				break;
			*/
				}
		}
		
		
	}
	
	private void doubleClick(View v) {
		// TODO �Զ����ɵķ������
		switch(v.getId()){

		case R.id.start_naiv:
			mTtsManager.startSpeaking("ȷ�Ͽ�ʼ���������ڹ滮����");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
			Intent itent=new Intent(MainActivity.this,Navi.class);
			itent.putExtra("startlat", lat);
			itent.putExtra("startlon", lon);
			itent.putExtra("endlat", endlp.getLatitude());
			itent.putExtra("endlon", endlp.getLongitude());
			startActivity(itent);
			break;
		case R.id.recognizerButton:
			this.poitext.setText(null);
			//mTtsManager.startSpeaking("��ʼ�����������룬��һ���ʼ����");
			/*try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}*/
			RecognizerButton();
			break;
		case R.id.searchButton:
			searchButton();
			break;
		/*case R.id.nextButton:
			nextButton();
			break;
		*/
			}
	}



	@Override
	public void onMapClick(LatLng arg0) {
		// TODO �Զ����ɵķ������
		Log.i(TAG,"��λ�ɹ�");
		amap.moveCamera(CameraUpdateFactory.changeLatLng(arg0));
	}

/////////////////////////////////////�������/////////////////////////////////////////
	
	
	private void RecognizerButton() {
		// TODO �Զ����ɵķ������
		FlowerCollector.onEvent(MainActivity.this, "iat_recognize");
	   this.poitext.setText(null);
	   mIatResults.clear();
	   setParam();
	   if (isShowDialog) {
			// ��ʾ��д�Ի���
			mIatDialog.setListener(mRecognizerDialogListener);
			mTtsManager.startSpeaking("��ʼ����");
			mIatDialog.show();
		} else {
			// ����ʾ��д�Ի���
			ret = mIat.startListening(mRecognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("��дʧ��,�����룺" + ret);
			} else {
				
			}
		}
	}

	
	private void showTip(String str) {
		// TODO �Զ����ɵķ������
		mToast.setText(str);
		mToast.show();
	}

	private void setParam() {
		// TODO �Զ����ɵķ������
		// ������д����
				mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
				// ���÷��ؽ����ʽ
				mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
				mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
				mIat.setParameter(SpeechConstant.DOMAIN,"iat"); 
		        mIat.setParameter(SpeechConstant.ACCENT,"mandarin");
				// ���ñ�����,����Ϊ"0"���ؽ���ޱ��,����Ϊ"1"���ؽ���б��
				mIat.setParameter(SpeechConstant.ASR_PTT, "0");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// �˳�ʱ�ͷ�����
		mIat.cancel();
		mIat.destroy();
	}

	
}
