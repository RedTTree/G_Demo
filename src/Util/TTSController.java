package Util;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * �����������
 */
public class TTSController implements AMapNaviListener {

    private Context mContext;
    private static TTSController ttsManager;
    private SpeechSynthesizer mTts;
    /**
     * ��ʼ��������
     */
    private InitListener mTtsInitListener = new InitListener() {
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

 

    public void init() {
        // ��ʼ���ϳɶ���.
        mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);
        initSpeechSynthesizer();
    }

    private TTSController(Context context) {
        mContext = context;
    }

    public static TTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new TTSController(context);
        }
        return ttsManager;
    }
    
    /**
     * ʹ��SpeechSynthesizer�ϳ��������������ϳ�Dialog.
     *
     * @param
     */
    public void startSpeaking(final String playText) {
        // ���������ϳ�.
        if (mTts != null){
        	
					  mTts.startSpeaking(playText, new SynthesizerListener() {

		                  @Override
		                  public void onSpeakResumed() {
		                      // TODO Auto-generated method stub

		                  }

		                  @Override
		                  public void onSpeakProgress(int arg0, int arg1, int arg2) {
		                      // TODO Auto-generated method stub

		                  }

		                  @Override
		                  public void onSpeakPaused() {
		                      // TODO Auto-generated method stub
		                	 
		                  }

		                  @Override
		                  public void onSpeakBegin() {
		                      // TODO Auto-generated method stub
		                
		                  }

		                  @Override
		                  public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		                      // TODO Auto-generated method stub

		                  }

		                  @Override
		                  public void onCompleted(SpeechError arg0) {
		                      // TODO Auto-generated method stub
		  		          }

		                  @Override
		                  public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
		                      // TODO Auto-generated method stub

		                  }
		              });	
		           }
    }

    public void stopSpeaking() {
        if (mTts != null)
            mTts.stopSpeaking();
    }

    private void initSpeechSynthesizer() {
        // ��ղ���
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // �������ߺϳɷ�����
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //���úϳ�����
        mTts.setParameter(SpeechConstant.SPEED, "40");
        //���úϳ�����
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //���úϳ�����
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //���ò�������Ƶ������
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // ���ò��źϳ���Ƶ������ֲ��ţ�Ĭ��Ϊtrue
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
        // ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");

    }


    public void destroy() {
        if (mTts != null) {
            mTts.stopSpeaking();
            mTts.destroy();
            ttsManager=null;
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {
        startSpeaking(s);
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onArriveDestination(NaviStaticInfo naviStaticInfo) {

    }

    @Override
    public void onArriveDestination(AMapNaviStaticInfo aMapNaviStaticInfo) {

    }

    @Override
    public void onCalculateRouteSuccess() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }
}