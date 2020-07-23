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
package com.t07m.synotransactions.transaction;

import com.t07m.synology.dsmclient.DsmConnection;
import com.t07m.synology.dsmclient.webapi.SurveillanceStationTransactions;
import com.t07m.synotransactions.transaction.Transaction.Format;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionFactory {

	private final @Getter String host;
	private final @Getter int port;
	private final @Getter boolean useSSL;
	private final @Getter String dsName, username, password;
	
	private DsmConnection dsmConnection;
	
	SurveillanceStationTransactions getTransactions() {
		if(dsmConnection == null) {
			dsmConnection = new DsmConnection(host, port, useSSL, true);
		}
		return dsmConnection.getWebApi().getSurveillanceStation().getTransactions();
	}
	
	public CompletedTransaction submitTransaction(String[] data, Format format, String deviceName) {
		return submitTransaction(data, format, (int)(System.currentTimeMillis()/1000), deviceName);
	}
	
	public CompletedTransaction submitTransaction(String[] data, Format format, int timestamp, String deviceName) {
		return new CompletedTransaction(data, format, timestamp, deviceName, this);
	}
	
	public RollingTransaction startTransaction(String sessionId, Format format, String deviceName) {
		return startTransaction(sessionId, format, (int)(System.currentTimeMillis()/1000), deviceName);
	}
	
	public RollingTransaction startTransaction(String sessionId, Format format, int timestamp, String deviceName) {
		return new RollingTransaction(sessionId, format, timestamp, deviceName, this);
	}
	
}
