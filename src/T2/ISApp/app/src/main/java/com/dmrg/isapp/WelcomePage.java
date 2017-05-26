package com.dmrg.isapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
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

public class WelcomePage extends AppCompatActivity {
    public final static String UserName = "PREF_USERNAME";
    public final static String Mode = "PREF_MODE";

    // shared preferences objects used to save the IP address and port so that the user doesn't have to
    // type them next time he/she opens the app.
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    String ipAddressHotspot = "172.20.10.2";
    String ipAddressHome = "192.168.1.68";
    String ipAddress;
    String port = "3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ipAddress = ipAddressHotspot;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        sharedPreferences = getSharedPreferences("HELPER_PREFS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /*editor.clear();
        editor.commit();*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                if (sharedPreferences.getString(Mode, "").equalsIgnoreCase("User")) {
                    if (ipAddress.length() > 0 && port.length() > 0) {
                        String uri = "http://" + ipAddress + ":" + port + "/api/mobile/user/userInfo";
                        String username = sharedPreferences.getString(UserName, "");
                        new HttpRequestAsyncTask(getApplicationContext(), uri, true, username).execute();
                    }
                } else if (sharedPreferences.getString(Mode, "").equalsIgnoreCase("Producer")) {
                    if (ipAddress.length() > 0 && port.length() > 0) {
                        String uri = "http://" + ipAddress + ":" + port + "/api/mobile/producer/producerInfo";
                        String username = sharedPreferences.getString(UserName, "");
                        new HttpRequestAsyncTask(getApplicationContext(), uri, false, username).execute();
                    }
                } else {
                    Intent mainIntent = new Intent(WelcomePage.this, LoginActivity.class);
                    WelcomePage.this.startActivity(mainIntent);
                    WelcomePage.this.finish();
                }
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Description: Send an HTTP Post request to a specified ip address and port.
     * Also send a parameter "parameterName" with the value of "parameterValue".
     *
     * @param user the username to send
     * @param _uri URI to post
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String user, String _uri, String name) {
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
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        // declare variables needed
        private String requestReply, uri;
        private Context context;
        private AlertDialog alertDialog;
        private Toast toast;
        private String username;
        private boolean userB;

        /**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         *
         * @param context   the application context, needed to create the dialog
         * @param _uri      the uri to post
         * @param _username username to send to the server
         */
        public HttpRequestAsyncTask(Context context, String _uri, boolean _userB, String _username) {
            this.username = _username;
            this.context = context;
            this.uri = _uri;
            this.userB = _userB;

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
            requestReply = sendRequest(username, uri, null);
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

            if (requestReply.equalsIgnoreCase("refused")) {
                toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                if (userB) {
                    try {
                        JSONObject user = new JSONObject(requestReply);

                        String name = user.getString("name");
                        String username = user.getString("username");
                        String password = user.getString("password");
                        String email = user.getString("email");
                        String date = user.getString("date_registered");
                        String devices = user.getString("devices");

                        Intent mainIntent = new Intent(WelcomePage.this, UserMainActivity.class);

                        mainIntent.putExtra("name", name);
                        mainIntent.putExtra("username", username);
                        mainIntent.putExtra("password", password);
                        mainIntent.putExtra("email", email);
                        mainIntent.putExtra("date", date);
                        mainIntent.putExtra("devices", devices);
                        mainIntent.putExtra("ip", ipAddress);

                        WelcomePage.this.startActivity(mainIntent);
                        WelcomePage.this.finish();
                    } catch (JSONException e) {
                        System.out.println("Error while parsing json -> " + e);
                    }
                } else {
                    try {
                        JSONObject producer = new JSONObject(requestReply);

                        String name = producer.getString("name");
                        String username = producer.getString("username");
                        String password = producer.getString("password");
                        String email = producer.getString("email");
                        String date = producer.getString("date_registered");
                        String device_models = producer.getString("device_models");
                        String device_types = producer.getString("device_types");

                        Intent mainIntent = new Intent(WelcomePage.this, ProducerMainActivity.class);

                        mainIntent.putExtra("name", name);
                        mainIntent.putExtra("username", username);
                        mainIntent.putExtra("password", password);
                        mainIntent.putExtra("email", email);
                        mainIntent.putExtra("date", date);
                        mainIntent.putExtra("device_models", device_models);
                        mainIntent.putExtra("device_types", device_types);
                        mainIntent.putExtra("ip", ipAddress);

                        WelcomePage.this.startActivity(mainIntent);
                        WelcomePage.this.finish();
                    } catch (JSONException e) {
                        System.out.println("Error while parsing json -> " + e);
                    }
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
