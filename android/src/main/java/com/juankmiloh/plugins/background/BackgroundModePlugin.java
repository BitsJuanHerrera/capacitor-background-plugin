package com.juankmiloh.plugins.background;

import android.Manifest;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.content.Context;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import android.util.Log;

@CapacitorPlugin(name = "BackgroundMode")
public class BackgroundModePlugin extends Plugin {
    private final BackgroundMode implementation = new BackgroundMode();
    private boolean isBackgroundActive = false;

    // Variables para almacenar los ajustes de la notificación
    private String notificationTitle = "Audio Service";
    private String notificationText = "Reproducción de audio en segundo plano";
    private String notificationIcon = "ic_launcher"; // Nombre del icono por defecto
    private boolean showWhen = true;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    // Metodo para solicitar permiso para activar notificaciones
    @PluginMethod
    public void requestNotificationPermission(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[] { Manifest.permission.POST_NOTIFICATIONS },
                        1);
            } else {
                // El permiso ya fue otorgado
                Log.i("[BackgroundMode]", "[PERMISSION] Notification Granted");
                call.resolve();
            }
        } else {
            // El permiso no es necesario para versiones anteriores
            call.resolve();
        }
    }

    // Método para activar el modo en segundo plano
    @PluginMethod
    public void enable(PluginCall call) {
        startForegroundService(call);
        isBackgroundActive = true;
        call.resolve();
    }

    // Método para desactivar el modo en segundo plano
    @PluginMethod
    public void disable(PluginCall call) {
        stopForegroundService(call);
        isBackgroundActive = false;
        call.resolve();
    }

    // Método para verificar si el modo en segundo plano está activo
    @PluginMethod
    public void isActive(PluginCall call) {
        JSObject result = new JSObject();
        result.put("active", isBackgroundActive);
        call.resolve(result);
    }

    // Método para configurar los ajustes desde JS
    @PluginMethod
    public void setSettings(PluginCall call) {
        JSObject data = call.getData();

        // Asignar valores desde el objeto de datos
        if (data.has("title")) {
            notificationTitle = data.getString("title");
        }
        if (data.has("text")) {
            notificationText = data.getString("text");
        }
        if (data.has("icon")) {
            notificationIcon = data.getString("icon");
        }
        if (data.has("showWhen")) {
            showWhen = Boolean.TRUE.equals(data.getBool("showWhen"));
        }

        // Enviar un Intent al servicio para actualizar la notificación
        Intent updateIntent = new Intent(getContext(), AudioService.class);
        updateIntent.putExtra("title", notificationTitle);
        updateIntent.putExtra("text", notificationText);
        updateIntent.putExtra("icon", notificationIcon);
        updateIntent.putExtra("showWhen", showWhen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(updateIntent); // Actualizar notificación en segundo plano
        }

        call.resolve();
    }

    // Método para obtener los ajustes actuales
    @PluginMethod
    public void getSettings(PluginCall call) {
        JSObject settings = new JSObject();
        settings.put("title", notificationTitle);
        settings.put("text", notificationText);
        settings.put("icon", notificationIcon);
        settings.put("showWhen", showWhen);
        call.resolve(settings);
    }

    public void startForegroundService(PluginCall call) {
        Intent serviceIntent = new Intent(getContext(), AudioService.class);
        // Pasar los valores configurados al servicio
        serviceIntent.putExtra("title", notificationTitle);
        serviceIntent.putExtra("text", notificationText);
        serviceIntent.putExtra("icon", notificationIcon);
        serviceIntent.putExtra("showWhen", showWhen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(serviceIntent);
        }
        call.resolve();
        Log.i("[BackgroundMode]", "[START] foreground service");
    }

    public void stopForegroundService(PluginCall call) {
        Intent serviceIntent = new Intent(getContext(), AudioService.class);
        getContext().stopService(serviceIntent);
        call.resolve();
        Log.i("[BackgroundMode]", "[STOP] foreground service");
    }

    /**
     * Llamado cuando se cierra la app
     */
    @Override
    public void handleOnDestroy() {
        Intent serviceIntent = new Intent(getContext(), AudioService.class);
        getContext().stopService(serviceIntent);
        Log.i("[BackgroundMode]", "[stopService] Service and notification stopped.");
    }

    public static class AudioService extends Service {
        private static final String CHANNEL_ID = "ForegroundAudioServiceChannel";

        private String notificationTitle = "Audio Service";
        private String notificationText = "Reproducción de audio en segundo plano";
        private String notificationIcon = "ic_launcher";
        private boolean showWhen = true;

        @Override
        public void onCreate() {
            super.onCreate();
            createNotificationChannel();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if (intent.hasExtra("title")) {
                notificationTitle = intent.getStringExtra("title");
            }
            if (intent.hasExtra("text")) {
                notificationText = intent.getStringExtra("text");
            }
            if (intent.hasExtra("icon")) {
                notificationIcon = intent.getStringExtra("icon");
            }
            if (intent.hasExtra("showWhen")) {
                showWhen = intent.getBooleanExtra("showWhen", true);
            }

            Notification notification = createNotification();

            Log.i("[BackgroundMode]", "[VERSION SDK ANDROID] " + Build.VERSION.SDK_INT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                startForeground(1, notification);
            }

            return START_REDELIVER_INTENT;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            // Detener el servicio en primer plano y eliminar la notificación
            stopForeground(true);
            Log.i("[BackgroundMode]", "[onDestroy] End Service");
        }

        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID,
                        "@juankmiiloh - Audio Service Channel",
                        NotificationManager.IMPORTANCE_LOW); // IMPORTANCE_LOW para quitar el sonido
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(serviceChannel);
                }
            }
        }

        private Notification createNotification() {
            Context context = getApplicationContext();
            int iconResId = context.getResources().getIdentifier(notificationIcon, "drawable",
                    context.getPackageName());

            // Crear un PendingIntent para abrir la app al presionar la notificación
            Intent notificationIntent = new Intent(context, getMainActivityClass());
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            return new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setSmallIcon(iconResId != 0 ? iconResId : context.getApplicationInfo().icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setShowWhen(showWhen)
                    .setContentIntent(pendingIntent) // Asignar el PendingIntent a la notificación
                    .build();
        }

        // Método para obtener la clase de la actividad principal de la app
        private Class<?> getMainActivityClass() {
            try {
                return Class.forName(getApplicationContext().getPackageName() + ".MainActivity");
            } catch (ClassNotFoundException e) {
                Log.e("[BackgroundMode]", "No se pudo encontrar la clase MainActivity", e);
                return null;
            }
        }
    }
}
