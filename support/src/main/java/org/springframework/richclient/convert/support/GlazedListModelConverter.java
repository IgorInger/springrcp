/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.convert.support;

import javax.swing.ListModel;

import org.springframework.binding.convert.support.AbstractConverter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;

/**
 * supports converting {@link EventList}s to {@link ListModel}
 * 
 * @author Mathias Broekelmann
 *
 */
public class GlazedListModelConverter extends AbstractConverter {

    private static final Class[] TARGET_CLASSES = new Class[] {ListModel.class};
    private static final Class[] SOURCE_CLASSES = new Class[] {EventList.class};

    protected Object doConvert(Object source, Class targetClass) throws Exception {
        return new EventListModel((EventList) source);
    }

    public Class[] getSourceClasses() {
        return SOURCE_CLASSES;
    }

    public Class[] getTargetClasses() {
        return TARGET_CLASSES;
    }

}