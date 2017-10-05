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

import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import de.mrapp.android.preference.activity.PreferenceActivity;

/**
 * An activity, which is used to demonstrate the default appearance of a {@link
 * PreferenceActivity}.
 *
 * @author Michael Rapp
 */
public class SettingsActivity extends AbstractPreferenceActivity {

    @Override
    public final void onNavigationCreated(@NonNull final PreferenceFragment fragment) {
        fragment.addPreferencesFromResource(R.xml.navigation);
    }

}