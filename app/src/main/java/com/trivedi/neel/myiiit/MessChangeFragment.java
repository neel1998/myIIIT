package com.trivedi.neel.myiiit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.trivedi.neel.myiiit.mess.Mess;
import com.trivedi.neel.myiiit.utils.Callback1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MessChangeFragment extends Fragment {
    private TextView mStartDateTextView, mEndDateTextView;
    private CheckBox mBreakfastCheckBox, mLunchCheckBox, mDinnerCheckBox, mUnregisterCheckBox;
    private Spinner mDateMessSpinner, mDaySpinner, mDayMealSpinner, mDayMessSpinner, mMonthSpinner, mMonthMessSpinner;
    private Button mDateChangeBtn, mDayChangeBtn, mRegisterUnregisterBtn;
    private ProgressBar mDateProg, mDayProg, mMonthProg;

    private Mess mess;

    private Calendar mMonth = Calendar.getInstance();
    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();

    private Map<String, Integer> messMap = new HashMap<>();

    private ArrayList<String> monthList = new ArrayList<>();

    private SimpleDateFormat mMonthFormat = new SimpleDateFormat("MMMM-y");
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("EEEE, d MMM y");

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mess = Mess.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_mess_change, container, false);

        mStartDateTextView = rootView.findViewById(R.id.start_date);
        mEndDateTextView = rootView.findViewById(R.id.end_date);

        mBreakfastCheckBox = rootView.findViewById(R.id.date_breakfast_box);
        mLunchCheckBox = rootView.findViewById(R.id.date_lunch_box);
        mDinnerCheckBox = rootView.findViewById(R.id.date_dinner_box);
        mUnregisterCheckBox = rootView.findViewById(R.id.unregister_box);

        mDateChangeBtn = rootView.findViewById(R.id.date_change_btn);
        mDayChangeBtn = rootView.findViewById(R.id.day_change_btn);
        mRegisterUnregisterBtn = rootView.findViewById(R.id.register_unregister_btn);

        mDateProg = rootView.findViewById(R.id.date_prog);
        mDayProg = rootView.findViewById(R.id.day_prog);
        mMonthProg =rootView.findViewById(R.id.month_prog);

        mDateMessSpinner = rootView.findViewById(R.id.date_mess_spinner);
        mDaySpinner = rootView.findViewById(R.id.day_spinner);
        mDayMealSpinner =rootView.findViewById(R.id.day_meal_spinner);
        mDayMessSpinner = rootView.findViewById(R.id.day_mess_spinner);
        mMonthSpinner = rootView.findViewById(R.id.month_spinner);
        mMonthMessSpinner = rootView.findViewById(R.id.month_mess_spinner);

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

        createMessMap();

        createMonthList();

        fillSpinners();

        mDateChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMealsDatewise();
            }
        });

        mDayChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMealsDaywise();
            }
        });

        mRegisterUnregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMealsMonthly();
            }
        });

        mUnregisterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateRegisterUnregisterBtn();
            }
        });

        return rootView;
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

    private void resetTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void highlightEndDate(boolean highlight) {
        if (highlight) {
            mEndDateTextView.setTextAppearance(getContext(), R.style.AppTheme_TextAppearance_HighlightDate);
        }
        else {
            mEndDateTextView.setTextAppearance(getContext(), R.style.AppTheme_TextAppearance_IgnoreDate);
        }
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

    private void createMessMap() {
        messMap.put("Yuktahaar", 3);
        messMap.put("South", 1);
        messMap.put("North", 2);
        messMap.put("Kadamb-Veg", 4);
        messMap.put("Kadamb Non-Veg", 6);
    }

    private void createMonthList() {
        for(int i = 1; i <= 12; ++i) {
          mMonth.add(Calendar.MONTH, 1);
          String formattedMonth = mMonthFormat.format(mMonth.getTimeInMillis());
          monthList.add(formattedMonth);
        }
    }

    private void fillSpinners() {
        ArrayAdapter<CharSequence> messAdapter = ArrayAdapter.createFromResource(getContext(), R.array.mess, R.layout.spinner_item);
        ArrayAdapter<CharSequence> mealAdapter = ArrayAdapter.createFromResource(getContext(), R.array.meal, R.layout.spinner_item);
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(getContext(), R.array.days, R.layout.spinner_item);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, monthList);

        messAdapter.setDropDownViewResource(R.layout.spinner_item);
        mealAdapter.setDropDownViewResource(R.layout.spinner_item);
        daysAdapter.setDropDownViewResource(R.layout.spinner_item);
        monthAdapter.setDropDownViewResource(R.layout.spinner_item);

        mDateMessSpinner.setAdapter(messAdapter);
        mDaySpinner.setAdapter(daysAdapter);
        mDayMealSpinner.setAdapter(mealAdapter);
        mDayMessSpinner.setAdapter(messAdapter);
        mMonthSpinner.setAdapter(monthAdapter);

        ArrayList<String > monthMessList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.mess)));
        monthMessList.remove(4);
        ArrayAdapter<String> monthMessAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, monthMessList);
        monthMessAdapter.setDropDownViewResource(R.layout.spinner_item);
        mMonthMessSpinner.setAdapter(monthMessAdapter);
    }

    private void updateRegisterUnregisterBtn() {
        if(mUnregisterCheckBox.isChecked()) {
            mRegisterUnregisterBtn.setText("Unregister");
        }
        else {
            mRegisterUnregisterBtn.setText("Register");
        }
    }

    private void changeMealsDatewise() {
        int meals = 0;
        mDateProg.setVisibility(View.VISIBLE);
        mDateChangeBtn.setVisibility(View.GONE);

        if (mBreakfastCheckBox.isChecked()) meals |= Mess.MEAL_BREAKFAST;
        if (mLunchCheckBox.isChecked()) meals |= Mess.MEAL_LUNCH;
        if (mDinnerCheckBox.isChecked()) meals |= Mess.MEAL_DINNER;

        if (meals == 0) {
            resetLayoutDatewise("Please Select meals to Change");
            return;
        }

        mess.changeMealsDatewise((Calendar) mStartDate.clone(), (Calendar) mEndDate.clone(), meals, messMap.get(mDateMessSpinner.getSelectedItem()), new Callback1<String>() {
            @Override
            public void success(String s) {
                resetLayoutDatewise(s);
            }

            @Override
            public void error(Exception e) {
                Log.d("MessChangeDatewise", e.getLocalizedMessage());
                resetLayoutDatewise(e.getLocalizedMessage());
            }
        });
    }

    private void changeMealsDaywise() {
        mDayProg.setVisibility(View.VISIBLE);
        mDayChangeBtn.setVisibility(View.GONE);

        mess.changeMealsDaywise((String)mDaySpinner.getSelectedItem(), mDayMealSpinner.getSelectedItemPosition() + 1, messMap.get(mDayMessSpinner.getSelectedItem()), new Callback1<String>() {
            @Override
            public void success(String s) {
                resetLayoutDaywise(s);
            }

            @Override
            public void error(Exception e) {
                Log.d("MessChangeDaywise", e.getLocalizedMessage());
                resetLayoutDaywise(e.getLocalizedMessage());
            }
        });
    }

    private void changeMealsMonthly() {
        mMonthProg.setVisibility(View.VISIBLE);
        mRegisterUnregisterBtn.setVisibility(View.GONE);

        mess.changeMealsMonthly(mUnregisterCheckBox.isChecked(), (String)mMonthSpinner.getSelectedItem(), messMap.get(mDayMessSpinner.getSelectedItem()), new Callback1<String>() {
            @Override
            public void success(String s) {
                resetLayoutMonthly(s);
            }

            @Override
            public void error(Exception e) {
                Log.d("MessChangeMonthly", e.getLocalizedMessage());
                resetLayoutMonthly(e.getLocalizedMessage());
            }
        });
    }

    private void resetLayoutDatewise(String message) {
        mDateProg.setVisibility(View.GONE);
        mDateChangeBtn.setVisibility(View.VISIBLE);

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();

        mBreakfastCheckBox.setChecked(false);
        mLunchCheckBox.setChecked(false);
        mDinnerCheckBox.setChecked(false);
    }

    private void resetLayoutDaywise(String message) {
        mDayProg.setVisibility(View.GONE);
        mDayChangeBtn.setVisibility(View.VISIBLE);

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();

        mDaySpinner.setSelection(0);
        mDayMealSpinner.setSelection(0);
        mDayMessSpinner.setSelection(0);
    }

    private void resetLayoutMonthly(String message) {
        mMonthProg.setVisibility(View.GONE);
        mRegisterUnregisterBtn.setVisibility(View.VISIBLE);

        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();

        mUnregisterCheckBox.setChecked(false);
        mMonthSpinner.setSelection(0);
        mMonthMessSpinner.setSelection(0);
    }

}