package mx.solucionesonline.num;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME = 3000; //This is 3 seconds
    public Singleton singleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        singleton = Singleton.getInstance();
        splash();
    }

    public void splash(){
        try {
            //Code to start timer and take action after the timer ends
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do any action here. Now we are moving to next page
                    //if(singleton.islogued) {
                    Intent mySuperIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mySuperIntent);
                /*}else{
                    Intent mySuperIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(mySuperIntent);
                }*/
                    /* This 'finish()' is for exiting the app when back button pressed
                     *  from Home page which is ActivityHome
                     */
                    finish();
                }
            }, SPLASH_TIME);
        }catch (Exception e){
            splash();
        }
    }
}

