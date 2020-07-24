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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Transaction {

	private static ExecutorService es = Executors.newWorkStealingPool(Math.min(Runtime.getRuntime().availableProcessors(), 4));
	
	public enum Format{String, Json};

	private final @Getter Format format;
	private final @Getter int timeStamp;
	private final @Getter String deviceName;
	
	private final @Getter(AccessLevel.PROTECTED) TransactionFactory transactionFactory;
	
	protected @Getter boolean submiting;
	protected @Getter boolean submited;
	protected @Getter boolean completed;

	private Thread thread = new Thread() {
		public void run() {
			process();
			submiting = false;
		}
	};
	
	synchronized final void invokeThread() {
		if(!submiting) {
			es.submit(thread);
			submiting = true;
		}
	}
	
	abstract void process();

	public void cleanup() {
		es.shutdown();
		try {
			es.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
	}
	
	public boolean insertTransaction(String deviceName, String content, Format format, int timestamp) {
		try {
			return getTransactionFactory().getTransactions().insert(
					getTransactionFactory().getDsName(), 
					deviceName, 
					content, 
					format.toString().toLowerCase(), 
					timestamp, 
					getTransactionFactory().getUsername(), 
					getTransactionFactory().getPassword());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean beginTransaction(String deviceName, String sessionId, int timeout, int timestamp) {
		try {
			return getTransactionFactory().getTransactions().begin(
					getTransactionFactory().getDsName(), 
					deviceName, 
					sessionId, 
					timeout, 
					timestamp, 
					getTransactionFactory().getUsername(), 
					getTransactionFactory().getPassword());
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
	public boolean completeTransaction(String deviceName, String sessionId, int timestamp) {
		try {
			return getTransactionFactory().getTransactions().complete(
					getTransactionFactory().getDsName(), 
					deviceName, 
					sessionId, 
					timestamp, 
					getTransactionFactory().getUsername(), 
					getTransactionFactory().getPassword());
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
	public boolean cancelTransaction(String deviceName, String sessionId, int timestamp) {
		try {
			return getTransactionFactory().getTransactions().cancel(
					getTransactionFactory().getDsName(), 
					deviceName, 
					sessionId, 
					timestamp, 
					getTransactionFactory().getUsername(), 
					getTransactionFactory().getPassword());
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
	public boolean appendTransaction(String deviceName, String sessionId, String content, int timestamp) {
		try {
			return getTransactionFactory().getTransactions().appendData(
					getTransactionFactory().getDsName(), 
					deviceName, 
					sessionId, 
					content, 
					timestamp, 
					getTransactionFactory().getUsername(), 
					getTransactionFactory().getPassword());
		} catch (IOException | URISyntaxException e) {}
		return false;
	}
	
}
