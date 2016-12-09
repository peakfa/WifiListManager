package zmartec.wifidemo;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;



public class WifiManual extends Dialog{
	private final String TAG = "WifiManual";

	private WifiSetup mWifiSetup;
	private Context mContextMain ;
	private EditText mEditTextPASSWD;
	private EditText mEditTextSSID;
	private String strPassword;
	private boolean bShowPassWD = true;
	private View mView;

	private TextView mStatus_TextView;

	private static final int MSG_DRAW_WIFI_STATUS_FAIL      = 1000;
	public Handler mScanHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_DRAW_WIFI_STATUS_FAIL:
					if(mStatus_TextView.getText().equals("Connecting...")) {
						mStatus_TextView.setText("Connecting fail!");
					}
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	public WifiManual(Context context, WifiSetup wifiSetup, boolean bEdit) {
		super(context, R.style.FullscreenTheme);
		mContextMain = context;
		mView = View.inflate(mContextMain,R.layout.wifi_manual, null);
		super.setContentView(mView);

		mWifiSetup = wifiSetup;
		mStatus_TextView = (TextView) mView.findViewById(R.id.wifi_manual_Status_TextView);
		mStatus_TextView.setText("Not Connected");

		bShowPassWD = true;
		mEditTextPASSWD = (EditText) mView.findViewById(R.id.wifi_manual_PassWD_EditText);
		mEditTextPASSWD.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		mEditTextSSID = (EditText) mView.findViewById(R.id.wifi_manual_SSID_EditText);
		if(wifiSetup.wifiItemSSID != null) {
			mEditTextSSID.setText(wifiSetup.wifiItemSSID);
		}
		if(bEdit == true) {
			mEditTextSSID.setEnabled(true);
			//mEditTextSSID.requestFocus();
			mEditTextSSID.setText("");
		}else{
			mEditTextSSID.setFocusable(false);
			mEditTextSSID.setEnabled(false);
			//mEditTextPASSWD.requestFocus();
		}


		mView.findViewById(R.id.wifi_manual_Button_show).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				if(bShowPassWD == true) {
					mEditTextPASSWD.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					bShowPassWD = false;
				}else{
					mEditTextPASSWD.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					bShowPassWD = true;
				}
			}
		});

		mView.findViewById(R.id.wifi_manual_Button_connect).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				strPassword = mEditTextPASSWD.getText().toString();

				if (strPassword != null) {
					mWifiSetup.wiFiAdmin.connect(mWifiSetup.wifiItemSSID, strPassword, WiFiAdmin.WifiCipherType.WIFICIPHER_WPA);
					mStatus_TextView.setText("Connecting...");
					ToolsUtil.getInstance().sendMsg(mScanHandler,MSG_DRAW_WIFI_STATUS_FAIL,4000);
				}
			}
		});

		InfoReceiver mInfoReceiver = new InfoReceiver();
		IntentFilter mInfoReceiverFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContextMain.registerReceiver(mInfoReceiver, mInfoReceiverFilter);
	}

	@Override
	public void setContentView(int layoutResID) {

	}

	@Override
	public void setContentView(View view, LayoutParams params) {

	}

	@Override
	public void setContentView(View view) {

	}

	private class InfoReceiver extends BroadcastReceiver{
		public  void onReceive(Context context, Intent intent){
			if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){//wifi连接上与否
				System.out.println("网络状态改变");
				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
					System.out.println("wifi网络连接断开");
					if(mStatus_TextView != null){
						mStatus_TextView.setText("Not Connected");
					}
				}else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
					WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					//获取当前wifi名称
					System.out.println("连接到网络 " + wifiInfo.getSSID());
					if(mStatus_TextView != null){
						if(wifiInfo.getSSID().equals(mWifiSetup.wifiItemSSID) == true) {
							mStatus_TextView.setText("Connected");
						}
					}
				}else if(info.getState().equals(NetworkInfo.State.CONNECTING)){
					if(mStatus_TextView != null){
						mStatus_TextView.setText("Connecting...");
					}
				}
			}
		}
	}

}