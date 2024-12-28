package plc.jidnation.datacaching;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    Button saveButton;
    Button loadButton;
    CacheManager cacheManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cacheManager = new CacheManager(this);
        usernameText = findViewById(R.id.usernameField);
       passwordText = findViewById(R.id.passwordField);
        saveButton = findViewById(R.id.saveCredential);
        loadButton = findViewById(R.id.loadCredentials);
    }

    public void saveCredentials(View view) {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
            return;
        }
        cacheManager.storeData("username", username);
        cacheManager.storeData("password", password);
        Toast.makeText(this, "Credentials saved successfully", Toast.LENGTH_SHORT).show();
        usernameText.setText("");
        passwordText.setText("");
    }

    public void loadCredentials(View view) {
        String username = cacheManager.retrieveData("username");
        String password = cacheManager.retrieveData("password");
        if(username != null && password != null){
            usernameText.setText(username);
            passwordText.setText(password);
            Toast.makeText(this, "Credentials found!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No saved credentials found", Toast.LENGTH_SHORT).show();
        }
    }
}