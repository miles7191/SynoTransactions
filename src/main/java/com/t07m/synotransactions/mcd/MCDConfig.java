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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cubespace.Yamler.Config.YamlConfig;

public class MCDConfig extends YamlConfig {

	private @Getter @Setter String SynoIP = "";
	private @Getter @Setter String SynoPort = "";
	private @Getter @Setter String SynoUsername = "";
	private @Getter @Setter String SynoPassword = "";
	private @Getter @Setter KeyStation[] KeyStations = new KeyStation[0];
	
	public MCDConfig() {
		CONFIG_HEADER = new String[]{"SynoTransactions MCD Configuration Data"};
		CONFIG_FILE = new File("mcd-config.yml");
	}
	
	@ToString
	public class KeyStation {
		private @Getter @Setter(AccessLevel.PACKAGE) int ID;
		private @Getter @Setter(AccessLevel.PACKAGE) boolean enabled;
		private @Getter @Setter(AccessLevel.PACKAGE) String IP;
		private @Getter @Setter(AccessLevel.PACKAGE) String domain;
		private @Getter @Setter(AccessLevel.PACKAGE) String username;
		private @Getter @Setter(AccessLevel.PACKAGE) String password;
		private @Getter @Setter(AccessLevel.PACKAGE) String reprintPath;
		private @Getter @Setter(AccessLevel.PACKAGE) String SynologyDeviceName;	
	}
}
