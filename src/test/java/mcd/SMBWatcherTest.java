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
package mcd;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.t07m.synotransactions.mcd.KeyStation;
import com.t07m.synotransactions.mcd.SMBWatcher;

class SMBWatcherTest {

	private static KeyStation ks;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ks = new KeyStation(
				0,
				true,
				System.getProperty("ksip"),
				System.getProperty("ksdomain"),
				System.getProperty("ksusername"),
				System.getProperty("kspassword"),
				System.getProperty("ksreprint"),
				null);
		System.out.println(ks);
	}

	@Test
	void test() throws InterruptedException {
		SMBWatcher watcher = new SMBWatcher(null, ks) {
			public void onNewBop(InputStream res) {
				System.out.println("New Bop");
			}
		};
		watcher.init();
		long runTarget = TimeUnit.MINUTES.toMillis(1);
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < runTarget) {
			watcher.process();
			Thread.sleep(1000);
		}
	}

}
