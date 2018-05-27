package bankparse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;

public class AlfaBank
{
    public ArrayList<Deposit> AlfaDep;
    public ArrayList<Deposit> moreDeps;
    Deposit tempDep;

    public AlfaBank()
    {
        AlfaDep = new ArrayList<Deposit>();
        moreDeps = new ArrayList<Deposit>();
    }

    public void Alfa_Parse(URL url)
    {
        try
        {
            Document doc = Jsoup.connect(url.toString()).get();
            Elements headings = doc.select("[href^=/make-money/] [data-level-menu=retail-savings]");

            System.out.println(headings.toString());


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
