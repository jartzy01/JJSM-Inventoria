package com.example.jjsminventoria;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityUITest {
    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp(){
        Intents.init();
    }

    @After
    public void tearDown(){
        Intents.release();
    }

    @Test
    public void testLoginButtonOpenNextActivity() {
        onView(withId(R.id.etEmail)).perform(typeText("100"),closeSoftKeyboard());

        onView(withId(R.id.etPassword)).perform(typeText("asdfg"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());

        intended(hasComponent(MainMenuActivity.class.getName()));
    }

    @Test
    public void testEmptyLoginFieldsError() {
        onView(withId(R.id.btnLogin)).perform(click());

//        onView(withText("Fields cannot be empty")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginButtonInvalidUser() {
        onView(withId(R.id.etEmail)).perform(typeText("123"),closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
    }
}
