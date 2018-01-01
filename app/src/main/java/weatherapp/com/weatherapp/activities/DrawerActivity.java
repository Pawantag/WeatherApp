package weatherapp.com.weatherapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import weatherapp.com.weatherapp.R;
import weatherapp.com.weatherapp.commonUtils.Configure;
import weatherapp.com.weatherapp.commonUtils.Utils;
import weatherapp.com.weatherapp.commonUtils.VolleySingleton;
import weatherapp.com.weatherapp.pojos.WeatherDetailsList;
import weatherapp.com.weatherapp.pojos.WeatherResponse;
import weatherapp.com.weatherapp.pojos.Weather_;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int statusCode;
    TextView orange, lightBlue;
    private ProgressBar progressbar;
    private EditText etxLocation;
    private TextView tvDateOne, tvDateTwo, tvDateThree, tvDateFour, tvTempOne, tvTempTwo, tvTempThree, tvTempFour, tvCurrentTemp, tvCity, tvWeatherStatus;
    private ImageView imgBackground, ivDrawerToggle, imgOne, imgTwo, imgThree, imgFour;
    private ArrayList<WeatherDetailsList> weatherDetailsLists;
    private RelativeLayout parentView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drawer);
        //Initialize all views.
        initViews();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);


        ivDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);

                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initViews() {
        ivDrawerToggle = findViewById(R.id.ivDrawerToggle);
        progressbar = findViewById(R.id.progressBar);
        imgBackground = findViewById(R.id.imgBackground);
        parentView = findViewById(R.id.mainRelativeLayout);

        orange = findViewById(R.id.orange);
        lightBlue = findViewById(R.id.lightBlue);

        //Dates
        tvDateOne = findViewById(R.id.tvDateOne);
        tvDateTwo = findViewById(R.id.tvDateTwo);
        tvDateThree = findViewById(R.id.tvDateThree);
        tvDateFour = findViewById(R.id.tvDateFour);

        //Temp
        tvTempOne = findViewById(R.id.tvTempOne);
        tvTempTwo = findViewById(R.id.tvTempTwo);
        tvTempThree = findViewById(R.id.tvTempThree);
        tvTempFour = findViewById(R.id.tvTempFour);

        //Images

        imgOne = findViewById(R.id.dot_one_id);
        imgTwo = findViewById(R.id.dot_two_id);
        imgThree = findViewById(R.id.dot_three_id);
        imgFour = findViewById(R.id.dot_four_id);

        //Title

        tvCurrentTemp = findViewById(R.id.tvCurrentTemp);
        tvCity = findViewById(R.id.tvCity);
        tvWeatherStatus = findViewById(R.id.tvWeatherStatus);

        getWeatherForecast("Pune");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change_city) {
            // Create custom dialog object
            final Dialog dialog = new Dialog(DrawerActivity.this);
            // Include dialog file
            dialog.setContentView(R.layout.dialog_select_city);
            dialog.setCanceledOnTouchOutside(false);

            etxLocation = dialog.findViewById(R.id.etxLocation);
            Button btnSearch = dialog.findViewById(R.id.btnSearch);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);


            btnSearch.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String location = etxLocation.getText().toString();

                    if (location != null && location.length() > 0) {
                        getWeatherForecast(location.trim());
                        dialog.dismiss();
                    } else {
                        etxLocation.setError("Please Enter Valid Location");
                        etxLocation.requestFocus();
                        return;
                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                }
            });


            dialog.show();


        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getWeatherForecast(String location) {
        statusCode = 0;
        String url = "";
        //Accept only Numbers
        if (location.matches("^[0-9]+$"))
            url = String.format(Configure.GET_WEATHER_DETAILS_BY_ZIP_URL, location.trim() + ",IN");
        //Accept only characters
        if (location.matches("[a-zA-Z]+\\.?"))
            url = String.format(Configure.GET_WEATHER_DETAILS_BY_NAME_URL, location.trim() + ",IN");
        //  Log.e("getYoPointsTransact: ", url);

        progressbar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {


                    Log.e("onResponse: ", "" + response.toString());

                    WeatherResponse mainResponse = new Gson().fromJson(response.toString(), WeatherResponse.class);

                    if (mainResponse != null) {

                        String city = mainResponse.getCity().getName();
                        String country = mainResponse.getCity().getCountry();
                        weatherDetailsLists = (ArrayList<WeatherDetailsList>) mainResponse.getWeatherDetailsList();


                        //  SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                        if (weatherDetailsLists != null) {

                            //DAY 1

                            WeatherDetailsList weatherDetailsList1 = weatherDetailsLists.get(0);
                            Weather_ weather1 = weatherDetailsList1.getWeather().get(0);

                            tvCurrentTemp.setText(String.format("%.2f°", weatherDetailsList1.getMain().getTemp()));
                            tvCity.setText(city + ", " + country);
                            tvWeatherStatus.setText(weather1.getMain());

                            if (weather1.getMain().contains("Clear")) {
                                imgBackground.setImageDrawable(getDrawable(R.drawable.ic_clear));
                                lightBlue.setVisibility(View.VISIBLE);
                                orange.setVisibility(View.GONE);
                            } else {
                                imgBackground.setImageDrawable(getDrawable(R.drawable.ic_cloudy));
                                lightBlue.setVisibility(View.GONE);
                                orange.setVisibility(View.VISIBLE);
                            }


                            tvDateOne.setText("Now");


                            Glide.with(DrawerActivity.this)
                                    .load(Configure.GET_ICON + weather1.getIcon() + ".png")
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(imgOne);


                            tvTempOne.setText(String.format("%.2f°", weatherDetailsList1.getMain().getTemp()));


                            //DAY 2

                            WeatherDetailsList weatherDetailsList2 = weatherDetailsLists.get(8);
                            Weather_ weather2 = weatherDetailsList2.getWeather().get(0);

                            //Date not coming into the proper format that's why im doing like  one..
                            String[] date2 = weatherDetailsList2.getDtTxt().trim().split(" ");

                            String dateFormatted2 = date2[0].substring(8);


                            tvTempTwo.setText(String.format("%.2f°", weatherDetailsList2.getMain().getTemp()));

                            tvDateTwo.setText(dateFormatted2);

                            Glide.with(DrawerActivity.this)
                                    .load(Configure.GET_ICON + weather2.getIcon() + ".png")
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(imgTwo);

                            //DAY 3

                            WeatherDetailsList weatherDetailsList3 = weatherDetailsLists.get(16);
                            Weather_ weather3 = weatherDetailsList3.getWeather().get(0);
                            String[] date3 = weatherDetailsList3.getDtTxt().trim().split(" ");
                            String dateFormatted3 = date3[0].substring(8);


                            tvTempThree.setText(String.format("%.2f°", weatherDetailsList3.getMain().getTemp()));

                            tvDateThree.setText(dateFormatted3);

                            Glide.with(DrawerActivity.this)
                                    .load(Configure.GET_ICON + weather3.getIcon() + ".png")
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(imgThree);


                            //DAY 4


                            WeatherDetailsList weatherDetailsList4 = weatherDetailsLists.get(24);
                            Weather_ weather4 = weatherDetailsList4.getWeather().get(0);
                            String[] date4 = weatherDetailsList4.getDtTxt().trim().split(" ");
                            String dateFormatted4 = date4[0].substring(8);


                            tvTempFour.setText(String.format("%.2f°", weatherDetailsList4.getMain().getTemp()));

                            tvDateFour.setText(dateFormatted4);

                            Glide.with(DrawerActivity.this)
                                    .load(Configure.GET_ICON + weather4.getIcon() + ".png")
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(imgFour);

                        }
                    }

                    progressbar.setVisibility(View.GONE);


                } catch (
                        Exception e)

                {
                    e.printStackTrace();
                    progressbar.setVisibility(View.GONE);
                }


            }


        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e(error.toString());
                progressbar.setVisibility(View.GONE);

                Snackbar.make(parentView, "Not Found !", Snackbar.LENGTH_LONG).show();

            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                try {
                    statusCode = volleyError.networkResponse.statusCode;

                } catch (Exception ex) {
                    statusCode = 400;
                }
                return super.parseNetworkError(volleyError);
            }


        };

        Utils.setVolleyRetryPolicy(jsonObjectRequest);
        VolleySingleton.getInstance(this).

                addToRequestQueue(jsonObjectRequest, "GET_WEATHER_DETAILS");
    }

}

