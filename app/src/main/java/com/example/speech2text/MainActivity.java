package com.example.speech2text;

import static com.example.speech2text.Functions.wishMe;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","before  ");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            Log.d("MainActivity", "Working ");
        }
        Dexter.withContext(this)
                .withPermission(android.Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener(){
                    @Override public void onPermissionGranted(PermissionGrantedResponse response){}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response){
                        System.exit(0);
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token){
                        token.continuePermissionRequest();
                    }
                }).check();

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);

        initTextToSpeech();
        findViewById();
        result();

        Button startButton = findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

    }
    private SpeechRecognizer recognizer;
    private TextView textView;
    private TextToSpeech tts;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "Granted");
            }
            else{
                Log.d(TAG, "Denied");
            }
        }
    }

    private void initTextToSpeech(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(tts.getEngines().size()==0){
                    Toast.makeText(MainActivity.this, "Engine is not Available", Toast.LENGTH_SHORT).show();
                }
                else{
                    String s = wishMe();
                    speak("Hi I'm JARVIS AI mark one. "+s);
                }
            }
        });
    }



    private void speak(String msg) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void findViewById() {
        textView = findViewById(R.id.textView);
    }

    private void response(String msg){
        String msgs = msg.toLowerCase(Locale.ROOT);
        if(msgs.contains("hi") || msgs.contains("hello") || msgs.contains("hey")){
            speak("Hello Sir, Jarvis at your service Please tell me how can I help you?");
        }
        if(msgs.contains("time")){
            Date date = new Date();
            String time = DateUtils.formatDateTime(this, date.getTime(),DateUtils.FORMAT_SHOW_TIME);
            speak(time);
        }
        if(msgs.contains("day")){
            Date date = new Date();
            String day = DateUtils.formatDateTime(this, date.getDay(), DateUtils.FORMAT_SHOW_WEEKDAY);
            speak("today is " + day);
        }
        if(msgs.contains("date")){
            SimpleDateFormat dt = new SimpleDateFormat("dd MM yyyy");
            Calendar cal = Calendar.getInstance();
            String today_Date = dt.format(cal.getTime());
            speak("the date today is"+today_Date);
        }

        String[] appNames = {"google", "facebook", "instagram", "code chef", "youtube", "linkedin", "hackerrank"};
        for(String appName: appNames){
            if(msgs.contains(appName)){
                String formattedAppName = appName.replace(" ", "");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www."+formattedAppName+".com"));
                startActivity(intent);
            }
        }
        if(msgs.contains("music")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_MUSIC);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if(msgs.contains("search")){
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+msgs.replace("search", " ")));
            startActivity(intent);
        }
        if(msgs.contains("remember")){
            speak("Okay i'll remember that for you!");
            writeToFile(msgs.replace("jarvis remember that", " "));
        }
        if(msgs.contains("know")){
            String data = readFromFile();
            speak("Yes sir you told me to remember that "+data);
        }
        if(msgs.contains("thank you")){
            speak("You're Welcome! I'm here to assist you. Is there anything else on your mind?");
        }
        if(msgs.contains("whatsapp")){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            intent.setType("text/plain");
            intent.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(intent, ""));
            startActivity(intent);
        }
        if (msgs.contains("calculator")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_CALCULATOR);

            PackageManager packageManager = getPackageManager();
            ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            if(resolveInfo != null){
                intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                startActivity(intent);
            }
            else{
                speak("No application found");
            }
        }


        if (msgs.contains("call")) {
            String contactName = msgs.replace("call", "").trim();

            if (!contactName.isEmpty()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return;
                }

                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        null,
                        "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") = ?",
                        new String[]{contactName.toUpperCase()},
                        null
                );

                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                            Cursor phoneCursor = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{String.valueOf(contactId)},
                                    null
                            );

                            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                Uri callUri = Uri.parse("tel:" + phoneNumber);
                                Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
                                startActivity(callIntent);
                            } else {
                                speak("No phone number found for the contact.");
                            }

                            if (phoneCursor != null) {
                                phoneCursor.close();
                            }
                        } else {
                            speak("No contact found. Please try again.");
                        }
                    } finally {
                        cursor.close();
                    }
                }
            }
        }
        if(msgs.contains("alarm")){
            String time = msgs.replace("set alarm for", "").trim();

            setAlarm(time);

            speak("Alarm set for "+ time);
        }
    }

    private void result() {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {
                    String errorMessage;
                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            errorMessage = "Audio recording error";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            errorMessage = "Client side error";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            errorMessage = "Insufficient permissions";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            errorMessage = "Network error";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            errorMessage = "Network timeout";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            errorMessage = "No match";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            errorMessage = "RecognitionService busy";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            errorMessage = "Server error";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            errorMessage = "No speech input";
                            break;
                        case SpeechRecognizer.ERROR_CANNOT_CHECK_SUPPORT:
                            errorMessage = "Not supported";
                            break;
                        case SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE:
                            errorMessage = "Language is not available";
                            break;
                        case SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED:
                            errorMessage = "Language is not supported";
                            break;
                        case SpeechRecognizer.ERROR_SERVER_DISCONNECTED:
                            errorMessage = "Server disconnected";
                            break;
                        case SpeechRecognizer.ERROR_TOO_MANY_REQUESTS:
                            errorMessage = "Too many requests";
                            break;
                        default:
                            errorMessage = "Unknown error";
                            break;
                    }
                    Log.e("MyTag", "Recognition error: " + errorMessage);
                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    Toast.makeText(MainActivity.this, ""+result.get(0), Toast.LENGTH_SHORT).show();
                    textView.setText(result.get(0));
                    response(result.get(0));

                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }
    }

    private String readFromFile() {
        String ret = "";
        try{
            InputStream inputStream = openFileInput("data.txt");
            if(inputStream!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String Recievestr;
                StringBuilder stringBuilder = new StringBuilder();

                while((Recievestr = bufferedReader.readLine())!=null){
                    stringBuilder.append("\n").append(Recievestr);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch(FileNotFoundException e){
            Log.e("Exception", "File not found "+ e);
        }
        catch(IOException e){
            Log.e("Exception", "can not read File"+e);
        }
        return ret;
    }

    private void writeToFile(String data) {
        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch(IOException e){
            Log.e("Exception","File Write Failed"+e);
        }
    }

    private void setAlarm(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }





    private String getDefaultClockAppPackageName() {
        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo packageInfo : installedPackages) {
            if (packageInfo.applicationInfo.metaData != null) {
                if (packageInfo.applicationInfo.metaData.getBoolean("android.alarm.clock", false)) {
                    return packageInfo.packageName;
                }
            }
        }

        return null;
    }


    public void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        recognizer.startListening(intent);
    }
}