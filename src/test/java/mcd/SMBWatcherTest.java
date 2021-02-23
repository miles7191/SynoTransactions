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

import org.junit.Before;
import org.junit.Test;

import com.t07m.synotransactions.mcd.MCDConfig;
import com.t07m.synotransactions.mcd.MCDConfig.MCDKeyStationConfig;
import com.t07m.synotransactions.mcd.SMBWatcher;

class SMBWatcherTest {

	private static MCDKeyStationConfig ks;
	
	@Before
	static void setUpBeforeClass() throws Exception {
		ks = new MCDConfig().new MCDKeyStationConfig() {
			public String getIP() {return System.getProperty("ksip");}
			public String getDomain() {return System.getProperty("ksdomain");}
			public String getUsername() {return System.getProperty("ksusername");}
			public String getPassword() {return System.getProperty("kspassword");}
			public String getReprintPath() {return System.getProperty("ksreprint");}
		};
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
