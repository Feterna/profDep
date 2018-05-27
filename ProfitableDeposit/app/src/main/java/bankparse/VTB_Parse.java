package bankparse;

import android.content.Context;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class VTB_Parse
{
    public ArrayList<String> depNames;
    public ArrayList<Deposit> VTBdeps;

    public VTB_Parse()
    {
        depNames = new ArrayList<String>();
        VTBdeps = new ArrayList<Deposit>();
    }

    public void VTBParse(URL url, Context context, long days, int sumToDep)
    {
        try
        {
            Document doc = Jsoup.connect(url.toString()).get();
            Elements names = doc.getElementsByClass("media-slider__nav-item");
            for (int i = 0; i < names.size(); i++)
                depNames.add(names.eq(i).text());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

            String result = "";
            InputStream inputStream = null;
            XSSFWorkbook workBook = null;
            Deposit withDescr;
            BMaths bm = new BMaths();

            for (int i = 0; i < depNames.size(); i++)
            {
               try
               {
                   inputStream = context.getAssets().open("ВТБ/" + depNames.get(i) + ".xlsx");
                   workBook = new XSSFWorkbook(inputStream);

                   for (int l = 0; l < workBook.getNumberOfSheets(); l++)
                   {
                       Sheet sheet = workBook.getSheetAt(l);
                       withDescr = new Deposit(depNames.get(i));
                       withDescr.initialSum = 30000;
                       withDescr.bankName = "ВТБ";
                       withDescr.description = sheet.getSheetName();

                       int rowNum = sheet.getLastRowNum();
                       Row row;
                       Cell cell;
                       String tempSum;
                       int startSum = 0;
                       int endSum = Integer.MAX_VALUE;
                       int startDay = 0;
                       int endDay = 0;
                       int numberOfRow = 0;
                       int numberOfCol = 0;
                       double tempMeaningofCell;
                       int tempMeaningfoIntCell;
                       String tempBet;

                       for (int j = 1; j <= rowNum; j++) {
                           row = sheet.getRow(j);
                           cell = row.getCell(0);

                           if (cell != null) {
                               if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                   tempSum = cell.getStringCellValue();
                               else {
                                   tempMeaningofCell = cell.getNumericCellValue();
                                   tempMeaningfoIntCell = (int) tempMeaningofCell;
                                   tempSum = Integer.toString(tempMeaningfoIntCell);
                               }

                               if (tempSum.contains("-")) {
                                   startSum = Integer.parseInt(bm.stripNonDigits(tempSum.substring(0, tempSum.indexOf("-"))));
                                   endSum = Integer.parseInt(bm.stripNonDigits(tempSum.substring(tempSum.indexOf("-") + 1)));
                               }

                               if (tempSum.contains(">")) {
                                   startSum = Integer.parseInt(bm.stripNonDigits(tempSum.substring(0, tempSum.indexOf(">"))));
                               }

                               if ((sumToDep >= startSum) && (sumToDep <= endSum))
                                   numberOfRow = j;
                           }
                       }

                       if (numberOfRow > 0) {
                           row = sheet.getRow(0);

                           for (int k = 1; k <= row.getLastCellNum(); k++) {
                               cell = row.getCell(k);

                               if (cell != null) {
                                   if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                       tempSum = cell.getStringCellValue();
                                   else {
                                       tempMeaningofCell = cell.getNumericCellValue();
                                       tempMeaningfoIntCell = (int) tempMeaningofCell;
                                       tempSum = Integer.toString(tempMeaningfoIntCell);
                                   }

                                   if (tempSum.contains("-")) {
                                       startDay = Integer.parseInt(bm.stripNonDigits(tempSum.substring(0, tempSum.indexOf("-"))));
                                       endDay = Integer.parseInt(bm.stripNonDigits(tempSum.substring(tempSum.indexOf("-") + 1)));
                                   } else {
                                       startDay = Integer.parseInt(bm.stripNonDigits(tempSum));
                                       endDay = Integer.parseInt(bm.stripNonDigits(tempSum));
                                   }

                                   if ((days >= startDay) && (days <= endDay))
                                       numberOfCol = k;
                               }
                           }
                       }

                       row = sheet.getRow(numberOfRow);
                       cell = row.getCell(numberOfCol);

                       if (cell.getCellType() != Cell.CELL_TYPE_STRING)
                       {
                           withDescr.isTermOk = true;
                           withDescr.bet = Float.parseFloat(bm.stripNonDigits(Double.toString(cell.getNumericCellValue())));
                       }

                       VTBdeps.add(withDescr);
                   }
               }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }





