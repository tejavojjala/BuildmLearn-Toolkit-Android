package org.buildmlearn.toolkit.espresso;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.WindowManager;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.constant.Constants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Created by VOJJALA TEJA on 09-02-2017.
 */
@Ignore
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SpellingsTest {
    @Rule
    public final ActivityTestRule<TemplateEditor> mActivityRule =
            new ActivityTestRule<TemplateEditor>(TemplateEditor.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, TemplateEditor.class);
                    result.putExtra(Constants.TEMPLATE_ID, 1);
                    return result;
                }
            };

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep() {
        sleep(1000);
    }

    private static void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("Allow"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @UiThreadTest
    @Before
    public void setUp() throws Throwable {
        final Activity activity = mActivityRule.getActivity();
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                KeyguardManager mKG = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(Context.KEYGUARD_SERVICE);
                mLock.disableKeyguard();

                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
        });
    }

    private void toolbarTitle() {
        String title = "Learn Spelling Template";
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(title)));
    }

    private void addSpellings() {
        String word = "Test Spellings";
        String description = "Spelling";

        onView(withId(R.id.button_add_item)).perform(click());
        onView(withId(R.id.info_word)).perform(typeText(word));
        closeSoftKeyboard();
        sleep();

        onView(withId(R.id.info_meaning)).perform(typeText(description));
        closeSoftKeyboard();
        sleep();
        onView(withText(R.string.quiz_add)).perform(click());

    }

    private void editSpellings() {
        String word = "Edit Spellings";
        String description = "Edited Spelling";

        onView(withId(R.id.template_editor_recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));
        sleep();
        onView(withId(R.id.action_edit)).perform(click());
        sleep();
        onView(withId(R.id.info_word)).perform(replaceText(word));
        closeSoftKeyboard();
        sleep();

        onView(withId(R.id.info_meaning)).perform(replaceText(description));
        closeSoftKeyboard();
        sleep();
        onView(withText(R.string.quiz_ok)).perform(click());

    }

    private void addTemplate() {

        onView(withId(R.id.author_name)).perform(replaceText("tejavojjala"));
        onView(withId(R.id.template_title)).perform(replaceText("Testing Spellings Template"));
    }

    private void checkSimulator() {
        String word = "Edit Info";
        String description = "Edit Description for testing info";

        onView(withId(R.id.action_simulate)).perform(click());
        sleep();

        onView(withText("Testing Spellings Template")).check(matches(isDisplayed()));
        sleep();

        onView(withText("tejavojjala")).check(matches(isDisplayed()));
        onView(withId(R.id.enter)).perform(click());
        sleep();

        onView(withId(R.id.speak_ico)).perform(click());
        sleep(2000);

        onView(withId(R.id.spell_it)).perform(scrollTo()).perform(click());
        sleep();

        onView(withId(R.id.et_spelling)).perform(typeText("Edit Spellings"));
        onView(withId(R.id.btn_submit)).perform(click());
        sleep();

        onView(withId(R.id.intro_response)).check(matches(withText("Great! You got it right.")));
        onView(withId(R.id.next)).perform(click());
        sleep();

        onView(withId(R.id.exit)).perform(click());
    }

    @Test
    public void testAllSerially() {
        allowPermissionsIfNeeded();
        sleep();
        toolbarTitle();
        addTemplate();
        addSpellings();
        editSpellings();
        checkSimulator();
        //    saveAPK();
    }

}