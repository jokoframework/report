package io.github.jokoframework.report.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class NumbersToSpanishWords {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumbersToSpanishWords.class);

    /**
     * Converts a given number to words representation in spanish
     *
     * @param number the number as String.
     * @return the words representation of the number.
     */
    public static String numberToWords(String number) {
        StringBuilder result = new StringBuilder();
        BigDecimal totalBigDecimal = new BigDecimal(number).setScale(2, BigDecimal.ROUND_DOWN);
        long parteEntera = totalBigDecimal.toBigInteger().longValue();
        int unidades = (int) (parteEntera % 1000);
        int miles = (int) ((parteEntera / 1000) % 1000);
        int millones = (int) ((parteEntera / 1000000) % 1000);
        int milMillones = (int) ((parteEntera / 1000000000) % 1000);
        String textTotal = totalBigDecimal.toPlainString();
        int radixLoc = textTotal.indexOf('.');
        int cents = Integer.parseInt(textTotal.substring(radixLoc + 1, textTotal.length()));

        if (parteEntera == 0) {
            result.append("Cero ");
            return result.toString();
        }

        if (milMillones > 0) result.append(processDigits(milMillones).toString() + "Mil ");
        if (millones > 0) result.append(processDigits(millones).toString());

        if (milMillones == 0 && millones == 1) result.append("Millon ");
        else if (milMillones > 0 || millones > 0) result.append("Millones ");

        if (miles > 0) result.append(processDigits(miles).toString() + "Mil ");
        if (unidades > 0) result.append(processDigits(unidades).toString());
        if (cents > 0) {
            result.append("con ");
            result.append(processDigits(cents).toString());
            result.append(cents >= 10 ? "Centavos " : "Centavo ");
        }
        String finalResult = result.toString();
        if (finalResult.startsWith("Un Mil") && !finalResult.startsWith("Un Millon")) {
            finalResult = result.toString().substring(3, finalResult.length());
        }
        LOGGER.debug("result: {}", finalResult);
        return finalResult;
    }

    private static String processCentenas(int unidades, int decenas, int centenas) {
        StringBuilder sb = new StringBuilder();
        String result;
        switch (centenas) {
            case 0:
                break;
            case 1:
                if (decenas == 0 && unidades == 0) {
                    sb.append("Cien ");
                } else {
                    sb.append("Ciento ");
                }
                break;
            case 2:
                sb.append("Doscientos ");
                break;
            case 3:
                sb.append("Trescientos ");
                break;
            case 4:
                sb.append("Cuatrocientos ");
                break;
            case 5:
                sb.append("Quinientos ");
                break;
            case 6:
                sb.append("Seiscientos ");
                break;
            case 7:
                sb.append("Setecientos ");
                break;
            case 8:
                sb.append("Ochocientos ");
                break;
            case 9:
                sb.append("Novecientos ");
                break;
            default:
                break;
        }
        result = sb.toString();
        return result;
    }

    private static String processDecenas(int unidades, int decenas) {
        StringBuilder sb = new StringBuilder();
        String result;
        switch (decenas) {
            case 1:
                if (unidades == 0) {
                    sb.append("Diez ");
                } else if (unidades == 1) {
                    sb.append("Once ");
                } else if (unidades == 2) {
                    sb.append("Doce ");
                } else if (unidades == 3) {
                    sb.append("Trece ");
                } else if (unidades == 4) {
                    sb.append("Catorce ");
                } else if (unidades == 5) {
                    sb.append("Quince ");
                } else sb.append("Dieci");
                break;
            case 2:
                if (unidades == 0) {
                    sb.append("Veinte ");
                } else {
                    sb.append("Veinti");
                }
                break;
            case 3:
                sb.append("Treinta ");
                break;
            case 4:
                sb.append("Cuarenta ");
                break;
            case 5:
                sb.append("Cincuenta ");
                break;
            case 6:
                sb.append("Sesenta ");
                break;
            case 7:
                sb.append("Setenta ");
                break;
            case 8:
                sb.append("Ochenta ");
                break;
            case 9:
                sb.append("Noventa ");
                break;
            default:
                break;
        }
        result = sb.toString();
        return result;
    }

    private static String processUnidades(int unidades, int decenas) {
        StringBuilder sb = new StringBuilder();
        String result;
        if (decenas > 2 && unidades > 0) {
            sb.append("y ");
        }

        if (decenas == 0 || (decenas > 2 && unidades > 0) || (decenas == 2 && unidades > 0) || (decenas > 0 && unidades > 5)) {
            switch (unidades) {
                case 1:
                    sb.append("Un ");
                    break;
                case 2:
                    sb.append("Dos ");
                    break;
                case 3:
                    sb.append("Tres ");
                    break;
                case 4:
                    sb.append("Cuatro ");
                    break;
                case 5:
                    sb.append("Cinco ");
                    break;
                case 6:
                    sb.append("Seis ");
                    break;
                case 7:
                    sb.append("Siete ");
                    break;
                case 8:
                    sb.append("Ocho ");
                    break;
                case 9:
                    sb.append("Nueve ");
                    break;
                default:
                    break;
            }
        }
        result = sb.toString();
        return result;
    }

    /**
     * Converts a three digits number to words representation in spanish
     *
     * @param n La cantidad a convertir.
     * @return the words representation of the number.
     */
    private static StringBuilder processDigits(int n) {
        StringBuilder result = new StringBuilder();
        int centenas = n / 100;
        int decenas = (n % 100) / 10;
        int unidades = (n % 10);
        result.append(NumbersToSpanishWords.processCentenas(unidades, decenas, centenas));
        result.append(NumbersToSpanishWords.processDecenas(unidades, decenas));
        result.append(NumbersToSpanishWords.processUnidades(unidades, decenas));
        return result;
    }

}
