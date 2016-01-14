package com.franklin.keepme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.franklin.keepme.DB.DBContract;
import com.franklin.keepme.DB.DBManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class createActivity extends Activity implements View.OnClickListener {
    private EditText titleField, descriptionField, dateField, fromTimeField, toTimeField;
    private ImageView checkMarkButton;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog fromTimePickerDialog, toTimePickerDialog;
    private Calendar fromTime, toTime;
    private SimpleDateFormat dateFormatter, timeFormatter;
    private int notifyTime = 0;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        dbManager = new DBManager(this);

        findViewsById();
        checkMarkButton.setOnClickListener(this);
        setDateTimeField();
        setNotifySpinner();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbManager.closeDB();
    }

    private void findViewsById() {
        titleField = (EditText) findViewById(R.id.create_title_input);
        descriptionField = (EditText) findViewById(R.id.create_description_input);
        checkMarkButton = (ImageView) findViewById(R.id.create_check_mark);
    }

    private void setNotifySpinner() {
        Spinner notifySpinner = (Spinner) findViewById(R.id.create_notify_spinner);
        ArrayAdapter<CharSequence> notifySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.notify_array, android.R.layout.simple_spinner_item);
        notifySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notifySpinner.setAdapter(notifySpinnerAdapter);
        notifySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        notifyTime = 0;
                        break;
                    case 1:
                        notifyTime = 5;
                        break;
                    case 2:
                        notifyTime = 10;
                        break;
                    case 3:
                        notifyTime = 15;
                        break;
                    case 4:
                        notifyTime = 30;
                        break;
                    case 5:
                        notifyTime = 60;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setDateTimeField() {
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
        timeFormatter = new SimpleDateFormat("hh:mm a");

        dateField = (EditText) findViewById(R.id.create_date_input);
        fromTimeField = (EditText) findViewById(R.id.create_input_time_from);
        toTimeField = (EditText) findViewById(R.id.create_input_time_to);

        dateField.setInputType(InputType.TYPE_NULL);
        fromTimeField.setInputType(InputType.TYPE_NULL);
        toTimeField.setInputType(InputType.TYPE_NULL);

        dateField.setOnClickListener(this);
        fromTimeField.setOnClickListener(this);
        toTimeField.setOnClickListener(this);
        fromTime = Calendar.getInstance();
        toTime = Calendar.getInstance();

        View.OnFocusChangeListener editTextListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (v == dateField) {
                        datePickerDialog.show();
                    } else if (v == fromTimeField) {
                        fromTimePickerDialog.show();
                    } else if (v == toTimeField) {
                        toTimePickerDialog.show();
                    }
                }
            }
        };
        dateField.setOnFocusChangeListener(editTextListener);
        fromTimeField.setOnFocusChangeListener(editTextListener);
        toTimeField.setOnFocusChangeListener(editTextListener);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fromTime.set(year, monthOfYear, dayOfMonth);
                toTime.set(year, monthOfYear, dayOfMonth);
                dateField.setText(dateFormatter.format(fromTime.getTime()));
            }
        },fromTime.get(Calendar.YEAR), fromTime.get(Calendar.MONTH), fromTime.get(Calendar.DAY_OF_MONTH));

        fromTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                fromTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                fromTime.set(Calendar.MINUTE, minute);
                fromTimeField.setText(timeFormatter.format(fromTime.getTime()));
            }
        }, fromTime.get(Calendar.HOUR_OF_DAY), fromTime.get(Calendar.MINUTE), true);

        toTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                toTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                toTime.set(Calendar.MINUTE, minute);
                toTimeField.setText(timeFormatter.format(toTime.getTime()));
            }
        }, toTime.get(Calendar.HOUR_OF_DAY), toTime.get(Calendar.MINUTE), true);
    }

    @Override
    public void onClick(View view) {
        if (view == dateField) {
            datePickerDialog.show();
        } else if (view == fromTimeField) {
            fromTimePickerDialog.show();
        } else if (view == toTimeField) {
            toTimePickerDialog.show();
        } else if (view == checkMarkButton) {
            submit();
        }
    }

    private void submit() {
        String title = titleField.getText().toString();
        String description = descriptionField.getText().toString();
        if (!submitCheck(title, description, fromTime, toTime)) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DBContract data = new DBContract(title , description,
                dateFormat.format(fromTime.getTime()), dateFormat.format(toTime.getTime()), notifyTime);
        dbManager.putData(data);
        Intent intent = new Intent(createActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private boolean submitCheck(String title, String description, Calendar fromTime, Calendar toTime) {
        String toast = null;
        if (title.length() < 1) {
            toast = "Please input title!";
        /*} else if (description.length() < 1) {
            toast = "Please input description!";*/
        } else if (fromTime.get(Calendar.YEAR) != toTime.get(Calendar.YEAR) ||
                fromTime.get(Calendar.DAY_OF_YEAR) != toTime.get(Calendar.DAY_OF_YEAR)) {
            toast = "from & to must be the same day!";
        } else if (!fromTime.before(toTime)) {
            toast = "from time must be before!";
        }
        if (toast == null) {
            return true;
        }
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        return false;
    }
}
