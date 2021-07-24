package com.margsapp.messenger.Welcome;

import androidx.fragment.app.Fragment;

import com.margsapp.messenger.R;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

public class MyWelcomeActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.background)
                .page(new FragmentWelcomePage() {
                          @Override
                          protected Fragment fragment() {
                              return new Fragment1();
                          }
                      }
                )
                .page(new FragmentWelcomePage() {
                          @Override
                          protected Fragment fragment() {
                              return new Fragment2();
                          }
                      }
                )
                .page(new FragmentWelcomePage() {
                          @Override
                          protected Fragment fragment() {
                              return new Fragment3();
                          }
                      }
                ).page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new Fragment4();
                    }
                }).page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new Fragment5();
                    }
                }).page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new Fragment6();
                    }
                }).page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new Fragment7();
                    }
                })
                .swipeToDismiss(true)
                .build();
    }
}