package com.dmrg.isapp;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.StringTokenizer;

public class ProducerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String name, username, password, email, date, device_types, device_models;
    ListView lv, lv2;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;
    String ipAddress;
    String port = "3000";
    Toast Pass;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_producer_main);
        lv = (ListView) findViewById(R.id.producer_main_DeviceTypeList);
        lv2 = (ListView) findViewById(R.id.producer_main_DeviceModelsList);

        mToolbar = (Toolbar) findViewById(R.id.producer_main_nav_action);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerProducer);
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
            device_types = extras.getString("device_types");
            device_models = extras.getString("device_models");
            ipAddress = extras.getString("ip");
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.producer_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.inflateHeaderView(R.layout.user_navigation_header);
        tv = (TextView) header.findViewById(R.id.UserName);
        tv.setText(username);


        StringTokenizer st = new StringTokenizer(device_types, ",");
        StringTokenizer st2;

        ArrayList<String> lst = new ArrayList<String>();
        String tokenSt1, tokenSt2;

        while (st.hasMoreTokens()) {
            tokenSt1 = st.nextToken();
            st2 = new StringTokenizer(tokenSt1, "\"[");

            while (st2.hasMoreTokens()) {
                tokenSt2 = st2.nextToken();

                if (!tokenSt2.equals("]")) {
                    //System.out.println("device type -> " + tokenSt2);
                    lst.add(tokenSt2);
                }
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_custom_layout, lst);

        lv.setAdapter(adapter);

        StringTokenizer st3 = new StringTokenizer(device_models, ",");
        StringTokenizer st4;

        ArrayList<String> lst2 = new ArrayList<String>();
        String tokenSt3, tokenSt4;

        while (st3.hasMoreTokens()) {
            tokenSt3 = st3.nextToken();
            st4 = new StringTokenizer(tokenSt3, "\"[");

            while (st4.hasMoreTokens()) {
                tokenSt4 = st4.nextToken();

                if (!tokenSt4.equals("]")) {
                    //System.out.println("device models -> " + tokenSt4);
                    lst2.add(tokenSt4);
                }
            }
        }

        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.my_custom_layout, lst2);

        lv2.setAdapter(adapter2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.toString()) {
            case "Register Device Type":
                Intent mainIntent1 = new Intent(ProducerMainActivity.this, ProducerRegisterDeviceType.class);

                mainIntent1.putExtra("name", name);
                mainIntent1.putExtra("username", username);
                mainIntent1.putExtra("password", password);
                mainIntent1.putExtra("email", email);
                mainIntent1.putExtra("date", date);
                mainIntent1.putExtra("device_types", device_types);
                mainIntent1.putExtra("device_models", device_models);
                mainIntent1.putExtra("ip", ipAddress);

                ProducerMainActivity.this.startActivity(mainIntent1);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                //ProducerMainActivity.this.finish();
                break;

            case "Remove Device Type":
                Intent mainIntent2 = new Intent(ProducerMainActivity.this, ProducerUnregisterDeviceType.class);

                mainIntent2.putExtra("name", name);
                mainIntent2.putExtra("username", username);
                mainIntent2.putExtra("password", password);
                mainIntent2.putExtra("email", email);
                mainIntent2.putExtra("date", date);
                mainIntent2.putExtra("device_types", device_types);
                mainIntent2.putExtra("device_models", device_models);
                mainIntent2.putExtra("ip", ipAddress);

                ProducerMainActivity.this.startActivity(mainIntent2);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                //ProducerMainActivity.this.finish();
                break;

            case "Register Device Model":
                Intent mainIntent3 = new Intent(ProducerMainActivity.this, ProducerRegisterDeviceModel.class);

                mainIntent3.putExtra("name", name);
                mainIntent3.putExtra("username", username);
                mainIntent3.putExtra("password", password);
                mainIntent3.putExtra("email", email);
                mainIntent3.putExtra("date", date);
                mainIntent3.putExtra("device_types", device_types);
                mainIntent3.putExtra("device_models", device_models);
                mainIntent3.putExtra("ip", ipAddress);

                ProducerMainActivity.this.startActivity(mainIntent3);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                //ProducerMainActivity.this.finish();
                break;

            case "Remove Device Model":
                Intent mainIntent4 = new Intent(ProducerMainActivity.this, ProducerUnregisterDeviceModel.class);

                mainIntent4.putExtra("name", name);
                mainIntent4.putExtra("username", username);
                mainIntent4.putExtra("password", password);
                mainIntent4.putExtra("email", email);
                mainIntent4.putExtra("date", date);
                mainIntent4.putExtra("device_types", device_types);
                mainIntent4.putExtra("device_models", device_models);
                mainIntent4.putExtra("ip", ipAddress);

                ProducerMainActivity.this.startActivity(mainIntent4);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                //ProducerMainActivity.this.finish();
                break;

            case "Check Device History":
                Pass = Toast.makeText(getApplicationContext(), "In future patches", Toast.LENGTH_SHORT);
                Pass.show();
                break;

            case "Check Device Type History":
                Pass = Toast.makeText(getApplicationContext(), "In future patches", Toast.LENGTH_SHORT);
                Pass.show();
                break;

            case "Check User History":
                Intent mainIntent7 = new Intent(ProducerMainActivity.this, ProducerCheckGivenUserHistory.class);

                mainIntent7.putExtra("name", name);
                mainIntent7.putExtra("username", username);
                mainIntent7.putExtra("password", password);
                mainIntent7.putExtra("email", email);
                mainIntent7.putExtra("date", date);
                mainIntent7.putExtra("device_types", device_types);
                mainIntent7.putExtra("device_models", device_models);
                mainIntent7.putExtra("ip", ipAddress);

                ProducerMainActivity.this.startActivity(mainIntent7);
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
                //ProducerMainActivity.this.finish();
                break;

            case "Logoff":
                Intent mainIntent8 = new Intent(ProducerMainActivity.this, ProducerLogoutActivity.class);

                mainIntent8.putExtra("ip", ipAddress);
                mainIntent8.putExtra("username", username);

                ProducerMainActivity.this.startActivity(mainIntent8);
                ProducerMainActivity.this.finish();
                break;
        }
        return true;
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
     * @param user the username to send
     * @param _uri URI to post
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String user, String _uri) {
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
}
