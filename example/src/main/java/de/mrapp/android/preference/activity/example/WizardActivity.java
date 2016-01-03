/*
 * AndroidPreferenceActivity Copyright 2014 - 2016 Michael Rapp
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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.PreferenceHeader;
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
    protected final void onCreatePreferenceHeaders() {
        addPreferenceHeadersFromResource(R.xml.wizard_preference_headers);
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addWizardListener(this);
    }

    @Override
    public final Bundle onNextStep(final int position,
                                   @NonNull final PreferenceHeader preferenceHeader,
                                   @NonNull final Fragment fragment, final Bundle bundle) {
        return new Bundle();
    }

    @Override
    public final Bundle onPreviousStep(final int position,
                                       @NonNull final PreferenceHeader preferenceHeader,
                                       @NonNull final Fragment fragment, final Bundle bundle) {
        return new Bundle();
    }

    @Override
    public final boolean onFinish(final int position,
                                  @NonNull final PreferenceHeader preferenceHeader,
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
    public final boolean onSkip(final int position,
                                @NonNull final PreferenceHeader preferenceHeader,
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

}