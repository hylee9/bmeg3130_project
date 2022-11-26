package org.jason.bmeg3130;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartFragment newInstance(String param1, String param2) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        ImageButton btnHeartRate = view.findViewById(R.id.btnHeartRate);
        ImageButton btnHeartPressure = view.findViewById(R.id.btnHeartPressure);
        ImageButton btnSpO2 = view.findViewById(R.id.btnSpO2);
        ImageButton btnTemperature = view.findViewById(R.id.btnTemperature);
        Button btnHelp = view.findViewById(R.id.btnHelp);
        Button btnWebsite = view.findViewById(R.id.btnWebsite);
        ImageButton btnMenu = view.findViewById(R.id.btnMenu);

        btnHeartRate.setOnClickListener(this);
        btnHeartPressure.setOnClickListener(this);
        btnSpO2.setOnClickListener(this);
        btnTemperature.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnWebsite.setOnClickListener(this);
        btnMenu.setOnClickListener(this);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Activity thisActivity = this.getActivity();
        assert thisActivity != null;

        NavController navController = Navigation.findNavController(v);

        WebScrape ws = new WebScrape();
        String data = null;
        try {
            data = ws.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (id == R.id.btnMenu) {
            navController.navigate(R.id.action_startFragment_to_infoFragment);
            return;
        } else if (id == R.id.btnSetting) {
            navController.navigate(R.id.action_startFragment_to_settingFragment);
            return;
        } else if (id == R.id.btnWebsite) {
            Uri uri = Uri.parse("https://www.ha.org.hk/visitor/ha_visitor_index.asp?Content_ID=10052"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = pref.edit();
        String name = pref.getString("name", "n/a");
        String phone = pref.getString("age", "n/a");
        String email = pref.getString("email", "n/a");
        String doctor_email = Utils.DOC_EMAIL;

        if (name.equals("n/a")) {
            Toast.makeText(thisActivity, "User Name Error", Toast.LENGTH_SHORT).show();
        } else if (phone.equals("n/a")) {
            Toast.makeText(thisActivity, "User Age Error", Toast.LENGTH_SHORT).show();
        } else if (email.equals("n/a")) {
            Toast.makeText(thisActivity, "User Email Error", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.txtHeartPressure) {
            // Need further dev
            TextView txtHeartPressure = thisActivity.findViewById(R.id.txtHeartPressure);
            txtHeartPressure.setText(data);
        } else if (id == R.id.btnSpO2) {
            // Need further dev
            TextView txtSpO2 = thisActivity.findViewById(R.id.txtSpO2);
            txtSpO2.setText(data);
        } else if (id == R.id.btnHeartRate) {
            TextView txtHeartRate = thisActivity.findViewById(R.id.txtHeartRate);

            //data split according to "Heartrate = 119.32"
            assert data != null;
            String[] dataList = data.split(" ");

            //formatting temperature
            double heartRate = Double.parseDouble(dataList[2]);
            DecimalFormat df = new DecimalFormat("##.#");
            df.setRoundingMode(RoundingMode.CEILING);

            txtHeartRate.setText(df.format(heartRate) + "bpm");
            Toast.makeText(thisActivity, "Temperature Updated", Toast.LENGTH_SHORT).show();
            edit.putString("heartrate", heartRate+"");
            edit.commit();

            if (heartRate > 120) {
                String toast = "You are having abnormal heart rate, emailing to doctor";
                Toast.makeText(thisActivity, toast, Toast.LENGTH_SHORT).show();

                String subject = "Patient " + name + " is having abnormal heart rate with " + heartRate + " bpm";
                String message = "Patient details: \n" + "Name: " + name + "\nPhone: " + phone + "\nEmail: " + email + "\nPlease contact him immediately.";
                JavaMailAPI javaMailAPI = new JavaMailAPI(thisActivity, doctor_email, subject, message);
                javaMailAPI.execute();
            }

            txtHeartRate.setText(data);
        } else if (id == R.id.btnTemperature) {
            TextView txtTemperature = thisActivity.findViewById(R.id.txtTemperature);

            //data split according to "Temperature = 31.055"
            assert data != null;
            String[] dataList = data.split(" ");

            //formatting temperature
            double temp = Double.parseDouble(dataList[2]);
            DecimalFormat df = new DecimalFormat("##.#");
            df.setRoundingMode(RoundingMode.CEILING);

            txtTemperature.setText(df.format(temp) + "°C");
            Toast.makeText(thisActivity, "Temperature Updated", Toast.LENGTH_SHORT).show();
            edit.putString("temperature", temp+"");
            edit.commit();

            if (temp > 37.5) {
                String toast = "You are having fever, emailing to doctor";
                Toast.makeText(thisActivity, toast, Toast.LENGTH_SHORT).show();

                String subject = "Patient " + name + " is having fever with temperature " + temp;
                String message = "Patient details: \n" + "Name: " + name + "\nPhone: " + phone + "\nEmail: " + email + "\nPlease contact him immediately.";
                JavaMailAPI javaMailAPI = new JavaMailAPI(thisActivity, doctor_email, subject, message);
                javaMailAPI.execute();
            }
        } else if (id == R.id.btnHelp) {
            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
            builder.setCancelable(true);
            builder.setTitle("Confirm asking for help?");
            builder.setMessage("Your information will be directed to doctors");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String toast = "Emailing to doctor";
                            Toast.makeText(thisActivity, toast, Toast.LENGTH_SHORT).show();
                            String subject = "Patient " + name + " is asking for help";
                            String message = "Patient details as follow\n\n" + "Name: " + name + "\nPhone: " + phone + "\nEmail: " + email + "\n\nPlease contact him immediately.";
                            JavaMailAPI javaMailAPI = new JavaMailAPI(thisActivity, doctor_email, subject, message);
                            javaMailAPI.execute();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String toast = "Stopped asking for help";
                    Toast.makeText(thisActivity, toast, Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}










