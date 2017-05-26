package com.dmrg.isapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName, etPassword, etName, etEmail, etRepeatPassword;
    Button bRegister;
    RadioButton bUser, bProducer;
    String ipAddress;
    String port = "3000";
    Toast Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAddress = extras.getString("ip");
        }

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.name_register);
        etUserName = (EditText) findViewById(R.id.username_register);
        etPassword = (EditText) findViewById(R.id.password_register);

        etRepeatPassword = (EditText) findViewById(R.id.repeatPassword_register);
        etEmail = (EditText) findViewById(R.id.email_register);

        bRegister = (Button) findViewById(R.id.register_button);
        bRegister.setOnClickListener(this);

        bUser = (RadioButton) findViewById(R.id.user_radioButton_register);
        bUser.setOnClickListener(this);

        bProducer = (RadioButton) findViewById(R.id.producer_radioButton_register);
        bProducer.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                if (isEmpty(etPassword) || isEmpty(etRepeatPassword) || isEmpty(etUserName) || isEmpty(etName)) {
                    Toast.makeText(this, "Failed to enter one or more fields", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String name = etName.getText().toString();
                    String username = etUserName.getText().toString();
                    String pass = etPassword.getText().toString();
                    String mail = etEmail.getText().toString();
                    String repeatPassword = etRepeatPassword.getText().toString();
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("E dd/MMM/yyyy hh:mm:ss", Locale.ENGLISH);
                    String date_registered = df.format(c.getTime());

                    String uri = new String();
                    boolean userB = true;

                    if (bUser.isChecked() && !bProducer.isChecked()) {
                        uri = "http://" + ipAddress + ":" + port + "/api/mobile/user/register";
                        userB = true;

                        //POST the server
                        if (ipAddress.length() > 0 && port.length() > 0 && pass.equals(repeatPassword)) {
                            new HttpRequestAsyncTask(v.getContext(), userB, uri, Pass, name, username, pass, mail, date_registered).execute();
                            break;
                        }
                    } else if (bProducer.isChecked() && !bUser.isChecked()) {
                        uri = "http://" + ipAddress + ":" + port + "/api/mobile/producer/register";
                        userB = false;

                        //POST the server
                        if (ipAddress.length() > 0 && port.length() > 0 && pass.equals(repeatPassword)) {
                            new HttpRequestAsyncTask(v.getContext(), userB, uri, Pass, name, username, pass, mail, date_registered).execute();
                            break;
                        }
                    }

                }

            case R.id.user_radioButton_register:
                bProducer.setChecked(false);
                bUser.setChecked(true);
                break;

            case R.id.producer_radioButton_register:
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
     * @param _uri            URI to post
     * @param name            the name to send
     * @param username        the username to send
     * @param pass            the password to send
     * @param email           the email to send
     * @param date_registered the date to send
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String _uri, String name, String username, String pass, String email, String date_registered) {
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

            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", pass));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("date_registered", date_registered));

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
        private String requestReply;
        private Context context;
        private AlertDialog alertDialog;
        private String uri;
        private Toast toast;
        private String name;
        private String username;
        private String pass;
        private String email;
        private String date_registered;
        private boolean userB;

        /**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         *
         * @param context   the application context, needed to create the dialog
         * @param _userB    true if it's a user, false if it's a producer
         * @param _uri      the uri to post
         * @param t         Toast to print
         * @param _name     name to send to the server
         * @param _username username to send to the server
         * @param _pass     password to send to the server
         * @param _email    mail to send to the server
         * @param _date     date to send to the server
         */
        public HttpRequestAsyncTask(Context context, boolean _userB, String _uri, Toast t, String _name, String _username, String _pass, String _email, String _date) {
            this.uri = _uri;
            this.name = _name;
            this.username = _username;
            this.pass = _pass;
            this.email = _email;
            this.date_registered = _date;
            this.context = context;
            this.toast = t;
            this.userB = _userB;
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
            requestReply = sendRequest(uri, name, username, pass, email, date_registered);
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

            if(userB) {
                if (requestReply.equalsIgnoreCase("Error Registering")) {
                    toast = Toast.makeText(context, "Error While Registering", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.equals("User Already Exists")) {
                    toast = Toast.makeText(context, "User Already Exists", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.contains("refused")) {
                    toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.equals("User Registered Successfully")) {
                    toast = Toast.makeText(context, "User Was Successfully Registered", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent mainIntent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(mainIntent1);
                    RegisterActivity.this.finish();
                }
            }else{
                if (requestReply.equalsIgnoreCase("Error Registering")) {
                    toast = Toast.makeText(context, "Error While Registering", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.equals("Producer Already Exists")) {
                    toast = Toast.makeText(context, "Producer Already Exists", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.contains("refused")) {
                    toast = Toast.makeText(context, "Connection Refused", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (requestReply.equals("Producer Registered Successfully")) {
                    toast = Toast.makeText(context, "Producer Was Successfully Registered", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent mainIntent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(mainIntent1);
                    RegisterActivity.this.finish();
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
