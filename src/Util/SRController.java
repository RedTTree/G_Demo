package Util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.autonavi.tbt.NaviStaticInfo;
import com.autonavi.tbt.TrafficFacilityInfo;
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

/**
 * ������д���
 */
public class SRController  {
	private static String TAG = SRController.class.getSimpleName();
	
    private Context mContext;
    private static SRController ttsManager;
	private Toast mToast;
	private String mResultText;
	//�Ƿ���ʾͼ��
	private Boolean isShow=true;
	// ������д����
    private SpeechRecognizer mlat;
 // ������дUI
 	private RecognizerDialog mIatDialog;
 // ��HashMap�洢��д���
 	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
 	/**
     * ��ʼ��������
     */
    private InitListener mlatInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("SHIXIN", "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, "��ʼ��ʧ��,�����룺" + code, Toast.LENGTH_SHORT).show();
            } else {
                // ��ʼ���ɹ���֮����Ե���startSpeaking����
                // ע���еĿ�������onCreate�����д�����ϳɶ���֮�����Ͼ͵���startSpeaking���кϳɣ�
                // ��ȷ�������ǽ�onCreate�е�startSpeaking������������
            }
        }
    };

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
	
	/**
	 * ��д������
	 */
   private RecognizerListener mRecognizerListener =new RecognizerListener(){

		@Override
		public void onBeginOfSpeech() {
			// TODO �Զ����ɵķ������
			showTip("��ʼ˵��");
		}

		@Override
		public void onEndOfSpeech() {
			// TODO �Զ����ɵķ������ 
			showTip("����˵��");
		}

		@Override
		public void onError(SpeechError error) {
			// TODO �Զ����ɵķ������
			error.getPlainDescription(true); //��ȡ����������
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO �Զ����ɵķ������
			// ���´������ڻ�ȡ���ƶ˵ĻỰid����ҵ�����ʱ���Ựid�ṩ������֧����Ա�������ڲ�ѯ�Ự��־����λ����ԭ��
			// ��ʹ�ñ����������ỰidΪnull
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}

		@Override
		public void onResult(RecognizerResult results , boolean arg1) {
			// TODO �Զ����ɵķ������
			Log.d("Result:",results.getResultString ());
			printResult(results);
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			// TODO �Զ����ɵķ������
			showTip("��ǰ����˵����������С��" + volume);
			Log.d(TAG, "������Ƶ���ݣ�"+data.length);
		}  		   
	   };
//////////////////////////////////////////////////////////////////////////////////	
//////////////////////////////////////////////////////////////////////////////////	   
	   
	   private SRController(Context context) {
	        mContext = context;
	    }
	   
	   public static SRController getInstance(Context context) {
	        if (ttsManager == null) {
	            ttsManager = new SRController(context);
	        }
	        return ttsManager;
	    }
	   
	   
	   /**
		 * ��ʼ��
		 */
	   
    public void init() {
       
        // ��ʼ���ϳɶ���.
      mlat=SpeechRecognizer.createRecognizer(mContext, mlatInitListener);
      mIatDialog=new RecognizerDialog(mContext,mlatInitListener);
      /**
  	 * ��������
  	 */
      initSpeechSynthesizer();
      
    }

    /**
     * ʹ��SpeechSynthesizer�ϳ��������������ϳ�Dialog.
     *
     * @param
     */
    public void startListening() {
        // ���������ϳ�.
    	if(this.isShow=true){
    		mResultText=null;
    		mIatDialog.setListener(mRecognizerDialogListener);
    		mIatDialog.show();
    	}
    	else{
        if (mlat != null){
        	mResultText=null;
          mlat.startListening(mRecognizerListener);
        }
    }
    }

    
    public void stopListening() {
        if (mlat != null)
        	mlat.stopListening();
          
    }

    /**
  	 * ��������
  	 */
    private void initSpeechSynthesizer() {
        // ��ղ���
        mlat.setParameter(SpeechConstant.DOMAIN,"iat"); 
        mlat.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        mlat.setParameter(SpeechConstant.ACCENT,"mandarin");
     // ���÷��ؽ����ʽ
     	mlat.setParameter(SpeechConstant.RESULT_TYPE, "json");	
    }

    public void destroy() {
        if (mlat != null) {
            mlat.stopListening();
            mlat.destroy();
            ttsManager=null;
        }
    }
    
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
    
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

		mResultText=resultBuffer.toString();
	}
	
	public String getString(){
	return mResultText;
	}
	
	private void setIsShow(Boolean initisShow){
	this.isShow=initisShow;
	}
	
}