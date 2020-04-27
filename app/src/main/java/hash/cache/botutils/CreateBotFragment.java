package hash.cache.botutils;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Objects;

public class CreateBotFragment extends Fragment {

    ImageButton add;
    User u;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_bot, container, false);

        add = view.findViewById(R.id.add);

        u = (User) Objects.requireNonNull(getArguments()).getSerializable("user");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), CreateBotActivity.class);
                intent.putExtra("user", u);
                startActivity(intent);
            }
        });

        return view;
    }

}
