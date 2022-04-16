import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Calculator {

    final int CALCULATION_LIMIT = 10; // change limit for number's size here
    String operators; // regexp format! change allowed arithmetic operations here
    String[] romeNumbers; // index template, must have the same (corresponding) numbers with arabicNumbers
    int[] arabicNumbers; // index template, must have the same (corresponding) numbers with romeNumbers
    Map<String, Integer> arabianRomanTemplate; // template for roman transmission, set automatically in constructor

    CountSystem countSystem; // current number system
    Operand operand; // current arithmetic operation, has method

    Calculator() {
        this.operators = "[\\+\\-\\/\\*]";
        this.romeNumbers =  new String[]{"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D"};
        this.arabicNumbers = new int[]{1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500};
        this.arabianRomanTemplate = IntStream
                .range(0, romeNumbers.length)
                .boxed()
                .collect(Collectors.toMap(x -> romeNumbers[x], x -> arabicNumbers[x]));
    }
    // main calculation method
    String calculate(String rawInputStr) {
        try {
            String validString = getValidString(rawInputStr);
            int[] operands = getValidOperands(validString);
            int result = this.operand.makeCalculation(operands);

            return switch (this.countSystem) {
                case ARABIAN -> Integer.toString(result);
                case ROMAN -> arabicToRoman(result);
            };
            // all exceptions from deep level print here
        } catch(Exception err) {
            String message = err.getMessage();
            if (message != null) System.out.println(err.getMessage());
        }
        return null;
    }
    // cut all spaces, make UpperCase
    String getValidString(String rawString) throws IOException {
        if(rawString.isBlank()) throw new IOException("Empty string");
        return rawString
                .replaceAll("\\s", "")
                .toUpperCase();
    }
    int[] getValidOperands(String validString) throws Exception {
        String[] rawOperands = validString.split(operators);
        if(rawOperands.length != 2) throw new IOException("Wrong calculate expression");
        String operator = String.valueOf(validString.charAt(rawOperands[0].length()));
        setOperator(operator);
        // check if input is arabian
        try {
            int[] validArabianOperands = getValidArabianOperands(rawOperands);
            this.countSystem = CountSystem.ARABIAN;
            return validArabianOperands;
            // check further if input is roman, if not - throw (doesn't catch here --> back to call method)
        } catch (NumberFormatException e) {
            int[] validRomanOperands = getValidRomanOperands(rawOperands, operator);
            this.countSystem = CountSystem.ROMAN;
            return validRomanOperands;
        }
        // quit method, arabian correct but out of limit
        catch(ArabianOutOfRuleException ex) {
            System.out.println(ex.getMessage());
            throw new IOException();
        }
    }
    // arabian to roman main logic
    String arabicToRoman(int arabicToChange) {
        StringBuilder roman = new StringBuilder();
        while (arabicToChange > 0) {
            for (int i = 0; i < arabicNumbers.length; i++) {
                if(arabicNumbers[i] > arabicToChange) {
                    roman.append(romeNumbers[i-1]);
                    arabicToChange -= arabicNumbers[i-1];
                    break;
                }
            }
        }
        return  roman.toString();
    }
    // roman to arabic main logic
    int romanToArabic(String roman) throws NotRomanNumberException {
        try {
            int arabicSum = getArabianFromTemplate(roman.length() - 1, roman);
            for (int i = roman.length() - 2; i >= 0; i--) {
                int currentNum = getArabianFromTemplate(i, roman);
                int previousNum = getArabianFromTemplate(i + 1, roman);
                arabicSum += currentNum >= previousNum ? currentNum : -currentNum;
            }
            return arabicSum;
        } catch (NotRomanNumberException err) {
            throw err; // above up to calculate method
        }
    }

    int getArabianFromTemplate(int i, String roman) throws NotRomanNumberException {
        try {
            String key = String.valueOf(roman.charAt(i));
            return this.arabianRomanTemplate.get(key);
        } catch (Exception err) {
            throw new NotRomanNumberException("Wrong roman number"); // above up to calculate method
        }
    }

    int[] getValidArabianOperands(String[] rawOperands)  throws NumberFormatException, ArabianOutOfRuleException {
        int leftArabianOperand = Integer.parseInt(rawOperands[0]);
        int rightArabianOperand = Integer.parseInt(rawOperands[1]);
        if (leftArabianOperand > CALCULATION_LIMIT || rightArabianOperand > CALCULATION_LIMIT) {
            throw new ArabianOutOfRuleException("Arabian number out of limit");
        }
        return new int[]{leftArabianOperand, rightArabianOperand};
    }

    int[] getValidRomanOperands(String [] rawOperands, String operator) throws RomanOutOfRuleException, NotRomanNumberException {
        int leftArabicOperand, rightArabicOperand;
        try {
            leftArabicOperand = romanToArabic(rawOperands[0]);
            rightArabicOperand = romanToArabic(rawOperands[1]);
        } catch (NotRomanNumberException err) {
            throw err; // above up to calculate method
        }
        if (leftArabicOperand > CALCULATION_LIMIT || rightArabicOperand > CALCULATION_LIMIT) {
            throw new RomanOutOfRuleException("Roman numbers are bigger than calculation limit");
        }
        if(rightArabicOperand >= leftArabicOperand && operator.equals("-")) {
            throw new RomanOutOfRuleException("Right roman number is bigger or equal to left one");
        }
        return new int[]{leftArabicOperand, rightArabicOperand};
    }

    void setOperator(String operator) {
        switch (operator) {
            case "*" -> this.operand = Operand.MULTI;
            case "/" -> this.operand = Operand.DIV;
            case "-" -> this.operand = Operand.MINUS;
            case "+" -> this.operand = Operand.PLUS;
        }
    }

}
