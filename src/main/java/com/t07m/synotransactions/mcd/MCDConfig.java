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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.cubespace.Yamler.Config.YamlConfig;

public class MCDConfig extends YamlConfig {

	private @Getter @Setter String SynoIP = "";
	private @Getter @Setter int SynoPort = 5000;
	private @Getter @Setter boolean UseSSL = false;
	private @Getter @Setter String DSName = "";
	private @Getter @Setter String SynoUsername = "";
	private @Getter @Setter String SynoPassword = "";
	private @Getter @Setter MCDKeyStationConfig[] KeyStations = new MCDKeyStationConfig[] {
			new MCDKeyStationConfig(0, false, "127.0.0.1", "", "", "", "", "")
	};
	
	public MCDConfig() {
		CONFIG_HEADER = new String[]{"SynoTransactions MCD Configuration Data"};
		CONFIG_FILE = new File("mcd-config.yml");
	}
	
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public class MCDKeyStationConfig extends YamlConfig{
		private @Getter @Setter int ID;
		private @Getter @Setter boolean Enabled;
		private @Getter @Setter String IP;
		private @Getter @Setter String Domain;
		private @Getter @Setter String Username;
		private @Getter @Setter String Password;
		private @Getter @Setter String ReprintPath;
		private @Getter @Setter String SynologyDeviceName;	
	}
}
