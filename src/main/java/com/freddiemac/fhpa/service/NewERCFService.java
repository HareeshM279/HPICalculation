package com.freddiemac.fhpa.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.freddiemac.fhpa.model.CCAdjOutput;
import com.freddiemac.fhpa.model.HPIPOMonthlyHist;
import com.freddiemac.fhpa.model.LongerHpiExpUsNsa;

public class NewERCFService {

	public void processHPICalculation() {
		List<LongerHpiExpUsNsa> longerHpiExpUsNsaResults = readHpiExpUsNsa();
		List<HPIPOMonthlyHist> monthlyHistResults = readHPIPOMonthlyHistFile();
		
		List<CCAdjOutput> outputList = new ArrayList<>();
		int qtr_no=1;
		String add_qtr="N";
		for (int i = 0; i < longerHpiExpUsNsaResults.size(); i++) {
			LongerHpiExpUsNsa longerHpiExpUsNsaResult = longerHpiExpUsNsaResults.get(i);
			
			if (longerHpiExpUsNsaResult.getPlace().equalsIgnoreCase("USA") && longerHpiExpUsNsaResult.getYear() >= 1991) {
				double lhpi=0.0;
				double index_nsa = longerHpiExpUsNsaResult.getHpi();
				int qtr = longerHpiExpUsNsaResult.getQuarter(); 
				if(i>0) {
					lhpi = longerHpiExpUsNsaResults.get(i-1).getHpi();
				}
				if(longerHpiExpUsNsaResult.getYear() == 1991 && longerHpiExpUsNsaResult.getQuarter() ==1) {
					lhpi = index_nsa;
				}
				double hpi3 = index_nsa;
				double hpi2 = lhpi*(Math.pow((index_nsa/lhpi), (2/3)));
				double hpi1 = lhpi*(Math.pow((index_nsa/lhpi), (1/3)));
				
				double qtr_no1 = (qtr_no -1)+(qtr_no - (qtr_no-1))/3;
				double qtr_no2 = (qtr_no -1)+2*(qtr_no - (qtr_no-1))/3;
				double qtr_no3 = qtr_no;
				
				int month1= 1+3*(qtr-1);
				int month2= 2+3*(qtr-1);
				int month3= 3+3*(qtr-1);
				
				CCAdjOutput mon1 = new CCAdjOutput();
				mon1.setYr(longerHpiExpUsNsaResult.getYear());
				mon1.setCd(longerHpiExpUsNsaResult.getPlace());
				mon1.setAddQtr(add_qtr);
				mon1.setHpiQtr(hpi1);
				mon1.setQtrSeq(qtr_no1);
				mon1.setMonth(month1);
				CCAdjOutput mon2 = new CCAdjOutput();
				mon2.setYr(longerHpiExpUsNsaResult.getYear());
				mon2.setCd(longerHpiExpUsNsaResult.getPlace());
				mon2.setAddQtr(add_qtr);
				mon2.setHpiQtr(hpi2);
				mon2.setQtrSeq(qtr_no2);
				mon2.setMonth(month2);
				CCAdjOutput mon3 = new CCAdjOutput();
				mon3.setYr(longerHpiExpUsNsaResult.getYear());
				mon3.setCd(longerHpiExpUsNsaResult.getPlace());
				mon3.setAddQtr(add_qtr);
				mon3.setHpiQtr(hpi3);
				mon3.setQtrSeq(qtr_no3);
				mon3.setMonth(month3);
				outputList.add(mon1);
				outputList.add(mon2);
				outputList.add(mon3);
			}
			
			
		}
	}
	
	public List<LongerHpiExpUsNsa> readHpiExpUsNsa() {
		List<LongerHpiExpUsNsa> resultMappers = new ArrayList<>();

		// input file
		// /fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/input./longer_HPI_EXP_us_nsa.csv
		try (BufferedReader br = new BufferedReader(
				new FileReader("C:\\Temp\\ERCF\\cc_adj\\202204\\input\\longer_HPI_EXP_us_nsa.csv"))) {
			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count > 4) {
					String[] values = line.split(",");
					LongerHpiExpUsNsa result = new LongerHpiExpUsNsa();
					result.setPlace(values[0]);
					result.setYear(Integer.parseInt(values[1]));
					result.setQuarter(Integer.parseInt(values[2]));
					result.setHpi(Double.parseDouble(values[3]));
					resultMappers.add(result);
				}
				count++;
			}
		} catch (FileNotFoundException e1) {
			System.out.println("LONGER HPI EXP US NSA file not found exception " + e1.getMessage());
		} catch (IOException e1) {
			System.out.println("Exception while reading the LONGER HPI EXP US NSA file" + e1.getMessage());
		}
		return resultMappers;
	}

	public List<HPIPOMonthlyHist> readHPIPOMonthlyHistFile() {
		List<HPIPOMonthlyHist> resultMappers = new ArrayList<>();
		// input file
		// /fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/input./
		try (BufferedReader br = new BufferedReader(
				new FileReader("C:\\Temp\\ERCF\\cc_adj\\202204\\input\\HPI_PO_monthly_hist.csv"))) {
			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (count > 4) {
					String[] values = line.split(",");
					HPIPOMonthlyHist result = new HPIPOMonthlyHist();
					if (values.length > 0) {

						result.setMonth(stringToDate("MM/dd/yyyy", values[0]));
						result.setEastNorthCentralNSA(Double.parseDouble(values[1]));
						result.setEastNorthCentralSA(Double.parseDouble(values[2]));
						result.setEastSouthCentralNSA(Double.parseDouble(values[3]));
						result.setEastSouthCentralSA(Double.parseDouble(values[4]));
						result.setMiddleAtlanticNSA(Double.parseDouble(values[5]));
						result.setMiddleAtlanticSA(Double.parseDouble(values[6]));
						result.setMountainNSA(Double.parseDouble(values[7]));
						result.setMountainSA(Double.parseDouble(values[8]));
						result.setNewEnglandNSA(Double.parseDouble(values[9]));
						result.setNewEnglandSA(Double.parseDouble(values[10]));
						result.setPacificNSA(Double.parseDouble(values[11]));
						result.setPacificSA(Double.parseDouble(values[12]));
						result.setSouthAtlanticNSA(Double.parseDouble(values[13]));
						result.setSouthAtlanticSA(Double.parseDouble(values[14]));
						result.setWestNorthCentralNSA(Double.parseDouble(values[15]));
						result.setWestNorthCentralSA(Double.parseDouble(values[16]));
						result.setWestSouthCentralNSA(Double.parseDouble(values[17]));
						result.setWestSouthCentralSA(Double.parseDouble(values[18]));
						result.setUsaNSA(Double.parseDouble(values[19]));
						result.setUsaSA(Double.parseDouble(values[20]));
						resultMappers.add(result);
					}
				}
				count++;
			}
		} catch (FileNotFoundException e1) {
			System.out.println("HPI Po Monthly Hist file not found exception " + e1.getMessage());
		} catch (IOException e1) {
			System.out.println("Exception while reading the HPI Po Monthly Hist File " + e1.getMessage());
		}
		return resultMappers;
	}
	
	private Date stringToDate(String format, String dateString) {
		DateFormat df = new SimpleDateFormat(format);
		Date date = new Date();
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
}
