package zmartec.wifidemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ToolsUtil {
	private static final String TAG = "ToolsUtil";
	private static ToolsUtil mToolsUtil = null;
	private String mTag = "xxx";

	public static synchronized ToolsUtil getInstance(){
		if(mToolsUtil == null){
			mToolsUtil = new ToolsUtil();
		}
		return mToolsUtil;
	}

	public void sendMsg(Handler handler, int iwhat, Object iobj)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.obj = iobj;
			mMsg.what = iwhat;
			handler.sendMessage(mMsg);
		}else{
			Log.e(TAG,"handler is null");
		}
	}

	public void sendMsg(Handler handler, int iwhat,int delay)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.what = iwhat;
			handler.sendMessageDelayed(mMsg,(long)delay);
		}else{
			Log.e(TAG,"handler is null");
		}
	}

	public void sendMsg(Handler handler, int iwhat, Object iobj,long delay)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.obj = iobj;
			mMsg.what = iwhat;
			handler.sendMessageDelayed(mMsg,delay);
		}else{
			Log.e(TAG,"handler is null");
		}
	}

	public void calc_test_thread(String tag)
	{
		mTag = tag;

		new Thread(new Runnable() {
			public void run() {
				Log.d(TAG,"calc test start:"+mTag);
				calc_test_func(mTag);
				Log.d(TAG,"calc test end:"+mTag);
			}
		}).start();
	}

	public void calc_test_func(String tag) {
		int loop_times = 0;

		double x=0.0;
		double y=0.0;
		double z=0.0;

		Log.d(TAG,"calc_test_func:"+mTag);

		while(loop_times++ < 1000000000){
			x = 2.0/47.0;
			y = 100.0/23.0;
			z = z+x*y;
		}
	}
}
