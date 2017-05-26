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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class UserCheckDeviceHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_user_check_device_history);

        mToolbar = (Toolbar) findViewById(R.id.checkDevH_nav_action);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.checkDevH_DrawerUser);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.checkDevH_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.inflateHeaderView(R.layout.user_navigation_header);
        tv = (TextView) header.findViewById(R.id.UserName);
        tv.setText(username);

        String uri = "http://" + ipAddress + ":" + port + "/api/mobile/user/devices";

        //POST the server
        if (ipAddress.length() > 0 && port.length() > 0) {
            new HttpRequestAsyncTask(getApplicationContext(), uri, Pass, username).execute();
        }

    }

    @Override
    public void onBackPressed() {

        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Description: Send an HTTP Post request to a specified ip address and port.
     * Also send a parameter "parameterName" with the value of "parameterValue".
     *
     * @param _uri URI to post
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String _uri, String username) {
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
                Intent mainIntent1 = new Intent(UserCheckDeviceHistory.this, UserRegisterDeviceActivity.class);

                mainIntent1.putExtra("name", name);
                mainIntent1.putExtra("username", username);
                mainIntent1.putExtra("password", password);
                mainIntent1.putExtra("email", email);
                mainIntent1.putExtra("date", date);
                mainIntent1.putExtra("devices", devices);
                mainIntent1.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent1);
                UserCheckDeviceHistory.this.finish();
                break;

            case "Register Device":
                Intent mainIntent2 = new Intent(UserCheckDeviceHistory.this, UserRegisterDeviceActivity.class);

                mainIntent2.putExtra("name", name);
                mainIntent2.putExtra("username", username);
                mainIntent2.putExtra("password", password);
                mainIntent2.putExtra("email", email);
                mainIntent2.putExtra("date", date);
                mainIntent2.putExtra("devices", devices);
                mainIntent2.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent2);
                UserCheckDeviceHistory.this.finish();
                break;

            case "Stop Monitoring Device":
                Intent mainIntent3 = new Intent(UserCheckDeviceHistory.this, UserStopMonitoringActivity.class);

                mainIntent3.putExtra("name", name);
                mainIntent3.putExtra("username", username);
                mainIntent3.putExtra("password", password);
                mainIntent3.putExtra("email", email);
                mainIntent3.putExtra("date", date);
                mainIntent3.putExtra("devices", devices);
                mainIntent3.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent3);
                UserCheckDeviceHistory.this.finish();
                break;

            case "Unregister Device":
                Intent mainIntent4 = new Intent(UserCheckDeviceHistory.this, UserUnregisterDevice.class);

                mainIntent4.putExtra("name", name);
                mainIntent4.putExtra("username", username);
                mainIntent4.putExtra("password", password);
                mainIntent4.putExtra("email", email);
                mainIntent4.putExtra("date", date);
                mainIntent4.putExtra("devices", devices);
                mainIntent4.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent4);
                UserCheckDeviceHistory.this.finish();
                break;

            case "Remove Device":
                Intent mainIntent5 = new Intent(UserCheckDeviceHistory.this, UserRemoveDeviceActivity.class);

                mainIntent5.putExtra("name", name);
                mainIntent5.putExtra("username", username);
                mainIntent5.putExtra("password", password);
                mainIntent5.putExtra("email", email);
                mainIntent5.putExtra("date", date);
                mainIntent5.putExtra("devices", devices);
                mainIntent5.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent5);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case "Check Personal History":
                Intent mainIntent6 = new Intent(UserCheckDeviceHistory.this, UserCheckHistory.class);

                mainIntent6.putExtra("name", name);
                mainIntent6.putExtra("username", username);
                mainIntent6.putExtra("password", password);
                mainIntent6.putExtra("email", email);
                mainIntent6.putExtra("date", date);
                mainIntent6.putExtra("devices", devices);
                mainIntent6.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent6);
                UserCheckDeviceHistory.this.finish();
                break;

            case "Logoff":
                Intent mainIntent7 = new Intent(UserCheckDeviceHistory.this, UserLogoutActivity.class);

                mainIntent7.putExtra("username", username);
                mainIntent7.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent7);
                UserCheckDeviceHistory.this.finish();
                break;

            case "Start Monitoring Device":
                Intent mainIntent8 = new Intent(UserCheckDeviceHistory.this, UserStartMonitoringActivity.class);

                mainIntent8.putExtra("name", name);
                mainIntent8.putExtra("username", username);
                mainIntent8.putExtra("password", password);
                mainIntent8.putExtra("email", email);
                mainIntent8.putExtra("date", date);
                mainIntent8.putExtra("devices", devices);
                mainIntent8.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent8);
                UserCheckDeviceHistory.this.finish();
                break;
        }
        return true;

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
         */
        public HttpRequestAsyncTask(Context context, String _uri, Toast t, String _username) {
            this.uri = _uri;

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
            requestReply = sendRequest(uri, username);
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
            if (requestReply.contains("User Was Successfully Logged Off")) {
                toast = Toast.makeText(context, "User Was Successfully Logged Off", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("User Was Not Logged In")) {
                toast = Toast.makeText(context, "User Was Not Logged In", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("User not found")) {
                toast = Toast.makeText(context, "User not found", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.contains("refused")) {
                toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("Device not found")) {
                toast = Toast.makeText(context, "Device not found", Toast.LENGTH_SHORT);
                toast.show();
            } else {

                Intent mainIntent1 = new Intent(UserCheckDeviceHistory.this, UserMainActivity.class);

                mainIntent1.putExtra("name", name);
                mainIntent1.putExtra("username", username);
                mainIntent1.putExtra("password", password);
                mainIntent1.putExtra("email", email);
                mainIntent1.putExtra("date", date);
                mainIntent1.putExtra("devices", devices);
                mainIntent1.putExtra("ip", ipAddress);

                UserCheckDeviceHistory.this.startActivity(mainIntent1);
                UserCheckDeviceHistory.this.finish();

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