package com.example.utilisateur.punchcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jrsao on 2/17/2015.
 * Activity au launch de l'application
 */
public class MainActivity extends ListActivity
{
    private AdapterOccupation _adapter;
    private OccupationParameters _tempParam = new OccupationParameters();

    /**
     * Initialise la view selon l'état
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar ab = getActionBar();
      //  ab.setCustomView(R.layout.custom_action_bar);
       // ab.setDisplayShowCustomEnabled(true);


        // set button
        ImageButton addButton = (ImageButton)findViewById(R.id.btn_add);
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.diameter);
                outline.setOval(0, 0, size, size);
            }
        };
        addButton.setOutlineProvider(viewOutlineProvider);
        addButton.setClipToOutline(true);


        _adapter = new AdapterOccupation(this);

        setListAdapter(_adapter);

        initListView();
    }


    @Override
    protected  void onPause()
    {
        Intent intent = new Intent(this,
                UpdateService.class);

        intent.setAction("UPDATE");
        this.startService(intent);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refreshListView();
    }

    /**
     * Initialise la ListView et ses listeners
     */
    private void initListView()
    {
        ListView lv = getListView();

        // click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onClickJob(view);
            }
        });

        // longClick
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                showPopupMenu(view, position);
                return true;
            }
        });
    }


    /**
     * Affiche un popup menu au longClick
     * @param convertView ancre
     * @param position position de l'item dans la liste
     */
    private void showPopupMenu(final View convertView, final int position)
    {
        PopupMenu popupMenu = new PopupMenu(this, convertView);
        popupMenu.inflate(R.menu.popup_menu_occupation);

        //item click
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {

                final DataBaseHandler db = new DataBaseHandler(MainActivity.this);
                final int id = MainActivity.this._adapter.getItem(position).getId();
                final Occupation occupation = db.getOccupation(id);

                switch (item.getItemId()) {
                    //delete
                    case R.id.item_delete_occupation:
                    {
                        showDeleteAlertDialog(occupation);
                    }
                    return true;

                    //send email
                    case R.id.item_send_mail:
                    {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        String[] to = new String[]{""};
                        String[] cc = new String[]{""};
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                        emailIntent.putExtra(Intent.EXTRA_CC, cc);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, MainActivity.this.getResources().getString(R.string.email_subject));
                        String name = MainActivity.this._adapter.getItem(position).getName();
                        HtmlMailBuilder mb = new HtmlMailBuilder(MainActivity.this,id,name);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, mb.get_text());
                        emailIntent.setType("message/rfc822");
                        MainActivity.this.startActivity(Intent.createChooser(emailIntent, "Email"));
                    }
                    return true;

                    //occupation parameters
                    case R.id.item_set_parameters:
                    {
                        Intent intent = new Intent("PunchCard.SettingsActivity");

                        intent.putExtra("id",occupation.getId());
                        MainActivity.this.startActivityForResult(intent, 2);
                    }
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }


    /**
     * Affiche une alerte pour le delete
     * @param occupation a deleter
     */
    private void showDeleteAlertDialog(final Occupation occupation)
    {
        final DataBaseHandler db = new DataBaseHandler(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle(R.string.delete_advertise);


        //button ok
        builder.setPositiveButton(R.string.ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        String message = occupation.getName() +  " " +
                                MainActivity.this.getString(R.string.occupation_deleted);

                        db.deleteOccupation(occupation);

                        MainActivity.this.refreshListView();

                        Toast.makeText(
                                MainActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        //button cancel
        builder.setNegativeButton(R.string.cancel_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }
        );


        final AlertDialog al = builder.create();
        al.show();
    }


    /**
     * Click sur un item de la liste
     * @param view
     */
    public void onClickJob(View view)
    {
        Intent intent = new Intent("PunchCard.ActivityHistory");

        int id = Integer.parseInt(
                ((TextView)view.findViewById(R.id.act_status)).getText().toString()
        );

        String name = ((TextView)view.findViewById(R.id.act_name)).getText().toString();

        intent.putExtra("id", id);
        intent.putExtra("name", name);

        startActivityForResult(intent, 1);
    }


    /**
     * Clieck sur le bouton Ajout ( + )
     * @param view
     */
    public void onClickAdd(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_add);
        builder.setTitle("Add job");

        final AlertDialog alertDialog = builder.create();
        final Activity act = this;

        alertDialog.show();

        final Button btnSave = (Button)alertDialog.findViewById(R.id.btn_add_save);


        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText txtBox = (EditText)alertDialog.findViewById(R.id.edit_name);


                //empeche text trop long
                if(txtBox.getText().toString().length() > 14) {
                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(alertDialog.getContext());
                    dlgAlert.setMessage("Name too long");
                    dlgAlert.setTitle("Error");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    return;
                }
                //create and add new occupation
                Occupation occupation = new Occupation();
                occupation.setName(txtBox.getText().toString());
                occupation.isIn(false);
                occupation.isSelected(false);

                occupation = addOccupation(occupation, _tempParam);
                refreshListView();

                alertDialog.dismiss();
            }
        });
    }


    /**
     * Ajoute une occupation dans la BD par le DataBaseHanlder
     * @param occupation
     * @param parameters
     * @return
     */
    private Occupation addOccupation(Occupation occupation, OccupationParameters parameters)
    {
        DataBaseHandler db = new DataBaseHandler(this);
        db.addOccupation(occupation);

        Occupation occ = db.getOccupation(occupation.getName());
        parameters.setOccupationId(occ.getId());

        db.addParameters(parameters);

        return occ;
    }


    /**
     * Rafraichie la ListView
     */
    public void refreshListView()
    {
        _adapter = new AdapterOccupation(this);

        _adapter.notifyDataSetInvalidated();
        setListAdapter(_adapter);
    }
}