import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.t07m.synotransactions.transaction.CompletedTransaction;
import com.t07m.synotransactions.transaction.RollingTransaction;
import com.t07m.synotransactions.transaction.Transaction.Format;
import com.t07m.synotransactions.transaction.TransactionFactory;

import lombok.Getter;

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

public class TransactionTest {

	private static @Getter String host, user, password;
	private static @Getter int port;
	private static @Getter boolean useSsl;
	
	private static @Getter TransactionFactory factory = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		host = System.getProperty("SynoHost");
		port = Integer.parseInt(System.getProperty("SynoPort"));
		useSsl = Boolean.parseBoolean(System.getProperty("SynoUseSsl"));
		user = System.getProperty("SynoUser");
		password = System.getProperty("SynoPassword");
		factory = new TransactionFactory(host, port, useSsl, null, user, password);
	}

	@Test
	public void completedTransactionTest() {
		CompletedTransaction ct = factory.submitTransaction(new String[] {"Line 1", "Line 2", "Line 3"}, Format.String, "POS 64");
		long start = System.currentTimeMillis();
		while(!ct.isSubmited() && (System.currentTimeMillis() - start) < TimeUnit.SECONDS.toMillis(5) ) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
		assert(ct.isSubmited());
	}
	
	@Test
	public void rollingTransactionTest() {
		RollingTransaction rt = factory.startTransaction(new Random().nextInt(10000)+"", Format.String, "POS 64");
		rt.append("Rolling");
		rt.append("Line 1");
		rt.append("Line 2");
		rt.complete();
		long start = System.currentTimeMillis();
		while(!rt.isSubmited() && (System.currentTimeMillis() - start) < TimeUnit.SECONDS.toMillis(5) ) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
		assert(rt.isSubmited());
	}

}
