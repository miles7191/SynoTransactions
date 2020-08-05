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

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Transaction {

	public enum TransactionType{
		Order(null), 
		Promo("**** PROMO ****"), 
		Refund("**** REFUND ****"), 
		Activation("ACTIVATION RECEIPT"), 
		BillableSale("** BILLABLE SALE **"), 
		Overring("**** OVERRING ****"), 
		ManagerMeal("Manager meal discount"), 
		EmployeeMeal("Employee meal discount");
		
		private final @Getter String marker;

		TransactionType(String marker){
			this.marker = marker;
		}
	};

	private @Getter @Setter(AccessLevel.PACKAGE) TransactionType transactionType;
	private @Getter @Setter(AccessLevel.PACKAGE) int KS;
	private @Getter @Setter(AccessLevel.PACKAGE) LocalDateTime timeStamp;
	private @Getter @Setter(AccessLevel.PACKAGE) int side;
	private @Getter @Setter(AccessLevel.PACKAGE) double order;
	private @Getter @Setter(AccessLevel.PACKAGE) String[] items;
	private @Getter @Setter(AccessLevel.PACKAGE) double subTotal;
	private @Getter @Setter(AccessLevel.PACKAGE) double tax;
	private @Getter @Setter(AccessLevel.PACKAGE) double total;
	private @Getter @Setter(AccessLevel.PACKAGE) double giftCard;
	private @Getter @Setter(AccessLevel.PACKAGE) double cashTendered;
	private @Getter @Setter(AccessLevel.PACKAGE) double cashless;
	private @Getter @Setter(AccessLevel.PACKAGE) double change;
	private @Getter @Setter(AccessLevel.PACKAGE) CashlessTransaction[] cashlessTransactions;
	private @Getter @Setter(AccessLevel.PACKAGE) String[] raw;
}
