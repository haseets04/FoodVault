package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    /*private static final String ConnectionString = "jdbc:mysql://localhost:3306/foodvault"; //check jdbc:mysql://localhost:3306/FoodVault //192.168.101.118
    private static final String DeviceDriver = "com.mysql.cj.jdbc.Driver"; //check
    private Connection connection = null;
    private Statement stmt;*/
    private Button loginBtn;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.login_btn);
        signUpButton = findViewById(R.id.sign_up);

        loginBtn.setOnClickListener(v -> {
            String emailAddress = ((EditText) findViewById(R.id.email_address_input)).getText().toString();
            String password = ((EditText) findViewById(R.id.password_input)).getText().toString();

            Log.i("Test Credentials", "Email Address: " + emailAddress + " and Password: " + password);

            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
        });

        signUpButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, RegisterProfileActivity.class)));




        /*try {
                    //Start the jtds SQL Server driver and obtain a connection to the database.
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Class.forName(DeviceDriver);
                    connection = DriverManager.getConnection(ConnectionString, "root", "mySQL@04");
                    //connection = DriverManager.getConnection(ConnectionString, username, password);
                    stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                    //Toast.makeText(MainActivity.this, "Connected...", Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "Connected...", Toast.LENGTH_SHORT).show();

                    //region Toggle visibility of UI Views.
                    //findViewById(R.id.vConnect).setVisibility(View.GONE);
                    //findViewById(R.id.vConnected).setVisibility(View.VISIBLE);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Could not instantiate device driver...", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Could not connect...", Toast.LENGTH_SHORT).show();
                }

                //insert new user record (for registering)
                //dummy variable
                String name = "name";
                String surname = "surname";
                String email = "email@gmail.com";
                String password1 = "password";
                String sql = String.format("INSERT INTO foodvault VALUES ('%s', '%s', '%s', '%s')", name, surname, email, password1);
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }*/


                /*PreparedStatement statement = null;
                try {
                    statement = connection.prepareStatement("SELECT * FROM foodvault.users");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                ResultSet resultSet = null try {
                    resultSet = statement.executeQuery();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }) {
                while (true) {
                    try {
                        if (!resultSet.next()) break;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        int id = resultSet.getInt("id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        String name = resultSet.getString("name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }*/
    }
}