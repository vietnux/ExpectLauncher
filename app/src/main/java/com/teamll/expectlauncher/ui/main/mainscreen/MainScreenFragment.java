package com.teamll.expectlauncher.ui.main.mainscreen;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetHostView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.model.Rectangle;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.ui.main.MainActivity;
import com.teamll.expectlauncher.ui.main.bottomsheet.RoundedBottomSheetDialogFragment;
import com.teamll.expectlauncher.ui.widgets.MotionRoundedBitmapFrameLayout;
import com.teamll.expectlauncher.util.Tool;

import java.util.ArrayList;

public class MainScreenFragment extends AppWidgetHostFragment implements View.OnTouchListener, LayoutSwitcher.EventSender,AppLoaderActivity.AppDetailReceiver, RoundedBottomSheetDialogFragment.BottomSheetListener {
    private static final String TAG="MainScreenFragment";

    private long savedTime;
    private float xDown;
    private float yDown;
    private View adaptiveDock;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: ");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                adaptiveDock = v;
                Log.d(TAG, "onTouch: action down");
                savedTime = System.currentTimeMillis();
                xDown = event.getRawX();
                yDown = event.getRawY();
        }
        return false;
    }
    public static MainScreenFragment newInstance() {

        MainScreenFragment fragment = new MainScreenFragment();
        return fragment;
    }

    private View mRootView;
    private TextView appDrawerButton;
    public MotionRoundedBitmapFrameLayout dock;
    private MainActivity activity;
    float statusBarHeight  = 0;
    float navigationHeight = 0;
    float oneDp = 0;
    Rectangle rect;
    public FrameLayout.LayoutParams dockParams;
    ImageView dockApp[] = new ImageView[4];
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        activity = (MainActivity) getActivity();
        statusBarHeight= Tool.getStatusHeight(activity.getResources());
        navigationHeight = Tool.getNavigationHeight(activity);
        rect = new Rectangle();
        int[] size = Tool.getScreenSize(activity);
        rect.setSize(size[0],size[1]);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_screen_fragment,container,false);
    }
    ScrollView scrollView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view,savedInstanceState);
        mRootView = view;
    dock = view.findViewById(R.id.dock);
    scrollView = view.findViewById(R.id.scrollview);
//
//    scrollView.setOnTouchListener(new View.OnTouchListener() {
//        long saved;
//        float savedPosX;
//        float savedPosY;
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    saved = System.currentTimeMillis();
//                    savedPosY = event.getRawY();
//                    savedPosX = event.getRawX();
//
//                    break;
//                case MotionEvent.ACTION_UP :
//                    if(System.currentTimeMillis() - saved>=300&&
//                            Math.sqrt(
//                                    (savedPosX-event.getRawX())*(savedPosX - event.getRawX())+
//                                    (savedPosY-event.getRawY())*(savedPosY-event.getRawY()))<=100)
//                    {
//                        RoundedBottomSheetDialogFragment fragment =  RoundedBottomSheetDialogFragment.newInstance(LayoutSwitcher.MODE.IN_MAIN_SCREEN);
//                         fragment.setListener(MainScreenFragment.this);
//                        fragment.show(getActivity().getSupportFragmentManager(),
//                                "song_popup_menu");
//                    }
//                    break;
//            }
//            return v.onTouchEvent(event);
//        }
//    });
    widgetContainer.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            RoundedBottomSheetDialogFragment fragment =  RoundedBottomSheetDialogFragment.newInstance(LayoutSwitcher.MODE.IN_MAIN_SCREEN);
            fragment.setListener(MainScreenFragment.this);
            fragment.show(getActivity().getSupportFragmentManager(),
                    "song_popup_menu");

            return true;
        }
    });
    dockApp[0] =dock.findViewById(R.id.dockApp1);
    dockApp[1] =dock.findViewById(R.id.dockApp2);
    dockApp[2] =dock.findViewById(R.id.dockApp3);
    dockApp[3] =dock.findViewById(R.id.dockApp4);
        for (View v :
                dockApp) {
            v.setOnTouchListener(this);
        }
    dock.setBackGroundColor(Color.WHITE);
    dock.setAlphaBackground(0.35f);
    dock.setRoundNumber(1.75f,true);
    dockParams = (FrameLayout.LayoutParams) dock.getLayoutParams();
    dockParams.topMargin = (int) (rect.Height - navigationHeight - dockParams.bottomMargin - dockParams.height);
    dockParams.bottomMargin = 0;
    dock.requestLayout();

    toggle = view.findViewById(R.id.toggleButton);
    toggleParams = (FrameLayout.LayoutParams) toggle.getLayoutParams();
    toggleParams.topMargin = dockParams.topMargin - toggleParams.height;
    toggle.requestLayout();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) scrollView.getLayoutParams();
        params.topMargin = (int) (statusBarHeight + params.topMargin);
        params.height =  toggleParams.topMargin - params.topMargin - params.bottomMargin;
        params.bottomMargin = 0;
        widgetContainer.setMinimumHeight(params.height);
        scrollView.requestLayout();
    Tool.getInstance().AddWallpaperChangedNotifier(dock);
        AppLoaderActivity ac = (AppLoaderActivity)getActivity();
        if(ac!=null)
            ac.addAppDetailReceiver(this);
        else throw new NullPointerException("Fragment was created from an activity class that doesn't inherit from AppLoaderActivity class");

    }
    private void dockClick(View v) {
        for (int i=0;i<appInDock.size();i++) {
            if (dockApp[i].getId() == v.getId()) {
                openApp(v, appInDock.get(i));
            }
        }
    }
   public FrameLayout.LayoutParams toggleParams ;
    public ImageView toggle;
    public void onUp(MotionEvent event) {
        if(System.currentTimeMillis() - savedTime <=300&& Math.sqrt(
                (xDown-event.getRawX())*(xDown - event.getRawX())+
                        (yDown-event.getRawY())*(yDown-event.getRawY()))<=100) {
           if(adaptiveDock!=null) {
               dockClick(adaptiveDock);
               adaptiveDock = null;
           }
        }
    }

    @Override
    public View getRoot() {
        return null;
    }

    private ArrayList<App> appInDock = new ArrayList<>();
    private String[] packageDock = {
            "com.google.android.dialer",
            "com.android.dialer",
            "com.google.android.apps.messaging",
            "com.android.messaging",
            "com.android.browser",
            "com.android.contacts",
            "com.android.music",
            "com.android.settings",
            "com.google.android.music",
            "com.google.android.apps.photos",
            "com.android.camera2",
            "com.android.chrome",
            "com.google.android.apps.docs",
            "com.android.documentsui",
            "com.google.android.videos",
            "com.google.android.apps.maps",
            "com.google.android.apps.photos",
            "org.chromium.webview_shell",
            "com.google.android.youtube",
            "com.google.android.googlequicksearchbox"};
    private boolean isPackageDock(App app) {
        for (String p :
                packageDock) {
            if(p.equals(app.getApplicationPackageName())) return true;
        }
        return false;
    }
    private ArrayList<App> apps = new ArrayList<>();
    @Override
    public void onLoadComplete(ArrayList<App> data) {
       apps.clear();
        apps.addAll(data);

        appInDock.clear();
        for (App a :
                apps) {
           if(isPackageDock(a)&&appInDock.size()<4) appInDock.add(a);
        }

        for(int i=0;i<appInDock.size();i++) {
            dockApp[i].setImageDrawable(appInDock.get(i).getIcon());
        }
    }
    void openApp(View v, AppDetail appDetail) {
        ((AppLoaderActivity)getActivity()).openApp(v,appDetail);
    }

    @Override
    public void onLoadReset() {
    }

    @Override
    public void onClickButtonInsideBottomSheet(View view) {
        switch (view.getId()) {
            case R.id.app_size:selectWidget(); break;
            case R.id.position:
                //TODO: call replace wallpaper function;
                Intent wallpagerIntent = new Intent(Intent.ACTION_SET_WALLPAPER);
                startActivity(Intent.createChooser(wallpagerIntent, "Select Wallpaper"));
                break;
            case R.id.app_icon_editor:
                break;
        }
    }

    public void setAlpha(float value) {
        Log.d(TAG, "setAlpha: "+value);
        widgetContainer.setAlpha(value);
        dock.setAlpha(value);
    }
}
