/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.core.syncinfo;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.subscribers.utils.SynchronizationSyncBytesCache;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.core.Policy;

/**
 * Override <code>SynchronizationSyncBytesCache</code> to log an error
 * if there are no parent bytes for a file.
 */
public class CVSSynchronizationCache extends SynchronizationSyncBytesCache {

	public CVSSynchronizationCache(QualifiedName name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.team.core.subscribers.helpers.SynchronizationCache#setSyncBytes(org.eclipse.core.resources.IResource, byte[])
	 */
	public boolean setSyncBytes(IResource resource, byte[] bytes) throws TeamException {
		boolean changed = super.setSyncBytes(resource, bytes);
		if (resource.getType() == IResource.FILE && getSyncBytes(resource) != null && !parentHasSyncBytes(resource)) {
			// Log a warning if there is no sync bytes available for the resource's
			// parent but there is valid sync bytes for the child
			CVSProviderPlugin.log(new TeamException(Policy.bind("ResourceSynchronizer.missingParentBytesOnSet", getSyncName().toString(), resource.getFullPath().toString())));
		}
		return changed;
	}

	/**
	 * Indicates whether the parent of the given local resource has sync bytes for its
	 * corresponding remote resource. The parent bytes of a remote resource are required
	 * (by CVS) to create a handle to the remote resource.
	 */
	protected boolean parentHasSyncBytes(IResource resource) throws TeamException {
		if (resource.getType() == IResource.PROJECT) return true;
		return (getSyncBytes(resource.getParent()) != null);
	}
}
