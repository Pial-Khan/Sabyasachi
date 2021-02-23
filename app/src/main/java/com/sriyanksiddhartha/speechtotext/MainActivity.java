package com.sriyanksiddhartha.speechtotext;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.speech.tts.TextToSpeech;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import android.text.format.DateUtils;
import java.util.Date;
import java.util.UUID;

import android.net.Uri;


public class MainActivity extends AppCompatActivity {

	private TextView txvResult;
	private TextToSpeech tts;
	private Locale locale;
	UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothSocket socket;
	private OutputStream os;
	private int flag=0;
	public String deviceName;
	public String deviceAddress;
	private BluetoothDevice device;

	 private BluetoothAdapter mBluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txvResult = (TextView) findViewById(R.id.txvResult);
		mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();


		initializeTextToSpeech();
	}

	public void getSpeechInput(View view) {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bn");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn");

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, 10);
		} else {
			Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
		}
	}

	private void initializeTextToSpeech() {
	    locale=new Locale("bn","BD");
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (tts.getEngines().size() == 0 ){
					Toast.makeText(MainActivity.this, "Missing.........",Toast.LENGTH_LONG).show();
					finish();
				} else {
					tts.setLanguage(locale);
					speak("আপনাকে অভিনন্দন");
				}
			}
		});
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 10:
				if (resultCode == RESULT_OK && data != null) {
					ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					txvResult.setText(result.get(0));

					processResult(result.get(0));

				}
				break;
		}
	}

	private void processResult(String result_message) {
		result_message = result_message.toLowerCase();


		if(result_message.indexOf("কি") != -1){
			if(result_message.indexOf("তোমার নাম") != -1){
				speak("আমার নাম সব্যসাচী ");
			}
//			if (result_message.indexOf("সময়") != -1){
//				String time_now = DateUtils.formatDateTime(this, new Date().getTime(),DateUtils.FORMAT_SHOW_TIME);
//				speak("The time is now: " + time_now);
//			}
		}

		else if(result_message.indexOf("কেমন")!=-1){
			speak("আমি ভালো আছি।আপনি কেমন আছেন ");
		}

		else if(result_message.indexOf("আছো")!=-1){
			speak("আমি ভালো আছি।আপনি কেমন আছেন ");
		}




		else if (result_message.indexOf("কত") != -1){
			if (result_message.indexOf("সময়")!= -1) {
				String time_now = DateUtils.formatDateTime(this, new Date().getTime(), DateUtils.FORMAT_SHOW_TIME);
				speak(time_now);
				//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/AnNJPf-4T70"));
				//startActivity(intent);
			}


			else if (result_message.indexOf("তারিখ")!= -1){
				openApp(this,"com.google.android.calendar");

			}
        }
		 else if (result_message.indexOf("ব্রাউজার") != -1){
			speak("ওপেন করা হচ্ছে");
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/AnNJPf-4T70"));
			startActivity(intent);
		}

		else if(result_message.indexOf("ওপেন ")!= -1){
			if(result_message.indexOf("জিমেইল")!= -1){
				Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("plain/text");
						//intent.setData(Uri.parse("test@gmail.com"));
						//intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
						intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"@gmail.com"});
						intent.putExtra(Intent.EXTRA_SUBJECT, "test");
						intent.putExtra(Intent.EXTRA_TEXT, "hello. this is a message sent from my demo app :-)");
				startActivity(intent);
			}
			else if(result_message.indexOf("ফোন বুক")!= -1) {
				Uri uri = Uri.parse("content://contacts");
				Intent intent = new Intent(Intent.ACTION_PICK, uri);
				intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
				startActivityForResult(intent, 1);
			}
			else if(result_message.indexOf("ম্যাপ")!=-1){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
					startActivity(intent);
				}
				else if(result_message.indexOf("ক্যামেরা ")!= -1){
					Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivity(intent);
				}
				else if(result_message.indexOf("কল")!= -1){

					openApp(this,"com.truecaller");
				}
				else if(result_message.indexOf("ফুডপান্ডা")!= -1){
					openApp(this,"com.global.foodpanda.android");
				}
				else if(result_message.indexOf("ফটোস")!= -1){
					openApp(this,"com.google.android.apps.photos");
				}
				else if(result_message.indexOf("ফটো")!= -1){
					openApp(this,"com.google.android.apps.photos");
				}
				else if(result_message.indexOf("ক্যালেন্ডার")!= -1){
					openApp(this,"com.google.android.calendar");
				}
				else if(result_message.indexOf("মজিলা")!= -1){
					openApp(this,"com.google.android.calen");
				}
				else if(result_message.indexOf("ফেসবুক")!= -1){
					openApp(this,"com.facebook.katana");
				}
		}

		else if(result_message.indexOf("জিমেইল")!= -1){
			speak("চালু করা হচ্ছে");
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("plain/text");
			//intent.setData(Uri.parse("test@gmail.com"));
			//intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
			intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"@gmail.com"});
			intent.putExtra(Intent.EXTRA_SUBJECT, "test");
			intent.putExtra(Intent.EXTRA_TEXT, "hello. this is a message sent from my demo app :-)");
			startActivity(intent);
		}
		else if(result_message.indexOf("ফোন বুক")!= -1) {
			speak("চালু করা হচ্ছে");
			Uri uri = Uri.parse("content://contacts");
			Intent intent = new Intent(Intent.ACTION_PICK, uri);
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
			startActivityForResult(intent, 1);
		}
		else if(result_message.indexOf("ম্যাপ")!=-1){
			speak("চালু করা হচ্ছে");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
		}
		else if(result_message.indexOf("ক্যামেরা ")!= -1){
			//Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//startActivity(intent);
			openApp(this,"com.google.android.GoogleCamera");
		}
		else if(result_message.indexOf("কল")!= -1){

			openApp(this,"com.truecaller");
		}
		else if(result_message.indexOf("ফুডপান্ডা")!= -1){
			openApp(this,"com.global.foodpanda.android");
		}
		else if(result_message.indexOf("ফটোস")!= -1){
			openApp(this,"com.google.android.apps.photos");
		}
		else if(result_message.indexOf("ফটো")!= -1){
			openApp(this,"com.google.android.apps.photos");
		}
		else if(result_message.indexOf("গ্যালারি")!= -1){
			openApp(this,"com.android.gallery");
		}
		else if(result_message.indexOf("ক্যালেন্ডার")!= -1){
			openApp(this,"com.google.android.calendar");
		}
		else if(result_message.indexOf("মজিলা")!= -1){
			openApp(this,"com.google.android.calen");
		}
		else if(result_message.indexOf("ফেসবুক")!= -1){
			openApp(this,"com.facebook.katana");
		}
		else if(result_message.indexOf("এলার্ম")!= -1){
			openApp(this,"com.google.android.deskclock");
		}

		else if (result_message.indexOf("ব্লুটুথ") !=-1){
//				Intent intent = new Intent(this, SelectController.class);
//				startActivity(intent);
			//selectFromList();

		}
		else if (result_message.indexOf("বার্তা") !=-1){
			Intent intent = new Intent(this, MessageActivity.class);
			startActivity(intent);
		}

		else if (result_message.indexOf("গান") !=-1){
			openApp(this,"com.google.android.music");
		}
		else if (result_message.indexOf("তারিখ")!= -1){
			openApp(this,"com.google.android.calendar");

		}
		else if (result_message.indexOf("ইউটিউব") !=-1){
			if(result_message.indexOf("")!=-1) {
				speak("সার্চ করা হচ্ছে");
				Intent intent = new Intent(MainActivity.this, youtubeactivity.class);


				//String url = "https://www.pipilika.com/search?q=" + result_message;
				//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pipilika.com/search?q="+ result_message));
				intent.putExtra("message", result_message);
				startActivity(intent);
			}
		}
		else if (result_message.indexOf("প্লে স্টোর") !=-1){
			openApp(this,"com.android.vending");
		}
		else if (result_message.indexOf("ক্যালকুলেটর") !=-1){
			openApp(this,"com.google.android.calculator");
		}
		else if (result_message.indexOf("ফাইল") !=-1){
			openApp(this,"com.google.android.apps.nbu.files");
		}
		else if (result_message.indexOf("ড্রাইভ") !=-1){
			openApp(this,"com.google.android.apps.docs");
		}

		else if (result_message.indexOf("আবহাওয়া") !=-1){
			openApp(this,"com.graph.weather.forecast.channel");
		}

		else if (result_message.indexOf("হোয়াটসঅ্যাপ") !=-1){
			openApp(this,"com.whatsapp");
		}


		else if (result_message.indexOf("টর্চ") !=-1){

		}


		else if (result_message.indexOf("ওয়াইফাই") !=-1){

			WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);


			if(!wifi.isWifiEnabled()) {
				//speak();

				//WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				wifi.setWifiEnabled(true);
			}
			else{
				//WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				wifi.setWifiEnabled(false);
			}
		}
		else if (result_message.indexOf("চালু") !=-1){
			Intent intent=new Intent(this,LightActivity.class);
			startActivity(intent);
		}


		else if (result_message.indexOf("") != -1){
			speak("সার্চ করা হচ্ছে");
			Intent intent = new Intent(MainActivity.this,WebViewActivity.class);

			//String url = "https://www.pipilika.com/search?q=" + result_message;
			//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pipilika.com/search?q="+ result_message));
			intent.putExtra("message",result_message);
			startActivity(intent);
		}

	}



//	@Override
//	protected void onResume() {
//
//		super.onResume();
//		SharedPreferences preferences = getSharedPreferences("preferences",MODE_PRIVATE);
//		deviceName = preferences.getString("controllerName", "NA");
//		deviceAddress = preferences.getString("controllerAddress", "");
//		//nameView.setText("Connected to " + "\"" + deviceName + "\"");
//		if(!deviceAddress.equals("")) {
//			//System.out.println(deviceAddress);
//			Log.v("hello check", deviceAddress);
//			device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
//			try {
//				socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//			} catch (IOException e) {
//				//nameView.setText("Connected to " + "\"" + "NA" + "\"");
//				//Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//			}
//			mBluetoothAdapter.cancelDiscovery();
//			if(socket != null){
//				try {
//					socket.connect();
//				} catch (IOException e) {
//					//nameView.setText("Connected to " + "\"" + "NA" + "\"");
//					//Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//					//Log.e(TAG, "socket connect failed: " + e.getMessage() + "\n");
//					try {
//						socket.close();
//					} catch (IOException e1) {
//						//Log.e(TAG, "socket closing failed: " + e1.getMessage() + "\n");
//						//Toast.makeText(getApplicationContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
//					}
//				}
//			}
//			if (socket != null){
//				try {
//					os = socket.getOutputStream();
//				} catch (IOException e) {
//					//66Log.e(TAG, "getting output stream failed: " + e.getMessage() + "\n");
//					//Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//				}
//			}
//		}
//		else {
//			Toast.makeText(getApplicationContext(),"Connect to a device",Toast.LENGTH_LONG).show();
//		}
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		if (os != null && socket.isConnected()) {
//			try {
//				os.flush();
//			} catch (IOException e) {
//				//Log.e(TAG, "flushing output stream failed: " + e.getMessage() + "\n");
//				Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
//			}
//		}
//		try {
//			if(socket != null)
//				socket.close();
//		} catch (IOException e) {
//			//Log.e(TAG, "closing socket failed: " + e.getMessage() + "\n");
//			Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
//		}
//	}


	private void speak(String message) {
		if(Build.VERSION.SDK_INT >= 21){

			tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
		} else {
			tts.speak(message, TextToSpeech.QUEUE_FLUSH,null);
		}
	}

	public  boolean openApp(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		try {
			Intent i = manager.getLaunchIntentForPackage(packageName);
			if (i == null) {
				speak("এপটি খুজে পাওয়া যাচ্ছে না");
				return false;
				//throw new ActivityNotFoundException();
			}
			speak("চালু করা হচ্ছে");
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			context.startActivity(i);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	public void sendSignal(int message) {
		if(socket != null){
			if(socket.isConnected()) {
				try {
					if (os != null){
						os.write(message);
						Log.d("OS Message : ", "" + message);
					}
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			else {
				//Toast.makeText(getBaseContext(), "Connect to the selected bluetooth device first", Toast.LENGTH_LONG).show();
			}
		}
		else {
			//Toast.makeText(getBaseContext(), "Connect to the selected bluetooth device first", Toast.LENGTH_LONG).show();
		}
	}

//	private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
//		try {
//			packageManager.getPackageInfo(packageName, 0);
//			return true;
//		} catch (PackageManager.NameNotFoundException e) {
//			return false;
//		}
//	}


}


