/*
 * Copyright 2014 - 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.preference.activity.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import de.mrapp.android.preference.activity.PreferenceActivity;

/**
 * The example app's main activity.
 *
 * @author Michael Rapp
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Initializes the button, which allows to show a default {@link PreferenceActivity}.
     */
    private void initializePreferenceButton() {
        Button preferenceButton = findViewById(R.id.preference_button);
        preferenceButton.setOnClickListener(createPreferenceButtonListener());
    }

    /**
     * Creates and returns a listener, which allows to show a default {@link PreferenceActivity}.
     *
     * @return The listener, which has been created as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createPreferenceButtonListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

        };
    }

    /**
     * Initializes the button, which allows to show a specific header of a {@link
     * PreferenceActivity}.
     */
    private void initializePreferenceInitialFragmentButton() {
        Button preferenceInitialFragmentButton =
                (Button) findViewById(R.id.preference_initial_fragment_button);
        preferenceInitialFragmentButton
                .setOnClickListener(createPreferenceInitialFragmentButtonListener());
    }

    /**
     * Creates a and returns a listener, which allows to show a specific header of a {@link
     * PreferenceActivity}.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createPreferenceInitialFragmentButtonListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                // TODO intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, BehaviorPreferenceFragment.class.getName());

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

                boolean useAlternativeTitleDefaultValue =
                        Boolean.valueOf(getString(R.string.use_alternative_title_preference_key));
                String useAlternativeTitleKey =
                        getString(R.string.use_alternative_title_preference_key);

                if (sharedPreferences
                        .getBoolean(useAlternativeTitleKey, useAlternativeTitleDefaultValue)) {
                    String titleDefaultValue =
                            getString(R.string.alternative_title_preference_default_value);
                    String titleKey = getString(R.string.alternative_title_preference_key);
                    String title = sharedPreferences.getString(titleKey, titleDefaultValue);
                    // TODO intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_TITLE, title);
                }

                startActivity(intent);
            }

        };
    }

    /**
     * Initializes the button, which allows to show a {@link PreferenceActivity} , whose headers can
     * be added or removed dynamically at runtime.
     */
    private void initializeDynamicPreferenceButton() {
        Button dynamicPreferenceButton = findViewById(R.id.dynamic_preference_button);
        dynamicPreferenceButton.setOnClickListener(createDynamicPreferenceButtonListener());
    }

    /**
     * Creates and returns a listener, which allows to show a {@link PreferenceActivity}, whose
     * headers can be added or removed dynamically at runtime.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createDynamicPreferenceButtonListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(MainActivity.this, DynamicSettingsActivity.class);
                startActivity(intent);
            }

        };
    }

    /**
     * Initializes the button, which allows to show a {@link PreferenceActivity} , which is used as
     * a wizard.
     */
    private void initializeWizardButton() {
        Button wizardButton = findViewById(R.id.wizard_button);
        wizardButton.setOnClickListener(createWizardButtonListener());
    }

    /**
     * Creates and returns a listener, which allows to show a {@link PreferenceActivity}, which is
     * used as a wizard.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createWizardButtonListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(MainActivity.this, WizardActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_BUTTON_BAR, true);
                intent.putExtra(PreferenceActivity.EXTRA_BACK_BUTTON_TEXT, R.string.back);
                intent.putExtra(PreferenceActivity.EXTRA_NEXT_BUTTON_TEXT, R.string.next);
                intent.putExtra(PreferenceActivity.EXTRA_FINISH_BUTTON_TEXT, R.string.finish);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_PROGRESS, true);
                intent.putExtra(PreferenceActivity.EXTRA_PROGRESS_FORMAT, R.string.progress);
                startActivity(intent);
            }

        };
    }

    @Override
    public final void setTheme(final int resid) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.theme_preference_key);
        String defaultValue = getString(R.string.theme_preference_default_value);
        int theme = Integer.valueOf(sharedPreferences.getString(key, defaultValue));

        if (theme != 0) {
            super.setTheme(R.style.DarkTheme);
        } else {
            super.setTheme(R.style.LightTheme);
        }
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializePreferenceButton();
        initializePreferenceInitialFragmentButton();
        initializeDynamicPreferenceButton();
        initializeWizardButton();
    }

}