package de.devmil.paperlaunch.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

import de.devmil.paperlaunch.R;
import de.devmil.paperlaunch.config.UserSettings;
import de.devmil.paperlaunch.service.LauncherOverlayService;
import de.devmil.paperlaunch.view.preferences.SeekBarPreference;

public class SettingsFragment extends PreferenceFragment {

    private UserSettings mUserSettings;
    private IActivationParametersChangedListener mActivationParametersChangedListener;

    public interface IActivationParametersChangedListener {
        void onActivationParametersChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity();

        mUserSettings = new UserSettings(context);

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        setPreferenceScreen(screen);

        addActivationSettings(context, screen);
        addAppearanceSettings(context, screen);
    }

    public void setOnActivationParametersChangedListener(IActivationParametersChangedListener listener) {
        mActivationParametersChangedListener = listener;
    }

    private void fireActivationParametersChanged() {
        if(mActivationParametersChangedListener != null) {
            mActivationParametersChangedListener.onActivationParametersChanged();
        }
    }

    private void addActivationSettings(Context context, PreferenceScreen screen) {
        PreferenceCategory activationCategory = new PreferenceCategory(context);

        screen.addPreference(activationCategory);

        activationCategory.setPersistent(false);
        activationCategory.setTitle(R.string.fragment_settings_category_activation_title);
        activationCategory.setIcon(R.mipmap.ic_wifi_tethering_black_36dp);

        SeekBarPreference sensitivityPreference = new SeekBarPreference(context, 5, 40);
        activationCategory.addPreference(sensitivityPreference);

        sensitivityPreference.setValue(mUserSettings.getSensitivityDip());
        sensitivityPreference.setTitle(R.string.fragment_settings_activation_sensitivity_title);
        sensitivityPreference.setPersistent(false);
        sensitivityPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUserSettings.load(getActivity());
                mUserSettings.setSensitivityDip((Integer) newValue);
                mUserSettings.save(getActivity());
                fireActivationParametersChanged();
                return true;
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final float heightDpi = metrics.heightPixels / metrics.density;

        SeekBarPreference offsetHeightPreference = new SeekBarPreference(context, 0, (int)heightDpi);
        activationCategory.addPreference(offsetHeightPreference);

        offsetHeightPreference.setValue((int) heightDpi - mUserSettings.getActivationOffsetHeightDip());
        offsetHeightPreference.setTitle(R.string.fragment_settings_activation_offset_height_title);
        offsetHeightPreference.setPersistent(false);
        offsetHeightPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUserSettings.load(getActivity());
                mUserSettings.setActivationOffsetHeightDip((int) heightDpi - (Integer) newValue);
                mUserSettings.save(getActivity());
                fireActivationParametersChanged();
                return true;
            }
        });

        int offsetMin = -(int)(heightDpi / 2);
        int offsetMax = (int)(heightDpi / 2);

        SeekBarPreference offsetPositionPreference = new SeekBarPreference(context, offsetMin, offsetMax);
        activationCategory.addPreference(offsetPositionPreference);

        offsetPositionPreference.setValue(mUserSettings.getActivationOffsetPositionDip());
        offsetPositionPreference.setTitle(R.string.fragment_settings_activation_offset_position_title);
        offsetPositionPreference.setPersistent(false);
        offsetPositionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUserSettings.load(getActivity());
                mUserSettings.setActivationOffsetPositionDip((Integer) newValue);
                mUserSettings.save(getActivity());
                fireActivationParametersChanged();
                return true;
            }
        });

        CheckBoxPreference vibrateOnActivationPreference = new CheckBoxPreference(context);
        activationCategory.addPreference(vibrateOnActivationPreference);

        vibrateOnActivationPreference.setChecked(mUserSettings.isVibrateOnActivation());
        vibrateOnActivationPreference.setTitle(R.string.fragment_settings_activation_vibrate_on_activation_title);
        vibrateOnActivationPreference.setSummaryOn(R.string.fragment_settings_activation_vibrate_on_activation_summary_on);
        vibrateOnActivationPreference.setSummaryOff(R.string.fragment_settings_activation_vibrate_on_activation_summary_off);
        vibrateOnActivationPreference.setPersistent(false);
        vibrateOnActivationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUserSettings.load(getActivity());
                mUserSettings.setVibrateOnActivation((Boolean) newValue);
                mUserSettings.save(getActivity());
                LauncherOverlayService.notifyConfigChanged(getActivity());
                return true;
            }
        });
    }

    private void addAppearanceSettings(final Context context, PreferenceScreen screen) {
        PreferenceCategory apperanceCategory = new PreferenceCategory(context);
        screen.addPreference(apperanceCategory);

        apperanceCategory.setPersistent(false);
        apperanceCategory.setTitle(R.string.fragment_settings_category_appearance_title);

        CheckBoxPreference showBackgroundPreference = new CheckBoxPreference(context);
        apperanceCategory.addPreference(showBackgroundPreference);

        showBackgroundPreference.setPersistent(false);
        showBackgroundPreference.setTitle(R.string.fragment_settings_appearance_background_title);
        showBackgroundPreference.setSummaryOn(R.string.fragment_settings_appearance_background_summary_on);
        showBackgroundPreference.setSummaryOff(R.string.fragment_settings_appearance_background_summary_off);
        showBackgroundPreference.setChecked(mUserSettings.isShowBackground());
        showBackgroundPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUserSettings.load(getActivity());
                mUserSettings.setShowBackground((Boolean) newValue);
                mUserSettings.save(getActivity());
                LauncherOverlayService.notifyConfigChanged(getActivity());
                return true;
            }
        });

        final ListPreference sidePreference = new ListPreference(context);
        apperanceCategory.addPreference(sidePreference);

        class SideEntry {
            SideEntry(String title, boolean value) {
                this.title = title;
                this.value = value;
            }
            String title;
            boolean value;
        }

        SideEntry[] sideEntryArray = new SideEntry[]
                {
                        new SideEntry(
                                context.getString(R.string.fragment_settings_appearance_side_optionleft),
                                false
                        ),
                        new SideEntry(
                                context.getString(R.string.fragment_settings_appearance_side_optionright),
                                true
                        )
                };

        List<CharSequence> sideEntryTitles = new ArrayList<>();
        List<CharSequence> sideEntryValues = new ArrayList<>();
        for(SideEntry se : sideEntryArray) {
            sideEntryTitles.add(se.title);
            sideEntryValues.add(Boolean.toString(se.value));
        }

        sidePreference.setPersistent(false);
        sidePreference.setTitle(R.string.fragment_settings_appearance_side_title);
        sidePreference.setEntries(sideEntryTitles.toArray(new CharSequence[sideEntryTitles.size()]));
        sidePreference.setEntryValues(sideEntryValues.toArray(new CharSequence[sideEntryValues.size()]));
        sidePreference.setValue(Boolean.toString(mUserSettings.isOnRightSide()));
        sidePreference.setSummary(context.getString(getSideSummary(mUserSettings.isOnRightSide())));
        sidePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean newBooleanValue = Boolean.parseBoolean((String)newValue);
                mUserSettings.load(getActivity());
                mUserSettings.setIsOnRightSide(newBooleanValue);
                mUserSettings.save(getActivity());
                sidePreference.setSummary(context.getString(getSideSummary(mUserSettings.isOnRightSide())));
                fireActivationParametersChanged();
                return true;
            }
        });
    }

    private int getSideSummary(boolean isOnRightSide) {
        return isOnRightSide ?
                R.string.fragment_settings_appearance_side_optionright_summary
                : R.string.fragment_settings_appearance_side_optionleft_summary;
    }
}
