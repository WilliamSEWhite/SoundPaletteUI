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
        import com.soundpaletteui.R;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private UserClient userClient;

    public SearchFragment() {
        // Required empty public constructor
    }

    // Static method to create a new instance of HomeFragment with userId
    public static com.soundpaletteui.Activities.Trending.SearchFragment newInstance(int userId) {
        com.soundpaletteui.Activities.Trending.SearchFragment fragment = new com.soundpaletteui.Activities.Trending.SearchFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId); // Pass userId with key "USER_ID"
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1); // Ensure correct key
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initComponents(rootView);

        Random random = new Random();
        int randomNumber = random.nextInt(6) + 10;
        Log.d("HomeFragment", "Initial randomNumber: " + randomNumber);
        replaceFragment(randomNumber);

        EditText inputSearch = rootView.findViewById(R.id.edittext_search);
        ImageButton buttonSearch = rootView.findViewById(R.id.button_search);

        // Set onClickListener for buttonSearch
        buttonSearch.setOnClickListener(v -> {
            String searchText = inputSearch.getText().toString().trim(); // Trim whitespace
            Log.d("TAG- SearchFragment", "Fetching new feed with ID: " + searchText);

            if (isNumeric(searchText)) {
                int searchNumber = Integer.parseInt(searchText);
                if (searchNumber >= 1 && searchNumber <= 9) { // Ensures it's within range
                    replaceFragment(searchNumber);
                }
            } else {
                if (searchText.toLowerCase().equals("art")) {
                    replaceFragment(7);
                } else if (searchText.toLowerCase().equals("music")) {
                    replaceFragment(8);
                } else {
                    Toast.makeText(requireContext(), "I heard you're trying to find " + searchText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }



    // Helper method to check if input is numeric
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+"); // Regex: Matches only digits
    }


    private void initComponents(View view) {
        // Get arguments instead of Intent
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0); // Correct key
        }

        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);

        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
    }

    private void getUser() {
        new Thread(() -> {
            try {
                user = userClient.getUser(userId);
                requireActivity().runOnUiThread(this::populateView);
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error fetching user", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void populateView() {
        if (user != null) {
            userList.clear();
            userList.add(user);
            mainContentAdapter.notifyDataSetChanged();
        }
    }

    private void replaceFragment(int userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(userId);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }
}
