package com.example.avigail.lastproject;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import java.util.regex.Pattern;
import android.widget.EditText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

/*public class AppUnitTest extends ActivityInstrumentationTestCase2<LoginActivity> {


    LoginActivity mActivity;
    private EditText username;
    private EditText password;

    @SuppressWarnings("deprecation")
    public AppUnitTest()
    {
        super("com.main.account.Login",LoginActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();

        username = (EditText) mActivity.findViewById(R.id.usernameET);
        password = (EditText) mActivity.findViewById(R.id.passwordET);
    }

    public void testPreconditions() {
        assertNotNull(username);
        assertNotNull(password);
    }


    public void testText() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                username.setText("admin");
                password.setText("admin");
            }
        });
        assertEquals("",username.getText());
        assertEquals("", password.getText());

    }

}*/
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import android.content.SharedPreferences;

@RunWith(MockitoJUnitRunner.class)
public class AppUnitTest {

    private static final String FAKE_STRING = "admin";
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    @Mock
    Context mMockContext;

    @Test
    public void readStringFromContext_LocalizedString() {
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.app_name))
                .thenReturn(FAKE_STRING);
        LoginActivity myObjectUnderTest = new LoginActivity();

        // ...when the string is returned from the object under test...
        String result = myObjectUnderTest.getUserName();

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));
    }
    public void emailValidation() {
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.app_name))
                .thenReturn(FAKE_STRING);
        LoginActivity myObjectUnderTest = new LoginActivity();

        // ...when the string is returned from the object under test...
        String result = myObjectUnderTest.getUserName();

        // ...then the result should be the expected one.
        //pattern =
        //assertThat(result, Pattern.compile(USERNAME_PATTERN));
    }

}