package bankparse;

import android.content.Context;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Sberbank_Parse
{
    final public int id = 1;
    public ArrayList<Deposit> SbDep;
    public ArrayList<Deposit> moreDeps;
    Deposit tempDep;

    public Sberbank_Parse()
    {
        SbDep = new ArrayList<Deposit>();
        moreDeps = new ArrayList<Deposit>();
    }

    public void SbParse(URL url)
    {
        try
        {
            Document doc = Jsoup.connect(url.toString()).get();
            ArrayList<String> names = new ArrayList<String>();
            BMaths countIt = new BMaths();

            Elements allDeposits = doc.getElementsByClass("dc-card-block").not(".dc-card-block_noimage").not(".dc-card-block_deposits");

            Elements newsHeadlines = allDeposits.select(".dc-card-block__title");
            Elements betsAll = allDeposits.select("ul li:eq(0)");
            Elements firstSum = allDeposits.select("ul li:eq(1)");

            for (int i = 0; i < newsHeadlines.size(); i++)
            {
                tempDep = new Deposit();

                if (allDeposits.eq(i).hasClass("dc-card-block_pension") || allDeposits.eq(i).hasClass("dc-card-block_pension_plus"))
                    tempDep.isElder = true;

                if (allDeposits.eq(i).hasClass("dc-card-block_social"))
                    tempDep.isSpecialProgram = true;

                names.add(newsHeadlines.eq(i).text());
                tempDep.name = newsHeadlines.eq(i).text();

                try
                {
                    tempDep.bet = Float.parseFloat(countIt.stripNonDigits(betsAll.eq(i).text()));
                }
                catch(Exception e)
                {
                    tempDep.bet = 1;
                }
                try
                {
                    tempDep.initialSum = Integer.parseInt(countIt.stripNonDigits(firstSum.eq(i).text()));
                }
                catch(Exception e)
                {
                    tempDep.initialSum = 0;
                }

                tempDep.bankName = "Сбербанк";

                SbDep.add(tempDep);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ParseExcel(Context context, Deposit dep, long days, int sumToDep)
    {
        String result = "";
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        Deposit withDescr;
        BMaths bm = new BMaths();

        try
        {

            inputStream = context.getAssets().open("Сбербанк/" + dep.name + ".xlsx");
            workBook = new XSSFWorkbook(inputStream);

            for (int i = 0; i < workBook.getNumberOfSheets(); i++)
            {
                Sheet sheet = workBook.getSheetAt(i);
                withDescr = new Deposit(dep.name, dep.initialSum);
                withDescr.bankName = "Сбербанк";
                dep.isExcel = true;
                withDescr.description = sheet.getSheetName();

                int rowNum = sheet.getLastRowNum();
                Row row;
                Cell cell;
                String tempSum;
                int startSum = 0; int endSum = Integer.MAX_VALUE;
                int startDay = 0; int endDay = 0;
                int numberOfRow = 0; int numberOfCol = 0;
                double tempMeaningofCell;
                int tempMeaningfoIntCell;
                String tempBet;

                for (int j = 1; j <= rowNum; j++)
                {
                    row = sheet.getRow(j);
                    cell = row.getCell(0);

                    if (cell != null)
                    {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                            tempSum = cell.getStringCellValue();
                        else
                        {
                            tempMeaningofCell = cell.getNumericCellValue();
                            tempMeaningfoIntCell = (int)tempMeaningofCell;
                            tempSum = Integer.toString(tempMeaningfoIntCell);
                        }

                        if (tempSum.contains("-"))
                        {
                            startSum = Integer.parseInt(bm.stripNonDigits(tempSum.substring(0, tempSum.indexOf("-"))));
                            endSum = Integer.parseInt(bm.stripNonDigits(tempSum.substring(tempSum.indexOf("-")+1)));
                        }

                        if (tempSum.contains(">"))
                        {
                            startSum = Integer.parseInt(bm.stripNonDigits(tempSum.substring(0, tempSum.indexOf(">"))));
                        }

                        if ((sumToDep >= startSum) && (sumToDep <= endSum))
                            numberOfRow = j;
                    }
                }

                if (numberOfRow > 0)
                {
                    row = sheet.getRow(0);

                    for (int k = 1; k <= row.getLastCellNum(); k++)
                    {
                        cell = row.getCell(k);

                        if (cell != null)
                        {
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                tempSum = cell.getStringCellValue();
                            else
                            {
                                tempMeaningofCell = cell.getNumericCellValue();
                                tempMeaningfoIntCell = (int)tempMeaningofCell;
                                tempSum = Integer.toString(tempMeaningfoIntCell);
                            }

                            if (tempSum.contains("-"))
                            {
                                startDay = Integer.parseInt(bm.stripNonDigits(tempSum.substring(0, tempSum.indexOf("-"))));
                                endDay = Integer.parseInt(bm.stripNonDigits(tempSum.substring(tempSum.indexOf("-")+1)));
                            }
                            else
                            {
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

                moreDeps.add(withDescr);
            }

        }
        catch (IOException e)
        {
        }



    }

}
