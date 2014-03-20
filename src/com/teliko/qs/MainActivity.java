package com.teliko.qs;

import java.util.ArrayList;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	// �������� ���������� ��� ��������� ��� ���������� ��� ��������
	ArrayList<Double> speeds = new ArrayList<Double>();
	CheckBox outdoorscheckBox,indoorscheckBox;
	Button markSpot;
	EditText weight;
	TextView userx,usery,distance,bearing,steps,speed,avgspeed,calories,elevation;
	int step=0;
	double calcDist=0,latitude=0,longitude=0,A=0.0395,B=0.00327,C=0.000455,K=1,pound=0.453592,mile=0.62137;
	boolean spot=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();    	
    	mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    	
		userx = (TextView) findViewById(R.id.latText);
		usery = (TextView) findViewById(R.id.longText);
		distance = (TextView) findViewById(R.id.distanceText);
		bearing = (TextView) findViewById(R.id.bearingText);
		steps = (TextView) findViewById(R.id.stepsText);
		speed = (TextView) findViewById(R.id.speedText);
		avgspeed = (TextView) findViewById(R.id.averangespeedText);
		calories = (TextView) findViewById(R.id.caloriesText);
		elevation = (TextView) findViewById(R.id.elevationText);
		weight = (EditText) findViewById(R.id.weightTextBox);
		outdoorscheckBox = (CheckBox) findViewById(R.id.outdoorscheckBox);
		indoorscheckBox = (CheckBox) findViewById(R.id.indoorscheckBox);
		// ������� ��� �������� ��� �� � ������ ����� �� �������� � ���. ���� ��������� ��� ���� ��� �
		outdoorscheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   if(isChecked){
				   indoorscheckBox.setChecked(false);
				   outdoorscheckBox.setChecked(true);
				   K=1;
				   }
				   else {
					   outdoorscheckBox.setChecked(false);
					   indoorscheckBox.setChecked(true);
					   K=0; 
				   }
			   }
			}
		);
		indoorscheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   if(isChecked){
					   outdoorscheckBox.setChecked(false);
					   indoorscheckBox.setChecked(true);
					   K=0;
					   }
					   else {
						   indoorscheckBox.setChecked(false);
						   outdoorscheckBox.setChecked(true);
						   K=1;
					   }
			   }
			}
		);
		markSpot = (Button) findViewById(R.id.button1);
		markSpot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                spot = true;
                latitude = Double.parseDouble(userx.getText().toString());
                longitude = Double.parseDouble(usery.getText().toString());
                markSpot.setTextSize(12);
                markSpot.setText("lat:"+latitude+" - lon:"+longitude);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public class MyLocationListener implements LocationListener{
  	  public void onLocationChanged(Location loc) {// ��� ���� �������� ��� ���������� ������: 
  		userx.setText(String.format("%.6f",loc.getLatitude())); //��������� ��� ����������� ������
  	    usery.setText(String.format("%.6f",loc.getLongitude())); //��������� ��� ����������� �������
  		  if (spot){ //�� ���� ������� �� �������
  			  
    	    bearing.setText(String.format("%.4f",calculateBearing(latitude, //��������� ��� ��������� ��� ��� �����, ���� ��� �������
    	    		  longitude,
					  loc.getLatitude(),
					  loc.getLongitude())));
    	    
    	    distance.setText(String.format("%.4f",calculateDistance( // ��������� ��� ��������� ���� ��� �������
    	    		latitude,
    	    		longitude,
					loc.getLatitude(),
					loc.getLongitude()
					)));
    	    step++;
    	    steps.setText(String.valueOf(step));
    	    
    	    speed.setText(String.valueOf(calculateSpeed())); // ��������� ��� ���������
    	    
    	    avgspeed.setText(String.valueOf(calculateAverageSpeed())); // ��������� ��� ����� ���������
    	    
    	    if (weight.getText().length() != 0)
    	    calories.setText(String.format("%.2f",calculateCalories(Double.parseDouble(speed.getText().toString())))); //��������� ��� �������� (�� ������������ �� ����� ��� �� �����)
    	    else
    	    	calories.setText("Insert your weight in Kgs");
    	    
    	    if (loc.hasAltitude()) elevation.setText(String.format("% 2f",loc.getAltitude())); //��������� ��� ��������� (�� �� ����������� � �������)
    	    else elevation.setText("Can't measure");
  	  }
  	  }
  	  
private double calculateDistance(double spotlat, double spotlng, double userlat, double userlng) { //������� ����������� ��������� �� ����� ��� ����� Haversine
    	
        double R = 6371; 
        double dLat =  Math.toRadians(userlat-spotlat);

        double dLon =  Math.toRadians(userlng-spotlng);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(spotlat)) * Math.cos(Math.toRadians(userlat)) * 
                Math.sin(dLon/2) * Math.sin(dLon/2); 
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        double d = R * c;
        calcDist = d;
        return d;
    }
    
    public double calculateBearing(double lat1, double long1, double lat2, double long2)   // ������� ����������� ��������� ��� ��� �����
    {  
        
        lat1 = Math.toRadians(lat1);  
        long1 = Math.toRadians(long1);  
        lat2 = Math.toRadians(lat2);  
        long2 = Math.toRadians(long2);  

        double deltaLong = long2 - long1;  

        double y = Math.sin(deltaLong) * Math.cos(lat2);  
        double x = Math.cos(lat1) * Math.sin(lat2) -  
                Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong);  
        double bearing = Math.atan2(y, x);  
        return (Math.toDegrees(bearing)+360)%360;  
    }  
    
  	  
    public double calculateCalories(double speed){  // ������� ����������� �������� ���� ��� ����� ��� ��������������� ���� ���������� �������
    	double D=0.00801*Math.pow(((Double.parseDouble(weight.getText().toString())*pound)/154),0.425)/(Double.parseDouble(weight.getText().toString())*pound);
    	double speedInMiles=speed*mile;
    	double cal = A+((B*speedInMiles)+((C*Math.pow(speedInMiles, 2))+K*(D*Math.pow(speedInMiles, 3))));
    	return cal;
    }
    
    public double calculateAverageSpeed(){ // ������� ����������� ����� ���� ���������
    	double speedsum=0;
    	for(int i=0;i<speeds.size();i++)
    		speedsum += speeds.get(i);    	
    	return speedsum/speeds.size();
    }
    
    public double calculateSpeed(){    	// ������� ����������� ��������� (�� ���/���)
        double sp = calcDist*3600;
        speeds.add(sp);
    	return sp;
    }
  	  public void onProviderDisabled(String provider) {
  	    Toast.makeText( getApplicationContext(), ((String) "GPS Disabled"), Toast.LENGTH_SHORT ).show();
  	  }

  	  public void onProviderEnabled(String provider) {
  	    Toast.makeText( getApplicationContext(), ((String) "GPS Enabled"), Toast.LENGTH_SHORT).show();
  	  }

  	  public void onStatusChanged(String provider, int status, Bundle extras) {
  	  }
  	 }
    
}
