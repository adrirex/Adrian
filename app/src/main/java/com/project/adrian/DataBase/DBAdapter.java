package com.project.adrian.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.project.adrian.DataModel.userdatamodel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBAdapter extends SQLiteOpenHelper {
    public static final String NUMASSIGNATURA = "NumAssignatura";
    public static final String GRUPO = "Grupo";
    public static final String ASSIGNATURA = "Assignatura";
    public static final String PROFESSOR = "Profesor";

    public static final String NUMHORARIO = "NumHorario";
    public static final String DIA = "Dia";
    public static final String HORA_INICIO = "Hora_inicio";
    public static final String HORA_FIN = "Hora_fin";

    public static final String IDCLASE = "IdClase";
    public static final String IDHORARIO = "IdHorario";
    public static final String IDASSIGNATURA = "IdAssignatura";

    private static final String DATABASE_NAME = "adrian";
    private static final String DATABASE_ASSIGNATURAS = "Assignaturas";
    private static final String DATABASE_HORARIOS = "Horarios";
    private static final String DATABASE_CLASES = "Clases";
    private static final int DATABASE_VERSION = 1;
    public static String updatecomment = "";
    public static int userid = 0;
    static String name = "adrian.sqlite";
    static String path = "";
    static ArrayList<userdatamodel> a;
    static ArrayList<userdatamodel> arr;
    static SQLiteDatabase sdb;

    private DBAdapter(Context v) {

        super(v, name, null, 1);
        path = "/data/data/" + v.getApplicationContext().getPackageName()
                + "/databases";
    }

    public static synchronized DBAdapter getDBAdapter(Context v) {
        return (new DBAdapter(v));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + DATABASE_ASSIGNATURAS + " (" +
                NUMASSIGNATURA + " INTEGER PRIMARY KEY  NOT NULL  UNIQUE, " +
                GRUPO + " VARCHAR NOT NULL, " +
                ASSIGNATURA + " VARCHAR NOT NULL, " +
                PROFESSOR + " VARCHAR NOT NULL);"
        );

        db.execSQL(" CREATE TABLE " + DATABASE_HORARIOS + " (" +
                NUMHORARIO + " INTEGER NOT NULL , " +
                DIA + " VARCHAR NOT NULL, " +
                HORA_INICIO + " VARCHAR NOT NULL, " +
                HORA_FIN + " VARCHAR NOT NULL);"
        );

        db.execSQL(" CREATE TABLE " + DATABASE_CLASES + " (" +
                IDCLASE + " INTEGER PRIMARY KEY  NOT NULL  UNIQUE, " +
                IDHORARIO + " INTEGER NOT NULL, " +
                IDASSIGNATURA + " INTEGER NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public boolean checkDatabase() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(path + "/" + name, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {

        }

        if (db == null) {
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public void createDatabase(Context v) {
        this.getReadableDatabase();
        try {
            InputStream myInput = v.getAssets().open(name);
            // Path to the just created empty db
            String outFileName = path + "/" + name;
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] bytes = new byte[1024];
            int length;
            while ((length = myInput.read(bytes)) > 0) {
                myOutput.write(bytes, 0, length);
            }
            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();


            InputStream is = v.getAssets().open("adrian.sqlite");
            System.out.println(new File(path + "/" + name).getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(path + "/" + name);
            int num = 0;
            while ((num = is.read()) > 0) {
                fos.write((byte) num);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void openDatabase() {
        try {
            sdb = SQLiteDatabase.openDatabase(path + "/" + name, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void insertAssignaturas(Integer numAss,String grupo, String assignatura, String profesor) {

        ContentValues cv = new ContentValues();
        cv.put("NumAssignatura",numAss);
        cv.put("Grupo", grupo);
        cv.put("Assignatura", assignatura);
        cv.put("Profesor", profesor);

        sdb.insert("Assignaturas", null, cv);
    }

    public void insertHorarios(Integer numHorarios,String dia, String Hora_inicio, String Hora_fin) {

        ContentValues cv = new ContentValues();
        cv.put("NumHorario",numHorarios);
        cv.put("Dia", dia);
        cv.put("Hora_inicio", Hora_inicio);
        cv.put("Hora_fin", Hora_fin);

        sdb.insert("Horarios", null, cv);
    }

    public void insertCla(Integer IdClass, Integer IdHorario, Integer IdAssignatura) {

        ContentValues cv = new ContentValues();
        cv.put("IdClase",IdClass);
        cv.put("IdHorario", IdHorario);
        cv.put("IdAssignatura", IdAssignatura);

        sdb.insert("Clases", null, cv);
    }

    public void insertUserData(String name, String address, String city, String area, String mobile, String rent, String ava, String cat, ArrayList<Bitmap> img) {
        byte[] byteArray = null;
        for (int i = 0; i < img.size(); i++) {
            if (img.get(i) != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.get(i).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
            }
        }
        ContentValues cv = new ContentValues();
        cv.put("username", name);
        cv.put("useraddress", address);
        cv.put("usercity", city);
        cv.put("userarea", area);
        cv.put("usermobile", mobile);
        cv.put("userrent", rent);
        cv.put("userava", ava);
        cv.put("usercat", cat);
        cv.put("userimages", byteArray);

        sdb.insert("userdata", null, cv);
    }

        public ArrayList<userdatamodel> getData(String startTime,String day) {
        try {
            Cursor c1 = sdb.rawQuery("select A.*  from Assignaturas  A , Horarios H , Clases C where  C.IdAssignatura = A.NumAssignatura and H.Hora_inicio='"+startTime+"' and  H.Dia='"+day+"' and A.Grupo ='GrupoB' and H.NumHorario=C.IdHorario", null);
            a = new ArrayList<userdatamodel>();
            while (c1.moveToNext()) {
                userdatamodel q1 = new userdatamodel();
                q1.setSub(c1.getString(2));
                q1.setProfesor(c1.getString(3));
                a.add(q1);
                Log.d("Adrian", "Hello");
            }
            return a;
        } catch (Exception e) {
            Log.d("DBAdapter", e + "");
        }
        return a;
    }

//    public int getData(String userName, String pass) {
//
//        Cursor c1 = sdb.rawQuery("select user_id,user_name,user_mobile,user_password from register where user_mobile='" + userName + "' and user_password='" + pass + "'", null);
//        int i = 0;
//
//        while (c1.moveToNext()) {
//            i = 1;
//            userid = c1.getInt(0);
//        }
//        return i;
//    }

    public void getComment(int id) {
        Cursor c1 = sdb.rawQuery("select id,lat,lan,date,time,photo,status,comment,type,videouri from photoDetails where id='" + id + "'", null);
        while (c1.moveToNext()) {
            updatecomment = c1.getString(7);
        }
    }

    public void Updateime(int id, String stu) {
        sdb.execSQL("UPDATE photoDetails set status='" + stu + "' WHERE id='" + id + "'");
    }

    public void UpdateComment(int id, String stu) {
        sdb.execSQL("UPDATE photoDetails set comment='" + stu + "' WHERE id='" + id + "'");
    }

    public void DeleteData(int id) {
        sdb.execSQL("DELETE FROM photoDetails WHERE id='" + id + "'");
    }

}
