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
package com.t07m.synotransactions.mcd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.t07m.synotransactions.mcd.Transaction.TransactionType;

public class BOPParser {

	public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
	
	public Transaction parse(Readable source) {
		return this.parse(new Scanner(source));
	}
	
	public Transaction parse(InputStream source) {
		return this.parse(new Scanner(source));
	}
	
	public Transaction parse(File source) throws FileNotFoundException {
		return this.parse(new Scanner(source));
	}
	
	public Transaction parse(Path source) throws IOException {
		return this.parse(new Scanner(source));
	}

	public Transaction parse(String source) {
		return this.parse(new Scanner(source));
	}
	
	public Transaction parse(ReadableByteChannel source) {
		return this.parse(new Scanner(source));
	}
	
	public Transaction parse(Scanner scanner) {
		ArrayList<String> lines = new ArrayList<String>();
		while(scanner.hasNextLine()) {
			lines.add(scanner.nextLine());
		}
		scanner.close();
		if(lines.size() > 0) {
			Transaction transaction = new Transaction();
			//Set raw data as backup
			transaction.setRaw(lines.toArray(new String[lines.size()]));
			
			removeTags(lines);
			transaction.setTransactionType(parseTransactionType(lines));
			
			return transaction;
		}
		return null;
	}
	
	private void removeTags(List<String> lines) {
		for(int i = 0; i < lines.size(); i++) {
			lines.set(i, lines.get(i).replaceAll("<(\"*\"|[^>])*>", ""));
		}
	}
	
	private TransactionType parseTransactionType(List<String> lines) {
		TransactionType defaultType = TransactionType.Order;
		ArrayList<String> markers = new ArrayList<String>();
		for(TransactionType tt : TransactionType.values()) {
			if(tt.getMarker() != null) {
				markers.add(tt.getMarker());
			}
		}
		int typeIndex = indexOfContaining(lines, 0, markers.toArray(new String[markers.size()]));
		if(typeIndex == -1)
			return defaultType;
		String line = lines.get(typeIndex);
		for(TransactionType tt : TransactionType.values()) {
			if(tt.getMarker() != null) {
				if(line.contains(tt.getMarker())) {
					lines.remove(typeIndex);
					return tt;
				}
			}
		}
		return null;
	}
	
	private int indexOfContaining(List<String> list, int startIndex, String... objectsToFind) {
		if (list == null) {
			return -1;
		}
		if (startIndex < 0) {
			startIndex = 0;
		}
		if (objectsToFind == null) {
			for (int i = startIndex; i < list.size(); i++) {
				if (list.get(i) == null) {
					return i;
				}
			}
		} else {
			for (int i = startIndex; i < list.size(); i++) {
				for(String objectToFind : objectsToFind) {
					if(list.get(i).contains(objectToFind)) {
						return i;
					}
				}
			}
		}
		return -1;
	}
}
