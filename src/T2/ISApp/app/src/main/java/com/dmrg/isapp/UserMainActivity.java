package com.dmrg.isapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import java.util.StringTokenizer;

public class UserMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    String name, username, password, email, date, devices;
    ListView lv;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;
    String ipAddress;
    String port = "3000";
    Toast Pass;
    TextView tv;
    PopupWindow mPopupWindow;
    RelativeLayout mRelativeLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_main);
        lv = (ListView) findViewById(R.id.user_main_DeviceList);


        mToolbar = (Toolbar) findViewById(R.id.user_main_nav_action);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerUser);
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

        navigationView = (NavigationView) findViewById(R.id.user_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.inflateHeaderView(R.layout.user_navigation_header);
        tv = (TextView) header.findViewById(R.id.UserName);
        tv.setText(username);

        StringTokenizer st = new StringTokenizer(devices, ",");
        StringTokenizer st2;

        ArrayList<String> lst = new ArrayList<String>();
        String tokenSt1, tokenSt2;

        while (st.hasMoreTokens()) {
            tokenSt1 = st.nextToken();
            st2 = new StringTokenizer(tokenSt1, "\"[");

            while (st2.hasMoreTokens()) {
                tokenSt2 = st2.nextToken();

                if (!tokenSt2.equals("]")) {
                    lst.add(tokenSt2);
                }
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_custom_layout, lst);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        TextView tv = (TextView) arg1;
        if (ipAddress.length() > 0 && port.length() > 0) {
            String uri = "http://" + ipAddress + ":" + port + "/api/mobile/device/deviceInfo";
            new HttpRequestAsyncTask2(getApplicationContext(), uri, Pass, tv.getText().toString(), username).execute();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
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
    public String sendRequest4(String _uri, String username, String name) {
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

    /**
     * Description: Send an HTTP Post request to a specified ip address and port.
     * Also send a parameter "parameterName" with the value of "parameterValue".
     *
     * @param _uri URI to post
     * @param name the name to send
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest3(String _uri, String username, String name) {
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

    /**
     * Description: Send an HTTP Post request to a specified ip address and port.
     * Also send a parameter "parameterName" with the value of "parameterValue".
     *
     * @param user the username to send
     * @param _uri URI to post
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest2(String user, String _uri, String name) {
        String serverResponse = "ERROR";

        try {
            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            URI uri = new URI(_uri);
            //System.out.println(_uri);
            HttpPost postRequest = new HttpPost(); // create an HTTP GET object
            postRequest.setURI(uri); // set the URL of the GET request
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("platform", "Android"));
            nameValuePairs.add(new BasicNameValuePair("username", user));
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
            case "Register Device":
                Intent mainIntent1 = new Intent(UserMainActivity.this, UserRegisterDeviceActivity.class);

                mainIntent1.putExtra("name", name);
                mainIntent1.putExtra("username", username);
                mainIntent1.putExtra("password", password);
                mainIntent1.putExtra("email", email);
                mainIntent1.putExtra("date", date);
                mainIntent1.putExtra("devices", devices);
                mainIntent1.putExtra("ip", ipAddress);

                UserMainActivity.this.startActivity(mainIntent1);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                //UserMainActivity.this.finish();
                break;

            case "Stop Monitoring Device":
                Intent mainIntent2 = new Intent(UserMainActivity.this, UserStopMonitoringActivity.class);

                mainIntent2.putExtra("name", name);
                mainIntent2.putExtra("username", username);
                mainIntent2.putExtra("password", password);
                mainIntent2.putExtra("email", email);
                mainIntent2.putExtra("date", date);
                mainIntent2.putExtra("devices", devices);
                mainIntent2.putExtra("ip", ipAddress);

                UserMainActivity.this.startActivity(mainIntent2);
                UserMainActivity.this.finish();
                break;

            case "Start Monitoring Device":
                Intent mainIntent3 = new Intent(UserMainActivity.this, UserStartMonitoringActivity.class);

                mainIntent3.putExtra("name", name);
                mainIntent3.putExtra("username", username);
                mainIntent3.putExtra("password", password);
                mainIntent3.putExtra("email", email);
                mainIntent3.putExtra("date", date);
                mainIntent3.putExtra("devices", devices);
                mainIntent3.putExtra("ip", ipAddress);

                UserMainActivity.this.startActivity(mainIntent3);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                //UserMainActivity.this.finish();

                break;

            case "Unregister Device":
                Intent mainIntent4 = new Intent(UserMainActivity.this, UserUnregisterDevice.class);

                mainIntent4.putExtra("name", name);
                mainIntent4.putExtra("username", username);
                mainIntent4.putExtra("password", password);
                mainIntent4.putExtra("email", email);
                mainIntent4.putExtra("date", date);
                mainIntent4.putExtra("devices", devices);
                mainIntent4.putExtra("ip", ipAddress);

                UserMainActivity.this.startActivity(mainIntent4);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case "Remove Device":
                Intent mainIntent5 = new Intent(UserMainActivity.this, UserRemoveDeviceActivity.class);

                mainIntent5.putExtra("name", name);
                mainIntent5.putExtra("username", username);
                mainIntent5.putExtra("password", password);
                mainIntent5.putExtra("email", email);
                mainIntent5.putExtra("date", date);
                mainIntent5.putExtra("devices", devices);
                mainIntent5.putExtra("ip", ipAddress);

                UserMainActivity.this.startActivity(mainIntent5);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case "Check Devices History":
                /*Intent mainIntent5 = new Intent(UserMainActivity.this, UserCheckDeviceHistory.class);

                mainIntent5.putExtra("name", name);
                mainIntent5.putExtra("username", username);
                mainIntent5.putExtra("password", password);
                mainIntent5.putExtra("email", email);
                mainIntent5.putExtra("date", date);
                mainIntent5.putExtra("devices", devices);
                mainIntent5.putExtra("ip", ipAddress);

                UserMainActivity.this.startActivity(mainIntent5);
                UserMainActivity.this.finish();*/
                Pass = Toast.makeText(getApplicationContext(), "In future patches", Toast.LENGTH_SHORT);
                Pass.show();
                break;


            case "Check Personal History":
                Intent mainIntent6 = new Intent(UserMainActivity.this, UserCheckHistory.class);

                mainIntent6.putExtra("name", name);
                mainIntent6.putExtra("username", username);
                mainIntent6.putExtra("password", password);
                mainIntent6.putExtra("email", email);
                mainIntent6.putExtra("date", date);
                mainIntent6.putExtra("devices", devices);
                mainIntent6.putExtra("ip", ipAddress);

                UserMainActivity.this.startActivity(mainIntent6);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            case "Logoff":
                Intent mainIntent7 = new Intent(getApplicationContext(), UserLogoutActivity.class);

                mainIntent7.putExtra("ip", ipAddress);
                mainIntent7.putExtra("username", username);

                UserMainActivity.this.startActivity(mainIntent7);
                UserMainActivity.this.finish();
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
            if (requestReply.contains("Device Was On or Off but not Unmonitored")) {
                toast = Toast.makeText(context, "Device Was On or Off but not Unmonitored", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("User Not Found")) {
                toast = Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.contains("refused")) {
                toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("Device Not Found")) {
                toast = Toast.makeText(context, "Device Not Found", Toast.LENGTH_SHORT);
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

                    toast = Toast.makeText(context, "Successfully started monitoring device " + name, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent mainIntent1 = new Intent(UserMainActivity.this, UserMainActivity.class);

                    mainIntent1.putExtra("name", name);
                    mainIntent1.putExtra("username", username);
                    mainIntent1.putExtra("password", password);
                    mainIntent1.putExtra("email", email);
                    mainIntent1.putExtra("date", date);
                    mainIntent1.putExtra("devices", devices);
                    mainIntent1.putExtra("ip", ipAddress);

                    UserMainActivity.this.startActivity(mainIntent1);
                    UserMainActivity.this.finish();

                } catch (JSONException e) {
                    e.printStackTrace();
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

    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask3 extends AsyncTask<Void, Void, Void> {

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
        public HttpRequestAsyncTask3(Context context, String _uri, Toast t, String _username, String _name) {
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
            requestReply = sendRequest3(uri, username, name);
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
                toast = Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.contains("refused")) {
                toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("Device Not Found")) {
                toast = Toast.makeText(context, "Device Not Found", Toast.LENGTH_SHORT);
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

                    toast = Toast.makeText(context, "Successfully stopped monitoring device " + name, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent mainIntent1 = new Intent(UserMainActivity.this, UserMainActivity.class);

                    mainIntent1.putExtra("name", name);
                    mainIntent1.putExtra("username", username);
                    mainIntent1.putExtra("password", password);
                    mainIntent1.putExtra("email", email);
                    mainIntent1.putExtra("date", date);
                    mainIntent1.putExtra("devices", devices);
                    mainIntent1.putExtra("ip", ipAddress);

                    UserMainActivity.this.startActivity(mainIntent1);
                    UserMainActivity.this.finish();

                } catch (JSONException e) {
                    e.printStackTrace();
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

    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask4 extends AsyncTask<Void, Void, Void> {

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
        public HttpRequestAsyncTask4(Context context, String _uri, Toast t, String _username, String _name) {
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
            requestReply = sendRequest4(uri, username, name);
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
            } else if (requestReply.equalsIgnoreCase("Device Not Removed")) {
                toast = Toast.makeText(context, "Device Not Removed", Toast.LENGTH_SHORT);
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

                    toast = Toast.makeText(context, "Device " + name + " Removed Successfully", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent mainIntent1 = new Intent(UserMainActivity.this, UserMainActivity.class);

                    mainIntent1.putExtra("name", name);
                    mainIntent1.putExtra("username", username);
                    mainIntent1.putExtra("password", password);
                    mainIntent1.putExtra("email", email);
                    mainIntent1.putExtra("date", date);
                    mainIntent1.putExtra("devices", devices);
                    mainIntent1.putExtra("ip", ipAddress);

                    UserMainActivity.this.startActivity(mainIntent1);
                    UserMainActivity.this.finish();

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

    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask2 extends AsyncTask<Void, Void, Void> {

        // declare variables needed
        private String requestReply, uri;
        private Context context;
        private AlertDialog alertDialog;
        private Toast toast;
        private String user;
        private String devName;

        /**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         *
         * @param context  the application context, needed to create the dialog
         * @param _uri     the uri to post
         * @param t        Toast to print
         * @param _user    username to send to the server
         * @param _devName name of the device to send to the server
         */
        public HttpRequestAsyncTask2(Context context, String _uri, Toast t, String _devName, String _user) {
            this.user = _user;
            this.context = context;
            this.toast = t;
            this.uri = _uri;
            this.devName = _devName;

            alertDialog = new AlertDialog.Builder(this.context)
                    .setTitle("HTTP Response From IP Address:")
                    .setCancelable(true)
                    .create();
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
            requestReply = sendRequest2(user, uri, devName);
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


            if (requestReply.equalsIgnoreCase("Device Not Found")) {
                toast = Toast.makeText(context, "Device Not Found", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equals("User Does Not Have That Device")) {
                toast = Toast.makeText(context, "User Does Not Have That Device", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.contains("refused")) {
                toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                toast.show();
            } else if (requestReply.equalsIgnoreCase("User Not Found")) {
                toast = Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT);
                toast.show();
            } else {

                try {
                    JSONObject device = new JSONObject(requestReply);

                    String name = device.getString("name");
                    String device_type = device.getString("device_type");
                    String producer = device.getString("producer");
                    String model = device.getString("model");
                    String current_state = device.getString("current_state");

                    mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);

                    // Initialize a new instance of LayoutInflater service
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                    // Inflate the custom layout/view
                    View customView = inflater.inflate(R.layout.activity_user_device_info_pop, null);

                    // Initialize a new instance of popup window
                    mPopupWindow = new PopupWindow(
                            customView,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                            false
                    );

                    mDrawerLayout.setBackgroundDrawable(new ColorDrawable(0xb0000000));
                    mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                    mPopupWindow.setOutsideTouchable(true);

                    final TextView tvDevName, tvDevType, tvModel, tvCurrentState, tvProducer;

                    tvDevName = (TextView) customView.findViewById(R.id.tvDevName);
                    tvDevName.setText(name);


                    tvDevType = (TextView) customView.findViewById(R.id.tvDevType);
                    tvDevType.setText(device_type);

                    tvModel = (TextView) customView.findViewById(R.id.tvDevModel);
                    tvModel.setText(model);

                    tvProducer = (TextView) customView.findViewById(R.id.tvDevProducer);
                    tvProducer.setText(producer);

                    tvCurrentState = (TextView) customView.findViewById(R.id.tvDevCurrentState);
                    tvCurrentState.setText(current_state);

                    Button b = (Button) customView.findViewById(R.id.startOrStopMon);

                    if (current_state.equalsIgnoreCase("Unmonitored")) {
                        b.setText("Start Monitoring");
                    } else if (current_state.equalsIgnoreCase("On")) {
                        b.setText("Stop Monitoring");
                    }


                    // Set a click listener for the start / stop monitoring device button
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button b3 = (Button) v;
                            switch (b3.getText().toString()) {
                                case "Stop Monitoring":
                                    String uri = "http://" + ipAddress + ":" + port + "/api/mobile/device/stopMonitoringDevice";

                                    //POST the server
                                    if (ipAddress.length() > 0 && port.length() > 0) {
                                        new HttpRequestAsyncTask3(v.getContext(), uri, Pass, username, tvDevName.getText().toString()).execute();
                                        break;
                                    }
                                case "Start Monitoring":
                                    String uri2 = "http://" + ipAddress + ":" + port + "/api/mobile/device/reMonitorDevice";

                                    //POST the server
                                    if (ipAddress.length() > 0 && port.length() > 0) {
                                        new HttpRequestAsyncTask(v.getContext(), uri2, Pass, username, tvDevName.getText().toString()).execute();
                                        break;
                                    }

                            }
                        }
                    });


                    Button b2 = (Button) customView.findViewById(R.id.unRegDev);

                    if (current_state.equalsIgnoreCase("Off")) {
                        b2.setText("Restart");
                    } else if (current_state.equalsIgnoreCase("On") || current_state.equalsIgnoreCase("Unmonitored")) {
                        b2.setText("Unregister");
                    }

                    // Set a click listener for the unregister device button
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Button b3 = (Button) v;
                            switch (b3.getText().toString()) {
                                case "Unregister":
                                    String uri = "http://" + ipAddress + ":" + port + "/api/mobile/device/unRegisterDevice";

                                    //POST the server
                                    if (ipAddress.length() > 0 && port.length() > 0) {
                                        new HttpRequestAsyncTask4(v.getContext(), uri, Pass, username, tvDevName.getText().toString()).execute();
                                        break;
                                    }

                                case "Restart":
                                    String uri2 = "http://" + ipAddress + ":" + port + "/api/mobile/device/reMonitorDevice";

                                    //POST the server
                                    if (ipAddress.length() > 0 && port.length() > 0) {
                                        new HttpRequestAsyncTask(v.getContext(), uri2, Pass, username, tvDevName.getText().toString()).execute();
                                        break;
                                    }
                            }
                        }
                    });

                    // Get a reference for the custom view close button
                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

                    // Set a click listener for the popup window close button
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Dismiss the popup window
                            mPopupWindow.dismiss();
                            mDrawerLayout.setBackgroundDrawable(null);
                        }
                    });

                    mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                        @Override
                        public void onDismiss() {
                            mPopupWindow.dismiss();
                            mDrawerLayout.setBackgroundDrawable(null);
                        }
                    });

                    // Finally, show the popup window at the center location of root relative layout
                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);


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


