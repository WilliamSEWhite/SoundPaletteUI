package com.soundpaletteui.Activities.Trending;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;
import com.soundpaletteui.UISettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentTransaction;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.soundpaletteui.Activities.Messages.MessageFragment;
        import com.soundpaletteui.Activities.Posts.PostFragment;
        import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
        import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
        import com.soundpaletteui.Infrastructure.Models.UserModel;
        import com.soundpaletteui.Infrastructure.SPWebApiRepository;
        import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
        import com.soundpaletteui.R;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;

/**
 * Provides search functionality and displays relevant posts.
 */
public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private UserModel user;
    private UserClient userClient;

    public SearchFragment() {
    }

    // Static method to create a new instance of HomeFragment with userId
    public static com.soundpaletteui.Activities.Trending.SearchFragment newInstance(int userId) {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflates the layout, sets up UI, and loads initial post content.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        UISettings.applyBrightnessGradientBackground(rootView, 330f);
        initComponents(rootView);

        Log.d("SearchFragment", "Initial Trending Algorithm");
        replaceFragment("trending", null);

        EditText inputSearch = rootView.findViewById(R.id.edittext_search);
        ImageButton buttonSearch = rootView.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            String searchText = inputSearch.getText().toString().trim();
            Log.d("SearchFragment", "Fetching new feed with ID: " + searchText);
            replaceFragment("search", searchText);
        });
        return rootView;
    }

    // Initializes the main content adapter and loads user data.
    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
    }

    // Replaces the PostFragment based on the algorithmType and userId
    private void replaceFragment(String algoType, String searchTerm) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, searchTerm);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }
}
