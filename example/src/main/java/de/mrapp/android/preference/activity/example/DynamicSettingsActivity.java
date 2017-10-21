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

import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.mrapp.android.preference.activity.NavigationPreference;
import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.example.dialog.RemovePreferenceHeaderDialogBuilder;
import de.mrapp.android.preference.activity.example.dialog.RemovePreferenceHeaderDialogListener;
import de.mrapp.android.preference.activity.example.fragment.NewPreferenceHeaderFragment;

/**
 * An activity, which is used to demonstrate a {@link PreferenceActivity}, whose headers can be
 * created or removed dynamically at runtime.
 *
 * @author Michael Rapp
 */
public class DynamicSettingsActivity extends AbstractPreferenceActivity
        implements RemovePreferenceHeaderDialogListener {

    /**
     * Shows a dialog, which allows to remove a specific preference header.
     */
    private void showRemovePreferenceHeaderDialog() {
        new RemovePreferenceHeaderDialogBuilder(this, getAllNavigationPreferences(), this).show();
    }

    /**
     * Dynamically adds a new navigation preference to the activity.
     */
    private void addNavigationPreference() {
        NavigationPreference navigationPreference = new NavigationPreference(this);
        navigationPreference.setTitle(getPreferenceHeaderTitle());
        navigationPreference.setFragment(NewPreferenceHeaderFragment.class.getName());
        getNavigationFragment().getPreferenceScreen().addPreference(navigationPreference);
        invalidateOptionsMenu();
    }

    /**
     * Removes all navigation preferences from the activity.
     */
    private void clearNavigationPreference() {
        PreferenceScreen preferenceScreen = getNavigationFragment().getPreferenceScreen();
        preferenceScreen.removeAll();
    }

    /**
     * Returns an unique title, which can be used for a new preference header.
     *
     * @return The title as an instance of the class {@link CharSequence}
     */
    private CharSequence getPreferenceHeaderTitle() {
        CharSequence originalTitle = getText(R.string.new_preference_header_title);
        CharSequence title = originalTitle;
        int counter = 1;

        while (isTitleAlreadyUsed(title)) {
            title = originalTitle + " (" + counter + ")";
            counter++;
        }

        return title;
    }

    /**
     * Returns, whether a preference header, which has a specific title, has already been added to
     * the activity, or not.
     *
     * @param title
     *         The title, whose presence should be checked, as an instance of the class {@link
     *         CharSequence}. The tile may not be null
     * @return True, if a preference header, which has the given title, has already been added to
     * the activity, false otherwise
     */
    private boolean isTitleAlreadyUsed(@NonNull final CharSequence title) {
        for (NavigationPreference navigationPreference : getAllNavigationPreferences()) {
            if (title.equals(navigationPreference.getTitle())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final void onRemovePreferenceHeader(final int position) {
        PreferenceScreen preferenceScreen = getNavigationFragment().getPreferenceScreen();
        Preference preference = preferenceScreen.getPreference(position);
        preferenceScreen.removePreference(preference);
        invalidateOptionsMenu();
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem removePreferenceHeaderMenuItem = menu.findItem(R.id.remove_preference_header);
        removePreferenceHeaderMenuItem.setEnabled(getNavigationPreferenceCount() != 0);
        MenuItem clearPreferenceHeadersMenuItem = menu.findItem(R.id.clear_preference_headers);
        clearPreferenceHeadersMenuItem.setEnabled(getNavigationPreferenceCount() != 0);

        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_preference_header:
                addNavigationPreference();
                return true;
            case R.id.remove_preference_header:
                showRemovePreferenceHeaderDialog();
                return true;
            case R.id.clear_preference_headers:
                clearNavigationPreference();
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected final void onCreateNavigation(
            @NonNull final android.preference.PreferenceFragment fragment) {
        fragment.addPreferencesFromResource(R.xml.dynamic_navigation);
        addNavigationPreference();
    }

}