package com.freddiemac.fhpa.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

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
				
				double hpi_mo3=0.0;
				double hpi_mo2=0.0;
				double hpi_mo1=0.0;
				
				String month1String = month1<10?"0"+month1:month1 +"/01"+"/"+longerHpiExpUsNsaResult.getYear();
				Date date1 = stringToDate("MM/dd/yyyy", month1String);
				double lhpi1=0.0;
				double lhpi2=0.0;
				int j = getIndexByProperty(monthlyHistResults,date1);
				if(j==0 || j == 1) {
					lhpi1 = monthlyHistResults.get(j).getUsaNSA();
					lhpi2 = monthlyHistResults.get(j).getUsaNSA();
				}else if(j >= 2) {
					lhpi1 = monthlyHistResults.get(j-1).getUsaNSA();
					lhpi2 = monthlyHistResults.get(j-2).getUsaNSA();
				}
				double gr_rt_mo1 = monthlyHistResults.get(j).getUsaNSA()/lhpi1;
				double gr_rt_mo2 = monthlyHistResults.get(j).getUsaNSA()/lhpi2;
				double lhpi1_qtr=0.0;
				double lhpi2_qtr=0.0;
				if(month1 == 1) {
					lhpi1_qtr = hpi1;
					lhpi2_qtr = hpi2;
				}else {
					lhpi1_qtr = hpi3;
					lhpi2_qtr = hpi2;
				}
				
				if(Arrays.asList(3, 6, 9, 12).contains(month3)) {
					hpi_mo3 = hpi3;
				}else {
					hpi_mo2 = lhpi2_qtr * gr_rt_mo2;
					hpi_mo1 = lhpi1_qtr * gr_rt_mo1;
				}
				String month2String = month2<10?"0"+month2:month2 +"/01"+"/"+longerHpiExpUsNsaResult.getYear();
				Date date2 = stringToDate("MM/dd/yyyy", month2String);
				String month3String = month3<10?"0"+month3:month3 +"/01"+"/"+longerHpiExpUsNsaResult.getYear();
				Date date3 = stringToDate("MM/dd/yyyy", month3String);
				
				Date valMon1 = monthlyHistResults.get(j).getMonth();
				valMon1.setDate(valMon1.getDate()+3);
				
				String lamaMonth1 = valMon1.getYear()+""+(valMon1.getMonth());
				valMon1.setDate(valMon1.getDate()+1);
				String lamaMonth2 = valMon1.getYear()+""+(valMon1.getMonth());
				valMon1.setDate(valMon1.getDate()+1);
				String lamaMonth3 = valMon1.getYear()+""+(valMon1.getMonth());
				CCAdjOutput mon1 = new CCAdjOutput();
				mon1.setYr(longerHpiExpUsNsaResult.getYear());
				mon1.setCd(longerHpiExpUsNsaResult.getPlace());
				mon1.setAddQtr(add_qtr);
				mon1.setHpiQtr(hpi1);
				mon1.setQtrSeq(qtr_no1);
				mon1.setMonth(month1);
				mon1.setHpiMo(hpi_mo1);
				mon1.setMon(date1);
				mon1.setLamaMonth(lamaMonth1);
				CCAdjOutput mon2 = new CCAdjOutput();
				mon2.setYr(longerHpiExpUsNsaResult.getYear());
				mon2.setCd(longerHpiExpUsNsaResult.getPlace());
				mon2.setAddQtr(add_qtr);
				mon2.setHpiQtr(hpi2);
				mon2.setQtrSeq(qtr_no2);
				mon2.setMonth(month2);
				mon2.setHpiMo(hpi_mo2);
				mon2.setMon(date2);
				mon2.setLamaMonth(lamaMonth2);
				CCAdjOutput mon3 = new CCAdjOutput();
				mon3.setYr(longerHpiExpUsNsaResult.getYear());
				mon3.setCd(longerHpiExpUsNsaResult.getPlace());
				mon3.setAddQtr(add_qtr);
				mon3.setHpiQtr(hpi3);
				mon3.setQtrSeq(qtr_no3);
				mon3.setMonth(month3);
				mon3.setHpiMo(hpi_mo3);
				mon3.setMon(date3);
				mon3.setLamaMonth(lamaMonth3);
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
	
	private static int getIndexByProperty(List<HPIPOMonthlyHist> hpiMonthlyHistResults, Date targetDate) {
        OptionalInt optionalIndex = IntStream.range(0, hpiMonthlyHistResults.size())
                .filter(i -> hpiMonthlyHistResults.get(i).getMonth().equals(targetDate))
                .findFirst();

        return optionalIndex.orElse(-1);
    }
}
