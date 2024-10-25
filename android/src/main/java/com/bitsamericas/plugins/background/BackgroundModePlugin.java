package com.bitsamericas.plugins.background;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
// import android.app.ServiceInfo;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.content.Context;
import androidx.core.app.NotificationCompat;
import android.util.Log;

@CapacitorPlugin(name = "BackgroundMode")
public class BackgroundModePlugin extends Plugin {
    private BackgroundMode implementation = new BackgroundMode();
    private boolean isBackgroundActive = false;

    @PluginMethod
    public void requestAutoStartPermission(PluginCall call) {
        try {
            JSObject result = new JSObject();
            Context context = getContext();
            Intent intent = new Intent();
            String manufacturer = Build.MANUFACTURER;

            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
            }

            if (intent != null) {
                context.startActivity(intent);
            }

            result.put("tipo_celular", manufacturer);

            call.resolve(result);
        } catch (Exception e) {
            Log.e("AutoStartPlugin", "Error opening auto-start settings: " + e.getMessage());
            call.reject("Error opening auto-start settings.");
        }
    }

    // Método para activar el modo en segundo plano
    @PluginMethod
    public void activate(PluginCall call) {
        startForegroundService(call);
        isBackgroundActive = true;
        call.resolve();
    }

    // Método para desactivar el modo en segundo plano
    @PluginMethod
    public void deactivate(PluginCall call) {
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

    public void startForegroundService(PluginCall call) {
        Intent serviceIntent = new Intent(getContext(), AudioService.class);
        getContext().startForegroundService(serviceIntent);
        call.resolve();
    }

    public void stopForegroundService(PluginCall call) {
        Intent serviceIntent = new Intent(getContext(), AudioService.class);
        getContext().stopService(serviceIntent);
        call.resolve();
    }

    public static class AudioService extends Service {
        private static final String CHANNEL_ID = "ForegroundAudioServiceChannel";
        private PowerManager.WakeLock wakeLock;

        @Override
        public void onCreate() {
            super.onCreate();
            createNotificationChannel();
            // Adquirir el WakeLock
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::AudioWakeLock");
                wakeLock.acquire();
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            int type = 0;
            Notification notification = createNotification();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                type = 2;
            }
            Log.i("AudioService", "Se establece el tipo de servicio a MEDIA_PLAYBACK. Tipo: " + type);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(1, notification, 2);
            } else {
                startForeground(1, notification);
            }

            // INICIAR REPRODUCCION DE AUDIO
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            // Detén la reproducción de audio aquí
            // Liberar el WakeLock cuando se detiene el servicio
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        }

        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID,
                        "Foreground Audio Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(serviceChannel);
                }
            }
        }

        private Notification createNotification() {
            Context context = getApplicationContext();

            return new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Audio Service")
                    .setContentText("Reproduciendo audio en segundo plano")
                    .setSmallIcon(context.getApplicationInfo().icon) // Cambia esto por tu icono
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true) // Notificación no desechable
                    .setShowWhen(true)
                    .build();
        }
    }
}
