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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.synotransactions.SynoTransactions;

import lombok.ToString;

@ToString(callSuper = true)
public class CompletedTransaction extends Transaction{
	
	private static Logger logger = LoggerFactory.getLogger(CompletedTransaction.class);
	
	private final String[] data;
	
	CompletedTransaction(String[] data, Format format, int timestamp, String deviceName, TransactionFactory factory) {
		super(format, timestamp, deviceName, factory);
		this.data = data;
		completed = true;
		this.invokeThread();
	}

	void process() {
		if(!this.insertTransaction(getDeviceName(), getFormat() == Format.Json ? String.join("", data) : getFormat() == Format.String ? String.join("\n", data) + "\nSTID: " + SynoTransactions.getIdentity() + "\nUUID: " + getUUID(): "", getFormat(), getTimeStamp())) {
			this.invokeThread();
		}else {
			submited = true;
		}
	}	
}
