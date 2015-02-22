package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

/**
 * Created by utilisateur on 2015-02-19.
 */
public class ActivityParameters extends Activity
{
    private OccupationParameters _parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initParametersFromIntent();

        setContentView(R.layout.activity_parameters);

        ListView lv = (ListView)findViewById(R.id.list);

        String[] values = new String[]
                {
                        OccupationParameters.Parameters.ROUND_TYPE.getValue(),
                        OccupationParameters.Parameters.ROUND_MINUTE.getValue(),
                        OccupationParameters.Parameters.RESET_DAY.getValue(),
                        OccupationParameters.Parameters.NB_WEEK_RESET.getValue()
                };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        R.layout.parameters_list_item, R.id.paramters_item, values);

        lv.setAdapter(adapter);
    }


    /**
     * contruit le parametre a partir des donnees envoyers en extra
     */
    private void initParametersFromIntent()
    {
        int id = getIntent()
                .getIntExtra("id", 0);

        if (id == 0) {
            int nbWeek = getIntent()
                    .getIntExtra(OccupationParameters.Parameters.NB_WEEK_RESET.getValue(), 0);

            int resetDay = getIntent()
                    .getIntExtra(OccupationParameters.Parameters.RESET_DAY.getValue(), 0);

            int minuteValue = getIntent()
                    .getIntExtra(OccupationParameters.Parameters.ROUND_MINUTE.getValue(), 0);

            int roundType = getIntent()
                    .getIntExtra(OccupationParameters.Parameters.ROUND_TYPE.getValue(), 0);


            _parameters = new OccupationParameters();

            _parameters.setNbDayBeforeReset(nbWeek);
            _parameters.setResetDay(OccupationParameters.DayOfWeek.values()[resetDay]);
            _parameters.setRoundType(OccupationParameters.RoundType.values()[roundType]);
            _parameters.setRoundMinuteValue(minuteValue);
        }

        else
        {
            DataBaseHandler db = new DataBaseHandler(this);
            _parameters = db.getParametersByOccupationId(id);
        }
    }

    public void onClickItemParameter(View view)
    {
        TextView textView = (TextView)view.findViewById(R.id.paramters_item);
        String value = textView.getText().toString();

        //TODO ajout des alertdialog pour changer les parametres le l'occupation

        if (value.equals(OccupationParameters.Parameters.NB_WEEK_RESET.getValue()))
        {
            dialogNbWeek();
        }

        else if (value.equals(OccupationParameters.Parameters.RESET_DAY.getValue()))
        {
            dialogResetDay();
        }
        else if (value.equals(OccupationParameters.Parameters.ROUND_MINUTE.getValue()))
        {
            dialogRoundType();
        }
        else if (value.equals(OccupationParameters.Parameters.ROUND_TYPE.getValue()))
        {

        }
    }

    private void dialogNbWeek()
    {
        Builder builder =  new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("How many week before reset time?");
        builder.setView(R.layout.custom_dialog_nb_week);

        final AlertDialog alertDialog = builder.create();


        alertDialog.show();

        final NumberPicker numberPicker = (NumberPicker)alertDialog.findViewById(R.id.numberPickerWeek);
        Button ok = (Button)alertDialog.findViewById(R.id.btn_ok_nb_week);
        Button cancel = (Button)alertDialog.findViewById(R.id.btn_cancel_nb_week);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _parameters.setNbDayBeforeReset(numberPicker.getValue());
                alertDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(52);
    }

    private void dialogResetDay()
    {
        Builder builder =  new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Set reset day");
        builder.setView(R.layout.dialog_spinner);
        builder.setCancelable(true);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        Spinner spinner = (Spinner)alertDialog.findViewById(R.id.custom_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(alertDialog.getContext(),
                R.layout.spinner_item, R.id.spi_item,
                getResources().getStringArray(R.array.day_of_week));

        spinner.setAdapter(adapter);

        spinner.setSelection(_parameters.getResetDay().getValue());


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                _parameters.setResetDay(OccupationParameters.DayOfWeek.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
    }

   private void dialogRoundType()
   {
       Builder builder =  new AlertDialog.Builder(this);

       builder.setIcon(R.drawable.ic_launcher);
       builder.setTitle("Set Round Type");
       builder.setView(R.layout.dialog_spinner);
       builder.setCancelable(true);

       final AlertDialog alertDialog = builder.create();

       alertDialog.show();

       Spinner spinner = (Spinner)alertDialog.findViewById(R.id.custom_spinner);

       ArrayAdapter<String> adapter = new ArrayAdapter<>(alertDialog.getContext(),
               R.layout.spinner_item, R.id.spi_item,
               getResources().getStringArray(R.array.round_type));

       spinner.setAdapter(adapter);

       spinner.setSelection(_parameters.getRoundType().getValue());


       spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
           {
               _parameters.setRoundType(OccupationParameters.RoundType.values()[position]);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent){}
       });
   }

    public void onClickApplyParameters(View view)
    {
        DataBaseHandler db = new DataBaseHandler(this);
        db.updateParameters(_parameters);

        Intent intent = new Intent();

        intent.putExtra(OccupationParameters.Parameters.NB_WEEK_RESET.getValue(),
                _parameters.getNbDayBeforeReset()
        );

        setResult(RESULT_OK, intent);

        finish();
    }
}
