package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
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

    public void onClickItemParameter(View view)
    {
        TextView textView = (TextView)view.findViewById(R.id.paramters_item);
        String value = textView.getText().toString();

        //TODO ajout des alertdialog pour changer les parametres le l'occupation

        if (value.equals(OccupationParameters.Parameters.NB_WEEK_RESET.getValue()))
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

        else if (value.equals(OccupationParameters.Parameters.RESET_DAY.getValue()))
        {
            Builder builder = new Builder(this);
        }
        else if (value.equals(OccupationParameters.Parameters.ROUND_MINUTE.getValue()))
        {

        }
        else if (value.equals(OccupationParameters.Parameters.ROUND_TYPE.getValue()))
        {

        }





        /*
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add);
        dialog.setTitle("Add job");

        // button SAVE
        Button btnSave = (Button)dialog.findViewById(R.id.btn_add_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            }
        });

        // button SET PARAMETERS
        Button btnParameters = (Button)dialog.findViewById(R.id.btn_add_set_parameters);
        btnParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent("PunchCard.Parameters"));
            }
        });

        dialog.show();
        */
    }

    public void onClickApplyParameters(View view)
    {
        Intent intent = new Intent();

        intent.putExtra(OccupationParameters.Parameters.NB_WEEK_RESET.getValue(),
                _parameters.getNbDayBeforeReset()
        );

        setResult(RESULT_OK, intent);

        finish();
    }
}
