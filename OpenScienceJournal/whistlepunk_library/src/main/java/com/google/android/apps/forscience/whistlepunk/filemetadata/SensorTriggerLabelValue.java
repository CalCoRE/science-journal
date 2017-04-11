/*
 *  Copyright 2017 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.google.android.apps.forscience.whistlepunk.filemetadata;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.apps.forscience.whistlepunk.AppSingleton;
import com.google.android.apps.forscience.whistlepunk.R;
import com.google.android.apps.forscience.whistlepunk.SensorAppearance;
import com.google.android.apps.forscience.whistlepunk.metadata.GoosciLabelValue;
import com.google.android.apps.forscience.whistlepunk.metadata.SensorTrigger;
import com.google.common.annotations.VisibleForTesting;

/**
 * A label auto-generated by a Sensor Trigger. This has some editable custom text, as well as
 * non-editable auto-generated text that is specific to the trigger which was fired.
 */
public class SensorTriggerLabelValue extends LabelValue {
    private static final int NUM_FIELDS = 3;
    private static final int INDEX_CUSTOM_STRING = 0;
    private static final int INDEX_AUTOGEN_STRING = 1;
    private static final int INDEX_SENSOR_ID = 2;
    private static final String KEY_CUSTOM_STRING = "custom";
    private static final String KEY_AUTOGEN_STRING = "auto";
    private static final String KEY_SENSOR_ID = "sensorId";

    public SensorTriggerLabelValue(GoosciLabelValue.LabelValue value) {
        super(value);
    }

    public SensorTriggerLabelValue() {
        super();
        mValue.type = GoosciLabelValue.LabelValue.SENSOR_TRIGGER;
    }

    @Override
    public boolean canEditTimestamp() {
        // Autogenerated label has uneditable timestamp.
        return false;
    }

    public void setCustomText(String text) {
        GoosciLabelValue.LabelValue value = getValue();
        if (value == null || value.data.length == 0) {
            createDataFields(value);
        }
        value.data[INDEX_CUSTOM_STRING].key = KEY_CUSTOM_STRING;
        value.data[INDEX_CUSTOM_STRING].value = text;
        setValue(value);
    }

    public String getCustomText() {
        return getCustomText(getValue());
    }

    public static String getCustomText(GoosciLabelValue.LabelValue value) {
        if (value.data.length > INDEX_CUSTOM_STRING &&
                TextUtils.equals(value.data[INDEX_CUSTOM_STRING].key, KEY_CUSTOM_STRING)) {
            return value.data[INDEX_CUSTOM_STRING].value;
        }
        return "";
    }

    public String getAutogenText() {
        return getAutogenText(getValue());
    }

    public static String getAutogenText(GoosciLabelValue.LabelValue value) {
        if (value.data.length > INDEX_AUTOGEN_STRING &&
                TextUtils.equals(value.data[INDEX_AUTOGEN_STRING].key, KEY_AUTOGEN_STRING)) {
            return value.data[INDEX_AUTOGEN_STRING].value;
        }
        return "";
    }

    public String getSensorId() {
        return getSensorId(getValue());
    }

    public static String getSensorId(GoosciLabelValue.LabelValue value) {
        if (value.data.length > INDEX_SENSOR_ID &&
                TextUtils.equals(value.data[INDEX_SENSOR_ID].key, KEY_SENSOR_ID)) {
            return value.data[INDEX_SENSOR_ID].value;
        }
        return "";
    }

    @VisibleForTesting
    static void populateLabelValue(GoosciLabelValue.LabelValue value, SensorTrigger trigger,
            String noteText) {
        createDataFields(value);
        value.data[INDEX_CUSTOM_STRING].key = KEY_CUSTOM_STRING;
        value.data[INDEX_CUSTOM_STRING].value = trigger.getNoteText();

        value.data[INDEX_AUTOGEN_STRING].key = KEY_AUTOGEN_STRING;
        value.data[INDEX_AUTOGEN_STRING].value = noteText;

        value.data[INDEX_SENSOR_ID].key = KEY_SENSOR_ID;
        value.data[INDEX_SENSOR_ID].value = trigger.getSensorId();
    }

    private static GoosciLabelValue.LabelValue createLabelValue(SensorTrigger trigger,
            Context context) {
        GoosciLabelValue.LabelValue value = new GoosciLabelValue.LabelValue();
        String noteText = value.data[INDEX_AUTOGEN_STRING].value = generateAutoNoteText(trigger, context);
        populateLabelValue(value, trigger, noteText);
        return value;
    }

    private static void createDataFields(GoosciLabelValue.LabelValue value) {
        value.type = GoosciLabelValue.LabelValue.SENSOR_TRIGGER;
        value.data = new GoosciLabelValue.LabelValue.DataEntry[NUM_FIELDS];
        value.data[INDEX_CUSTOM_STRING] = new GoosciLabelValue.LabelValue.DataEntry();
        value.data[INDEX_AUTOGEN_STRING] = new GoosciLabelValue.LabelValue.DataEntry();
        value.data[INDEX_SENSOR_ID] = new GoosciLabelValue.LabelValue.DataEntry();
    }

    private static String generateAutoNoteText(SensorTrigger trigger,
            Context context) {
        SensorAppearance appearance = AppSingleton.getInstance(context)
                .getSensorAppearanceProvider().getAppearance(trigger.getSensorId());
        String units = appearance.getUnits(context);
        String sensorName = appearance.getName(context);
        String triggerWhenText = context.getResources().getStringArray(
                R.array.trigger_when_list_note_text)[trigger.getTriggerWhen()];
        return context.getResources().getString(R.string.trigger_label_auto_text, sensorName,
                triggerWhenText, trigger.getValueToTrigger(), units);
    }

}