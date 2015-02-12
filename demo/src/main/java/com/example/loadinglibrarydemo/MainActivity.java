package com.example.loadinglibrarydemo;

import org.json.JSONException;
import org.json.JSONObject;

import loader.ImageLoader;
import loader.JsonLoader;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loadinglibrarydemo.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		ImageLoader il = new ImageLoader(this);
		
		il.DisplayOrQueueImage("http://cdn2.carbuyer.co.uk/sites/carbuyer_d7/files/jato_uploaded/Hyundai-i10-micro-car-2012-front-quarter-main.jpg", (ImageView)findViewById(R.id.imageView1));
		il.DisplayOrQueueImage("http://ibuycarsdfw.com/wp-content/uploads/2013/09/audio.jpg", (ImageView)findViewById(R.id.imageView2));
		il.DisplayOrQueueImage("http://globe-views.com/dcim/dreams/car/car-03.jpg", (ImageView)findViewById(R.id.imageView3));
		il.DisplayOrQueueImage("http://images.wisegeek.com/silver-car-isolated.jpg", (ImageView)findViewById(R.id.imageView4));
		il.DisplayOrQueueImage("http://www.youthedesigner.com/wp-content/uploads/2011/05/cool-car-designs-13.jpg", (ImageView)findViewById(R.id.imageView5));
		il.DisplayOrQueueImage("http://wallpaperskat.com/wp-content/uploads/2014/09/Latest-Chevrolet-Car-Wallpapers.jpg", (ImageView)findViewById(R.id.imageView6));
		il.DisplayOrQueueImage("http://www.popularmechanics.com/cm/popularmechanics/images/Rl/future_cars_09_0211-lgn.jpg", (ImageView)findViewById(R.id.imageView7));
		il.DisplayOrQueueImage("http://www.motorimports.co.uk/wp-content/uploads/2013/03/shutterstock_100205033.jpg", (ImageView)findViewById(R.id.imageView8));
		il.DisplayOrQueueImage("http://car-pictures.cars.com/images/?IMG=cac10doc201c11403.png&WIDTH=624&AUTOTRIM=1&SPECIAL=&ACT=F", (ImageView)findViewById(R.id.imageView9));	
		/*il.DisplayOrQueueImage("http://nebula.wsimg.com/1787b2c6bafadef0db3dbe6c346641fe?AccessKeyId=1821D8B5D5D098E69FB2&disposition=0&alloworigin=1", (ImageView)findViewById(R.id.imageView10));
		il.DisplayOrQueueImage("http://www.sixt.com/uploads/pics/Mercedes_C300.png", (ImageView)findViewById(R.id.imageView11));
		il.DisplayOrQueueImage("http://static1.squarespace.com/static/53b70d29e4b0657eb1ab3588/t/53baf39de4b09821ce6c579b/1404761007081/deluxe+car+car+detail+wash+package", (ImageView)findViewById(R.id.imageView12));
		il.DisplayOrQueueImage("http://www.imotors.com/Images/Default/small_car.jpg", (ImageView)findViewById(R.id.imageView13));
		il.DisplayOrQueueImage("http://lapa.ringoldsz.lv/wp-content/uploads/2014/11/cars1.gif", (ImageView)findViewById(R.id.imageView14));
		il.DisplayOrQueueImage("http://www.dacemotorcompany.co.uk/img/slider-car.png", (ImageView)findViewById(R.id.imageView15));*/
		
		
		JSONObject obj = JsonLoader.GetResponseObject("http://echo.jsontest.com/key/value/one/two", this);
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		TextView textView2 = (TextView) findViewById(R.id.textView2);
		
		try {
			if (obj != null) {
				textView1.setText(obj.getString("one"));
				textView2.setText(obj.getString("key"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
