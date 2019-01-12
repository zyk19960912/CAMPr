package com.example.a1.campr;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LogInTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void logInTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.email)));
        appCompatEditText.perform(scrollTo(), replaceText("test@ucsd.edu"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.password)));
        appCompatEditText2.perform(scrollTo(), replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.login_button), withText("Log In")));
        appCompatButton.perform(click())                // click() is a ViewAction
                .check(matches(isDisplayed()));
        sleep(1000);

        onView(allOf(withText("Get Started"))).check(matches(isDisplayed()));

        ViewInteraction CAMPr = onView(allOf(withId(R.id.textView3))).check(matches(withText("CAMPr")));
        ViewInteraction Adopter = onView(allOf(withId(R.id.textView4))).check(matches(withText("Adopter")));
        ViewInteraction Lister = onView(allOf(withId(R.id.textView5))).check(matches(withText("Lister")));

        ViewInteraction getStartedButton = onView(
                allOf(withId(R.id.start), withText("Get Started")));
        getStartedButton.check(matches((isDisplayed())));
        getStartedButton.perform(click());

    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
