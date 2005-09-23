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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;

/**
 * An implementation of the <code>DialogPage</code> interface that collects a
 * group of dialog pages together so that they can be used in place of a single
 * <code>DialogPage</code>
 * <p>
 * It is expected that subclasses will provide various ways of displaying the
 * active page and navigating to inactive child pages.
 * <p>
 * Services of a <code>CompositeDialogPage</code> include:
 * <ul>
 * <li>creating the page controls of the child pages and determining the
 * largest sized of these controls.
 * <li>keeping track of an active page that will be used to provide
 * messages/titles for the composite.
 * <li>pageComplete property of composite is true if all child pages are
 * complete
 * </ul>
 * 
 * @author oliverh
 * @see org.springframework.richclient.dialog.TabbedDialogPage
 */
public abstract class CompositeDialogPage extends AbstractDialogPage {
   
    private final ChildChangeHandler childChangeHandler = new ChildChangeHandler();

    private List pages = new ArrayList();

    private int largestPageWidth;

    private int largestPageHeight;

    private boolean autoConfigureChildPages = true;

    private DialogPage activePage;

    public CompositeDialogPage(String pageId) {
        super(pageId);
    }

    public void setAutoConfigureChildPages(boolean autoConfigure) {
        this.autoConfigureChildPages = autoConfigure;
    }

    /**
     * Adds a DialogPage to the list of pages managed by this
     * CompositeDialogPage.
     * 
     * @param page
     *            the page to add
     */
    public void addPage(DialogPage page) {
        pages.add(page);
        if (autoConfigureChildPages) {
            String id = getId() + "." + page.getId();
            getObjectConfigurer().configure(page, id);
        }
    }

    /**
     * Adds a new page to the list of pages managed by this CompositeDialogPage.
     * The page is created by wrapping the form page in a FormBackedDialogPage.
     * 
     * @param formPage
     *            the form page to be insterted
     * @return the DialogPage that wraps formPage
     */
    public DialogPage addForm(Form form) {
        DialogPage page = createDialogPage(form);
        addPage(page);
        return page;
    }

    /**
     * Adds an array DialogPage to the list of pages managed by this
     * CompositeDialogPage.
     * 
     * @param pages
     *            the pages to add
     */
    public void addPages(DialogPage[] pages) {
        for (int i = 0; i < pages.length; i++) {
            addPage(pages[i]);
        }
    }

    /**
     * Subclasses should extend if extra pages need to be added before the
     * composite creates its control. New pages should be added by calling
     * <code>addPage</code>.
     */
    protected void addPages() {
    }

    protected List getPages() {
        return pages;
    }

    /**
     * Sets the active page of this CompositeDialogPage.
     * 
     * @param activePage
     *            the page to be made active. Must be one of the child pages.
     */
    public void setActivePage(DialogPage activePage) {
        Assert.isTrue(activePage == null || pages.contains(activePage));
        if (this.activePage == activePage) {
            return;
        }
        this.activePage = activePage;
        updateMessage();
    }

    /**
     * Gets the active page of this CompositeDialogPage.
     * 
     * @return the active page; or null if no page is active.
     */
    public DialogPage getActivePage() {
        return activePage;
    }

    protected DialogPage createDialogPage(Form form) {
        return new FormBackedDialogPage(form, !autoConfigureChildPages);
    }

    protected void createPageControls() {
        addPages();
        Assert.notEmpty(getPages(), "Pages must have been added first");
        for (Iterator i = pages.iterator(); i.hasNext();) {
            DialogPage page = (DialogPage)i.next();
            prepareDialogPage(page);
        }
    }

    /**
     * Prepare a dialog page - Add our property listeners and configure the control's look.
     * @param page to process
     */
    protected void prepareDialogPage(DialogPage page) {
        page.addPropertyChangeListener(childChangeHandler);
        JComponent c = page.getControl();
        GuiStandardUtils.attachDialogBorder(c);
        Dimension size = c.getPreferredSize();
        if (size.width > largestPageWidth) {
            largestPageWidth = size.width;
        }
        if (size.height > largestPageHeight) {
            largestPageHeight = size.height;
        }
    }
    
    /**
     * Get the size of the largest page added so far.
     * @return Dimension of largest page
     */
    public Dimension getLargestPageSize() {
        return new Dimension(largestPageWidth + UIConstants.ONE_SPACE, largestPageHeight + UIConstants.ONE_SPACE);
    }

    protected void updatePageComplete(DialogPage page) {
        boolean pageComplete = true;
        for (Iterator i = pages.iterator(); i.hasNext();) {
            if (!((DialogPage)i.next()).isPageComplete()) {
                pageComplete = false;
                break;
            }
        }
        setPageComplete(pageComplete);
    }

    protected void updatePageLabels(DialogPage page) {
    }

    protected void updateMessage() {
        if (activePage != null) {
            setDescription(activePage.getDescription());
            setMessage(activePage.getMessage());
        }
        else {
            setDescription(null);
            setMessage(null);
        }
    }
    
    protected class ChildChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (DialogPage.PAGE_COMPLETE_PROPERTY.equals(e.getPropertyName())) {
                CompositeDialogPage.this.updatePageComplete((DialogPage)e.getSource());
            }
            else if (Messagable.MESSAGE_PROPERTY.equals(e.getPropertyName())) {
                if (getActivePage() == e.getSource()) {
                    updateMessage();
                }
            }
            else {
                CompositeDialogPage.this.updatePageLabels((DialogPage)e.getSource());
            }
        }
    }
}