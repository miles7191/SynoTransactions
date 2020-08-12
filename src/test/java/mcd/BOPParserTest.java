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

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.t07m.synotransactions.mcd.BOPParser;
import com.t07m.synotransactions.mcd.MCDTransaction;

public class BOPParserTest {

	private static String bopDir = "src/test/resources/mcd/bops";

	private static BOPParser bopParser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bopParser = new BOPParser();
	}

	@Test
	public void testAllBOPS() {
		File dir = new File(bopDir);
		File[] files = dir.listFiles();
		System.out.println("Testing " + files.length + " BOPs");
		for(File file : files) {
			assert(testBOP(file));
		}
		System.out.println("Done");
	}

	public boolean testBOP(File file) {
		try {
			MCDTransaction trans = bopParser.parse(file);
			if(trans != null) {
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("General Parse Failed: " + file.getName());
		return false;
	}

}
