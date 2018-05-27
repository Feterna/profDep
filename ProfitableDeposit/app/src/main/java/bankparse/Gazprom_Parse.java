package bankparse;

import android.content.Context;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Gazprom_Parse
{
    final public int id = 1;
    public ArrayList<Deposit> GPBDep;
    public ArrayList<Deposit> moreDeps;
    Deposit tempDep;

    public Gazprom_Parse()
    {
        GPBDep = new ArrayList<Deposit>();
        moreDeps = new ArrayList<Deposit>();
    }

    public void GazpromParse(URL url)
    {
        try
        {
            Document doc = Jsoup.connect(url.toString()).get();
            ArrayList<String> names = new ArrayList<String>();
            BMaths countIt = new BMaths();

            Element table = doc.select("table.about-deposit").get(0);

            Elements rows = table.select("tr");

            Elements newsHeadlines = new Elements();
            Elements betsAll = new Elements();
            Elements firstSum = new Elements();
            String cutSt;
            int EUpos = 0;

            for (int i = 1; i < rows.size(); i++)
            {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                firstSum.add(cols.get(2));
                betsAll.add(cols.get(1));

                tempDep = new Deposit();

                tempDep.bankName = "Газпромбанк";

                newsHeadlines.add(cols.get(0));
                tempDep.name = newsHeadlines.eq(i-1).text().replace("\"", "").replace("! ", "").replace("«", "").replace("»", "").replace("–", "-");

                if (tempDep.name.contains("Пенси"))
                    tempDep.isElder = true;

                cutSt = firstSum.eq(i-1).eachText().get(0);
                for (int j = 0; j < cutSt.length(); j++)
                {
                    if (cutSt.charAt(j) == '$')
                        EUpos = j;

                }
                if (EUpos != 0)
                    cutSt = cutSt.substring(0, EUpos);

                tempDep.initialSum = Integer.parseInt(countIt.stripNonDigits(cutSt));


                EUpos = 0;

                cutSt = betsAll.eq(i-1).eachText().get(0);

                for (int j = 0; j < cutSt.length(); j++)
                {
                    if (cutSt.charAt(j) == '$')
                        EUpos = j;

                }
                if (EUpos != 0)
                    cutSt = cutSt.substring(0, EUpos);

                tempDep.bet = Float.parseFloat(countIt.stripNonDigits(cutSt));


                EUpos = 0;

                GPBDep.add(tempDep);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void GAZ_Excel(Context context, Deposit dep, long days, int sumToDep)
    {
        String result = "";
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        Deposit withDescr;
        BMaths bm = new BMaths();

        try
        {

            inputStream = context.getAssets().open("Газпромбанк/" + dep.name + ".xlsx");
            workBook = new XSSFWorkbook(inputStream);

            for (int i = 0; i < workBook.getNumberOfSheets(); i++)
            {
                Sheet sheet = workBook.getSheetAt(i);
                withDescr = new Deposit(dep.name, dep.initialSum);
                withDescr.bankName = "Газпромбанк";
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
            e.printStackTrace();
        }
    }
}
