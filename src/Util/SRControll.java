package Util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviStaticInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.example.g_demo.MainActivity;
import com.example.g_demo.Navi;
import com.example.g_demo.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;

/**
 * ������д���
 */
public class SRControll extends Activity {
	private static String TAG = SRControll.class.getSimpleName();
    
    private Context mContext;
	private static SRControll ttsManager;
	public String poitext;
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
			poitext=resultBuffer.toString();
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
		
		
		
		
		

		
	    private SRControll(Context context) {
	        mContext = context;
	    }
	    
		 public static SRControll getInstance(Context context) {
		        if (ttsManager == null) {
		            ttsManager = new SRControll(context);
		        }
		        return ttsManager;
		    }
		 public void init() {
				// ��ʼ��ʶ����UIʶ�����
				// ʹ��SpeechRecognizer���󣬿ɸ��ݻص���Ϣ�Զ�����棻
			mIat = SpeechRecognizer.createRecognizer(SRControll.this, mInitListener);
				
				// ��ʼ����дDialog�����ֻʹ����UI��д���ܣ����贴��SpeechRecognizer
				// ʹ��UI��д���ܣ������sdk�ļ�Ŀ¼�µ�notice.txt,���ò����ļ���ͼƬ��Դ
				mIatDialog = new RecognizerDialog(SRControll.this, mInitListener);
		    }

		
		 
		 
		 
		 
		
		public void RecognizerButton() {
			// TODO �Զ����ɵķ������
			FlowerCollector.onEvent(SRControll.this, "iat_recognize");
		 poitext=null;
		   mIatResults.clear();
		   setParam();
		   if (isShowDialog) {
				// ��ʾ��д�Ի���
				mIatDialog.setListener(mRecognizerDialogListener);
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
			        
		}
		@Override
		protected void onDestroy() {
			super.onDestroy();
			// �˳�ʱ�ͷ�����
			mIat.cancel();
			mIat.destroy();
		}
   
		
}