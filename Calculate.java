import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculate
{
	public String expression() throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String exp = br.readLine();

		if(exp.equals("quit"))
			return exp;

		if(exp.length()>2)
		{
			String str = exp.substring(0,2);
			//Run the function of simplify
			if(str.equals("!s"))
				return exp;
			//Run the function of derivative
			if(str.equals("!d"))
				return exp;
		}

		//Judge the illegal char by java.util.regex
		String reg1 = "[^0-9a-zA-Z(\\*)(\\+)]";
		if(Match(reg1,exp))
		{
			exp = "Error, wrong expression";
			return exp;
		}

		//filter the combination of digit and character
		String reg2 = "([a-zA-Z]+)(\\d)+";
		if(Match(reg2,exp))
		{
			exp = "Error, wrong expression";
			return exp;
		}

		//filter the combination of digit and character
		String reg3 = "(\\d)+([a-zA-Z]+)";
		if(Match(reg3,exp))
		{
			exp = "Error, wrong expression";
			return exp;
		}

		//filter String of multiple charater
		String reg4 = "([a-zA-Z]+){2,}";
		if(Match(reg4,exp))
		{
			exp = "Error, wrong expression";
			return exp;
		}

		return exp;
	}
	public static boolean Match(String re,String exp)
	{
		Pattern pattern = Pattern.compile(re);
		Matcher matcher = pattern.matcher(exp);
		if(matcher.find())
			return true;
		else
			return false;
	}

	public static boolean IsDigit(String str)
	{
		//Judge string is a digit number or not, using regular expression
		Pattern pattern = Pattern.compile("(\\d)+");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static String Multiple(String exp)
	{
		int num = 1;
		StringBuilder sbvar = new StringBuilder();//StringBuilder object for combination of variable
		StringBuilder Mulexp = new StringBuilder();//StringBuilder object for combination of factor
		String MulPart[] = exp.split("\\*");
		for(int j=0; j<MulPart.length; j++)
		{
			if(IsDigit(MulPart[j]))
				num *= Integer.parseInt(MulPart[j]);//multiple numers
			else
				sbvar.append("*").append(MulPart[j]);//concat the variable
		}
		//combination of number and variable
		Mulexp.append(Integer.toString(num)).append(sbvar.toString());
		return Mulexp.toString();
	}

	public static String CalculateExp(String exp)
	{
		int num;
		String sMidExp = "";//the string of expression after multiple
		String finalExp = "";//the final expression after simplify

		String Mulexp = "";

		//only do the multiple when there is no addition
		if(!Match("\\+",exp))
		{
			exp = Multiple(exp);
			return exp;
		}

		//first step,make the mutiple calculate
		String AddPart[] = exp.split("\\+");
		StringBuilder MidExp = new StringBuilder();//using StringBuilder to concat String
		for(int i=0; i<AddPart.length; i++)
		{
			Mulexp = Multiple(AddPart[i]);
			//addition of these factor
			MidExp.append("+").append(Mulexp);
			//get the expression of the middle part of simplify()
			sMidExp = MidExp.toString().substring(1);
		}

		//second step,make the addition calculate
		String AddSeg[] =sMidExp.split("\\+");
		StringBuilder Addexp = new StringBuilder();//
		StringBuilder AidAddexp = new StringBuilder();//StringBuilder object help for final combination
		num = 0;
		for(int k=0; k<AddSeg.length; k++)
		{
			if(IsDigit(AddSeg[k]))
				num += Integer.parseInt(AddSeg[k]);//addition of these numbers
			else
				AidAddexp.append("+").append(AddSeg[k]);//hold these non-digit factor
		}
		Addexp.append(Integer.toString(num)).append(AidAddexp.toString());
		finalExp = Addexp.toString();

		return finalExp;
	}

	public String simplify(String exp,String SimpExp)
	{
		char var;
		int val;
		//Get the variable in the expression
		var = SimpExp.substring(10).charAt(0);
		//Get the value of the variable
		val = Integer.parseInt(SimpExp.substring(12));
		//character or digit to String
		String svar = String.valueOf(var);
		String sval = Integer.toString(val);

		if(!Match(svar,exp))
			return exp;

		//replace all variable by the value
		String s = exp.replace(svar,sval);
		s = Calculate.CalculateExp(s);
		return s;
	}

	public String derivative(String exp,String DervativeExp)
	{
		String finalExp;

		//get the variable from this expression of dervative
		char var;
		var = DervativeExp.charAt(DervativeExp.length()-1);

		//Judge the variable is in the expression or not
		String svar = Character.toString(var);
		if(!Match(svar,exp))
		{
			System.out.println("Error, no variable");
			return exp;
		}

		String AddSeg[] = exp.split("\\+");
		int varCount;//count the number of variable of each additional part
		StringBuilder SBAddSeg = new StringBuilder();
		for(int i=0; i<AddSeg.length; i++)
		{
			String AddPart = "";
			//Judge the variable is in the AddSeg or not
			if(Match(svar,AddSeg[i]))
			{
				if(AddSeg[i].length() == 1)
					AddPart = "1";
				else if(AddSeg[i].length() == 3)
					AddPart = AddSeg[i].substring(0,1);
				else
				{
					varCount = 0;
					StringBuilder Factors = new StringBuilder();
					String vars[] = AddSeg[i].split("\\*");
					for(int j=0; j<vars.length; j++)
					{
						if(vars[j].equals(svar))
							varCount += 1;
						else
							Factors.append("*").append(vars[j]);
					}

					StringBuilder varFactors = new StringBuilder(Integer.toString(varCount));
					for(int k=1; k<varCount; k++)
					{
						varFactors.append("*").append(var);
					}
					varFactors.append(Factors.toString());
					AddPart = varFactors.toString();
				}
				SBAddSeg.append("+").append(AddPart);
			}
		}
		finalExp = SBAddSeg.toString().substring(1);
		finalExp = Calculate.CalculateExp(finalExp);
		return finalExp;
	}

	public static void main(String[] args) throws IOException
	{
		Calculate Calobject = new Calculate();
		String exp;
		String strSimplify;
		String SimpExp;
		String strDerivative;
		String DerExp;

		//Get the Expression
		exp = Calobject.expression();
		while(!exp.equals("quit"))
		{
			System.out.println(exp);

			//input the simplify expression
			String SimplifyExp = Calobject.expression();
			// If the string "simplify" in it,then run simplify()
			long SimstartTime=System.nanoTime();
			strSimplify = SimplifyExp.substring(0,9);
			//Simplify the expression
			if(strSimplify.equals("!simplify"))
			{
				SimpExp = Calobject.simplify(exp,SimplifyExp);
				System.out.println(SimpExp);
			}
			long SimendTime=System.nanoTime();

			//input the dervative expression
			String DervativeExp = Calobject.expression();
			//If the string "!d/dx" in it,then run dervative()
			long DerstartTime=System.nanoTime();
			strDerivative = DervativeExp.substring(0,2);
			if(strDerivative.equals("!d"))
			{
				DerExp = Calobject.derivative(exp,DervativeExp);
				System.out.println(DerExp);
			}
			long DerendTime=System.nanoTime();

			//Output the time of Simplify and Derivative
			System.out.println("Simplify and Derivative time:"+(SimendTime-SimstartTime+DerendTime-DerstartTime)/1000+"us");

			exp = Calobject.expression();
		}
	}
}
