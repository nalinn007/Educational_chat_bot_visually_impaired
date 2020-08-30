package com.tushar.cortana;

import android.Manifest;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;
import com.takusemba.multisnaprecyclerview.OnSnapListener;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;


public class MainPage extends AppCompatActivity {
    final int REQ_CODE_SPEECH_INPUT = 1;
    final int YourRequestCode=1;
    GifImageView mains,listeners;
    TextView listens;
    TextView message,reply;
    TextToSpeech textToSpeech;


    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";

    private static final String TAG = "MainActivity";
    private Assistant watsonAssistant;
    private SessionResponse watsonAssistantSession;

    String inputmessage=new String();
    String results=new String();
    SpeechRecognizer recognizer;
    private Context mContext;
    TextView resultTextView;


    JSONArray items=new JSONArray();
    JSONObject images=new JSONObject();
    JSONObject result_g;

    ArrayList<String> arr=new ArrayList<>();
    ArrayList<String> arr2=new ArrayList<>();
    TextView texts4;

    MultiSnapRecyclerView multiSnapRecyclerView;
    ArrayList<String> aList=new ArrayList<>();
    TextView imgtext;
    TextView webtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        mains=(GifImageView)findViewById(R.id.main);
        listeners=(GifImageView)findViewById(R.id.gifImageView);
        multiSnapRecyclerView=findViewById(R.id.imagelist);
        imgtext=(TextView)findViewById(R.id.textView5);
        webtext=(TextView)findViewById(R.id.textView6);

        ToolTipsManager mToolTipsManager;
        mToolTipsManager = new ToolTipsManager();


        ImageButton bac=(ImageButton)findViewById(R.id.imageButton411);
        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainPage.this,SelectActivity.class);
                startActivity(i);
            }
        });


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        listens=(TextView)findViewById(R.id.textView2);
        resultTextView = (TextView) findViewById(R.id.textView3);


        texts4=(TextView)findViewById(R.id.textView4);

//        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
//                .setTopColorRes(R.color.ld_textInputError)
//                .setButtonsColorRes(R.color.colorPrimary)
//                .setIcon(R.drawable.error)
//                .setTitle("No Voice feature")
//                .setMessage("Your phone does not support the voice feature")
//                .setPositiveButton("Continue", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                })
//                .show();


        watsonAssistant = new Assistant("2018-02-28", new IamOptions.Builder()
                .apiKey("NjtcJc1OvF5-CnQvf_OgEfrF0pwhzOU_LAYhAwle-2SP")
                .build());
        watsonAssistant.setEndPoint("https://gateway-lon.watsonplatform.net/assistant/api");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        multiSnapRecyclerView.setLayoutManager(layoutManager);
        multiSnapRecyclerView.setLayoutManager(layoutManager);



        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (ContextCompat.checkSelfPermission(MainPage.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainPage.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    YourRequestCode);
        }


        //get results by typing
        final EditText edittext = (EditText) findViewById(R.id.editText);
        ConstraintLayout mRootView = ((ConstraintLayout)findViewById(R.id.lkj));
        ToolTip.Builder builder = new ToolTip.Builder(this, edittext,mRootView , "Enter a search query", ToolTip.POSITION_ABOVE);
        mToolTipsManager.show(builder.build());
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    inputmessage= edittext.getText().toString();
                    String url="https://nlp-02.azurewebsites.net/users?txt="+inputmessage;
                    String resl=new String();




                    listeners.setVisibility(View.VISIBLE);
                    mains.setVisibility(View.GONE);
                    multiSnapRecyclerView.setVisibility(View.INVISIBLE);
                    imgtext.setVisibility(View.INVISIBLE);
                    webtext.setVisibility(View.INVISIBLE);
                    aList.clear();
                    edittext.setText(inputmessage);

                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            try {

                                if (watsonAssistantSession == null) {
                                    ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId("b08c44f2-2f5a-4f3f-9666-54ae9be7a6bb").build());
                                    watsonAssistantSession = call.execute().getResult();
                                }

                                MessageInput input = new MessageInput.Builder()
                                        .text(inputmessage)
                                        .build();
                                MessageOptions options = new MessageOptions.Builder()
                                        .assistantId("b08c44f2-2f5a-4f3f-9666-54ae9be7a6bb")
                                        .input(input)
                                        .sessionId(watsonAssistantSession.getSessionId())
                                        .build();

                                MessageResponse response = watsonAssistant.message(options).execute().getResult();
                                Log.i(TAG, "run: "+response);
                                final Message outMessage = new Message();
                                if (response != null &&
                                        response.getOutput() != null &&
                                        !response.getOutput().getGeneric().isEmpty() &&
                                        "text".equals(response.getOutput().getGeneric().get(0).getResponseType())) {
                                    String str1=response.getOutput().getGeneric().get(0).getText();
                                    final String strs=response.getOutput().getGeneric().get(0).getText();

                                    if(str1.toLowerCase().contains("didn't") || str1.toLowerCase().contains("reword")) {
                                        int speechStatus = textToSpeech.speak("I didn't understand.Searching Google for resources", TextToSpeech.QUEUE_FLUSH, null, null);
                                        if (speechStatus == TextToSpeech.ERROR) {
                                            Log.e("TTS", "Error in converting Text to Speech!");
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listens.setText("Here's what I what found!!");
                                                listeners.setVisibility(View.VISIBLE);
                                                listens.setVisibility(View.VISIBLE);
                                                resultTextView.setText("");
                                                texts4.setText("");


                                            }
                                        });

                                        //Search Engine Google Code
                                        String searchString=inputmessage;
                                        String searchStringNoSpaces = searchString.replace(" ", "+");

                                        // Your API key
                                        // TODO replace with your value
                                        String key="AIzaSyAJD6s89FxsWImcwFBBQ7w2q5cGZ2j02qg";

                                        // Your Search Engine ID
                                        // TODO replace with your value
                                        String cx = "006889087434720476417:aaedxkhnhtn";

                                        String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
                                        URL url = null;
                                        try {
                                            url = new URL(urlString);
                                        } catch (MalformedURLException e) {
                                            Log.e(TAG, "ERROR converting String to URL " + e.toString());
                                        }
                                        Log.d(TAG, "Url = "+  urlString);


                                        // start AsyncTask
                                        GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
                                        searchTask.execute(url);



                                    }
                                    else {
                                        int speechStatus = textToSpeech.speak(response.getOutput().getGeneric().get(0).getText(), TextToSpeech.QUEUE_FLUSH, null, null);

                                        if (speechStatus == TextToSpeech.ERROR) {
                                            Log.e("TTS", "Error in converting Text to Speech!");
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listens.setText(strs);
                                                listeners.setVisibility(View.VISIBLE);
                                                listens.setVisibility(View.VISIBLE);
                                                resultTextView.setText("");
                                                texts4.setText("");
                                            }
                                        });
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                    return true;
                }
                return false;
            }
        });


        //get results by voice
        ImageView start = (ImageView) findViewById(R.id.imageButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiSnapRecyclerView.setVisibility(View.INVISIBLE);
                imgtext.setVisibility(View.INVISIBLE);
                webtext.setVisibility(View.INVISIBLE);
                aList.clear();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                        "com.tushar.cortana");

                recognizer = SpeechRecognizer
                        .createSpeechRecognizer(MainPage.this);
                final RecognitionListener listener = new RecognitionListener() {
                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> voiceResults = results
                                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (voiceResults == null) {
                            System.out.println("No voice results");
                            listens.setText("No Results Found");
                        } else {
                            System.out.println("Printing matches: ");

                            //if matches found
                            for (String match : voiceResults) {
                                System.out.println(match);

                                inputmessage=match;
                                String url="https://nlp-02.azurewebsites.net/users?txt=hellll"+inputmessage;
                                String resl=new String();

                                String done=new String();
                                edittext.setText(inputmessage);

                                Thread thread = new Thread(new Runnable() {
                                    public void run() {
                                        try {

                                            if (watsonAssistantSession == null) {
                                                ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId("b08c44f2-2f5a-4f3f-9666-54ae9be7a6bb").build());
                                                watsonAssistantSession = call.execute().getResult();
                                            }

                                            MessageInput input = new MessageInput.Builder()
                                                    .text(inputmessage)
                                                    .build();
                                            MessageOptions options = new MessageOptions.Builder()
                                                    .assistantId("b08c44f2-2f5a-4f3f-9666-54ae9be7a6bb")
                                                    .input(input)
                                                    .sessionId(watsonAssistantSession.getSessionId())
                                                    .build();

                                            final MessageResponse response = watsonAssistant.message(options).execute().getResult();
                                            Log.i(TAG, "run: "+response);
                                            final Message outMessage = new Message();
                                            if (response != null &&
                                                    response.getOutput() != null &&
                                                    !response.getOutput().getGeneric().isEmpty() &&
                                                    "text".equals(response.getOutput().getGeneric().get(0).getResponseType())) {
                                                String str1=response.getOutput().getGeneric().get(0).getText();

                                                if(str1.toLowerCase().contains("didn't") || str1.toLowerCase().contains("reword")) {
                                                    int speechStatus = textToSpeech.speak("I didn't understand.Searching Google for resources", TextToSpeech.QUEUE_FLUSH, null, null);
                                                    if (speechStatus == TextToSpeech.ERROR) {
                                                        Log.e("TTS", "Error in converting Text to Speech!");
                                                    }

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            listens.setText("Here's what I what found!!");
                                                            resultTextView.setText("");
                                                            texts4.setText("");


                                                        }
                                                    });

                                                    String searchString=inputmessage;
                                                    String searchStringNoSpaces = searchString.replace(" ", "");

                                                    // Your API key
                                                    // TODO replace with your value
                                                    String key="AIzaSyAJD6s89FxsWImcwFBBQ7w2q5cGZ2j02qg";

                                                    // Your Search Engine ID
                                                    // TODO replace with your value
                                                    String cx = "006889087434720476417:aaedxkhnhtn";

                                                    String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
                                                    URL url = null;
                                                    try {
                                                        url = new URL(urlString);
                                                    } catch (MalformedURLException e) {
                                                        Log.e(TAG, "ERROR converting String to URL " + e.toString());
                                                    }
                                                    Log.d(TAG, "Url = "+  urlString);


                                                    // start AsyncTask
                                                    GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
                                                    searchTask.execute(url);

                                                }
                                                else {
                                                    int speechStatus = textToSpeech.speak(response.getOutput().getGeneric().get(0).getText(), TextToSpeech.QUEUE_FLUSH, null, null);

                                                    if (speechStatus == TextToSpeech.ERROR) {
                                                        Log.e("TTS", "Error in converting Text to Speech!");
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            listens.setText(response.getOutput().getGeneric().get(0).getText());
                                                            resultTextView.setText("");
                                                            texts4.setText("");
                                                        }
                                                    });
                                                }

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                thread.start();
                                break;

                            }
                        }
                    }

                    @Override
                    public void onReadyForSpeech(Bundle params) {
                        System.out.println("Ready for speech");
                        mains.setVisibility(View.INVISIBLE);
                        listeners.setVisibility(View.VISIBLE);
                        listens.setVisibility(View.VISIBLE);
                        listens.setText("I'm Listening...");


                    }
                    @Override
                    public void onError(int error) {
                        System.err.println("Error listening for speech: " + error);
                        listens.setText("I can't hear your voice");
                        int speechStatus = textToSpeech.speak("I can't hear your voice.", TextToSpeech.QUEUE_FLUSH, null, null);
                        if (speechStatus == TextToSpeech.ERROR) {
                            Log.e("TTS", "Error in converting Text to Speech!");
                        }
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        System.out.println("Speech starting");
                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onEndOfSpeech() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {
                        // TODO Auto-generated method stub

                    }
                };
                recognizer.setRecognitionListener(listener);
                recognizer.startListening(intent);
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bring up Voice Chat");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech Not Available",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<String> result = new ArrayList<>();
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    EditText input = ((EditText) findViewById(R.id.editText));
                    input.setText(result.get(0));
                    // set the input data to the editText alongside if want to.



                }
                break;
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if(recognizer!=null){
            recognizer.destroy();
        }
    }

    private String send_message(){
        results="Some error Occured,Sorry";

//        final String inputmessage = inputMessage;
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                try {
//
//                    if (watsonAssistantSession == null) {
//                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId("b08c44f2-2f5a-4f3f-9666-54ae9be7a6bb").build());
//                        watsonAssistantSession = call.execute().getResult();
//                    }
//
//                    MessageInput input = new MessageInput.Builder()
//                            .text(inputmessage)
//                            .build();
//                    MessageOptions options = new MessageOptions.Builder()
//                            .assistantId("b08c44f2-2f5a-4f3f-9666-54ae9be7a6bb")
//                            .input(input)
//                            .sessionId(watsonAssistantSession.getSessionId())
//                            .build();
//
//                    MessageResponse response = watsonAssistant.message(options).execute().getResult();
//                    Log.i(TAG, "run: "+response);
//                    final Message outMessage = new Message();
//                    if (response != null &&
//                            response.getOutput() != null &&
//                            !response.getOutput().getGeneric().isEmpty() &&
//                            "text".equals(response.getOutput().getGeneric().get(0).getResponseType())) {
//                            results=response.getOutput().getGeneric().get(0).getText();
//                            System.out.println(results);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();

        return results;
    }



    //Google Search Async task

    private class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String> {

        protected void onPreExecute() {
            Log.d(TAG, "AsyncTask - onPreExecute");
            // show progressbar


        }


        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];
            Log.d(TAG, "AsyncTask - doInBackground, url=" + url);

            // Http connection
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.e(TAG, "Http connection ERROR " + e.toString());
            }


            try {
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();

            } catch (IOException e) {
                Log.e(TAG, "Http getting response code ERROR " + e.toString());
            }

            Log.d(TAG, "Http response code =" + responseCode + " message=" + responseMessage);

            try {

                if (responseCode == 200) {

                    // response OK

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    rd.close();

                    conn.disconnect();


                    result = sb.toString();


                    Log.d(TAG, "result=" + result);

                    return result;

                } else {

                    // response problem

                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                    Log.e(TAG, errorMsg);
                    result = errorMsg;
                    return result;

                }
            } catch (IOException e) {
                Log.e(TAG, "Http Response ERROR " + e.toString());
            }


            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "AsyncTask - onPostExecute, result=" + result);
            // hide progressbar
            // make TextView scrollable
            resultTextView.setMovementMethod(new ScrollingMovementMethod());
            // show result
            resultTextView.setText(result);
            try {
                result_g = new JSONObject(result);
                items=result_g.getJSONArray("items");
                for (int i=0; i < items.length(); i++)
                {
                    try {
                        JSONObject oneObject = items.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("htmlTitle");
                        String oneObjectsItem2 = oneObject.getString("htmlSnippet");
                        images=oneObject.getJSONObject("pagemap");
                        Log.d("OPESS",images.toString());
                        JSONArray imgs=new JSONArray();
                        imgs=images.getJSONArray("cse_image");
                        JSONObject srcs=imgs.getJSONObject(0);
                        String srcc=srcs.getString("src");
                        Log.d("OPESS",srcc);


                        aList.add(srcc);


                        arr.add(oneObjectsItem);
                        arr2.add(oneObjectsItem2);
                        if(i==0) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                resultTextView.setText(Html.fromHtml(oneObjectsItem+oneObjectsItem2, Html.FROM_HTML_MODE_LEGACY));
                                Spanned ter= Html.fromHtml(oneObjectsItem+oneObjectsItem2, Html.FROM_HTML_MODE_LEGACY);
                                int speechStatus = textToSpeech.speak(ter, TextToSpeech.QUEUE_FLUSH, null, null);

                                if (speechStatus == TextToSpeech.ERROR) {
                                    Log.e("TTS", "Error in converting Text to Speech!");
                                }
                            } else {
                                resultTextView.setText(Html.fromHtml(oneObjectsItem+oneObjectsItem2));
                                Spanned ter= Html.fromHtml(oneObjectsItem+oneObjectsItem2);
                                int speechStatus = textToSpeech.speak(ter, TextToSpeech.QUEUE_FLUSH, null, null);

                                if (speechStatus == TextToSpeech.ERROR) {
                                    Log.e("TTS", "Error in converting Text to Speech!");
                                }
                            }
                        }
                        else{
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                texts4.setText(Html.fromHtml(oneObjectsItem+oneObjectsItem2, Html.FROM_HTML_MODE_LEGACY));
                                Spanned ter= Html.fromHtml(oneObjectsItem+oneObjectsItem2, Html.FROM_HTML_MODE_LEGACY);
                                int speechStatus = textToSpeech.speak(ter, TextToSpeech.QUEUE_FLUSH, null, null);

                                if (speechStatus == TextToSpeech.ERROR) {
                                    Log.e("TTS", "Error in converting Text to Speech!");
                                }
                            } else {
                                texts4.setText(Html.fromHtml(oneObjectsItem+oneObjectsItem2));
                                Spanned ter= Html.fromHtml(oneObjectsItem+oneObjectsItem2);
                                int speechStatus = textToSpeech.speak(ter, TextToSpeech.QUEUE_FLUSH, null, null);

                                if (speechStatus == TextToSpeech.ERROR) {
                                    Log.e("TTS", "Error in converting Text to Speech!");
                                }
                            }

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgtext.setVisibility(View.VISIBLE);
                                webtext.setVisibility(View.VISIBLE);
                                multiSnapRecyclerView.setVisibility(View.VISIBLE);


                            }
                        });

                    } catch (JSONException e) {
                        Log.d("OPESS","Something went wrong with JSON"+e.getMessage());
                    }
                    if(i>5){
                        break;
                    }
                }
            }
            catch(JSONException e){
                Log.d("IDSS","JSONEXCEPTION");
            }

        }
    }
}

class HttpGetRequest extends AsyncTask<String, Void, String> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    @Override
    protected String doInBackground(String... params){
        String stringUrl = params[0];
        String result;
        String inputLine;
        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);
            Log.i("NETSS","Reached ASYNC");
            //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();
            //Set methods and timeouts
            Log.i("NETSS","Reached ASYNC 2");
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            //Connect to our url
            connection.connect();
            Log.i("NETSS","Reached ASYNC 3");
            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            Log.i("NETSS","Reached ASYNC 4");
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();

        }
        catch(IOException e){
            Log.i("NETSS","Error"+e);
            e.printStackTrace();
            result = null;
        }
        return result;
    }
    protected void onPostExecute(String result){

        super.onPostExecute(result);
    }
}

