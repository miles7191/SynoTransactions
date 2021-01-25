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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Application;
import com.t07m.console.Console;
import com.t07m.console.NativeConsole;
import com.t07m.swing.console.ConsoleWindow;
import com.t07m.synotransactions.command.WhoAmICommand;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;

public class SynoTransactions extends Application{

	private static Logger logger = LoggerFactory.getLogger(SynoTransactions.class);

	private static String identity;

	public static void main(String[] args) {
		boolean gui = true;
		if(args.length > 0) {
			for(String arg : args) {
				if(arg.equalsIgnoreCase("-nogui")) {
					gui = false;
				}
			}
		}
		new SynoTransactions(gui).start();
	}

	private KeyStationManager keyStationManager;

	public SynoTransactions(boolean gui) {
		super(gui, "Syno Transactions");
	}

	public void init() {		
		Config config = new Config();
		try {
			config.init();
			config.save();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.err.println("Unable to load configuration file!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			System.exit(-1);
		}
		String keyStationManagerClass = config.getKeyStationManagerClass();
		if(keyStationManagerClass == null) {
			logger.error("No KeyStationManager Specified!");
			logger.error("Please specify the class for the KeyStationManager and restart the application.");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			System.exit(-1);
		}else {
			try {
				logger.info("Initializing SynoTransactions with identity: " + this.getIdentity());
				logger.info("Constructing KSM: " + keyStationManagerClass);
				Class<KeyStationManager> cls = (Class<KeyStationManager>) Class.forName(keyStationManagerClass);
				Constructor<KeyStationManager> cons = cls.getConstructor(SynoTransactions.class);
				keyStationManager = cons.newInstance(this);
				this.registerService(keyStationManager);
				this.getConsole().registerCommand(new WhoAmICommand(this));
			} catch (ClassNotFoundException | ClassCastException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				logger.error("Unable to initiate specified KeyStationManager: " + e.getMessage());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e2) {}
				System.exit(-1);
			}
		}
	}

	public static String getIdentity() {
		if(identity == null) {
			try {
				identity = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				identity = "";
			}
			if(identity.length() > 0) {
				identity += "-";
			}
			identity += UUID.randomUUID().toString().split("-")[0].toUpperCase();
		}
		return identity;		
	}

	public class Config extends YamlConfig {

		@Comment("Class path to KeyStationManager")
		private @Getter @Setter String KeyStationManagerClass = "";

		public Config() {
			CONFIG_HEADER = new String[]{"SynoTransactions General Configuration Data"};
			CONFIG_FILE = new File("config.yml");
		}

	}

}
