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
import java.util.Map.Entry;

import com.t07m.synotransactions.SurveillanceStation;

import lombok.Getter;

public class RollingTransaction extends Transaction{

	private final String sessionId;
	
	private ArrayList<Entry<String, Boolean>> data = new ArrayList<Entry<String, Boolean>>();

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
				for(Entry<String, Boolean> e : data) {
					if(!e.getValue().booleanValue()) {
						if(!getSurveillanceStation().appendTransaction(getDeviceName(), sessionId, e.getKey(), (int) (System.currentTimeMillis()/1000))) {
							this.invokeThread();
							//TODO: log failed submiting transaction
							return;
						}else {
							e.setValue(true);
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
	
	public void Append(String data) {
		synchronized(data){
			this.data.add(new Entry<String, Boolean>(){
				
				private boolean submited = false;
				
				public String getKey() {
					return data;
				}
				public Boolean getValue() {
					return submited;
				}
				public Boolean setValue(Boolean value) {
					submited = value;
					return submited;
				}
			});
			this.invokeThread();
		}
	}
}
