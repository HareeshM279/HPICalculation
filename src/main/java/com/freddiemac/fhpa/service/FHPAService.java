package com.freddiemac.fhpa.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.freddiemac.fhpa.model.CCAdjInput;
import com.freddiemac.fhpa.model.CUURSAL;
import com.freddiemac.fhpa.model.HPIPOMonthlyHist;
import com.freddiemac.fhpa.model.LongerHpiExpUsNsa;

public class FHPAService {

	private int qtr_no;
	private String add_qtr;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

	private LocalDate lamaDate = LocalDate.now();
	private LocalDate hpiQtr = LocalDate.now(); // constant value ? 06/30/2021 // hpi end date
	private int month = 9;
	private List<Integer> quarters = Arrays.asList(3, 6, 9);
	private LocalDate hpi_mo;

	public void process() {
		System.out.println("Process Started");
		List<LongerHpiExpUsNsa> resultMapper = prepareTheData();
		for (int i = 0; i < resultMapper.size(); i++) {
			LongerHpiExpUsNsa result = resultMapper.get(i);
			int cutOffMonth = hpiQtr.getMonthValue();
			int quarter = result.getQuarter();
			int year = result.getYear();
			if (result.getPlace().equalsIgnoreCase("USA") && result.getYear() >= 1991) {
				qtr_no = 0;
				add_qtr = "N";
			}
			if (month != 3 || month != 6 || month != 9 || month != 12) {
				if (quarters.contains(cutOffMonth)) {
					quarter = quarter + 1;
					qtr_no = qtr_no + 1;
				} else if (cutOffMonth == 12) {
					quarter = result.getQuarter() + 1;
					qtr_no = qtr_no + 1;
					year = year + 1;
				}
				add_qtr = "Y";
			}
			double lhpi = 1;
			double hpi3;
			double hpi2;
			double hpi1;

			double qtr_no1;
			double qtr_no2;
			double qtr_no3;

			double lhpi1_qtr;
			double lhpi2_qtr;
			if (i > 0) {
				lhpi = resultMapper.get(i - 1).getHpi();
				lhpi1_qtr = resultMapper.get(i - 1).getQuarter();
			}

			if (i > 1) {
				lhpi2_qtr = resultMapper.get(i - 2).getQuarter();
			}
			if (result.getYear() == 1991 && quarter == 1) {
				hpi3 = result.getHpi();
				hpi2 = lhpi * ((i / lhpi) * (2 / 3));
				hpi1 = lhpi * ((i / lhpi) * (1 / 3));

				qtr_no1 = (qtr_no - 1) + (qtr_no - (qtr_no - 1)) / 3;
				qtr_no2 = (qtr_no - 1) + 2 * (qtr_no - (qtr_no - 1)) / 3;
				qtr_no3 = qtr_no;
			}

			int month1 = 1 + 3 * (quarter - 1);
			int month2 = 2 + 3 * (quarter - 1);
			int month3 = 3 + 3 * (quarter - 1);

			if (result.getYear() == 1991 && month == 1) {
				lhpi1_qtr = hpiQtr.getMonthValue();
			}

			if (result.getYear() == 1991 && (month == 1 || month == 2)) {
				lhpi2_qtr = hpiQtr.getMonthValue();
			}

			List<HPIPOMonthlyHist> montlyHistResults = processHPIPOMonthlyHistFile();
			calculateGrowthRate(montlyHistResults);

		}
	}

	public List<LongerHpiExpUsNsa> prepareTheData() {
		List<LongerHpiExpUsNsa> resultMappers = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(
				"/fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/input./longer_HPI_EXP_us_nsa.csv"))) {
			String line;
			int count=0;
			while ((line = br.readLine()) != null) {
				if(count > 4) {
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

	public List<HPIPOMonthlyHist> processHPIPOMonthlyHistFile() {
		List<HPIPOMonthlyHist> resultMappers = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(
				"/fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/input./HPI_PO_monthly_hist.csv"))) {
			String line;
			int count=0;
			while ((line = br.readLine()) != null) {
				if(count > 4) {
					String[] values = line.split(",");
					HPIPOMonthlyHist result = new HPIPOMonthlyHist();
					result.setMonth(LocalDate.parse(values[0]));
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
				count++;
			}
		} catch (FileNotFoundException e1) {
			System.out.println("HPI Po Monthly Hist file not found exception " + e1.getMessage());
		} catch (IOException e1) {
			System.out.println("Exception while reading the HPI Po Monthly Hist File "+e1.getMessage());
		}
		return resultMappers;
	}

	public void calculateGrowthRate(List<HPIPOMonthlyHist> hpiPoMonthlyHistList) {
		String lamaMonth = "";
		for (int i = 0; i < hpiPoMonthlyHistList.size(); i++) {
			HPIPOMonthlyHist monthlyHist = hpiPoMonthlyHistList.get(i);
			LocalDate mon = monthlyHist.getMonth();

			String startDateString = "01Jan1991";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMyyyy", Locale.ENGLISH);
			LocalDate startDate = LocalDate.parse(startDateString, formatter);

			String endDateString = "01Mar1991";
			LocalDate endDate = LocalDate.parse(endDateString, formatter);
			double lhpi1 = 0.0;
			double lhpi2 = 0.0;
			if (mon.compareTo(startDate) == 0) {
				lhpi1 = monthlyHist.getUsaNSA();
			}
			if (mon.compareTo(endDate) < 0) {
				lhpi2 = monthlyHist.getUsaNSA();
			}

			double gr_rt_mo1 = monthlyHist.getUsaNSA() / lhpi1;
			double gr_rt_mo2 = monthlyHist.getUsaNSA() / lhpi2;

			int year = mon.getYear();
			int month = mon.getYear();
			int coutOffMonth = lamaDate.getMonthValue();
			double hpi_mo;
			double lhpi1_qtr = 1;
			double lhpi2_qtr = 1;
			if (Arrays.asList(3, 6, 9, 12).contains(coutOffMonth)) {
				hpi_mo = hpiQtr.getMonthValue();
			} else {
				if (Arrays.asList(3, 6, 9, 12).contains(month)) {
					hpi_mo = hpiQtr.getMonthValue();
				} else if (Arrays.asList(1, 4, 7, 10).contains(month)) {
					hpi_mo = lhpi1_qtr * gr_rt_mo1;
				} else if (Arrays.asList(2, 5, 8, 11).contains(month)) {
					hpi_mo = lhpi2_qtr * gr_rt_mo2;
				}
			}

			LocalDate valMonth = mon.plusMonths(3);
			int valMo = valMonth.getMonthValue();
			int valYr = valMonth.getYear();
			lamaMonth = valYr + "" + valMo;

		}
		List<CUURSAL> cuurSalList = processCUURData();

		for (int i = 0; i < cuurSalList.size(); i++) {
			CUURSAL cuurSal = cuurSalList.get(i);
			LocalDate cpiDate = cuurSal.getObservationDate();

			String startDateString = "01Mar1975";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMyyyy", Locale.ENGLISH);
			LocalDate startDate = LocalDate.parse(startDateString, formatter);
//    		
//    		String endDateString = "01Mar1991";
//    		LocalDate endDate = LocalDate.parse(endDateString, formatter);
			double cpiValue = cuurSal.getCUUR0000SA0L2();
			double rollAvg;
			if (cpiDate.compareTo(startDate) >= 0) {
				if (i > 2)
					rollAvg = (cuurSalList.get(i).getCUUR0000SA0L2() + cuurSalList.get(i - 1).getCUUR0000SA0L2()
							+ cuurSalList.get(i - 2).getCUUR0000SA0L2()) / 3;
				else
					rollAvg = cuurSal.getCUUR0000SA0L2();
			} else {
				rollAvg = cpiValue;
			}
			LocalDate hpi;
			if (add_qtr.equalsIgnoreCase("N")) {
				hpi = hpiQtr;
			} else {
				hpi = hpi_mo;
			}

			double dsfHpi = hpi.getMonthValue() / rollAvg;
			double qtr_seq = 1;// doubt
			double lrsfHpiTrend = 0.66112295 * Math.exp(0.002619948 * qtr_seq);
			double adjPct = (dsfHpi / lrsfHpiTrend) - 1;
			double ltvAdjPct;
			double ccAdj;
			if (adjPct > 0.5) {
				ltvAdjPct = 1 / (1 + ((1.05 * lrsfHpiTrend / dsfHpi) - 1));
				ccAdj = 1.05 * lrsfHpiTrend / dsfHpi - 1;
			} else if (adjPct < -0.5) {
				ltvAdjPct = 1 / (1 + ((0.95 * lrsfHpiTrend / dsfHpi) - 1));
				ccAdj = 0.955 * lrsfHpiTrend / dsfHpi - 1;
			} else {
				ltvAdjPct = 1;
				ccAdj = 0;
			}
		}
		processCCAdjInputFile();
	}

	public List<CUURSAL> processCUURData() {
		List<CUURSAL> resultMappers = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(
				"/fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/input./CUUR0000SA0L2.csv"))) {
			String line;
			int count=0;
			while ((line = br.readLine()) != null) {
				if(count >10) {
					String[] values = line.split(",");
					CUURSAL result = new CUURSAL();
					result.setObservationDate(LocalDate.parse(values[0]));
					result.setCUUR0000SA0L2(Double.parseDouble(values[1]));
					resultMappers.add(result);
				}
				count++;
			}
		} catch (FileNotFoundException e1) {
			System.out.println("CUUR0000SA0L2 file not found exception " + e1.getMessage());
		} catch (IOException e1) {

		}

		return resultMappers;
	}

	private void processCCAdjInputFile() {
		List<CCAdjInput> resultMappers = new ArrayList<>();
		StringBuffer builder = new StringBuffer();
		List<String[]> dataLines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(
				"/fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/input./cc_adj_input.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				CCAdjInput result = new CCAdjInput();
				result.setDate(values[0]);
				result.setAdjustmentFactor(Double.parseDouble(values[1]));
				builder.append(result.getDate() + "/t" + result.getAdjustmentFactor() + "/n");
				dataLines.add(new String[] { values[0], values[1] });
				resultMappers.add(result);
			}
		} catch (FileNotFoundException e1) {
			System.out.println("CUUR0000SA0L2 file not found exception " + e1.getMessage());
		} catch (IOException e1) {

		}
		mtmltvCounterCylinderFactor(builder);
		givenDataArray_whenConvertToCSV_thenOutputCreated(dataLines, "/fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/output/cc_adj_202109.csv");
		givenDataArray_whenConvertToCSV_thenOutputCreated(dataLines, "/fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/output/cc_adj_input.csv");
	}

	private void mtmltvCounterCylinderFactor(StringBuffer output) {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(
				"/fmacdata/utility/carrac/euc_dev/ccf/final_rule/assumptions/cc_adj/202109/output/MTMLTV_Counter_Cyclical_Factor.txt"))) {
			out.write(output.toString());
		} catch (IOException e) {
			System.out.println("Exception ");
		}
	}

	public void givenDataArray_whenConvertToCSV_thenOutputCreated(List<String[]> dataLines, String CSV_FILE_NAME) {
		File csvOutputFile = new File(CSV_FILE_NAME);
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			dataLines.stream().map(this::convertToCSV).forEach(pw::println);
		}catch (FileNotFoundException e) {
			System.out.println("Exception while creating the CSV File "+e.getMessage());
		}
	}

	public String convertToCSV(String[] data) {
		return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining(","));
	}

	public String escapeSpecialCharacters(String data) {
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}
}
