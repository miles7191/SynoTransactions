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

import java.util.ArrayList;

import com.t07m.synotransactions.SurveillanceStation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class RollingTransaction extends Transaction{

	private final String sessionId;
	
	private ArrayList<TransactionEntry> data = new ArrayList<TransactionEntry>();

	private boolean shouldCancel, canceled, shouldComplete;
	private @Getter boolean completed;

	RollingTransaction(String sessionId, Format format, int timestamp, String deviceName, SurveillanceStation surveillanceStation) {
		super(format, timestamp, deviceName, surveillanceStation);
		this.sessionId = sessionId;
	}

	void process() {
		if(!canceled) {
			if(shouldCancel) {
				canceled = getSurveillanceStation().cancelTransaction(getDeviceName(), sessionId, getTimeStamp());
			}else if(!completed){
				for(TransactionEntry e : data) {
					if(!e.isSubmited()) {
						if(!getSurveillanceStation().appendTransaction(getDeviceName(), sessionId, e.getData(), e.getTime())) {
							this.invokeThread();
							//TODO: log failed submiting transaction
							return;
						}else {
							e.setSubmited(true);
						}
					}
				}
				if(shouldComplete) {
					completed = getSurveillanceStation().completeTransaction(getDeviceName(), sessionId, (int) (System.currentTimeMillis()/1000));
				}
			}
		}		
	}

	public void cancel() {
		shouldCancel = true;
		this.invokeThread();
	}
	
	public void complete() {
		shouldComplete = true;
		this.invokeThread();
	}
	
	public void append(String data) {
		this.append(data, (int) (System.currentTimeMillis()/1000));
	}
	
	public void append(String data, int timestamp) {
		synchronized(data){
			this.data.add(new TransactionEntry(data, timestamp));
			this.invokeThread();
		}
	}
	
	@RequiredArgsConstructor
	private class TransactionEntry {
		private final @Getter String data;
		private final @Getter int time;
		private @Getter @Setter boolean submited = false;
	}
	
}
