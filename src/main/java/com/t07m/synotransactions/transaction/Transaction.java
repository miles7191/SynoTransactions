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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.t07m.synotransactions.SurveillanceStation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Transaction {

	private static ExecutorService es = Executors.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 4));
	
	public enum Format{String, Json};

	private final @Getter Format format;
	private final @Getter int timeStamp;
	private final @Getter String deviceName;
	
	private final @Getter(AccessLevel.PROTECTED) SurveillanceStation surveillanceStation;
	private boolean threadQueued ;

	private Thread thread = new Thread() {
		public void run() {
			process();
		}
	};
	
	synchronized final void invokeThread() {
		if(!threadQueued) {
			es.submit(thread);
			threadQueued = true;
		}
	}
	
	abstract void process();

	public void cleanup() {
		es.shutdown();
		try {
			es.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
	}
	
}
