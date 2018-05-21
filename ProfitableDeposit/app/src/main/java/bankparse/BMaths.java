package bankparse;


import java.util.ArrayList;
import java.util.Collections;

public class BMaths {
    public BMaths()
    {
    }

    public String stripNonDigits(CharSequence input)
    {
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c > 47 && c < 58) || c == 44 || c == 46) {
                if (c == 44)
                    c = 46;
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public void sortByProfit(ArrayList<Deposit> deps, int initial, long days)
    {
        for (Deposit tempd : deps)
        {
            if (initial >= tempd.initialSum)
            {
                tempd.profit = (((float)initial) / 100.0) * tempd.bet * (((float)days) / 365.0);
            }
            else
                {
                    tempd.profit = 0;
                }
        }

        Collections.sort(deps, (o1, o2) ->
        {
            if (o1.profit > o2.profit)
                return -1;
            else if (o1.profit == o2.profit)
                return 0;
            else return 1;
        });

    }
}
