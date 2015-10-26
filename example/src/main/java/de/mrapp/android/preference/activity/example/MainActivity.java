/*
 * AndroidPreferenceActivity Copyright 2014 - 2015 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package de.mrapp.android.preference.activity.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.example.fragment.BehaviorPreferenceFragment;

/**
 * The example app's main activity.
 *
 * @author Michael Rapp
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Initializes the activity's toolbar.
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Initializes the button, which allows to show a default {@link PreferenceActivity}.
     */
    private void initializePreferenceButton() {
        Button preferenceButton = (Button) findViewById(R.id.preference_button);
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
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                        BehaviorPreferenceFragment.class.getName());

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
                    intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_TITLE, title);
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
        Button dynamicPreferenceButton = (Button) findViewById(R.id.dynamic_preference_button);
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
        Button wizardButton = (Button) findViewById(R.id.wizard_button);
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
        initializeToolbar();
        initializePreferenceButton();
        initializePreferenceInitialFragmentButton();
        initializeDynamicPreferenceButton();
        initializeWizardButton();
    }

}