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
package de.mrapp.android.preference.activity.example.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.PreferenceFragment;
import de.mrapp.android.preference.activity.RestoreDefaultsListener;
import de.mrapp.android.preference.activity.example.R;

/**
 * A listener, which allows to show a dialog, which asks the user for confirmation, whether the
 * default values of a fragment's preferences should be restored, or not.
 *
 * @author Michael Rapp
 */
public class RestoreDefaultsDialogListener implements RestoreDefaultsListener {

    /**
     * The context the listener belongs to.
     */
    private final Context context;

    /**
     * Creates and returns a listener, which allows to restore the default values of a fragment's
     * preferences.
     *
     * @param fragment
     *         The fragment, whose preferences' default values should be restored, as an instance of
     *         the class {@link PreferenceFragment}
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createAcceptListener(final PreferenceFragment fragment) {
        return new OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                fragment.restoreDefaults();
            }

        };
    }

    /**
     * Creates a new listener, which allows to show a dialog, which asks the user for confirmation,
     * whether the default values of a fragment's preferences should be restored, or not.
     *
     * @param context
     *         The context, the listener should belong to, as an instance of the class {@link
     *         Context}
     */
    public RestoreDefaultsDialogListener(final Context context) {
        this.context = context;
    }

    @Override
    public final boolean onRestoreDefaultValuesRequested(
            @NonNull final PreferenceFragment fragment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(R.string.restore_defaults_dialog_title);
        dialogBuilder.setMessage(R.string.restore_defaults_dialog_message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(android.R.string.ok, createAcceptListener(fragment));
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
        dialogBuilder.show();
        return false;
    }

    @Override
    public final boolean onRestoreDefaultValueRequested(@NonNull final PreferenceFragment fragment,
                                                        @NonNull final Preference preference,
                                                        final Object currentValue) {
        return true;
    }

    @Override
    public final void onRestoredDefaultValue(@NonNull final PreferenceFragment fragment,
                                             @NonNull final Preference preference,
                                             final Object oldValue, final Object newValue) {

    }

}