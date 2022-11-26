package org.jason.bmeg3130;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class InfoFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        Button btnBackToHome = view.findViewById(R.id.btnBackToHome);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        btnBackToHome.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Activity thisActivity = this.getActivity();
        assert thisActivity != null;

        NavController navController = Navigation.findNavController(v);

        if (id == R.id.btnBackToHome) {
            navController.navigate(R.id.action_infoFragment_to_startFragment);
        } else if (id == R.id.btnSubmit) {
            EditText edtName = thisActivity.findViewById(R.id.edtName);
            EditText edtEmail = thisActivity.findViewById(R.id.edtEmail);
            EditText edtPhone = thisActivity.findViewById(R.id.edtPhone);
            TextView textViewInfo = thisActivity.findViewById(R.id.textViewInfo);
            String name = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            String phone = edtPhone.getText().toString();
            textViewInfo.setText("Name: "+name+"\nPhone: "+phone+"\nEmail: "+email);

            //save the info
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(thisActivity);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("name", name);
            edit.putString("age", phone);
            edit.putString("email", email);
            edit.apply();
        }
    }
}