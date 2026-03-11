package com.example.crudsqlite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertaUtil {

    public static void mostrarAlertaConfirmacion(Context context, String mensaje, final OnConfirmacionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensaje)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onConfirmacion(true);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onConfirmacion(false);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public interface OnConfirmacionListener {
        void onConfirmacion(boolean confirmado);
    }
}
