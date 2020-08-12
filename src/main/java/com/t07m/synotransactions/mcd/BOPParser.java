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
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.t07m.synotransactions.mcd.MCDCashlessTransaction.PaymentType;
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
			removeEmptyLines(lines);
			transaction.setTransactionType(parseTransactionType(lines));
			transaction.setHeader(parseHeader(lines));
			transaction.setKS(parseKS(lines));
			transaction.setTimeStamp(parseTimeStamp(lines));
			transaction.setSide(parseSide(lines));
			transaction.setOrderNumber(parseOrderNumber(lines));
			transaction.setItems(parseItems(lines));
			transaction.setSubTotal(parseSubtotal(lines));
			transaction.setTax(parseTax(lines));
			transaction.setTotal(parseTotal(lines));
			transaction.setCashTendered(parseCashTendered(lines));
			transaction.setChange(parseChange(lines));
			transaction.setCashless(parseCashless(lines));
			transaction.setTotalSavings(parseTotalSavings(lines));
			transaction.setCashlessTransactions(parseCashlessTransactions(lines));
			transaction.setFooter(lines.toArray(new String[lines.size()]));			
			return transaction;
		}
		return null;
	}

	private void removeTags(List<String> lines) {
		for(int i = 0; i < lines.size(); i++) {
			lines.set(i, lines.get(i).replaceAll("<(\"*\"|[^>])*>", ""));
		}
	}

	private void removeEmptyLines(List<String> lines) {
		Iterator<String> itr = lines.iterator();
		while(itr.hasNext()) {
			String line = itr.next();
			if(line == null || StringUtils.countMatches(line, " ") == line.length()) {
				itr.remove();
			}
		}
	}

	private TransactionType parseTransactionType(List<String> lines) {
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
			return TransactionType.defaultType();
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

	private String[] parseHeader(List<String> lines) {
		int telIndex = indexOfContaining(lines, 0, line -> line.contains("TEL#"));
		List<String> header = new ArrayList<String>();
		Iterator<String> itr = lines.iterator();
		for(int i = 0; i < telIndex+1; i++) {
			if(itr.hasNext()) {
				String line = itr.next();
				if(line.length() == 39) {
					header.add(line);
				}
				itr.remove();
			}
		}
		return header.toArray(new String[header.size()]);
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

	private int parseSide(List<String> lines) {
		int sideIndex = indexOfContaining(lines, 0, line -> line.contains("Side"));
		if(sideIndex != -1) {
			try {
				String line = lines.get(sideIndex);
				int side = Integer.parseInt(lines.get(sideIndex).substring(4,5).replace(" ", ""));
				line = line.substring(6, line.length());
				lines.set(sideIndex, line);
				return side;
			}catch(NumberFormatException e) {}
		}
		return -1;
	}

	private double parseOrderNumber(List<String> lines) {
		int orderIndex = indexOfContaining(lines, 0, line -> line.contains("Order"));
		if(orderIndex != -1) {
			try {
				String line = lines.get(orderIndex);
				int orderPosistion = line.indexOf("Order");
				double orderNumber = Double.parseDouble(lines.get(orderIndex).substring(orderPosistion, line.length()).replace("/", ".").replace("Order", ""));
				line = line.substring(0, orderPosistion);
				if(StringUtils.countMatches(line, ' ') == line.length()) {
					lines.remove(orderIndex);
				}else {
					lines.set(orderIndex, line);
				}
				return orderNumber;
			}catch(NumberFormatException e) {}
		}
		return -1;
	}

	private String[] parseItems(List<String> lines) {
		int subtotalIndex = indexOfContaining(lines, 0, line -> line.contains("Subtotal"));
		List<String> items = new ArrayList<String>();
		if(subtotalIndex != -1) {
			Iterator<String> itr = lines.iterator();
			for(int i = 0; i < subtotalIndex; i++) {
				if(itr.hasNext()) {
					items.add(itr.next());
					itr.remove();
				}
			}
		}
		return items.toArray(new String[items.size()]);
	}

	private double parseSubtotal(List<String> lines) {
		int subtotalIndex = indexOfContaining(lines, 0, line -> line.contains("Subtotal"));
		if(subtotalIndex != -1) {
			try {
				double subtotal = Double.parseDouble(lines.get(subtotalIndex).replace("Subtotal", ""));
				lines.remove(subtotalIndex);
				return subtotal;
			} catch (NumberFormatException e) {}
		}
		return -1;
	}

	private double parseTax(List<String> lines) {
		int taxIndex = indexOfContaining(lines, 0, line -> line.contains("Tax"));
		if(taxIndex != -1) {
			try {
				double tax = Double.parseDouble(lines.get(taxIndex).replace("Tax", ""));
				lines.remove(taxIndex);
				return tax;
			} catch (NumberFormatException e) {}
		}
		return -1;
	}

	private double parseTotal(List<String> lines) {
		int totalIndex = indexOfContaining(lines, 0, line -> line.contains("Total") && !line.contains("Savings"));
		if(totalIndex != -1) {
			try {
				String line = lines.get(totalIndex);
				int totalPosition = line.indexOf("Total");
				double total = Double.parseDouble(lines.get(totalIndex).substring(totalPosition + 5));
				lines.remove(totalIndex);
				return total;
			} catch (NumberFormatException e) {}
		}
		return -1;
	}

	private double parseCashTendered(List<String> lines) {
		int cashIndex = indexOfContaining(lines, 0, line -> line.contains("Cash Tendered"));
		if(cashIndex != -1) {
			try {
				double cash = Double.parseDouble(lines.get(cashIndex).replace("Cash Tendered", ""));
				lines.remove(cashIndex);
				return cash;
			} catch (NumberFormatException e) {}
		}
		return -1;
	}

	private double parseChange(List<String> lines) {
		int changeIndex = indexOfContaining(lines, 0, line -> line.contains("Change"));
		if(changeIndex != -1) {
			try {
				double change = Double.parseDouble(lines.get(changeIndex).replace("Change", ""));
				lines.remove(changeIndex);
				return change;
			} catch (NumberFormatException e) {}
		}
		return -1;
	}

	private double parseCashless(List<String> lines) {
		int cashlessIndex = indexOfContaining(lines, 0, line -> line.contains("Cashless"));
		if(cashlessIndex != -1) {
			try {
				double cashless = Double.parseDouble(lines.get(cashlessIndex).replace("Cashless", ""));
				lines.remove(cashlessIndex);
				return cashless;
			} catch (NumberFormatException e) {}
		}
		return -1;
	}

	private double parseTotalSavings(List<String> lines) {
		int totalSavingsIndex = indexOfContaining(lines, 0, line -> line.contains("Total Savings"));
		if(totalSavingsIndex != -1) {
			try {
				double totalSavings = Double.parseDouble(lines.get(totalSavingsIndex).replace("Total Savings", ""));
				lines.remove(totalSavingsIndex);
				return totalSavings;
			} catch (NumberFormatException e) {}
		}
		return -1;
	}

	private MCDCashlessTransaction[] parseCashlessTransactions(List<String> lines) {
		List<MCDCashlessTransaction> transactions = new ArrayList<MCDCashlessTransaction>();
		int merIndex = -1;
		while((merIndex = indexOfContaining(lines, 0, line -> line.contains("MER#"))) != -1) {
			MCDCashlessTransaction transaction = new MCDCashlessTransaction();
			transaction.setMER(parseCashlessMER(lines, merIndex));
			transaction.setCardIssuer(parseCashlessCardIssuer(lines, merIndex+1));
			transaction.setAccountNumber(parseCashlessAccountNumber(lines, merIndex+1));
			transaction.setTransactionAmount(parseCashlessTransactionAmount(lines, merIndex+1));
			transaction.setAuthorizationCode(parseCashlessAuthorizationCode(lines, merIndex+1));
			transaction.setSEQ(parseCashlessSEQNumber(lines, merIndex+1));
			transaction.setAID(parseCashlessAID(lines, merIndex+1));
			transaction.setPaymentType(parseCashlessPaymentType(lines, merIndex+1));
			lines.remove(merIndex);
			transactions.add(transaction);
		}
		return transactions.toArray(new MCDCashlessTransaction[transactions.size()]);
	}

	private int parseCashlessMER(List<String> lines, int merIndex) {
		if(merIndex != -1) {
			try {
			return Integer.parseInt(lines.get(merIndex).replace("MER# ", ""));
			}catch (NumberFormatException e) {}
		}
		return -1;		
	}
	
	private String parseCashlessCardIssuer(List<String> lines, int start) {
		int issuerIndex = indexOfContaining(lines, start, line -> line.contains("CARD ISSUER"), line ->line.contains("MER#"));
		if(issuerIndex != -1) {
			String line = lines.get(issuerIndex + 1);
			String issuer = null;
			if(line.contains("SALE")) {
				issuer = line.substring(0, line.indexOf("SALE") - 1);
				line = line.substring(line.indexOf("SALE") + 4);
			}else if(line.contains("REFUND")) {
				issuer = line.substring(0, line.indexOf("REFUND") - 1);
				line = line.substring(line.indexOf("REFUND") + 6);
			}else if(line.contains("REVERSAL")) {
				issuer = line.substring(0, line.indexOf("REVERSAL") - 1);
				line = line.substring(line.indexOf("REVERSAL") + 7);
			}
			if(issuer != null) {
				lines.set(issuerIndex + 1, line);
				lines.remove(issuerIndex);
				return issuer;
			}
		}
		return null;
	}

	private String parseCashlessAccountNumber(List<String> lines, int start) {
		int cardNumberIndex = indexOfContaining(lines, start, line -> line.contains("****"), line -> line.contains("MER#"));
		if(cardNumberIndex != -1) {
			String cardNumber = lines.get(cardNumberIndex).replace(" ", "");
			lines.remove(cardNumberIndex);
			return cardNumber;
		}
		return null;
	}

	private double parseCashlessTransactionAmount(List<String> lines, int start) {
		int transactionAmountndex = indexOfContaining(lines, start, line -> line.contains("TRANSACTION AMOUNT"), line -> line.contains("MER#"));
		if(transactionAmountndex != -1) {
			try {
				double transactionAmount = Double.parseDouble(lines.get(transactionAmountndex).replace("TRANSACTION AMOUNT", ""));
				lines.remove(transactionAmountndex);
				return transactionAmount;
			}catch (NumberFormatException e) {}
		}
		return -1;
	}
	
	private String parseCashlessAuthorizationCode(List<String> lines, int start) {
		int authorizationCodeIndex = indexOfContaining(lines, start, line -> line.contains("AUTHORIZATION CODE - "), line -> line.contains("MER#"));
		if(authorizationCodeIndex != -1) {
			String authorizationCode = lines.get(authorizationCodeIndex).replace("AUTHORIZATION CODE - ", "");
			lines.remove(authorizationCodeIndex);
			return authorizationCode;
		}
		return null;
	}
	
	private PaymentType parseCashlessPaymentType(List<String> lines, int start) {
		ArrayList<String> markers = new ArrayList<String>();
		for(PaymentType tt : PaymentType.values()) {
			if(tt.getMarker() != null) {
				markers.add(tt.getMarker());
			}
		}
		int typeIndex = indexOfContaining(lines, start, new LineFilter() {
			public boolean accept(String line) {
				for(String marker : markers.toArray(new String[markers.size()])) {
					if(line.contains(marker))
						return true;
				}
				return false;
			}
		}, line -> line.contains("MER#"));
		if(typeIndex == -1)
			return PaymentType.defaultType();
		String line = lines.get(typeIndex);
		for(PaymentType tt : PaymentType.values()) {
			if(tt.getMarker() != null) {
				if(line.contains(tt.getMarker())) {
					lines.remove(typeIndex);
					return tt;
				}
			}
		}
		return null;
	}

	private String parseCashlessSEQNumber(List<String> lines, int start) {
		int seqNumberIndex = indexOfContaining(lines, start, line -> line.contains("SEQ# "), line -> line.contains("MER#"));
		if(seqNumberIndex != -1) {
			String seqNumber = lines.get(seqNumberIndex).replace("SEQ# ", "");
			lines.remove(seqNumberIndex);
			return seqNumber;
		}
		return null;
	}
	
	private String parseCashlessAID(List<String> lines, int start) {
		int aidIndex = indexOfContaining(lines, start, line -> line.contains("AID: "), line -> line.contains("MER#"));
		if(aidIndex != -1) {
			String aid = lines.get(aidIndex).replace("AID: ", "");
			lines.remove(aidIndex);
			return aid;
		}
		return null;
	}
	
	private int indexOfContaining(List<String> list, int startIndex, LineFilter filter) {
		return indexOfContaining(list, startIndex, filter, line -> false);
	}

	private int indexOfContaining(List<String> list, int startIndex, LineFilter filter, LineFilter abortFilter) {
		if (list == null) {
			return -1;
		}
		if (startIndex < 0) {
			startIndex = 0;
		}
		for(int i = startIndex; i < list.size(); i++) {
			String line = list.get(i);
			if(abortFilter == null && line == null) {
				return -1;
			}else if(abortFilter.accept(line)) {
				return -1;
			}else if(filter == null && line == null) {
				return i;
			}else if(filter.accept(line)) {
				return i;
			}
		}
		return -1;
	}

	interface LineFilter{

		public boolean accept(String line);

	}
}
