package com.example.n3t.n3tandroidapp.feature;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DisplayImage extends AppCompatActivity {

    //Server post URL, change when the server is changed
    private String urlString = "https://n3t-api.herokuapp.com/postDataLocationPhotoStringJSON";
    private Handler mWaitHandler = new Handler();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private Date currentDT;
    private double logLat = -1000;
    private double logLon = -1000;

    byte[] imageByteArray;

    //Google API key needed for any Google API service used, in this case for the static maps
    private String API_KEY = "AIzaSyDFWj6I9Ip1POrxoaflAC7p_jVxLZtsf0U";

    /*
    The account details are:
    email: northlandinnovationn3t@gmail.com
    password: n3tandroidapp!
    */

    private Bitmap rotatedImage;
    private Bitmap bitmapImage;
    private String encodedImage = "";

    private String polyline = "jlfyEi~gf`@|@gEJuAFiBLqBToB\\cB`@wAb@k@\\[j@Wb@K`B[|@SpA]^S\\WV_@Re@Pe@Hk@FwAJcCTgGl@qO@k@JaAXcAp@qAdAeBbDiF~C_FrCqEdAeBh@cAd@mAv@cBRYZS`Ae@VI`AGdAGr@C~@Fp@@p@Eb@G\\Kb@QfAo@P[Je@@MC]Mg@WUUOo@W_@S[]GSEYBe@Li@t@qB\\oAJi@`@}B\\_CZ_CTuAhAiEj@wBNg@Xu@V_@PMz@m@j@i@T[Tk@XkA\\mAVa@Z[^S^Q^WRYPg@PmAHeBTeAVk@z@mBz@mBl@gAt@u@vAw@d@w@X_AjAgEfDyLdBuG^wBF}A@wAK{AG_C?cC?sBI{@So@a@aAg@yAWcAIk@GeAJiBRuA\\uBJ_B@wAByAF_BGiAUyAQsAE}@BgALuAXaCJg@^oA~@_Dn@uBzAqFl@uB`AcCfCkG`BmE^iAHo@FqA?E?WIe@Ii@Si@e@kAkC_G{@mBQg@Mg@Ec@@i@\\eBJi@f@iAj@eA\\g@l@g@b@YZ]Ta@Ji@Fk@Bk@BwAF}@TcAj@iALa@Fi@PcC\\gGV{EBk@Fs@Fa@Rc@R[Vi@\\oAHi@XaCd@eGD}@Jm@Ni@Ne@f@iAVk@h@{@nAoBt@mBh@mBRuANuAFsBHgEFs@Ni@\\mA`@}ChB_ML}@Hu@BcA?aACwBE}EG}ECwAAwAHiAVwATo@h@eAj@gANg@Hi@JwAHwAVmDp@}IPaC@OYMi@OoAYgB[gA_@i@o@Sc@Ii@YaCUaCWaC]}BKi@Cg@Lu@x@qB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);


        currentDT = new Date();
        final String date = new SimpleDateFormat("dd/MM/yyyy").format(currentDT);
        final String time = new SimpleDateFormat("hh:mm").format(currentDT);



        //Get location setting status, and if it is on then get location
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                //If location is disabled by the user...
                public void onProviderDisabled(String provider) {
                    ((TextView) findViewById(R.id.display_text)).setText("Date: "+date+", Time: "+time+"\nTurn GPS on for location");
                }
            };
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 1);
                return;
            }
            //Update location every 100 milliseconds
            //minDistance parameter can be changed to make the app update location per distance
            locationManager.requestLocationUpdates("gps", 100, 0, locationListener);

            imageByteArray = CameraLayout.imageAsByteArray;

            //Convert the byte array into a bitmap image, rotate the image and resize it so that it doesn't take up too much space
            if (imageByteArray == null) {
                ((TextView) findViewById(R.id.display_text)).setText("Date: "+date+", Time: "+time+"\nTurn GPS on for location\nError retrieving image.");
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmapImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, options);
                rotatedImage = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), matrix, true);
                ((ImageView) findViewById(R.id.image_image)).setImageBitmap(rotatedImage);
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), rotatedImage, currentDT.toString(), "");

            }

            //Encode the image to send to the server

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedImage.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            Log.i("Encoded image", encodedImage);

            // Display location underneath image

            location = locationManager.getLastKnownLocation("gps");
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                String degrees = convertCoordinatesToDegrees(lat, lon);
                logLat = lat;
                logLon = lon;
                ((TextView) findViewById(R.id.display_text)).setText("Date: "+date+", Time: "+time+"\n"+degrees); //Lat: " + Math.round(location.getLatitude() * 100) / 100 + "\nLon: " + Math.round(location.getLongitude() * 100) / 100
            }

            //Every time a photo is taken, the app will request a static map from google.
            //It will also send IMU and location data to the server, along with the image.(out of service)
            getCurrentMap(location);
            LoggingFileHandler logger = new LoggingFileHandler();
            logger.addLog(logLat, logLon , currentDT, encodedImage);  // ENABLE THIS AND DISABLE BELOW FOR PHOTO UPLOAD
            //logger.addLogNoImage(logLocation, currentDT);
            // sendUpdates(makeJsonObject());
        }catch(OutOfMemoryError oom){
            Context context = getApplicationContext();
            CharSequence text = "Error displaying image";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            startActivity(new Intent(DisplayImage.this, CameraLayout.class));
            Log.i("ERROR OUT OF MEMORY", oom.getMessage());
            finish();
        }

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //After 3 seconds, the camera will automatically restart.
                //This time can be increased if we want to give the user more time to look at the photo.

                try {
                    bitmapImage.recycle();
                    rotatedImage.recycle();
                    Intent intent = new Intent(getApplicationContext(), CameraLayout.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 3000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        mWaitHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        mWaitHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), DisplayImage.class);
                startActivity(intent);
            }
        }
    }

    //Send photo, location, IMU and date/time to server as a JSON object
    private void sendUpdates(JSONObject jsonObject) {

        RequestQueue q = Volley.newRequestQueue(this);
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("got response photo", response.toString());
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error sending to server", error.toString());
            }
        };

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlString, jsonObject, success, failure);
        req.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        q.add(req);
    }

    //Create a JSON object to send to the server.
    private JSONObject makeJsonObject() {

        JSONObject o = new JSONObject();
        try {
            Date currentTime = Calendar.getInstance().getTime();
            try {
                location = locationManager.getLastKnownLocation("gps");
            } catch (SecurityException e){}
            o.put("IMU_x", "-200");
            o.put("IMU_y", "-200");
            o.put("IMU_z", "-200");
            o.put("dateTime", currentTime);
            o.put("humidity", "-200");
            o.put("barometricPressure", "-200");
            if (location == null){
                o.put("longitude", "-200");
                o.put("latitude", "-200");
            } else {
                o.put("longitude", location.getLongitude());
                o.put("latitude", location.getLatitude());
            }
            o.put("temperature", "-200");
            o.put("windSpeed", "-200");
            o.put("photo", "data:image/png;base64,"+encodedImage);
            //Log.i("ENCODED IMAGE", encodedImage);
            o.put("id", 0);

        } catch (JSONException e) {
            Log.i("json", "json fail");
        }
        return o;
    }


    //Get the static map of where the image was taken from Google Static Maps API
    private void getCurrentMap(Location location) {
        String url = "";
        //getDirections();
        try {
            //For actual testing and app:
            //url = "http://maps.google.com/maps/api/staticmap?path=enc%3A"+polyline+"&maptype=satellite&size=800x800&markers=size:mid%7Ccolor:green%7C" + location.getLatitude() + "," + location.getLongitude() + "&key=" + API_KEY;

            //Temporary for displaying while outside of specified location:
            url = "http://maps.google.com/maps/api/staticmap?path=weight:10|enc%3A"+polyline+"&maptype=satellite&size=800x800&markers=size:large%7Ccolor:green%7C-35.776801, 174.266028&sensor=false&key=" + API_KEY;
        } catch (Exception e) {
            Log.i("URL ERROR", e.toString());
            return;
        }
        RequestQueue q = Volley.newRequestQueue(this);
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.i("MAP SUCCESS", "LETS GO!!");
                        ((ImageView) findViewById(R.id.map_image)).setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.i("MAP ERROR", error.toString());
                    }
                });
        q.add(request);
    }

    // In the future, if we need to find the polyline of a road.
    // Currently using the constant polyline of Otaika Valley Road so this method is not in use.
    private void getDirections() {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=-35.755098, 174.207892&destination=-35.780766, 174.307630&mode=driving&key="+API_KEY;

        RequestQueue q = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String s = "";
                        try {
                            s = response.getString("points");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("DIRECTIONS", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("DIRECTION ERROR", error.toString());

                    }
                });
        q.add(request);

    }


    //Helper method that converts coordinates from decimal to direction/degree/minute/second.
    //This is easier for the user to understand and looks better.
    //Could make a new class with static method to replace this method if this activity starts lagging.
    private String convertCoordinatesToDegrees (double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("Lat: S ");
        } else {
            builder.append("Lat: N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append("\n");

        if (longitude < 0) {
            builder.append("Lon: W ");
        } else {
            builder.append("Lon: E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }

}
