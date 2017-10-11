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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.WizardListener;

/**
 * An activity, which is used to demonstrate the appearance of a {@link PreferenceActivity}, which
 * is used a wizard.
 *
 * @author Michael Rapp
 */
public class WizardActivity extends AbstractPreferenceActivity implements WizardListener {

    /**
     * Creates and returns a listener, which allows to finish the wizard.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createFinishListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                finish();
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to skip the wizard.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createAcceptSkipListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                finish();
            }

        };
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addWizardListener(this);
    }

    @Override
    public final Bundle onNextStep(@NonNull final NavigationPreference navigationPreference,
                                   @NonNull final Fragment fragment, final Bundle bundle) {
        return new Bundle();
    }

    @Override
    public final Bundle onPreviousStep(@NonNull final NavigationPreference navigationPreference,
                                       @NonNull final Fragment fragment, final Bundle bundle) {
        return new Bundle();
    }

    @Override
    public final boolean onFinish(@NonNull final NavigationPreference navigationPreference,
                                  @NonNull final Fragment fragment, final Bundle bundle) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.finish_wizard_dialog_title);
        dialogBuilder.setMessage(R.string.finish_wizard_dialog_message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(android.R.string.ok, createFinishListener());
        dialogBuilder.show();
        return true;
    }

    @Override
    public final boolean onSkip(@NonNull final NavigationPreference navigationPreference,
                                @NonNull final Fragment fragment, final Bundle bundle) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.skip_wizard_dialog_title);
        dialogBuilder.setMessage(R.string.skip_wizard_dialog_message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(android.R.string.ok, createAcceptSkipListener());
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
        dialogBuilder.show();
        return false;
    }

    @Override
    public final void onNavigationCreated(@NonNull final PreferenceFragment preferenceFragment) {
        preferenceFragment.addPreferencesFromResource(R.xml.wizard_navigation);
    }

}