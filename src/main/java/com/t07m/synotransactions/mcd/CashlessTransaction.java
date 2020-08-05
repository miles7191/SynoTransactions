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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class CashlessTransaction {

	private @Getter @Setter(AccessLevel.PACKAGE) String authorizationCode;
	private @Getter @Setter(AccessLevel.PACKAGE) String AID;
	private @Getter @Setter(AccessLevel.PACKAGE) String SEQ;
	private @Getter @Setter(AccessLevel.PACKAGE) boolean swipe;
	private @Getter @Setter(AccessLevel.PACKAGE) boolean chipRead;
	private @Getter @Setter(AccessLevel.PACKAGE) double transactionAmount;
	private @Getter @Setter(AccessLevel.PACKAGE) String accountNumber;
	private @Getter @Setter(AccessLevel.PACKAGE) String cardIssuer;
	private @Getter @Setter(AccessLevel.PACKAGE) String MER;

	CashlessTransaction(){
		
	}	
}
