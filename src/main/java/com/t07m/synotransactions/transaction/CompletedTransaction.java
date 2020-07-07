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

import com.t07m.synotransactions.SurveillanceStation;

import lombok.Getter;

public class CompletedTransaction extends Transaction{

	private final String[] data;
	
	CompletedTransaction(String[] data, Format format, int timestamp, String deviceName, SurveillanceStation surveillanceStation) {
		super(format, timestamp, deviceName, surveillanceStation);
		this.data = data;
		this.invokeThread();
	}

	void process() {
		if(!this.getSurveillanceStation().insertTransaction(getDeviceName(), getFormat() == Format.Json ? String.join("", data) : getFormat() == Format.String ? String.join("\n", data) : "", getFormat().toString(), getTimeStamp())) {
			this.invokeThread();
			//TODO: Log failed submiting transaction
		}
	}	
}
