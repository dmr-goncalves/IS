package com.dmrg.isapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class UserUnregisterDevice extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    EditText etName;
    Button bUnRegister;
    String ipAddress;
    String port = "3000";
    Toast Pass;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;
    TextView tv;
    String username, name, password, email, date, devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_unregister_device);

        mToolbar = (Toolbar) findViewById(R.id.unRegDev_nav_action);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.user_unRegDev_drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            username = extras.getString("username");
            password = extras.getString("password");
            email = extras.getString("email");
            date = extras.getString("date");
            devices = extras.getString("devices");
            ipAddress = extras.getString("ip");
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.user_unRegDev_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.inflateHeaderView(R.layout.user_navigation_header);
        tv = (TextView) header.findViewById(R.id.UserName);
        tv.setText(username);
        etName = (EditText) findViewById(R.id.user_unRegDev_devName);


        bUnRegister = (Button) findViewById(R.id.user_unRegDev_button);
        bUnRegister.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_unRegDev_button:
                if (isEmpty(etName)) {
                    Toast.makeText(this, "Failed to enter one or more fields", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String device_name = etName.getText().toString();

                    String uri = "http://" + ipAddress + ":" + port + "/api/mobile/device/unRegisterDevice";

                    //POST the server
                    if (ipAddress.length() > 0 && port.length() > 0) {
                        new HttpRequestAsyncTask(v.getContext(), uri, Pass, username, device_name).execute();
                        break;
                    }
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Description: Send an HTTP Post request to a specified ip address and port.
     * Also send a parameter "parameterName" with the value of "parameterValue".
     *
     * @param _uri URI to post
     * @param name the name to send
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String _uri, String username, String name) {
        String serverResponse = "ERROR";

        try {
            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            URI uri = new URI(_uri);
            //System.out.println(uri);
            HttpPost postRequest = new HttpPost(); // create an HTTP GET object
            postRequest.setURI(uri); // set the URL of the GET request
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("platform", "Android"));
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(postRequest); // execute the request
            // get the ip address server's reply
            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            serverResponse = in.readLine();
            //System.err.println(serverResponse);
            // Close the connection
            content.close();
        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // URL syntax error
            serverResponse = e.getMessage();
            e.printStackTrace();
        }
        // return the server's reply/response text
        return serverResponse;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.toString()) {
            case "Home":
                Intent mainIntent1 = new Intent(UserUnregisterDevice.this, UserMainActivity.class);

                mainIntent1.putExtra("name", name);
                mainIntent1.putExtra("username", username);
                mainIntent1.putExtra("password", password);
                mainIntent1.putExtra("email", email);
                mainIntent1.putExtra("date", date);
                mainIntent1.putExtra("devices", devices);
                mainIntent1.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent1);
                UserUnregisterDevice.this.finish();
                break;


            case "Register Device":
                Intent mainIntent2 = new Intent(UserUnregisterDevice.this, UserRegisterDeviceActivity.class);

                mainIntent2.putExtra("name", name);
                mainIntent2.putExtra("username", username);
                mainIntent2.putExtra("password", password);
                mainIntent2.putExtra("email", email);
                mainIntent2.putExtra("date", date);
                mainIntent2.putExtra("devices", devices);
                mainIntent2.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent2);
                UserUnregisterDevice.this.finish();
                break;

            case "Stop Monitoring Device":
                Intent mainIntent3 = new Intent(UserUnregisterDevice.this, UserStopMonitoringActivity.class);

                mainIntent3.putExtra("name", name);
                mainIntent3.putExtra("username", username);
                mainIntent3.putExtra("password", password);
                mainIntent3.putExtra("email", email);
                mainIntent3.putExtra("date", date);
                mainIntent3.putExtra("devices", devices);
                mainIntent3.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent3);
                UserUnregisterDevice.this.finish();
                break;

            case "Remove Device":
                Intent mainIntent4 = new Intent(UserUnregisterDevice.this, UserRemoveDeviceActivity.class);

                mainIntent4.putExtra("name", name);
                mainIntent4.putExtra("username", username);
                mainIntent4.putExtra("password", password);
                mainIntent4.putExtra("email", email);
                mainIntent4.putExtra("date", date);
                mainIntent4.putExtra("devices", devices);
                mainIntent4.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent4);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case "Check Devices History":
                /*Intent mainIntent4 = new Intent(UserUnregisterDevice.this, UserCheckDeviceHistory.class);

                mainIntent4.putExtra("name", name);
                mainIntent4.putExtra("username", username);
                mainIntent4.putExtra("password", password);
                mainIntent4.putExtra("email", email);
                mainIntent4.putExtra("date", date);
                mainIntent4.putExtra("devices", devices);
                mainIntent4.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent4);
                UserUnregisterDevice.this.finish();*/
                Pass = Toast.makeText(getApplicationContext(), "In future patches", Toast.LENGTH_SHORT);
                Pass.show();
                break;

            case "Check Personal History":
                Intent mainIntent5 = new Intent(UserUnregisterDevice.this, UserCheckHistory.class);

                mainIntent5.putExtra("name", name);
                mainIntent5.putExtra("username", username);
                mainIntent5.putExtra("password", password);
                mainIntent5.putExtra("email", email);
                mainIntent5.putExtra("date", date);
                mainIntent5.putExtra("devices", devices);
                mainIntent5.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent5);
                UserUnregisterDevice.this.finish();
                break;

            case "Logoff":
                Intent mainIntent6 = new Intent(UserUnregisterDevice.this, UserLogoutActivity.class);

                mainIntent6.putExtra("username", username);
                mainIntent6.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent6);
                UserUnregisterDevice.this.finish();
                break;

            case "Start Monitoring Device":
                Intent mainIntent8 = new Intent(UserUnregisterDevice.this, UserStartMonitoringActivity.class);

                mainIntent8.putExtra("name", name);
                mainIntent8.putExtra("username", username);
                mainIntent8.putExtra("password", password);
                mainIntent8.putExtra("email", email);
                mainIntent8.putExtra("date", date);
                mainIntent8.putExtra("devices", devices);
                mainIntent8.putExtra("ip", ipAddress);

                UserUnregisterDevice.this.startActivity(mainIntent8);
                UserUnregisterDevice.this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        // declare variables needed
        private String requestReply;
        private Context context;
        private AlertDialog alertDialog;
        private String parameter;
        private String parameterValue;
        private String uri;
        private Toast toast;
        private String username;
        private String name;


        /**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         *
         * @param context   the application context, needed to create the dialog
         * @param _uri      the uri to post
         * @param t         Toast to print
         * @param _username username to send to the server
         * @param _name     name to send to the server
         */
        public HttpRequestAsyncTask(Context context, String _uri, Toast t, String _username, String _name) {
            this.uri = _uri;
            this.name = _name;
            this.context = context;
            this.toast = t;
            this.username = _username;
        }

        /**
         * Name: doInBackground
         * Description: Sends the request to the ip address
         *
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            /*alertDialog.setMessage("Data sent, waiting for reply from server...");
            if(!alertDialog.isShowing())
            {
                alertDialog.show();
            }*/
            requestReply = sendRequest(uri, username, name);
            return null;
        }

        /**
         * Name: onPostExecute
         * Description: This function is executed after the HTTP request returns from the ip address.
         * The function sets the dialog's message with the reply text from the server and display the dialog
         * if it's not displayed already (in case it was closed by accident);
         *
         * @param aVoid void parameter
         */
        @Override
        protected void onPostExecute(Void aVoid) {

            if (requestReply.equalsIgnoreCase("User Not Found")) {
                toast = Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.contains("refused")) {
                toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("Device Not Found")) {
                toast = Toast.makeText(context, "Device Not Found", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("Device Was Already Off")) {
                toast = Toast.makeText(context, "Device Was Already Off", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                try {
                    JSONObject user = new JSONObject(requestReply);
                    String name = user.getString("name");
                    String username = user.getString("username");
                    String password = user.getString("date_registered");
                    String email = user.getString("email");
                    String date = user.getString("date_registered");
                    String devices = user.getString("devices");

                    toast = Toast.makeText(context, "Device " + name + " Unregistered Successfully", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent mainIntent1 = new Intent(UserUnregisterDevice.this, UserMainActivity.class);

                    mainIntent1.putExtra("name", name);
                    mainIntent1.putExtra("username", username);
                    mainIntent1.putExtra("password", password);
                    mainIntent1.putExtra("email", email);
                    mainIntent1.putExtra("date", date);
                    mainIntent1.putExtra("devices", devices);
                    mainIntent1.putExtra("ip", ipAddress);

                    UserUnregisterDevice.this.startActivity(mainIntent1);
                    UserUnregisterDevice.this.finish();

                } catch (JSONException e) {
                    System.out.println("Error while parsing json -> " + e);
                }
            }
        }


        /**
         * Name: onPreExecute
         * Description: This function is executed before the HTTP request is sent to ip address.
         * The function will set the dialog's message and display the dialog.
         */
        @Override
        protected void onPreExecute() {
            /*alertDialog.setMessage("Sending data to server, please wait...");
            if(!alertDialog.isShowing())
            {
                alertDialog.show();
            }*/
        }

    }

}