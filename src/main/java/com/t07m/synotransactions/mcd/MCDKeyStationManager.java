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

import com.t07m.synotransactions.KeyStationManager;
import com.t07m.synotransactions.SynoTransactions;

import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class MCDKeyStationManager extends KeyStationManager{

	private MCDConfig config;
	
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
			app.getConsole().log("Unable to load MCD configuration file!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			System.exit(-1);
		}
	}
	
	public void process() {
		
	}

}
