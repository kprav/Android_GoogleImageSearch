package com.codepath.googleimagesearch.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.googleimagesearch.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends DialogFragment {

    private Spinner spnImageSize;
    private Spinner spnColorFilter;
    private Spinner spnImageType;
    private EditText etSiteFilter;
    private Button btnSave;
    private Button btnCancel;

    private static String imageSize;
    private static String colorFilter;
    private static String imageType;
    private static String siteFilter;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String imageSize, String colorFilter, String imageType, String siteFilter) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("imageSize", imageSize);
        args.putString("colorFilter", colorFilter);
        args.putString("imageType", imageType);
        args.putString("siteFilter", siteFilter);
        fragment.setArguments(args);
        SettingsFragment.imageSize = imageSize;
        SettingsFragment.colorFilter = colorFilter;
        SettingsFragment.imageType = imageType;
        SettingsFragment.siteFilter = siteFilter;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        spnImageSize = (Spinner) view.findViewById(R.id.spnImageSize);
        spnColorFilter = (Spinner) view.findViewById(R.id.spnColorFilter);
        spnImageType = (Spinner) view.findViewById(R.id.spnImageType);
        etSiteFilter = (EditText) view.findViewById(R.id.etSiteFilter);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        String title = getArguments().getString("title", "Set image filters");
        getDialog().setTitle(title);
        final Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(225);
        getDialog().getWindow().setBackgroundDrawable(d);

        setupSpinnerImageSize(view);
        setupSpinnerColorFilter(view);
        setupSpinnerImageType(view);
        setSaveButtonListener();
        setCancelButtonListener();

        if (!siteFilter.equalsIgnoreCase("any")) {
            etSiteFilter.setText(siteFilter);
            etSiteFilter.setSelection(etSiteFilter.getText().length());
        }

        spnImageSize.requestFocus();

        return view;
    }

    private void setValue(String name, String value) {
        if (value.equals(""))
            value = "Any";
        value = value.toLowerCase();
        if (value.equalsIgnoreCase("Extra-Large"))
            value = "xlarge";
        switch (name) {
            case "imageSize":
                imageSize = value;
                break;
            case "colorFilter":
                colorFilter = value;
                break;
            case "imageType":
                imageType = value;
                break;
            case "siteFilter":
                siteFilter = value;
                break;
        }
    }

    private void setupSpinnerImageSize(View view) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_size_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnImageSize.setAdapter(adapter);
        spnImageSize.setSelection(getSpinnerIndex(spnImageSize, SettingsFragment.imageSize));
        spnImageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setValue("imageSize", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private void setupSpinnerColorFilter(View view) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.color_filter_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnColorFilter.setAdapter(adapter);
        spnColorFilter.setSelection(getSpinnerIndex(spnColorFilter, SettingsFragment.colorFilter));
        spnColorFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setValue("colorFilter", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private void setupSpinnerImageType(View view) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnImageType.setAdapter(adapter);
        spnImageType.setSelection(getSpinnerIndex(spnImageType, SettingsFragment.imageType));
        spnImageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setValue("imageType", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    // Update the settings
    private void setSaveButtonListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValue("siteFilter", etSiteFilter.getText().toString().trim());
                if (mListener == null)
                    mListener = (OnFragmentInteractionListener) getActivity();
                mListener.onFinishSettingsFragment(imageSize, colorFilter, imageType, siteFilter);
                dismiss();
            }
        });
    }

    private void setCancelButtonListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onDetach();
            }
        });
    }

    // Get the position of an Spinner item
    private int getSpinnerIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFinishSettingsFragment(String imageSize, String colorFilter, String imageType, String siteFilter);
    }

}
