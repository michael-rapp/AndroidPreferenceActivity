/*
 * Copyright 2014 - 2016 Michael Rapp
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
package de.mrapp.android.preference.activity.fragment;

import android.app.Fragment;
import android.support.annotation.NonNull;

/**
 * The interface, which must be implemented by all classes, which should be notified on events
 * concerning fragments.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface FragmentListener {

    /**
     * The method, which is invoked, when the fragment has been created.
     *
     * @param fragment
     *         The observed fragment as an instance of the class {@link Fragment}
     */
    void onFragmentCreated(@NonNull Fragment fragment);

}