package com.example.speech2text;

import android.Manifest;
import static com.example.speech2text.Functions.wishMe;

//import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import android.app.AlarmManager;
//import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.media.MediaPlayer;
import android.media.AudioManager;
import android.net.Uri;
//import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
//import java.security.AccessControlContext;
//import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

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
//        player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
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
        //        startService(new Intent(this, AudioRecorderService.class));

    }
    private SpeechRecognizer recognizer;
    private TextView textView;
    private TextToSpeech tts;

//    private MediaPlayer player;
//    private final int requestId = 1;

//    private Object context;

//    public boolean isReadStoragePermissionGranted(){
//        if(Build.VERSION.SDK_INT >= 23){
//            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                Log.v(TAG, "Permission is granted 1");
//                return true;
//            }
//            else{
//                Log.v(TAG, "Permission is revoked 1");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//                return false;
//            }
//        }
//        else{
//            Log.v(TAG, "Permission is granted1");
//            return true;
//        }
//    }
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
//        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
//            tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
//        }
//        else{
//            tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
//        }
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void findViewById() {
        textView = findViewById(R.id.textView);
    }

    private void response(String msg){
        String msgs = msg.toLowerCase(Locale.ROOT);
        if(msgs.contains("hi")){
            speak("Hello Sir, Jarvis at your service Please tell me how can I help you?");
        }
        if(msgs.contains("time")){
            Date date = new Date();
            String time = DateUtils.formatDateTime(this, date.getTime(),DateUtils.FORMAT_SHOW_TIME);
            speak(time);
        }
        if(msgs.contains("date")){
            SimpleDateFormat dt = new SimpleDateFormat("dd MM yyyy");
            Calendar cal = Calendar.getInstance();
            String today_Date = dt.format(cal.getTime());
            speak("the date today is"+today_Date);
        }
        if(msgs.contains("google")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(intent);
        }
        if(msgs.contains("code chef")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.codechef.com"));
            startActivity(intent);
        }
        if(msgs.contains("youtube")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
            startActivity(intent);
        }
        if(msgs.contains("facebook")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
            startActivity(intent);
        }
        if(msgs.contains("instagram")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"));
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
        if (msgs.contains("music")) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.setPackage("com.android.music"); // Package name of the default music player app
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            sendBroadcast(intent);
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
        if(msgs.contains("calculator")){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setPackage("com.vivo.calculator");
            startActivity(Intent.createChooser(intent, ""));
            startActivity(intent);
        }
        if(msgs.contains("youtube music")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_MUSIC);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
//        if(msgs.indexOf("song")!=-1) {
//            playMusic musicPlayer = new playMusic();
//            musicPlayer.playMusic("https://");
//        }
        if(msgs.contains("call")){
            Uri number = Uri.parse("tel:9714157380");
            Intent intent = new Intent(Intent.ACTION_DIAL, number);
            try{
                startActivity(intent);
            }catch(ActivityNotFoundException e){
                //dls ds
            }
        }
//        if(msgs.indexOf("set alarm at")!=-1){
//            Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2} [AP]M");
//            Matcher matcher = pattern.matcher(msgs);
//            if (matcher.find()) {
//                try {
//                    String timeString = matcher.group();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
//                    Calendar alarmTime = Calendar.getInstance();
//                    alarmTime.setTime(dateFormat.parse(timeString));
//
//                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//                    AccessControlContext context = getContext();
//                    Intent intent = new Intent(context, new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//                            Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
//                }catch (ParseException e){
//                    e.printStackTrace();
//                }
//            }
//
//        }
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

    public void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        recognizer.startListening(intent);
    }
}