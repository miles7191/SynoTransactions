/*
 * Copyright (C) 2021 Matthew Rosato
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
package com.t07m.synotransactions.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.console.Command;
import com.t07m.console.Console;
import com.t07m.synotransactions.SynoTransactions;

import joptsimple.OptionSet;

public class WhoAmICommand extends Command{

	private static Logger logger = LoggerFactory.getLogger(WhoAmICommand.class);
	
	private final SynoTransactions trans;
	
	public WhoAmICommand(SynoTransactions trans) {
		super("WhoAmI");
		this.trans = trans;
	}

	public void process(OptionSet optionSet, Console console) {
		logger.info("I Am: " + trans.getIdentity());
	}

}
