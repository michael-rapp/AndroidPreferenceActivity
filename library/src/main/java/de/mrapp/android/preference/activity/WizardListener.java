/*
 * Copyright 2014 - 2018 Michael Rapp
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
package de.mrapp.android.preference.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Defines the interface a class, which should be notified when the user navigates within a {@link
 * PreferenceActivity}, which is used as wizard, by using its next-, back- and finish-button. The
 * return values of the interface's methods allow to take influence on the navigation, e.g. if the
 * currently shown preferences should be validated.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface WizardListener {

    /**
     * The method, which is invoked, when the user wants to navigate to the next step of the
     * wizard.
     *
     * @param navigationPreference
     *         The currently selected navigation preference as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     * @param fragment
     *         The currently shown preference fragment as an instance of the class Fragment. The
     *         fragment may not be null
     * @param bundle
     *         A bundle, which contains the parameters, which have been passed to the currently
     *         shown preference fragment, as an instance of the class {@link Bundle} or null, if no
     *         parameters have been passed to the fragment
     * @return The bundle, which should be passed to the next fragment, as an instance of the class
     * {@link Bundle} or null, if navigating to the next step of the wizard should not be allowed
     */
    Bundle onNextStep(@NonNull NavigationPreference navigationPreference,
                      @NonNull Fragment fragment, @Nullable Bundle bundle);

    /**
     * The method, which is invoked, when the user wants to navigate to the previous step of the
     * wizard.
     *
     * @param navigationPreference
     *         The currently selected navigation preference as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     * @param fragment
     *         The currently shown preference fragment as an instance of the class Fragment. The
     *         fragment may not be null
     * @param bundle
     *         A bundle, which contains the parameters, which have been passed to the currently
     *         shown preference fragment, as an instance of the class {@link Bundle} or null, if no
     *         parameters have been passed to the fragment
     * @return The bundle, which should be passed to the next fragment, as an instance of the class
     * {@link Bundle} or null, if navigating to the previous step of the wizard should not be
     * allowed
     */
    Bundle onPreviousStep(@NonNull NavigationPreference navigationPreference,
                          @NonNull Fragment fragment, @Nullable Bundle bundle);

    /**
     * The method, which is invoked, when the user wants to finish the last step of the wizard.
     *
     * @param navigationPreference
     *         The currently selected navigation preference as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     * @param fragment
     *         The currently shown preference fragment as an instance of the class Fragment. The
     *         fragment may not be null
     * @param bundle
     *         A bundle, which contains the parameters, which have been passed to the currently
     *         shown preference fragment, as an instance of the class {@link Bundle} or null, if no
     *         parameters have been passed to the fragment
     * @return True, if finishing the wizard should be allowed, false otherwise
     */
    boolean onFinish(@NonNull NavigationPreference navigationPreference, @NonNull Fragment fragment,
                     @Nullable Bundle bundle);

    /**
     * The method, which is invoked, when the user wants to skip the wizard.
     *
     * @param navigationPreference
     *         The currently selected navigation preference as an instance of the class {@link
     *         NavigationPreference}. The navigation preference may not be null
     * @param fragment
     *         The currently shown preference fragment as an instance of the class Fragment. The
     *         fragment may not be null
     * @param bundle
     *         A bundle, which contains the parameters, which have been passed to the currently
     *         shown preference fragment, as an instance of the class {@link Bundle} or null, if no
     *         parameters have been passed to the fragment
     * @return True, if skipping the wizard should be allowed, false otherwise
     */
    boolean onSkip(@NonNull NavigationPreference navigationPreference, @NonNull Fragment fragment,
                   @Nullable Bundle bundle);

}