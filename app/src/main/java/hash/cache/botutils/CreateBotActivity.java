package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class CreateBotActivity extends AppCompatActivity {

    EditText setName, setAvatar, setCallback;
    Button preview;
    ImageView avatar;

    Spinner groupSpinner;
    ArrayList<Group> groups = new ArrayList<>();
    Group setGroup;

    User u;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.publish) {
            saveBot();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bot);

        setTitle("Create Bot");

        u = (User) Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        setName = findViewById(R.id.setName);
        setAvatar = findViewById(R.id.setAvatarURL);
        setCallback = findViewById(R.id.setCallbackURL);

        preview = findViewById(R.id.preview);
        avatar = findViewById(R.id.setAvatar);

        groupSpinner = findViewById(R.id.groups);

        getGroups(u);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    avatar.setImageBitmap(new DownloadImageAsyncTask().execute(setAvatar.getText().toString()).get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setSpinner() {

        ArrayAdapter<Group> spinnerArrayAdapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, groups);

        groupSpinner.setAdapter(spinnerArrayAdapter);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setGroup = (Group) groupSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                groupSpinner.setSelection(0);
            }
        });


    }

    public void getGroups(User u) {
        ApiRequest request = new ApiRequest("GROUP", u);
        new RequestAsyncTask(this).execute(request);
    }

    public void postGroups(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONArray groupsArr = json.getJSONArray("response");
            for(int i = 0; i < groupsArr.length(); i++) {
                  Group group = new Group();
                  group.setName(groupsArr.getJSONObject(i).getString("name"));
                  group.setId(groupsArr.getJSONObject(i).getString("id"));
                  groups.add(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSpinner();
    }

    public void saveBot() {

        JSONObject payload = new JSONObject();
        try {
            payload.put("bot[name]", setName.getText().toString());
            payload.put("bot[avatar_url]", setAvatar.getText().toString());
            payload.put("bot[callback_url]", setCallback.getText().toString());
            payload.put("bot[dm_notification]", "0");
            payload.put("bot[group_id]", setGroup.getId());

            ApiRequest request = new ApiRequest("CREATE", payload, u);

            new RequestAsyncTask(this).execute(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void goBack() {
        Intent intent = new Intent(getApplicationContext(), ListBotsActivity.class);
        finish();
        startActivity(intent);
    }

}
