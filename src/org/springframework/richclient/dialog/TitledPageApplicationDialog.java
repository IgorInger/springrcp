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
package org.springframework.richclient.dialog;

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.richclient.core.Message;
import org.springframework.richclient.form.Form;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A TitledApplicationDialog that delegates to a single DialogPage for its
 * title, content and messages.
 * 
 * @author oliverh
 */
public abstract class TitledPageApplicationDialog extends TitledApplicationDialog implements PropertyChangeListener {

    private DialogPage dialogPage;

    public TitledPageApplicationDialog(DialogPage dialogPage) {
        super();
        setDialogPage(dialogPage);
    }

    public TitledPageApplicationDialog(Form form, Window parent) {
        this(new FormBackedDialogPage(form), parent);
    }

    public TitledPageApplicationDialog(DialogPage dialogPage, Window parent) {
        super(dialogPage.getTitle(), parent);
        setDialogPage(dialogPage);
    }

    public TitledPageApplicationDialog(DialogPage dialogPage, Window parent, CloseAction closeAction) {
        super(dialogPage.getTitle(), parent, closeAction);
        setDialogPage(dialogPage);
    }

    private void setDialogPage(DialogPage dialogPage) {
        Assert.notNull(dialogPage, "The single dialog page to display is required");
        this.dialogPage = dialogPage;
    }

    protected DialogPage getDialogPage() {
        return dialogPage;
    }

    protected JComponent createTitledDialogContentPane() {
        dialogPage.addPropertyChangeListener(this);
        update();
        return dialogPage.getControl();
    }

    protected Message getDescription() {
        return new Message(dialogPage.getDescription());
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (Messagable.MESSAGE_PROPERTY.equals(e.getPropertyName())) {
            update();
        }
        else if (DialogPage.PAGE_COMPLETE_PROPERTY.equals(e.getPropertyName())) {
            setEnabled(dialogPage.isPageComplete());
        }
        else {
            update();
        }
    }

    protected void update() {
        if (!StringUtils.hasText(getTitle())) {
            setTitle(dialogPage.getTitle());
        }
        updateTitlePane();
        updateMessagePane();
    }

    protected void updateTitlePane() {
        setTitlePaneTitle(dialogPage.getTitle());
        setTitlePaneImage(dialogPage.getImage());
    }

    protected void updateMessagePane() {
        setMessage(dialogPage.getMessage());
    }    
}