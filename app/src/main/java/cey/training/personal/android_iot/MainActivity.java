package cey.training.personal.android_iot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Locale;

import cey.training.personal.android_iot.smarthome.DevicesHandler;
import cey.training.personal.android_iot.smarthome.SmartHome;
import cey.training.personal.android_iot.smarthome.SmartHomeState;
import yuku.ambilwarna.AmbilWarnaDialog;

//TODO change level of exceptions handling, that leads to BLUETOOTH_OFF state
//TODO check new device connections

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DevicesHandler {
    private static final String TAG = "SmartHomeApp";

    LedState ledState;
    SmartHome myHome;
    private boolean isDeviceConnected;

    BoxAdapter boxAdapter;
    ModeBoxAdapter boxAdapterMode;
    ArrayList<DeviceView> deviceViews = new ArrayList<DeviceView>();
    ArrayList<ModeView> modeViews = new ArrayList<ModeView>();
    RelativeLayout buttonPanel;
    Button switchButton;
    TextView messageView;
    private int MY_PERMISSION_REQUEST_CONSTANT = 3526;

    //--Getters and setters--
    public LedState getLedState() {
        return ledState;
    }

    public void setLedState(LedState ledState) {
        this.ledState = ledState;
        log("Current state is set to " + ledState.toString());
        String buttonText;
        switch (ledState) {
            case OFF:
                buttonText = "Turn on";
                break;
            case ON:
                buttonText = "Turn off";
                break;
            default:
                buttonText = "Stub";
                buttonPanel.setVisibility(View.GONE);
        }
        if (switchButton != null) {
            switchButton.setText(String.format(Locale.getDefault(), "%s", buttonText));
        }
    }

    //--UI functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        SugarContext.init(getApplicationContext());
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                MY_PERMISSION_REQUEST_CONSTANT);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupMyObjects();

        setLedState(LedState.OFF);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("App exit");
        myHome.exit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        log("In menu " + Integer.toString(id) + " selected");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //--DevicesHandler realisation
    @Override
    public void handleSmartHomeState(SmartHomeState state) {
        //TODO check thread
        log("handleSmartHomeState: " + state.toString());
        ProgressBar bar = (ProgressBar) findViewById(R.id.connection_bar);

        switch (state) {
            case NO_BLUETOOTH:
                bar.setVisibility(View.GONE);
                exitApp();
                break;
            case BLUETOOTH_OFF:
                setupBluetooth();
                break;
            case IN_PROGRESS:
                if (findViewById(R.id.switcher_layout)!=null){
                bar.setVisibility(View.VISIBLE);
                buttonPanel.setVisibility(View.GONE);}
                break;
            case FINISHED:
                bar.setVisibility(View.GONE);
                break;
            case CONNECTED:
                if (findViewById(R.id.switcher_layout)!=null){
                bar.setVisibility(View.GONE);
                buttonPanel.setVisibility(View.VISIBLE);}
                break;
        }

        //reboot in case BLUETOOTH_OFF state, so we don't need to out it
        if (messageView != null && !state.equals(SmartHomeState.BLUETOOTH_OFF)) {
            messageView.setText(String.format(Locale.getDefault(), "%s", state.toString()));
        }
    }

    @Override
    public void addDevice(String[] input) {
        //TODO replace ListView to RecyclerView
        log("addDevice:\n" +
                "name: " + input[0]);
        deviceViews.add(new DeviceView(input[0], input[1], input[2]));
        //deviceListAdapter.add(name);
        boxAdapter.notifyDataSetChanged();
        //deviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void chooseDevice(String[] input) {
        if (!isDeviceConnected) {
            log("chooseDevice:\n" +
                    "name: " + input[0]);

            isDeviceConnected = true;
            deviceViews.clear();
            if (input == null) {
                input = new String[3];
                input[0] = input[1] = input[2] = "UNKNOWN";
            }
            deviceViews.add(new DeviceView(input[0], input[1], input[2]));
            boxAdapter.notifyDataSetInvalidated();
        }
    }

    @Override
    public void log(String msg) {
        Log.w(TAG, msg);
    }

    @Override
    public void log(String msg, boolean isError) {
        if (isError)
            Log.e(TAG, msg);
        else
            Log.w(TAG, msg);
    }

    @Override//when bluetooth is ready
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("Main Activity onActivityResult:\n" +
                "requestCode: " + Integer.toString(requestCode) + "\n" +
                "resultCode: " + Integer.toString(resultCode));

        myHome.onActivityResult(requestCode, resultCode);
    }

    @Override
    public void handleAnswer(String answer) {
        if (isDeviceConnected && messageView != null) {
            messageView.setText(String.format(Locale.getDefault(), "%s", answer));
        }
    }
    //--My functions

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_CONSTANT) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String answer = "";
                for (int i = 0; i < permissions.length; i++) {
                    answer += "PERMISSION: " + permissions[i] + " is ";
                    if (grantResults[i] != 0) {
                        answer += "NOT ";
                    }
                    answer += "GRANTED\n";
                }
                log(answer);
            } else {
                exitApp();
            }
        }
    }

    void setupMyObjects() {
        log("Objects setup");
        isDeviceConnected = false;

        setupButtons();
        setupMessageView();
        setupListView();
        setupBluetooth();
    }

    private void setupButtons() {
        log("Buttons setup");
        buttonPanel = (RelativeLayout) findViewById(R.id.button_panel);

        switchButton = (Button) findViewById(R.id.switch_button);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log("Button clicked");
                switch (getLedState()) {
                    case OFF:
                        setLedState(LedState.ON);
                        break;
                    case ON:
                        setLedState(LedState.OFF);
                        break;
                }
                sendLedState();
            }
        });

        Button disconnectButton = (Button) findViewById(R.id.disconnect_button);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myHome != null) {
                    myHome.exit();
                }
            }
        });

        Button modeButton = (Button) findViewById(R.id.mode_button);
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                setContentView(R.layout.mode_select);
                setupModeListView();
            }
        });
        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, 0xff000000, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                log("selected color: "+color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });
        Button colorButton = (Button) findViewById(R.id.color_button);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                // initialColor is the initially-selected color to be shown in the rectangle on the left of the arrow.
// for example, 0xff000000 is black, 0xff0000ff is blue. Please be aware of the initial 0xff which is the alpha.

            dialog.show();
            }
        });
    }

    private void setupMessageView() {
        log("MessageView setup");
        messageView = (TextView) findViewById(R.id.message_text);
    }

    private void setupListView() {
        log("ListView setup");
        ListView deviceList = (ListView) findViewById(R.id.devices_list);
        boxAdapter = new BoxAdapter(this, deviceViews);
        deviceList.setAdapter(boxAdapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                log("onItemClick:\n" +
                        "Position: " + Integer.toString(position));
                if (!isDeviceConnected)
                    myHome.chooseDevice(position);
            }
        });
    }

    private void setupModeListView() {
        String[] names = {"Выключить", "Включить все диоды",
                "плавная смена цветов всей ленты", "крутящаяся радуга", "случайная смена цветов", "бегающий светодиод",
                "бегающий паровозик светодиодов", "вращаются красный и синий", "вращается половина"+
                "красных и половина синих", "случайный стробоскоп", "пульсация одним цветом",
                "пульсация со сменой цветов", "плавная смена яркости по вертикали (для кольца)",
                "безумие красных светодиодов", "безумие случайных цветов",
                "белый синий красный бегут по кругу (ПАТРИОТИЗМ!)", "пульсирует значок радиации",
                "красный светодиод бегает по кругу", "бело синий градиент (?)",
                "тоже хрень какая то", "красные вспышки спускаются вниз", "полумесяц",
                "эффект пламени", "радуга в вертикаьной плоскости (кольцо)", "пакман",
                "безумие случайных вспышек", "полицейская мигалка", "RGB пропеллер",
                "случайные вспышки красного в вертикаьной плоскости",
                "зелёненькие бегают по кругу случайно", "крутая плавная вращающаяся радуга",
                "чёт сломалось", "чёт сломалось", "плавное заполнение цветом", "бегающие светодиоды"
                , "линейный огонь", "беготня секторов круга (не работает)",
                "очень плавная вращающаяся радуга", "случайные разноцветные включения (1 - танцуют "
                + "все, 0 - случайный 1 диод)", "бегущие огни", "случайные вспышки белого цвета",
                "случайные вспышки белого цвета на белом фоне",
                "бегущие каждые 3 (ЧИСЛО СВЕТОДИОДОВ ДОЛЖНО БЫТЬ НЕЧЁТНОЕ)",
                "бегущие каждые 3 радуга (ЧИСЛО СВЕТОДИОДОВ ДОЛЖНО БЫТЬ КРАТНО 3)",
                "стробоскоп", "прыгающие мячики", "прыгающие мячики цветные", "длинное демо",
                "короткое демо"};
        log("Mode ListView setup");
        ListView modeList = (ListView) findViewById(R.id.mode_list);
        boxAdapterMode = new ModeBoxAdapter(this, modeViews);
                /*new: creating mode select list*/
        modeList.setAdapter(boxAdapterMode);
        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                log("onItemClick:\n" +
                        "Position: " + Integer.toString(position));
//                        if (!isDeviceConnected)
//                            myHome.chooseDevice(position);
                myHome.setMessage("#0:"+Integer.toString(position)+"@");
            }
        });
        for (int i = 0; i < names.length; i++)
            modeViews.add(new ModeView(i, Integer.toString(i)+") "+names[i]));
        boxAdapterMode.notifyDataSetChanged();
    }

    private String prepareSendData(String ... inData){
        log("getted "+inData.length+ " params");
        String data="";
        for (int i=0; i< inData.length; i++){
            log ("["+i+"] "+inData[i]);
        }

        return data;
    }

    private void setupBluetooth() {
        log("Bluetooth setup");
        isDeviceConnected = false;
        boxAdapter.clear();
        boxAdapter.notifyDataSetChanged();
        myHome = new SmartHome(this, this);
    }

    void sendLedState() {
        if (myHome != null) {
            String toSend = Integer.toString(getLedState().ordinal());
            log("Send " + toSend + " to LED");
            myHome.setMessage(toSend);
        }
    }

    void exitApp() {
        log("Exit app");
        AsyncTask exit = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(3000);
                    finishAffinity();
                } catch (InterruptedException ex) {
                    log(ex.getLocalizedMessage());
                }
                return null;
            }
        };
        exit.execute();//TODO Check warning
    }
}