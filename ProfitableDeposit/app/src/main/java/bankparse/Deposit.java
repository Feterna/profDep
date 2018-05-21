package bankparse;

import java.net.URL;

public class Deposit
{
    public String name;
    public float bet;
    public int initialSum;
    public double profit;
    public String bankName;
    public URL depUrl;
    public String description;
    public boolean isElder = false;
    public boolean isSpecialProgram = false;
    public boolean isRefill = false; //пополнение
    public boolean isRemoval = false; //снятие
    public boolean isTermOk = false; //подходит ли срок вклада
    public boolean isExcel = false; //есть ли вклад в базе

    public Deposit()
    {
        name = "not found";
        bet = 1;
        initialSum = 0;
        depUrl = null;
        description = "";
    }

    public Deposit(String name)
    {
        this.name = name;
        bet = 1;
        initialSum = 0;
        depUrl = null;
        description = "";
    }

    public Deposit(String name, float bet)
    {
        this.name = name;
        this.bet = bet;
        initialSum = 0;
        depUrl = null;
        description = "";
    }

    public Deposit(String name, int initialSum)
    {
        this.name = name;
        this.initialSum = initialSum;
        description = "";
    }

    public Deposit(String name, float bet, int initialSum)
    {
        this.name = name;
        this.bet = bet;
        this.initialSum = initialSum;
        depUrl = null;
        description = "";
    }

    public Deposit(String name, float bet, int initialSum, URL depU)
    {
        this.name = name;
        this.bet = bet;
        this.initialSum = initialSum;
        depUrl = depU;
        description = "";
    }

    public String toString()
    {
        String prof = String.format("%1$+.2f", this.profit);
        return this.bankName + " " + this.name + " " + this.description + " " + prof;
    }

    public String info()
    {
        String prof = String.format("%1$.2f", this.bet);
        return this.bankName + " " + this.name + " Ставка: " + prof + "% Минимальная сумма: " + this.initialSum;
    }

    public String userDep()
    {
        return this.bankName + " " + this.name + " Вложенная сумма: " + this.initialSum;
    }


}
