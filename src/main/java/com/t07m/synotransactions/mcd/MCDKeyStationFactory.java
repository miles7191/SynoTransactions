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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synotransactions.SynoTransactions;
import com.t07m.synotransactions.mcd.MCDConfig.MCDKeyStationConfig;
import com.t07m.synotransactions.transaction.Transaction.Format;
import com.t07m.synotransactions.transaction.TransactionFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MCDKeyStationFactory {

	private static Logger logger = LoggerFactory.getLogger(MCDKeyStationFactory.class);
	
	private final TransactionFactory transactionFactory;
	private final BOPParser bopParser;
	
	public class MCDKeyStation {

		private @Getter @Setter MCDKeyStationConfig config;
		
		private @Getter SMBWatcher smbWatcher;
		
		public MCDKeyStation(MCDKeyStationConfig config) {
			this.config = config;
		}
		
		public boolean start(SynoTransactions app) {
			if(smbWatcher != null) {
				smbWatcher.cleanup();
			}
			smbWatcher = new SMBWatcher(app, config) {
				public void onNewBop(InputStream res) {
					MCDTransaction trans = bopParser.parse(res);
					if(trans != null) {
						transactionFactory.submitTransaction(MCDReceiptFormatter.format(trans), Format.String, config.getSynologyDeviceName());
						logger.info("KeyStation " + config.getID() + " submitted new transaction to " + config.getSynologyDeviceName() + ".");
					}else {
						logger.warn(config.getSynologyDeviceName() + " Unable to parse transaction!");
					}
				}
			};
			smbWatcher.init();
			app.registerService(smbWatcher);
			return false;
		}
		
		public boolean isRunning() {
			return smbWatcher != null && smbWatcher.isRunning();
		}
		
		public void cleanup() {
			if(smbWatcher != null) {
				smbWatcher = null;
			}
		}
		
	}

	
}


