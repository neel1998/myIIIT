package com.example.neel.myiiit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neel.myiiit.mess.Mess;
import com.example.neel.myiiit.utils.Callback1;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessCancelFragment extends Fragment {
    private CheckBox mBreakfastCheckBox, mLunchCheckBox, mDinnerCheckBox, mUncancelCheckBox;
    private Button mSubmitButton;
    private TextView mStartDateTextView, mEndDateTextView;
    private TextView mEndDateLabelTextView;
    private TextView mCancelUncancelLabelTextView;

    private Mess mess;

    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();

    private ProgressBar mCancelProgressBar;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("EEEE, d MMM y");

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mess = Mess.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_mess_cancel, container, false);

        mStartDateTextView = rootView.findViewById(R.id.start_date);
        mEndDateTextView = rootView.findViewById(R.id.end_date);

        mCancelUncancelLabelTextView = rootView.findViewById(R.id.cancel_uncancel_label);
        mEndDateLabelTextView = rootView.findViewById(R.id.to_end_date_label);

        mCancelProgressBar = rootView.findViewById(R.id.cancel_prog);

        mBreakfastCheckBox = rootView.findViewById(R.id.breakfast_box);
        mLunchCheckBox = rootView.findViewById(R.id.lunch_box);
        mDinnerCheckBox = rootView.findViewById(R.id.dinner_box);
        mUncancelCheckBox = rootView.findViewById(R.id.uncancel_box);

        mSubmitButton = rootView.findViewById(R.id.submit_btn);

        handleDateUpdates();

        mStartDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartDatePicker();
            }
        });

        mEndDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEndDatePicker();
            }
        });

        mUncancelCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateCancelUncancelLabel();
            }
        });
        updateCancelUncancelLabel();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  cancelMeals();
            }
        });

        return rootView;
    }
    private void cancelMeals(){
        int meals = 0;
        mCancelProgressBar.setVisibility(View.VISIBLE);
        mSubmitButton.setVisibility(View.GONE);

        if (mBreakfastCheckBox.isChecked()) meals |= Mess.MEAL_BREAKFAST;
        if (mLunchCheckBox.isChecked()) meals |= Mess.MEAL_LUNCH;
        if (mDinnerCheckBox.isChecked()) meals |= Mess.MEAL_DINNER;

        if (meals == 0) {
            resetLayout("Please Select meals to Cancel");
            return;
        }
        mess.cancelMeals((Calendar) mStartDate.clone(), (Calendar) mEndDate.clone(), meals, mUncancelCheckBox.isChecked(), new Callback1<String>() {
            @Override
            public void success(String s) {
                resetLayout(s);
            }

            @Override
            public void error(Exception e) {
                Log.d("MessCancellation", e.getLocalizedMessage());
                resetLayout(e.getLocalizedMessage());
            }
        });
    }

    private void resetLayout(String message) {
        mCancelProgressBar.setVisibility(View.GONE);
        mSubmitButton.setVisibility(View.VISIBLE);

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();

        mBreakfastCheckBox.setChecked(false);
        mLunchCheckBox.setChecked(false);
        mDinnerCheckBox.setChecked(false);
        mUncancelCheckBox.setChecked(false);
    }

    private void openStartDatePicker() {
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mStartDate.set(year, month, dayOfMonth);
                handleDateUpdates();
            }
        }, mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), mStartDate.get(Calendar.DAY_OF_MONTH));

        pickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

        pickerDialog.show();
    }

    private void openEndDatePicker() {
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEndDate.set(year, month, dayOfMonth);
                handleDateUpdates();
            }
        }, mEndDate.get(Calendar.YEAR), mEndDate.get(Calendar.MONTH), mEndDate.get(Calendar.DAY_OF_MONTH));

        pickerDialog.getDatePicker().setMinDate(mStartDate.getTimeInMillis());

        pickerDialog.show();
    }

    private void highlightEndDate(boolean highlight) {
        if (highlight) {
            mEndDateLabelTextView.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Medium);
            mEndDateTextView.setTextAppearance(getContext(), R.style.AppTheme_TextAppearance_HighlightDate);
        }
        else {
            mEndDateLabelTextView.setTextAppearance(getContext(), R.style.AppTheme_TextAppearance_IgnoreDateLabel);
            mEndDateTextView.setTextAppearance(getContext(), R.style.AppTheme_TextAppearance_IgnoreDate);
        }
    }

    private void updateCancelUncancelLabel() {
        if (mUncancelCheckBox.isChecked()) {
            mCancelUncancelLabelTextView.setText("Uncancel meals for");
        } else {
            mCancelUncancelLabelTextView.setText("Cancel meals for");
        }
    }

    private String formatDate(Calendar date) {
        String formattedDate = mDateFormat.format(date.getTimeInMillis());

        Calendar today = Calendar.getInstance();
        resetTime(today);

        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        if (date.equals(today)) {
            return "Today, " + formattedDate;
        } else if (date.equals(tomorrow)) {
            return "Tomorrow, " + formattedDate;
        }

        return formattedDate;
    }

    private void handleDateUpdates() {
        resetTime(mStartDate);
        resetTime(mEndDate);

        if (mEndDate.before(mStartDate)) {
            mEndDate.setTimeInMillis(mStartDate.getTimeInMillis());
        }

        if (mEndDate.after(mStartDate)) {
            highlightEndDate(true);
        } else {
            highlightEndDate(false);
        }

        mStartDateTextView.setText(formatDate(mStartDate));
        mEndDateTextView.setText(formatDate(mEndDate));
    }

    private void resetTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
