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

import lombok.Getter;

public class Transaction {

	public enum Format{String, Json};
	
	private final @Getter String[] data;
	private final @Getter Format format;
	private final @Getter int timestamp;
	private final @Getter String deviceName;
	
	public Transaction(String deviceName, Format format, String... data) {
		this(deviceName, -1, format, data);
	}
	
	public Transaction(String deviceName, int timestamp, Format format, String... data) {
		this.deviceName = deviceName;
		this.data = data;
		this.timestamp = timestamp;
		this.format = format;
	}
}
