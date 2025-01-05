package plc.jidnation.datacaching;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    Button saveButton;
    Button loadButton;
    Button saveToDBButton;
    CacheManager cacheManager;
    DBManager dataManager;
    UserListHandler myListAdapter;
    ArrayList<UserInfo> users = new ArrayList<>();


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

         dataManager = new DBManager(this);

        cacheManager = new CacheManager(this);
        usernameText = findViewById(R.id.usernameField);
       passwordText = findViewById(R.id.passwordField);
        saveButton = findViewById(R.id.saveCredential);
        loadButton = findViewById(R.id.loadCredentials);
        saveToDBButton = findViewById(R.id.saveToDatabase);
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

    public void saveToDatabase(View view) {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DBManager.columnUsername, username);
        values.put(DBManager.columnPassword, password);
        dataManager.insert(values);
        Toast.makeText(this, "Credentials saved to database successfully", Toast.LENGTH_SHORT).show();
        usernameText.setText("");
        passwordText.setText("");
    }

    public void loadFromDatabase(View view) {
    loadElement(view);
    }

    @SuppressLint("Range")
    public void loadElement(View view){
        users.clear();
        String[] selectionArgs = {"%" + usernameText.getText().toString() +  "%"};
        Cursor cursor = dataManager.query(
                null, "userName like ? ", selectionArgs,
                DBManager.columnUsername
        );
        if (cursor.moveToFirst()) {
//            StringBuilder tableData = new StringBuilder();
            do {
                final String username = cursor.getString(cursor.getColumnIndex(DBManager.columnUsername));
                 final String password = cursor.getString(cursor.getColumnIndex(DBManager.columnPassword));

//    tableData.append(cursor.getString(cursor.getColumnIndex(DBManager.columnUsername))).append(cursor.getString(cursor.getColumnIndex(DBManager.columnPassword)))
//            .append(": :");
                System.out.println("username: " + username + " password: " + password);
                users.add(new UserInfo(username, password));
            } while (cursor.moveToNext());
//            Toast.makeText(this, tableData, Toast.LENGTH_SHORT).show();
            ListView listView = findViewById(R.id.usersList);
            myListAdapter = new UserListHandler(users);

            listView.setOnItemClickListener((parent, viewItem, position, id) -> {
                UserInfo userInfo = users.get(position);
                LayoutInflater myLayoutInflater = getLayoutInflater();
                @SuppressLint("InflateParams") View myView = myLayoutInflater.inflate(R.layout.users_layout, null);

                TextView usernameText = myView.findViewById(R.id.item_username);
                TextView passwordText = myView.findViewById(R.id.item_password);

                usernameText.setText(userInfo.username);
                passwordText.setText(userInfo.password);

                Toast toast = new Toast(getApplicationContext());
                toast.setView(viewItem);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();

            });
            listView.setAdapter(myListAdapter);
        }
    }

    public void deleteFromDatabase(View view) {
    }

    private class UserListHandler extends BaseAdapter{
        final List<UserInfo> userList;
        public UserListHandler(List<UserInfo> userList){
            this.userList = userList;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.users_layout, null);

            UserInfo userInfo = userList.get(position);

            TextView username = view.findViewById(R.id.item_username);
            TextView password = view.findViewById(R.id.item_password);
            Button deleteButton = view.findViewById(R.id.deleteUser);
            Button updateButton = view.findViewById(R.id.updateButton);

            username.setText(userInfo.username);
            password.setText(userInfo.password);
            deleteButton.setOnClickListener(v -> {
                dataManager.delete(DBManager.columnUsername + "=?", new String[]{userInfo.username});
                userList.remove(userInfo);
                notifyDataSetChanged();
            });
            updateButton.setOnClickListener(v -> {
                // Update user credentials
                ContentValues values = new ContentValues();
                values.put(DBManager.columnUsername, usernameText.getText().toString());
                values.put(DBManager.columnPassword, userInfo.password);
                dataManager.update(values, DBManager.columnUsername + "=?", new String[]{userInfo.username});
                loadElement(v);
            });

            return view;
        }
    }

}