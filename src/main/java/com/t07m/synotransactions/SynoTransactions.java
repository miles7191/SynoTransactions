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

import com.t07m.application.Application;
import com.t07m.swing.console.ConsoleWindow;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.Yamler.Config.YamlConfig;

public class SynoTransactions extends Application{

	public static void main(String[] args) {
		new SynoTransactions();
	}
	
	private @Getter ConsoleWindow console;
	
	private KeyStationManager keyStationManager;
	
	public void init() {
		this.console = new ConsoleWindow("SynoTransactions") {
			public void closeRequested() {
				stop();
			}
		};
		
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
		
		this.console.setup();
		this.console.setLocationRelativeTo(null);
		this.console.setVisible(true);		
		
		String keyStationManagerClass = config.getKeyStationManagerClass();
		if(keyStationManagerClass == null) {
			this.console.log("No KeyStationManager Specified!");
			this.console.log("Please specify the class for the KeyStationManager and restart the application.");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			System.exit(-1);
		}else {
			try {
				Class<KeyStationManager> cls = (Class<KeyStationManager>) Class.forName(keyStationManagerClass);
				Constructor<KeyStationManager> cons = cls.getConstructor(SynoTransactions.class);
				keyStationManager = cons.newInstance(this);
				this.registerService(keyStationManager);
			} catch (ClassNotFoundException | ClassCastException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				this.console.log("Unable to initiate specified KeyStationManager: " + e.getMessage());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e2) {}
				System.exit(-1);
			}
		}
		
	}
	
	public class Config extends YamlConfig {

		@Comment("Class path to KeyStationManager")
		private @Getter @Setter String KeyStationManagerClass;
		
		public Config() {
			CONFIG_HEADER = new String[]{"SynoTransactions General Configuration Data"};
			CONFIG_FILE = new File("config.yml");
		}
		
	}
	
}
