/*
 * Copyright (c) 2002-2004 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.springframework.binding.swing;

import javax.swing.JToggleButton;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Converts ValueModels to the <code>ToggleButtonModel</code> interface.
 * Useful to bind {@link javax.swing.JRadioButton}and
 * {@link javax.swing.JRadioButtonMenuItem}to a ValueModel.
 * <p>
 * 
 * This adapter holds a <em>choice</em> object that is used to determine the
 * selection state if the underlying ValueModel changes its value. This model is
 * selected if the subject's value equals the choice object. And if the
 * selection is set, the choice object is set to the subject.
 * 
 * @author Karsten Lentzsch
 * @author Keith Donald
 */
public final class RadioButtonAdapter extends JToggleButton.ToggleButtonModel {

    /**
     * Refers to the underlying ValueModel that stores the state.
     */
    private final ValueModel valueModel;

    /**
     * Holds the object that is compared with the value model's value to
     * determine whether this adapter is selected or not.
     */
    private final Object choice;

    private ValueChangeListener valueChangeHandler;

    public RadioButtonAdapter(ValueModel valueModel, Object choice) {
        Assert.notNull(valueModel, "The subject must not be null.");
        this.valueModel = valueModel;
        this.choice = choice;
        this.valueChangeHandler = new ValueChangeHandler();
        valueModel.addValueChangeListener(valueChangeHandler);
    }

    // Handles changes in the subject's value.
    private class ValueChangeHandler implements ValueChangeListener {
        public void valueChanged() {
            setSelected(isSelected());
        }
    }

    /**
     * Checks and answers whether the button is selected.
     * 
     * @return true if the button is selected, false if deselected
     */
    public boolean isSelected() {
        return ObjectUtils.nullSafeEquals(choice, valueModel.getValue());
    }

    /**
     * Sets the selected state of the button.
     * 
     * @param selected
     *            true selects the toggle button, false deselects it
     */
    public void setSelected(boolean selected) {
        boolean oldValue = isSelected();
        if (oldValue == selected) {
            return;
        }
        if (selected) {
            updateValueModelSilently(selected);
        }
        super.setSelected(selected);
    }

    private void updateValueModelSilently(boolean selected) {
        valueModel.removeValueChangeListener(valueChangeHandler);
        valueModel.setValue(choice);
        valueModel.addValueChangeListener(valueChangeHandler);
    }

}