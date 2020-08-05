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
import java.util.Scanner;

import lombok.Getter;

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
		//TODO: Parse data and return transaction
		return null;
	}
}
