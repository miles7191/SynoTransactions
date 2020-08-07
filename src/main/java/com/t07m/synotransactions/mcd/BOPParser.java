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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.t07m.synotransactions.mcd.MCDTransaction.TransactionType;

public class BOPParser {

	public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

	public MCDTransaction parse(Readable source) {
		return this.parse(new Scanner(source));
	}

	public MCDTransaction parse(InputStream source) {
		return this.parse(new Scanner(source));
	}

	public MCDTransaction parse(File source) throws FileNotFoundException {
		return this.parse(new Scanner(source));
	}

	public MCDTransaction parse(Path source) throws IOException {
		return this.parse(new Scanner(source));
	}

	public MCDTransaction parse(String source) {
		return this.parse(new Scanner(source));
	}

	public MCDTransaction parse(ReadableByteChannel source) {
		return this.parse(new Scanner(source));
	}

	public MCDTransaction parse(Scanner scanner) {
		ArrayList<String> lines = new ArrayList<String>();
		while(scanner.hasNextLine()) {
			lines.add(scanner.nextLine());
		}
		scanner.close();
		if(lines.size() > 0) {
			MCDTransaction transaction = new MCDTransaction();
			//Set raw data as backup
			transaction.setRaw(lines.toArray(new String[lines.size()]));

			removeTags(lines);
			transaction.setTransactionType(parseTransactionType(lines));
			transaction.setKS(parseKS(lines));
			transaction.setTimeStamp(parseTimeStamp(lines));

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
		int typeIndex = indexOfContaining(lines, 0, new LineFilter() {
			public boolean accept(String line) {
				for(String marker : markers.toArray(new String[markers.size()])) {
					if(line.contains(marker))
						return true;
				}
				return false;
			}
		});
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

	private int parseKS(List<String> lines) {
		int ksIndex = indexOfContaining(lines, 0, line -> line.contains("KS#"));
		if(ksIndex != -1) {
			try {
				String line = lines.get(ksIndex);
				int ks = Integer.parseInt(lines.get(ksIndex).substring(4,6).replace(" ", ""));
				line = line.substring(6, line.length());
				lines.set(ksIndex, line);
				return ks;
			}catch(NumberFormatException e) {}
		}
		return -1;
	}

	private LocalDateTime parseTimeStamp(List<String> lines) {
		int dateIndex = indexOfContaining(lines, 0, new LineFilter() {
			public boolean accept(String line) {
				if((line.contains("AM") || line.contains("PM")) && StringUtils.countMatches(line, "/") == 2) {
					return true;
				}
				return false;
			}
		});
		if(dateIndex != -1) {
			try {
				LocalDateTime timeStamp = LocalDateTime.parse(lines.get(dateIndex).substring(14), dateFormat);
				lines.remove(dateIndex);
				return timeStamp;
			}catch (DateTimeParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private int indexOfContaining(List<String> list, int startIndex, LineFilter filter) {
		if (list == null) {
			return -1;
		}
		if (startIndex < 0) {
			startIndex = 0;
		}
		if (filter == null) {
			for (int i = startIndex; i < list.size(); i++) {
				if (list.get(i) == null) {
					return i;
				}
			}
		} else {
			for (int i = startIndex; i < list.size(); i++) {
				if(filter.accept(list.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	interface LineFilter{

		public boolean accept(String line);

	}
}
