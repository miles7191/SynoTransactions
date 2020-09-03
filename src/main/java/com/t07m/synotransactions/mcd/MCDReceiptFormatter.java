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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.t07m.synotransactions.mcd.MCDTransaction.TransactionType;

public class MCDReceiptFormatter {
	
	private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

	public static String[] format(MCDTransaction trans) {
		List<String> lines = new ArrayList<String>();
		if(trans.getTransactionType() != TransactionType.Order) {
			lines.add(trans.getTransactionType().toString());
		}
		String ks = "";
		if(trans.getKS() != -1) {
			ks = "KS# " + trans.getKS();
		}
		String timeStamp = "";
		if(trans.getTimeStamp() != null) {
			timeStamp = trans.getTimeStamp().format(BOPParser.dateFormat);
			while(ks.length() + timeStamp.length() < 39) {
				ks += " ";
			}
		}
		lines.add(ks + timeStamp);
		String side = "";
		if(trans.getSide() != -1) {
			side = "Side " + trans.getSide();
		}
		String order = "";
		if(trans.getOrderNumber() != -1) {
			order = "Order " + Double.toString(trans.getOrderNumber()).replace(".0", "").replace(".", "/");
			while(side.length() + order.length() < 39) {
				side += " ";
			}
		}
		lines.add(side + order);
		for(String s : trans.getItems()) {
			lines.add(s);
		}
		if(trans.getSubTotal() != -1) {
			String subtotal = "Subtotal";
			String subtotalAmount = decimalFormat.format(trans.getSubTotal());
			String space = "";
			while(subtotal.length() + space.length() + subtotalAmount.length() < 39) {
				space += " ";
			}
			lines.add(subtotal + space + subtotalAmount);
		}
		if(trans.getTax() != -1) {
			String tax = "  Tax";
			String taxAmount = decimalFormat.format(trans.getTax());
			String space = "";
			while(tax.length() + space.length() + taxAmount.length() < 39) {
				space += " ";
			}
			lines.add(tax + space + taxAmount);
		}
		if(trans.getTotal() != -1) {
			String total = "Total";
			String totalAmount = decimalFormat.format(trans.getTotal());
			String space = "";
			while(total.length() + space.length() + totalAmount.length() < 39) {
				space += " ";
			}
			lines.add(total + space + totalAmount);
		}
		if(trans.getCashless() != -1) {
			String cashless = "Cashless";
			String cashlessAmount = decimalFormat.format(trans.getCashless());
			String space = "";
			while(cashless.length() + space.length() + cashlessAmount.length() < 39) {
				space += " ";
			}
			lines.add(cashless + space + cashlessAmount);
		}
		if(trans.getCashTendered() != -1) {
			String cash = "Cash Tendered";
			String cashAmount = decimalFormat.format(trans.getCashTendered());
			String space = "";
			while(cash.length() + space.length() + cashAmount.length() < 39) {
				space += " ";
			}
			lines.add(cash + space + cashAmount);
		}
		if(trans.getChange() != -1) {
			String change = "Change";
			String changeAmount = decimalFormat.format(trans.getChange());
			String space = "";
			while(change.length() + space.length() + changeAmount.length() < 39) {
				space += " ";
			}
			lines.add(change + space + changeAmount);
		}
		if(trans.getCashlessTransactions().length > 0) {
			for(MCDCashlessTransaction ct : trans.getCashlessTransactions()) {
				if(ct.getMER() != -1) {
					lines.add("MER# " + ct.getMER());
				}
				if(ct.getCardIssuer() != null) {
					String issuer = ct.getCardIssuer();
					if(ct.getAccountNumber() != null) {
						issuer += ct.getAccountNumber();
					}
					lines.add(issuer);
				}
				if(ct.getTransactionAmount() != -1) {
					String cttrans = "TRANSACTION AMOUNT";
					String cttransAmount = decimalFormat.format(ct.getTransactionAmount());
					String space = "";
					while(cttrans.length() + space.length() + cttransAmount.length() < 39) {
						space += " ";
					}
					lines.add(cttrans + space + cttransAmount);
				}
				if(ct.getPaymentType() != null) {
					lines.add(ct.getPaymentType().toString());
				}
				if(ct.getAuthorizationCode() != null) {
					lines.add("AUTHORIZATION CODE - " + ct.getAuthorizationCode());
				}
				if(ct.getSEQ() != null) {
					lines.add("SEQ# " + ct.getSEQ());
				}
				if(ct.getAID() != null) {
					lines.add("AID: " + ct.getAID());
				}
			}
		}
		return lines.toArray(new String[lines.size()]);
	}
	
}
