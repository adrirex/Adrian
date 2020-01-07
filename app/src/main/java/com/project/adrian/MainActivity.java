package com.project.adrian;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.project.adrian.DataBase.DBAdapter;
import com.project.adrian.DataModel.userdatamodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    String status;
    InsertDataBaseValues insertDataBaseValues;
    DBAdapter obj;
    ArrayList<userdatamodel> dh = new ArrayList<userdatamodel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertDataBaseValues = new InsertDataBaseValues();
        ArrayList<Integer> NumAssignatura = new ArrayList<>();
        ArrayList<String> Grupo = new ArrayList<>();
        ArrayList<String> Assignatura = new ArrayList<>();
        ArrayList<String> Profesor = new ArrayList<>();

        ArrayList<Integer> NumHorario = new ArrayList<>();
        ArrayList<String> Dia = new ArrayList<>();
        ArrayList<String> Hora_inicio = new ArrayList<>();
        ArrayList<String> Hora_fin = new ArrayList<>();

        ArrayList<Integer> IdClase = new ArrayList<>();
        ArrayList<Integer> IdHorario = new ArrayList<>();
        ArrayList<Integer> IdAssignatura = new ArrayList<>();

        NumAssignatura = insertDataBaseValues.NumAssignatura;
        Grupo = insertDataBaseValues.Group;
        Assignatura = insertDataBaseValues.Assignatura;
        Profesor = insertDataBaseValues.Profesor;

        NumHorario = insertDataBaseValues.NumHorario;
        Dia = insertDataBaseValues.Dia;
        Hora_inicio = insertDataBaseValues.Hora_inicio;
        Hora_fin = insertDataBaseValues.Hora_fin;

        IdClase = insertDataBaseValues.IdClase;
        IdHorario = insertDataBaseValues.IdHorario;
        IdAssignatura = insertDataBaseValues.IdAssignatura;


        SharedPreferences sharedPreferences = getSharedPreferences("app", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        status = sharedPreferences.getString("status", "");
        if (status.equals("hello")) {
        } else {
            obj = DBAdapter.getDBAdapter(getApplicationContext());
            if (obj.checkDatabase() == false)
                obj.createDatabase(getApplicationContext());
            obj.openDatabase();

            for (int i = 0; i < NumAssignatura.size(); i++) {
                System.out.println(NumAssignatura.get(i));
                obj.insertAssignaturas(NumAssignatura.get(i), Grupo.get(i), Assignatura.get(i), Profesor.get(i));
            }

            for (int i = 0; i < NumHorario.size(); i++) {
                obj.insertHorarios(NumHorario.get(i), Dia.get(i), Hora_inicio.get(i), Hora_fin.get(i));
            }

            for (int i = 0; i < IdClase.size(); i++) {
                obj.insertCla(IdClase.get(i), IdHorario.get(i), IdAssignatura.get(i));
            }
            editor.putString("status", "hello");
            editor.apply();
        }

        Intent abc=new Intent(getApplicationContext(),MyService.class);
        abc.putExtra("millisec",1000L);
        startService(abc);
    }
}
