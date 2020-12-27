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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synotransactions.KeyStationManager;
import com.t07m.synotransactions.SynoTransactions;
import com.t07m.synotransactions.mcd.MCDConfig.MCDKeyStationConfig;
import com.t07m.synotransactions.mcd.MCDKeyStationFactory.MCDKeyStation;
import com.t07m.synotransactions.transaction.TransactionFactory;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class MCDKeyStationManager extends KeyStationManager{

	private static Logger logger = LoggerFactory.getLogger(MCDKeyStationManager.class);
	
	private MCDConfig config;

	private @Getter MCDKeyStationFactory ksFactory;

	private List<MCDKeyStation> keyStations;

	public MCDKeyStationManager(SynoTransactions app) {
		super(app);
	}

	public void init() {
		this.config = new MCDConfig();
		try {
			this.config.init();
			this.config.save();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			logger.error("Unable to load MCD configuration file!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			System.exit(-1);
		}
		ksFactory = new MCDKeyStationFactory(
				new TransactionFactory(config.getSynoIP(), config.getSynoPort(), config.isUseSSL(), config.getDSName(), config.getSynoUsername(), config.getSynoPassword()),
				new BOPParser());
		keyStations = new ArrayList<MCDKeyStation>();
		for(MCDKeyStationConfig ksConfig : config.getKeyStations()) {
			addKeyStation(ksConfig);
		}
	}

	public void process() {
		synchronized(keyStations) {
			for(MCDKeyStation ks : keyStations) {
				if(ks.getConfig().isEnabled()) {
					if(ks.getSmbWatcher() == null) {
						ks.start(getApp());
					}
				}else {
					if(ks.getSmbWatcher() != null) {
						ks.cleanup();
						cleanupKeyStation(ks);
					}
				}
			}
		}
	}

	private void addKeyStation(MCDKeyStationConfig ksConfig) {
		synchronized(keyStations) {
			for(MCDKeyStation ks : keyStations) {
				if(ks.getConfig().equals(ksConfig)) {
					return;
				}else if(ks.getConfig().getID() == ksConfig.getID()) {
					logger.warn("Attempted to load KS with duplicate ID! " + ksConfig.getID());
					return;
				}
			}
			keyStations.add(ksFactory.new MCDKeyStation(ksConfig));
			logger.info("Loaded KS: " + ksConfig.getID());
		}
	}
	
	private void cleanupKeyStation(MCDKeyStation ks) {
		getApp().removeService(ks.getSmbWatcher());
	}

	public void cleanup() {
		synchronized(keyStations) {
			Iterator<MCDKeyStation> itr = keyStations.iterator();
			while(itr.hasNext()) {
				MCDKeyStation ks = itr.next();
				itr.remove();
				getApp().removeService(ks.getSmbWatcher());
				ks.cleanup();
			}
			SMBWatcher.shutdown();
		}
	}

}
