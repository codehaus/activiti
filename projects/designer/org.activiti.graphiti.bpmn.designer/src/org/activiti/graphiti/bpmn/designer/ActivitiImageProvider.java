/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.graphiti.bpmn.designer;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class ActivitiImageProvider extends AbstractImageProvider {

	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "org.activiti.graphiti.bpmn.designer."; //$NON-NLS-1$

	// The image identifier for an EReference.
	public static final String IMG_EREFERENCE = PREFIX + "ereference"; //$NON-NLS-1$
	
	// Event image icons
	public static final String IMG_STARTEVENT_NONE = PREFIX + "startevent.none"; //$NON-NLS-1$
	public static final String IMG_ENDEVENT_NONE = PREFIX + "endevent.none"; //$NON-NLS-1$
	
	// Gateway image icons
	public static final String IMG_GATEWAY_PARALLEL = PREFIX + "gateway.parallel"; //$NON-NLS-1$
	public static final String IMG_GATEWAY_EXCLUSIVE = PREFIX + "gateway.exclusive"; //$NON-NLS-1$
	
	// Task image icons
	public static final String IMG_USERTASK = PREFIX + "usertask"; //$NON-NLS-1$
	public static final String IMG_SCRIPTTASK = PREFIX + "scripttask"; //$NON-NLS-1$
	public static final String IMG_SERVICETASK = PREFIX + "servicetask"; //$NON-NLS-1$
	
	// Sub process icons
	public static final String IMG_SUBPROCESS_COLLAPSED = PREFIX + "subprocess.collapsed"; //$NON-NLS-1$
	public static final String IMG_SUBPROCESS_EXPANDED = PREFIX + "subprocess.expanded"; //$NON-NLS-1$

	@Override
	protected void addAvailableImages() {
		// register the path for each image identifier
		addImageFilePath(IMG_EREFERENCE, "icons/ereference.gif"); //$NON-NLS-1$
		addImageFilePath(IMG_STARTEVENT_NONE, "icons/type.startevent.none.png"); //$NON-NLS-1$
		addImageFilePath(IMG_ENDEVENT_NONE, "icons/type.endevent.none.png"); //$NON-NLS-1$
		addImageFilePath(IMG_GATEWAY_PARALLEL, "icons/type.gateway.parallel.png"); //$NON-NLS-1$
		addImageFilePath(IMG_GATEWAY_EXCLUSIVE, "icons/type.gateway.exclusive.png"); //$NON-NLS-1$
		addImageFilePath(IMG_USERTASK, "icons/type.user.png"); //$NON-NLS-1$
		addImageFilePath(IMG_SCRIPTTASK, "icons/type.script.png"); //$NON-NLS-1$
		addImageFilePath(IMG_SERVICETASK, "icons/type.service.png"); //$NON-NLS-1$
		addImageFilePath(IMG_SUBPROCESS_COLLAPSED, "icons/type.subprocess.collapsed.png"); //$NON-NLS-1$
		addImageFilePath(IMG_SUBPROCESS_EXPANDED, "icons/type.subprocess.expanded.png"); //$NON-NLS-1$
	}
}
