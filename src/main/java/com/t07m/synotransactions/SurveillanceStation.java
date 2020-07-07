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
package com.t07m.synotransactions;

import java.io.IOException;
import java.net.URISyntaxException;

import com.t07m.synology.dsmclient.DsmConnection;
import com.t07m.synology.dsmclient.webapi.SurveillanceStationTransactions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SurveillanceStation {

	private final @Getter String host;
	private final @Getter int port;
	private final @Getter boolean useSSL;
	private final @Getter String dsName, username, password;
	
	private DsmConnection dsmConnection;
	
	private SurveillanceStationTransactions getTransactions() {
		if(dsmConnection == null) {
			dsmConnection = new DsmConnection(host, port, useSSL, true);
		}
		return dsmConnection.getWebApi().getSurveillanceStation().getTransactions();
	}
	
	public boolean insertTransaction(String deviceName, String content, String format, int timestamp) {
		try {
			return getTransactions().insert(dsName, deviceName, content, format, timestamp, username, password);
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
	public boolean beginTransaction(String deviceName, String sessionId, int timeout, int timestamp) {
		try {
			return getTransactions().begin(dsName, deviceName, sessionId, timeout, timestamp, username, password);
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
	public boolean completeTransaction(String deviceName, String sessionId, int timestamp) {
		try {
			return getTransactions().complete(dsName, deviceName, sessionId, timestamp, username, password);
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
	public boolean cancelTransaction(String deviceName, String sessionId, int timestamp) {
		try {
			return getTransactions().cancel(dsName, deviceName, sessionId, timestamp, username, password);
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
	public boolean appendTransaction(String deviceName, String sessionId, String content, int timestamp) {
		try {
			return getTransactions().appendData(dsName, deviceName, sessionId, content, timestamp, username, password);
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
}
