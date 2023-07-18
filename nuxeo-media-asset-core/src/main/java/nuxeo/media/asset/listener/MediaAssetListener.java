/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Michael Vachette
 */

package nuxeo.media.asset.listener;

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.ABOUT_TO_CREATE;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.ABOUT_TO_IMPORT;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.BEFORE_DOC_UPDATE;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import nuxeo.media.asset.service.MediaAssetService;

public class MediaAssetListener implements EventListener {

    @Override
    public void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext docCtx)) {
            return;
        }

        DocumentModel doc = docCtx.getSourceDocument();

        MediaAssetService mediaAssetService = Framework.getService(MediaAssetService.class);

        if (!mediaAssetService.isDocumentSupported(doc)) {
            return;
        }

        if (ABOUT_TO_CREATE.equals(event.getName()) || ABOUT_TO_IMPORT.equals(event.getName())) {
            mediaAssetService.updateDocumentMediaFacet(doc);
        } else if (BEFORE_DOC_UPDATE.equals(event.getName())) {
            Property contentProperty = doc.getProperty("file:content");
            if (!contentProperty.isDirty()) {
                return;
            }
            mediaAssetService.updateDocumentMediaFacet(doc);
        }
    }

}
