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
package de.mrapp.android.preference.activity;

/**
 * An implementation of the abstract class {@link android.database.DataSetObserver}, which allows to
 * check, whether the method <code>onChanged():void</code> or <code>onInvalidated():void</code> has
 * been invoked by an observed class, or not.
 *
 * @author Michael Rapp
 */
public class DataSetObserver extends android.database.DataSetObserver {

    /**
     * True, if the method <code>onChanged():void</code> has been invoked at least once, false
     * otherwise.
     */
    private boolean onChanged;

    /**
     * True, if the method <code>onInvalidated():void</code> has been invoked at least once, false
     * otherwise.
     */
    private boolean onInvalidated;

    /**
     * Returns, whether the method <code>onChanged():void</code> has been invoked at least once, or
     * not.
     *
     * @return True, if the method <code>onChanged():void</code> has been invoked at least once,
     * false otherwise
     */
    public final boolean hasOnChangedBeenCalled() {
        return onChanged;
    }

    /**
     * Returns, whether the method <code>onInvalidated():void</code> has been invoked at least once,
     * or not.
     *
     * @return True, if the method <code>onInvalidated():void</code> has been invoked at least once,
     * false otherwise
     */
    public final boolean hasOnInvalidatedBeenCalled() {
        return onInvalidated;
    }

    /**
     * Resets the data set observer.
     */
    public final void reset() {
        onChanged = false;
        onInvalidated = false;
    }

    @Override
    public final void onChanged() {
        super.onChanged();
        onChanged = true;
    }

    @Override
    public final void onInvalidated() {
        super.onInvalidated();
        onInvalidated = true;
    }

}