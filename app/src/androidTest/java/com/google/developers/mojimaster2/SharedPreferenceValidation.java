package com.google.developers.mojimaster2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayWithSize;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
* Created by Prajwal Maruti Waingankar on 14-04-2022, 00:54
* Copyright (c) 2021 . All rights reserved.
* Email: prajwalwaingankar@gmail.com
* Github: prajwalmw
*/

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class SharedPreferenceValidation {
    private int answerItems = 2;

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testSharedPref() {
        // click on settings to go to this screen
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                2),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(androidx.appcompat.R.id.title), withText("Settings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        // Get the preferences recyclerview which is by default as our is a ListPreference which
        // internally uses a recyclerview so we are using this recyclerview here
        onView(withId(androidx.preference.R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem
                        (hasDescendant(withText(R.string.pref_answers_title)),
                        click()));

        // here we are show a dialog so from that we select one of the option.
        onView(withText("Show two answers"))
                .perform(click());

        // Go back to Mainscreen to verify the items.
        Espresso.pressBackUnconditionally();

        // Validate the answerview custom view to check the total no of items of radiobutton shown on screen.
        onView(withId(R.id.ans_view)).check(matches(allOf(
                isDisplayed(),
                hasChildren(is(answerItems)))));
    }

    public static Matcher<View> hasChildren(final Matcher<Integer> numChildrenMatcher) {
        return new TypeSafeMatcher<View>() {

            /**
             * matching with viewgroup.getChildCount()
             */
            @Override
            public boolean matchesSafely(View view) {
                return view instanceof ViewGroup && numChildrenMatcher.matches(((ViewGroup)view).getChildCount());
            }

            /**
             * gets the description
             */
            @Override
            public void describeTo(Description description) {
                description.appendText(" a view with # children is ");
                numChildrenMatcher.describeTo(description);
            }
        };
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}
