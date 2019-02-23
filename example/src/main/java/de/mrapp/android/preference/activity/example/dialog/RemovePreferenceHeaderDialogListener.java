/*
 * Copyright 2014 - 2019 Michael Rapp
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
package de.mrapp.android.preference.activity.example.dialog;

import de.mrapp.android.preference.activity.PreferenceActivity;

/**
 * Defines the interface, a class, which should be notified, when the user closes a dialog, which
 * allows to remove a specific preference header from a {@link PreferenceActivity}, confirmatively.
 *
 * @author Michael Rapp
 */
public interface RemovePreferenceHeaderDialogListener {

    /**
     * The method, which is invoked when the user closes the dialog confirmatively in order to
     * remove the preference header, which belongs to a specific position.
     *
     * @param position
     *         The position of the preference header, which should be removed, as an {@link Integer}
     *         value
     */
    void onRemovePreferenceHeader(int position);

}