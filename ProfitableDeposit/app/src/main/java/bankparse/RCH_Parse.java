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

//view-source:https://www.rshb.ru/natural/deposits/summary-deposits// - тут подробнее описания вкладов для калькулятора
public class RCH_Parse
{
    final public int id = 1;
    public ArrayList<Deposit> RCHDep;
    public ArrayList<Deposit> moreDeps;
    Deposit tempDep;
    ArrayList<String> dep_urls = new ArrayList<String>();

    public RCH_Parse()
    {
        RCHDep = new ArrayList<Deposit>();
        moreDeps = new ArrayList<Deposit>();
    }

    public void RCHParse(URL url)
    {
        try
        {
            Document doc = Jsoup.connect(url.toString()).get();
            ArrayList<String> names = new ArrayList<String>();
            BMaths countIt = new BMaths();
            Document tempdoc;

            Element table = doc.select("table").get(0);
            Elements rows = table.select("tr");

            Elements newsHeadlines = new Elements();
            Elements betsAll = new Elements();
            Elements firstSum = new Elements();
            String cutSt;
            int EUpos = 0;
            int k = 0;

            for (int i = 1; i < rows.size(); i++)
            {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                firstSum.add(cols.get(4));
                betsAll.add(cols.get(8));
                cutSt = firstSum.eq(i-1).text();

                //если курс рубля первый
                if (cutSt.charAt(0) == 'R')
                {
                    EUpos = 0;
                    tempDep = new Deposit();
                    tempDep.bankName = "Россельхозбанк";
                    newsHeadlines.add(cols.get(0).getElementsByTag("a").first());

                    //dep_urls.add(newsHeadlines.eq(k).attr("href"));
                    //tempDep.depUrl = new URL(dep_urls.get(k));

                    if (newsHeadlines.eq(k).hasAttr("br"))
                        newsHeadlines.eq(k).removeAttr("br");


                    tempDep.name = newsHeadlines.eq(k).text();

                    if (tempDep.name.contains("Пенси"))
                        tempDep.isElder = true;

                    for (int j = 0; j < cutSt.length(); j++)
                    {
                        if (cutSt.charAt(j) == 'E' || (cutSt.charAt(j) == 'U' && cutSt.charAt(j-1) != 'R'))
                            EUpos = j;
                    }
                    if (EUpos != 0)
                        cutSt = cutSt.substring(0, EUpos);


                    tempDep.initialSum = Integer.parseInt(countIt.stripNonDigits(cutSt));

                    if (betsAll.eq(i-1).hasAttr("strong") )
                        betsAll.eq(i - 1).remove("strong");
                    if (betsAll.eq(i-1).hasAttr("b"))
                        betsAll.eq(i - 1).remove("b");
                    cutSt = betsAll.eq(i-1).text();

                    for (int j = 0; j < cutSt.length(); j++)
                    {
                        if (cutSt.charAt(j) == 'E' || (cutSt.charAt(j) == 'U' && cutSt.charAt(j-1) != 'R'))
                            EUpos = j;
                    }
                    if (EUpos != 0)
                        cutSt = cutSt.substring(0, EUpos);


                    tempDep.bet = Float.parseFloat(countIt.stripNonDigits(cutSt));

                    k++;

                    RCHDep.add(tempDep);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void RCH_Excel(Context context, Deposit dep, long days, int sumToDep)
    {
        String result = "";
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        Deposit withDescr;
        BMaths bm = new BMaths();

        try
        {
            if (dep.name.contains("новый"))
                dep.name.replaceAll("новый", "");

            inputStream = context.getAssets().open("Россельхозбанк/" + dep.name + ".xlsx");
            workBook = new XSSFWorkbook(inputStream);

            for (int i = 0; i < workBook.getNumberOfSheets(); i++)
            {
                Sheet sheet = workBook.getSheetAt(i);
                withDescr = new Deposit(dep.name, dep.initialSum);
                withDescr.bankName = "Россельхозбанк";
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
