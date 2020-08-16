/*
 * Copyright (C) 2020 Matthew Rosato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t07m.synotransactions.mcd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.t07m.application.Service;
import com.t07m.synotransactions.SynoTransactions;
import com.t07m.synotransactions.mcd.MCDConfig.MCDKeyStationConfig;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.SmbResource;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import lombok.NonNull;

public abstract class SMBWatcher extends Service<SynoTransactions>{

	private static ExecutorService es = Executors.newCachedThreadPool();
	
	private final MCDKeyStationConfig ks;
	private SmbResource[] oldBOPs;

	public SMBWatcher(SynoTransactions app, @NonNull MCDKeyStationConfig ks) {
		super(app, TimeUnit.SECONDS.toMillis(1));
		this.ks = ks;
	}

	public void init() {
		oldBOPs = getBOPs();
	}

	public void process() {
		SmbResource[] currentBOPs = getBOPs();
		for(SmbResource res : currentBOPs) {
			if(!containsByName(oldBOPs, res.getName())) {
				es.submit(() -> {try {
					onNewBop(res.openInputStream());
				} catch (CIFSException e) {}});
			}
		}
		oldBOPs = currentBOPs;
	}

	public static void shutdown() {
		es.shutdown();
		try {
			es.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
	}
	
	public abstract void onNewBop(InputStream res);

	private boolean containsByName(SmbResource[] resources, String name) {
		if(resources != null && name != null) {
			for(SmbResource res : resources) {
				if(res.getName().equals(name))
					return true;
			}
		}
		return false;
	}

	private SmbResource[] getBOPs() {
		List<SmbResource> bops = new ArrayList<SmbResource>();
		try {
			SmbResource folder = getContext().get(getRemoteURL());
			Iterator<SmbResource> itr = folder.children("*.bop");
			while(itr.hasNext()) {
				bops.add(itr.next());
			}
		} catch (CIFSException e) {
			e.printStackTrace();
		}
		return bops.toArray(new SmbResource[bops.size()]);
	}

	private CIFSContext getContext() {
		return SingletonContext.getInstance().withCredentials(getAuth());
	}

	private NtlmPasswordAuthenticator getAuth() {
		return new NtlmPasswordAuthenticator(ks.getDomain(), ks.getUsername(), ks.getPassword());
	}

	private String getRemoteURL() {
		return "smb://" + ks.getIP() + ks.getReprintPath();
	}

}
