/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.preference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.binding.validation.Severity;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.settings.SettingsException;
import org.springframework.richclient.settings.SettingsManager;

/**
 * Manages the PreferenceStore and the PreferenceDialog.
 * 
 * @author Peter De Bruycker
 */
public class PreferenceManager {

	private PreferenceDialog dialog;

	private List preferencePages = new ArrayList();

	private SettingsManager settingsManager;

	public void showDialog() {
		if (dialog == null) {
			dialog = new PreferenceDialog();

			for (Iterator iter = preferencePages.iterator(); iter.hasNext();) {
				PreferencePage page = (PreferencePage) iter.next();
				if (page.getParent() == null) {
					dialog.addPreferencePage(page);
				} else {
					dialog.addPreferencePage(page.getParent(), page);
				}
			}

			dialog.setTitle("Preferences");
			try {
				dialog.setSettings(settingsManager.getUserSettings());
			} catch (SettingsException e) {
				new MessageDialog("Error", new Message(e.getMessage(), Severity.ERROR)).showDialog();
				e.printStackTrace();
			}
		}

		// dialog creation can fail
		if (dialog != null) {
			dialog.showDialog();
		}
	}

	public void setPreferencePages(List pages) {
		preferencePages = pages;
	}

	public SettingsManager getSettingsManager() {
		return settingsManager;
	}

	public void setSettingsManager(SettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}
}