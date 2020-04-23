package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListBotsActivity extends AppCompatActivity {

    User u;
    ArrayList<Bot> bots;
    ArrayList<Fragment> botsFragList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bots);

        u = (User) getIntent().getExtras().get("user");

        System.out.println("EDITOR COOKIE IS" + u.getEditor_token());

        bots = new ArrayList<>();
        botsFragList = new ArrayList<>();

        getBots();
    }

    @Override
    protected void onRestart() {
        //refreshes activity on restart
        removeBots();
        getBots();
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //when instance saves, remove bots and their fragments
        removeBots();
        super.onSaveInstanceState(outState);
    }

    public void getBots() {

        //make bot index request
        ApiRequest apiRequest = new ApiRequest("INDEX", u);
        try {
            parseBots(new RequestAsyncTask().execute(apiRequest).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showErrToast();
        }
    }

    public void parseBots(String index) {
        try {
            //parse JSON from response to bot index request
            JSONArray response = new JSONObject(index).getJSONArray("response");
            for (int i = 0; i < response.length(); i++) {
                Bot b = new Bot();
                JSONObject fields = response.getJSONObject(i);
                b.setName(fields.getString("name"));
                b.setId(fields.getString("bot_id"));
                b.setGroupName(fields.getString("group_name"));
                b.setGroupId(fields.getString("group_id"));
                b.setAvatarUrl(fields.getString("avatar_url"));
                b.setCallbackUrl(fields.getString("callback_url"));
                b.setDm(fields.getBoolean("dm_notification"));

                addBot(b);
            }
        } catch (JSONException e) {
           showErrToast();
           e.printStackTrace();
        }
    }

    public void addBot(Bot bot) {

        //send bot argument to fragment
        BotFragment botFrag = new BotFragment();
        Bundle args = new Bundle();
        args.putSerializable("bot", bot);
        args.putSerializable("user", u);
        botFrag.setArguments(args);

        //adds bot fragment to botFragHolder
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
        fragmentTransaction.add(R.id.botFragHolder, botFrag);
        fragmentTransaction.commitAllowingStateLoss();

        //add fragment and bot to list to keep track of
        botsFragList.add(botFrag);
        bots.add(bot);
    }

    public void removeBots() {

        //remove each fragment
        for(int i = 0; i < botsFragList.size(); i++)
            if(botsFragList.get(i) != null) {
                FragmentManager fragMan = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
                fragmentTransaction.remove(botsFragList.get(i));
                fragmentTransaction.commitAllowingStateLoss();
            }

        //clear lists
        botsFragList.clear();
        bots.clear();
    }

    public void showErrToast() {
        Toast.makeText(getApplicationContext(), "Error while making request", Toast.LENGTH_LONG).show();
    }
}