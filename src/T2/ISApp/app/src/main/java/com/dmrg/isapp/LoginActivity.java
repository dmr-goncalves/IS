package com.dmrg.isapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String UserName = "PREF_USERNAME";
    public final static String Mode = "PREF_MODE";

    // shared preferences objects used to save the IP address and port so that the user doesn't have to
    // type them next time he/she opens the app.
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    TextView bRegister;
    Button bLogin;
    RadioButton bUser, bProducer;

    EditText etUserName, etPassword;
    String ipAddressHotspot = "172.20.10.2";
    String ipAddressHome = "192.168.1.68";
    String ipAddress;
    String port = "3000";

    Toast Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ipAddress = ipAddressHotspot;

        etUserName = (EditText) findViewById(R.id.username_login);
        etPassword = (EditText) findViewById(R.id.password_login);

        bLogin = (Button) findViewById(R.id.login_button);
        bLogin.setOnClickListener(this);

        bRegister = (TextView) findViewById(R.id.register_button_inLogin);
        bRegister.setOnClickListener(this);

        bUser = (RadioButton) findViewById(R.id.user_radioButton_login);
        bUser.setOnClickListener(this);
        bUser.setChecked(true);

        bProducer = (RadioButton) findViewById(R.id.producer_radioButton_login);
        bProducer.setOnClickListener(this);
        bProducer.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: //Login
                String user = etUserName.getText().toString();
                String pass = etPassword.getText().toString();//getHash(etPassword.getText().toString());

                if (isEmpty(etUserName) || isEmpty(etPassword)) {
                    Pass = Toast.makeText(this, "One or more fields are not filled!", Toast.LENGTH_SHORT);
                    Pass.show();
                    break;
                } else {
                    //Send POST to server
                    if (ipAddress.length() > 0 && port.length() > 0) {
                        if (bUser.isChecked() && !bProducer.isChecked()) {
                            String uri = "http://" + ipAddress + ":" + port + "/api/mobile/user/login";
                            new HttpRequestAsyncTask(v.getContext(), true, uri, Pass, user, pass).execute();
                            break;
                        } else if (bProducer.isChecked() && !bUser.isChecked()) {
                            String uri = "http://" + ipAddress + ":" + port + "/api/mobile/producer/login";
                            new HttpRequestAsyncTask(v.getContext(), false, uri, Pass, user, pass).execute();
                            break;
                        }
                    }
                }
            case R.id.register_button_inLogin: //Register
                Intent mainIntent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                mainIntent1.putExtra("ip", ipAddress);
                LoginActivity.this.startActivity(mainIntent1);
                break;

            case R.id.user_radioButton_login:
                bProducer.setChecked(false);
                bUser.setChecked(true);
                break;

            case R.id.producer_radioButton_login:
                bProducer.setChecked(true);
                bUser.setChecked(false);
                break;
        }
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
     * @param user the username to send
     * @param pass the password to send
     * @param _uri URI to post
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String user, String pass, String _uri) {
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
            nameValuePairs.add(new BasicNameValuePair("password", pass));
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
        private String user;
        private String pass;
        private boolean userB;

        /**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         *
         * @param context the application context, needed to create the dialog
         * @param _userB  true if it's a user, false if it's a producer
         * @param _uri    the uri to post
         * @param t       Toast to print
         * @param _user   username to send to the server
         * @param _pass   password to send to the server
         */
        public HttpRequestAsyncTask(Context context, boolean _userB, String _uri, Toast t, String _user, String _pass) {
            this.user = _user;
            this.pass = _pass;
            this.context = context;
            this.toast = t;
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
            requestReply = sendRequest(user, pass, uri);
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

            if (userB) {

                System.out.println("rqReply -> " + requestReply);
                if (requestReply.equalsIgnoreCase("User Not Found Or Wrong Password")) {
                    toast = Toast.makeText(context, "User Not Found Or Wrong Password", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.equals("User Was Already Logged In")) {
                    toast = Toast.makeText(context, "User Was Already Logged In", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.contains("refused")) {
                    toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    try {
                        JSONObject user = new JSONObject(requestReply);
                        String name = user.getString("name");
                        String username = user.getString("username");
                        String password = user.getString("password");
                        String email = user.getString("email");
                        String date = user.getString("date_registered");
                        String devices = user.getString("devices");

                        sharedPreferences = getSharedPreferences("HELPER_PREFS", Context.MODE_PRIVATE);
                        editor = sharedPreferences.edit();

                        editor.putString(Mode, "User");
                        editor.putString(UserName, username);

                        editor.commit();

                        toast = Toast.makeText(context, "Welcome " + username, Toast.LENGTH_LONG);
                        toast.show();

                        Intent mainIntent1 = new Intent(LoginActivity.this, UserMainActivity.class);

                        mainIntent1.putExtra("name", name);
                        mainIntent1.putExtra("username", username);
                        mainIntent1.putExtra("password", password);
                        mainIntent1.putExtra("email", email);
                        mainIntent1.putExtra("date", date);
                        mainIntent1.putExtra("devices", devices);
                        mainIntent1.putExtra("ip", ipAddress);

                        LoginActivity.this.startActivity(mainIntent1);
                        LoginActivity.this.finish();
                    } catch (JSONException e) {
                        System.out.println("Error while parsing json -> " + e);
                    }
                }
            } else {
                if (requestReply.equalsIgnoreCase("Producer Not Found Or Wrong Password")) {
                    toast = Toast.makeText(context, "Producer Not Found Or Wrong Password", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.equals("Producer Was Already Logged In")) {
                    toast = Toast.makeText(context, "Producer Was Already Logged In", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.contains("refused")) {
                    toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                    toast.show();
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

                        toast = Toast.makeText(context, "Welcome " + username, Toast.LENGTH_LONG);
                        toast.show();

                        sharedPreferences = getSharedPreferences("HELPER_PREFS", Context.MODE_PRIVATE);
                        editor = sharedPreferences.edit();

                        editor.putString(Mode, "Producer");
                        editor.putString(UserName, username);

                        editor.commit();

                        Intent mainIntent1 = new Intent(LoginActivity.this, ProducerMainActivity.class);

                        mainIntent1.putExtra("name", name);
                        mainIntent1.putExtra("username", username);
                        mainIntent1.putExtra("password", password);
                        mainIntent1.putExtra("email", email);
                        mainIntent1.putExtra("date", date);
                        mainIntent1.putExtra("device_models", device_models);
                        mainIntent1.putExtra("device_types", device_types);
                        mainIntent1.putExtra("ip", ipAddress);

                        LoginActivity.this.startActivity(mainIntent1);
                        LoginActivity.this.finish();
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
