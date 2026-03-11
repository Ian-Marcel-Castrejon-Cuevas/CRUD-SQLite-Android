package com.example.crudsqlite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView ImageCreate, ImageRead, ImageUpdate, ImageDelete, ImageErase, ImageSubir, ImageBajar;

    EditText EditId, EditFabricante, EditModelo, EditProcesador;

    AyudanteBD aBD;
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageCreate = findViewById(R.id.imageView);
        ImageRead = findViewById(R.id.imageView2);
        ImageUpdate = findViewById(R.id.imageView3);
        ImageDelete = findViewById(R.id.imageView4);
        ImageErase = findViewById(R.id.imageView5);
        ImageSubir = findViewById(R.id.imageView6);
        ImageBajar = findViewById(R.id.imageView7);

        EditId = findViewById(R.id.editTextText);
        EditFabricante = findViewById(R.id.editTextText2);
        EditModelo = findViewById(R.id.editTextText3);
        EditProcesador = findViewById(R.id.editTextText4);

        ImageRead.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                aBD = new AyudanteBD(getApplicationContext(), "00_20091187bd", null, 1);
                db = aBD.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM productos",null);

                String Registros = "REGISTROS \n\n";

                if (cursor != null) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        Registros += "ID: " + cursor.getString(0) + "\n";
                        Registros += "Fabricante: " + cursor.getString(1) + "\n";
                        Registros += "Modelo: " + cursor.getString(2) + "\n";
                        Registros += "Procesador: " + cursor.getString(3) + "\n\n";
                    }

                    Bundle bolsa = new Bundle();

                    bolsa.putString("registros", Registros.toString());

                    Intent int1 = new Intent(getApplicationContext(), RegistrosGeneral.class);
                    int1.putExtras(bolsa);
                    startActivity(int1);

                    cursor.close();
                } else {
                    Toast.makeText(getApplicationContext(), "No hay ningun dato", Toast.LENGTH_SHORT).show();
                }

                db.close();

                return true;
            }
        });

        ImageBajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertaUtil.mostrarAlertaConfirmacion(MainActivity.this, "¿Estás seguro de bajar los registros?", new AlertaUtil.OnConfirmacionListener() {
                    @Override
                    public void onConfirmacion(boolean confirmado) {
                        if (confirmado) {

                            bajar E = new bajar();
                            E.execute("http://huasteco.tiburcio.mx/~a20091187/consultar.php/");

                            Toast.makeText(getApplicationContext(), "Bajada Exitosa!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        ImageSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertaUtil.mostrarAlertaConfirmacion(MainActivity.this, "¿Estás seguro de subir los registros?", new AlertaUtil.OnConfirmacionListener() {
                    @Override
                    public void onConfirmacion(boolean confirmado) {
                        if (confirmado) {
                            aBD = new AyudanteBD(getApplicationContext(), "00_20091187bd", null, 1);
                            db = aBD.getReadableDatabase();
                            Cursor cursor = db.rawQuery("SELECT * FROM productos",null);

                            subir T = new subir();
                            T.execute("http://huasteco.tiburcio.mx/~a20091187/eliminar.php/");

                            while (cursor.moveToNext()){
                                subir H = new subir();
                                H.execute("http://huasteco.tiburcio.mx/~a20091187/insertar.php?id=" + cursor.getString(0) + "&nombre=" + cursor.getString(1) + "&precio=" + cursor.getString(2) + "&cat=" + cursor.getString(3));
                            }

                            Toast.makeText(getApplicationContext(), "Subida Exitosa!!", Toast.LENGTH_SHORT).show();

                            cursor.close();
                            db.close();
                        }
                    }
                });
            }
        });

        ImageCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!EditId.getText().toString().isEmpty() && !EditFabricante.getText().toString().isEmpty() &&
                        !EditModelo.getText().toString().isEmpty() && !EditProcesador.getText().toString().isEmpty()){

                    try {
                        aBD=new AyudanteBD(getApplicationContext(),"00_20091187bd",null,1);
                        db = aBD.getWritableDatabase();
                        if (db!=null) {

                            String id = EditId.getText().toString();

                            String query = "SELECT id FROM productos WHERE id = ?";
                            Cursor cursor = db.rawQuery(query, new String[]{id});

                            if (cursor.moveToFirst()) {
                                Toast.makeText(getApplicationContext(), "El ID ya está registrado.", Toast.LENGTH_SHORT).show();
                            } else {
                                ContentValues valores = new ContentValues();
                                valores.put("id", id);
                                valores.put("nombre", EditFabricante.getText().toString());
                                valores.put("precio", EditModelo.getText().toString());
                                valores.put("cat", EditProcesador.getText().toString());


                                db.insert("productos", null, valores);

                                Toast.makeText(getApplicationContext(), "Registro Agregado", Toast.LENGTH_SHORT).show();
                            }
                            cursor.close();
                            db.close();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "DB fue null :(", Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Error en la base de datos", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    Toast.makeText(getApplicationContext(), "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        ImageRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = EditId.getText().toString();

                if (!id.isEmpty()) {
                    try {
                        aBD = new AyudanteBD(getApplicationContext(), "00_20091187bd", null, 1);
                        db = aBD.getReadableDatabase();

                        if (db != null) {
                            Cursor cursor = db.rawQuery("SELECT * FROM productos WHERE id = ?", new String[]{id});

                            if (cursor != null && cursor.moveToFirst()) {
                                String fabricante = cursor.getString(1);
                                String modelo = cursor.getString(2);
                                String procesador = cursor.getString(3);


                                EditFabricante.setText(fabricante);
                                EditModelo.setText(modelo);
                                EditProcesador.setText(procesador);

                                cursor.close();
                            } else {
                                Toast.makeText(getApplicationContext(), "Verifique el ID", Toast.LENGTH_SHORT).show();
                            }

                            db.close();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al realizar la consulta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor, ingresa un ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!EditId.getText().toString().isEmpty() && !EditFabricante.getText().toString().isEmpty() &&
                        !EditModelo.getText().toString().isEmpty() && !EditProcesador.getText().toString().isEmpty()) {


                    try {
                        aBD = new AyudanteBD(getApplicationContext(), "00_20091187bd", null, 1);
                        db = aBD.getWritableDatabase();
                        if (db != null) {

                            String id = EditId.getText().toString();

                            String query = "SELECT id FROM productos WHERE id = ?";

                            AlertaUtil.mostrarAlertaConfirmacion(MainActivity.this, "¿Estás seguro de actualizar el registro?", new AlertaUtil.OnConfirmacionListener() {
                                @Override
                                public void onConfirmacion(boolean confirmado) {
                                    if (confirmado) {
                                        Cursor cursor = db.rawQuery(query, new String[]{id});

                                        if (cursor.moveToFirst()) {
                                            ContentValues valores = new ContentValues();
                                            valores.put("fabricante", EditFabricante.getText().toString());
                                            valores.put("modelo", EditModelo.getText().toString());
                                            valores.put("procesador", EditProcesador.getText().toString());

                                            db.update("productos", valores, "id = ?", new String[]{id});

                                            Toast.makeText(getApplicationContext(), "Registro Actualizado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "El ID no existe.", Toast.LENGTH_SHORT).show();
                                        }
                                        cursor.close();
                                        db.close();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "DB fue null :(", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error en la base de datos", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = EditId.getText().toString();

                if (!id.isEmpty()) {
                    try {
                        aBD = new AyudanteBD(getApplicationContext(), "00_20091187bd", null, 1);
                        db = aBD.getWritableDatabase();

                        if (db != null) {
                            String query = "SELECT id FROM productos WHERE id = ?";
                            AlertaUtil.mostrarAlertaConfirmacion(MainActivity.this, "¿Estás seguro de borrar el registro?", new AlertaUtil.OnConfirmacionListener() {
                                @Override
                                public void onConfirmacion(boolean confirmado) {
                                    if (confirmado) {
                                        Cursor cursor = db.rawQuery(query, new String[]{id});

                                        if (cursor.moveToFirst()) {
                                            db.delete("productos", "id = ?", new String[]{id});
                                            Toast.makeText(getApplicationContext(), "Registro Eliminado", Toast.LENGTH_SHORT).show();
                                            EditId.setText("");
                                            EditFabricante.setText("");
                                            EditModelo.setText("");
                                            EditProcesador.setText("");
                                        } else {
                                            Toast.makeText(getApplicationContext(), "El ID no existe.", Toast.LENGTH_SHORT).show();
                                        }
                                        cursor.close();
                                        db.close();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Error en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al eliminar el registro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor, ingresa un ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertaUtil.mostrarAlertaConfirmacion(MainActivity.this, "¿Estás seguro de borrar los datos?", new AlertaUtil.OnConfirmacionListener() {
                    @Override
                    public void onConfirmacion(boolean confirmado) {
                        if (confirmado) {
                            EditId.setText("");
                            EditFabricante.setText("");
                            EditModelo.setText("");
                            EditProcesador.setText("");
                        }
                    }
                });
            }
        });
    }

    class subir extends AsyncTask<String,Void,String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            return ConexionWeb(strings[0]);
        }
    }

    class bajar extends AsyncTask<String,Void,String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray tabla = new JSONArray(s);

                aBD = new AyudanteBD(getApplicationContext(), "00_20091187bd", null, 1);
                db = aBD.getReadableDatabase();
                db.execSQL("DELETE FROM productos");

                for(int i=0;i<tabla.length();i++) {
                    JSONObject renglon = tabla.getJSONObject(i);

                    ContentValues valorese = new ContentValues();
                    valorese.put("id", renglon.getString("id"));
                    valorese.put("fabricante", renglon.getString("nombre"));
                    valorese.put("modelo", renglon.getString("precio"));
                    valorese.put("procesador", renglon.getString("cat"));

                    db.insert("productos", null, valorese);
                }

                db.close();

            } catch (Exception e) {
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            return ConexionWeb(strings[0]);
        }
    }

    String ConexionWeb(String direccion) {

        String pagina="";
        try {
            URL url = new URL(direccion);

            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();


            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conexion.getInputStream()));

                String linea = reader.readLine();

                while (linea != null) {
                    pagina += linea + "\n";
                    linea = reader.readLine();
                }
                reader.close();

            } else {
                pagina += "ERROR: " + conexion.getResponseMessage() + "\n";
            }
            conexion.disconnect();
        }
        catch (Exception e){
            pagina+=e.getMessage();
        }
        return pagina;
    }
}