package com.vume.allinonegames.Fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vume.allinonegames.Model.JoshErrorResponse;
import com.vume.allinonegames.Model.JoshFcmTokenUpdateResponseData;
import com.vume.allinonegames.R;
import com.vume.allinonegames.SplashScreenActivity;
import com.vume.allinonegames.Util.ErrorUtils;
import com.vume.allinonegames.Util.JoshApplication;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoshRummyFcmService extends FirebaseMessagingService {
	public final static String TAG = "JOSHCALLBREAKFCM";

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		//RemoteMessage.Notification notification = remoteMessage.getNotification();
		Map<String, String> data = remoteMessage.getData();
		Log.d(TAG, "From = "+remoteMessage.getFrom() +"    data = "+data.toString());
		sendNotification(data);
	}

	@Override
	public void onNewToken(final String newToken) {
		super.onNewToken(newToken);
		Log.d(TAG,"received on new token = "+newToken);
		sendRegistrationToServer(newToken);
	}

	private void sendNotification(Map<String, String> data) {
		final String title = data.get("title");
		final String content = data.get("body");
		String channelId = getString(R.string.notification_channel_id);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Log.d(TAG, "Title = "+title+"  "+"Content = "+content);

		Intent intent = new Intent(this, SplashScreenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channelId,
					"Josh Rummy",
					NotificationManager.IMPORTANCE_DEFAULT);
			channel.enableLights(true);
			channel.enableVibration(true);
			AudioAttributes audioAttributes = new AudioAttributes.Builder()
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.setUsage(AudioAttributes.USAGE_NOTIFICATION)
					.build();
			channel.setSound(defaultSoundUri, audioAttributes);

			notificationManager.createNotificationChannel(channel);
		}

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setWhen(System.currentTimeMillis())
						.setSmallIcon(R.mipmap.ic_launcher_round)
						.setContentTitle(title)
						.setContentText(content)
						.setAutoCancel(true)
						.setVibrate(new long[]{0, 500, 1000})
						.setDefaults(Notification.DEFAULT_LIGHTS )
						.setSound(defaultSoundUri)
						.setContentIntent(pendingIntent);

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}

	private void sendRegistrationToServer(final String newToken){
		JoshApplication.getFcmTokenUpdateApiCall(getResources().getString(R.string.josh_server_api_key),
				JoshApplication.installKey(JoshApplication.mContext), JoshApplication.FCM_ID_TYPE, newToken).enqueue(new Callback<JoshFcmTokenUpdateResponseData>() {
			@Override
			public void onResponse(Call<JoshFcmTokenUpdateResponseData> call, Response<JoshFcmTokenUpdateResponseData> response) {
				if (response.isSuccessful()) {
					Log.d(TAG, "OnNewToken Service getFcmTokenUpdateApiCall Success - " + response.body().toString());
					final JoshFcmTokenUpdateResponseData joshFcmTokenUpdateResponseData = response.body();
					JoshApplication.saveFcmToken(newToken);

				} else {
					JoshErrorResponse errorResponseObj = ErrorUtils.parseError(response);
					Log.d(TAG, "OnNewToken Service getFcmTokenUpdateApiCall UnSuccessfull - " + response.errorBody());

				}
			}

			@Override
			public void onFailure(Call<JoshFcmTokenUpdateResponseData> call, Throwable t) {
				Log.d(TAG, "OnNewToken Service getFcmTokenUpdateApiCall UnSuccessfull - " + t.getLocalizedMessage());

			}
		});
	}
}