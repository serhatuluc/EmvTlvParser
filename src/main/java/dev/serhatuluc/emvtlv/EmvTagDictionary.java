package dev.serhatuluc.emvtlv;

import java.util.Map;
import java.util.TreeMap;

/**
 * EMVCo Book 3/4 (Annex A) standard tag names. Tags not present here are either
 * proprietary/vendor-specific or simply missing from this dictionary.
 */
public final class EmvTagDictionary {

    private EmvTagDictionary() {
    }

    public static final Map<String, String> NAMES = buildNames();

    public static boolean isKnown(String tag) {
        return NAMES.containsKey(tag.toUpperCase());
    }

    public static String name(String tag) {
        return NAMES.get(tag.toUpperCase());
    }

    private static Map<String, String> buildNames() {
        Map<String, String> m = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        m.put("42", "Issuer Identification Number (IIN)");
        m.put("4F", "Application Dedicated File (ADF) Name");
        m.put("50", "Application Label");
        m.put("56", "Track 1 Data");
        m.put("57", "Track 2 Equivalent Data");
        m.put("5A", "Application Primary Account Number (PAN)");
        m.put("5F20", "Cardholder Name");
        m.put("5F24", "Application Expiration Date");
        m.put("5F25", "Application Effective Date");
        m.put("5F28", "Issuer Country Code");
        m.put("5F2A", "Transaction Currency Code");
        m.put("5F2D", "Language Preference");
        m.put("5F30", "Service Code");
        m.put("5F34", "Application Primary Account Number (PAN) Sequence Number");
        m.put("5F36", "Transaction Currency Exponent");
        m.put("61", "Application Template");
        m.put("6F", "File Control Information (FCI) Template");
        m.put("70", "READ RECORD Response Message Template");
        m.put("71", "Issuer Script Template 1");
        m.put("72", "Issuer Script Template 2");
        m.put("77", "Response Message Template Format 2");
        m.put("80", "Response Message Template Format 1");
        m.put("81", "Amount, Authorised (Binary)");
        m.put("82", "Application Interchange Profile");
        m.put("83", "Command Template");
        m.put("84", "Dedicated File (DF) Name");
        m.put("86", "Issuer Script Command");
        m.put("87", "Application Priority Indicator");
        m.put("88", "Short File Identifier (SFI)");
        m.put("89", "Authorisation Code");
        m.put("8A", "Authorisation Response Code");
        m.put("8C", "Card Risk Management Data Object List 1 (CDOL1)");
        m.put("8D", "Card Risk Management Data Object List 2 (CDOL2)");
        m.put("8E", "Cardholder Verification Method (CVM) List");
        m.put("8F", "Certification Authority Public Key Index");
        m.put("90", "Issuer Public Key Certificate");
        m.put("91", "Issuer Authentication Data");
        m.put("92", "Issuer Public Key Remainder");
        m.put("93", "Signed Static Application Data");
        m.put("94", "Application File Locator (AFL)");
        m.put("95", "Terminal Verification Results");
        m.put("97", "Transaction Certificate Data Object List (TDOL)");
        m.put("98", "Transaction Certificate (TC) Hash Value");
        m.put("99", "Transaction Personal Identification Number (PIN) Data");
        m.put("9A", "Transaction Date");
        m.put("9B", "Transaction Status Information");
        m.put("9C", "Transaction Type");
        m.put("9D", "Directory Definition File (DDF) Name");
        m.put("A5", "File Control Information (FCI) Proprietary Template");
        m.put("BF0C", "File Control Information (FCI) Issuer Discretionary Data");
        m.put("9F01", "Acquirer Identifier");
        m.put("9F02", "Amount, Authorised (Numeric)");
        m.put("9F03", "Amount, Other (Numeric)");
        m.put("9F04", "Amount, Other (Binary)");
        m.put("9F05", "Application Discretionary Data");
        m.put("9F06", "Application Identifier (AID) - terminal");
        m.put("9F07", "Application Usage Control");
        m.put("9F08", "Application Version Number - ICC");
        m.put("9F09", "Application Version Number - terminal");
        m.put("9F0B", "Cardholder Name Extended");
        m.put("9F0D", "Issuer Action Code - Default");
        m.put("9F0E", "Issuer Action Code - Denial");
        m.put("9F0F", "Issuer Action Code - Online");
        m.put("9F10", "Issuer Application Data");
        m.put("9F11", "Issuer Code Table Index");
        m.put("9F12", "Application Preferred Name");
        m.put("9F13", "Last Online Application Transaction Counter (ATC) Register");
        m.put("9F14", "Lower Consecutive Offline Limit");
        m.put("9F15", "Merchant Category Code");
        m.put("9F16", "Merchant Identifier");
        m.put("9F17", "Personal Identification Number (PIN) Try Counter");
        m.put("9F18", "Issuer Script Identifier");
        m.put("9F1A", "Terminal Country Code");
        m.put("9F1B", "Terminal Floor Limit");
        m.put("9F1C", "Terminal Identification");
        m.put("9F1D", "Terminal Risk Management Data");
        m.put("9F1E", "Interface Device (IFD) Serial Number");
        m.put("9F1F", "Track 1 Discretionary Data");
        m.put("9F20", "Track 2 Discretionary Data");
        m.put("9F21", "Transaction Time");
        m.put("9F22", "Certification Authority Public Key Index - terminal");
        m.put("9F23", "Upper Consecutive Offline Limit");
        m.put("9F26", "Application Cryptogram");
        m.put("9F27", "Cryptogram Information Data");
        m.put("9F2D", "ICC PIN Encipherment Public Key Certificate");
        m.put("9F2E", "ICC PIN Encipherment Public Key Exponent");
        m.put("9F2F", "ICC PIN Encipherment Public Key Remainder");
        m.put("9F32", "Issuer Public Key Exponent");
        m.put("9F33", "Terminal Capabilities");
        m.put("9F34", "Cardholder Verification Method (CVM) Results");
        m.put("9F35", "Terminal Type");
        m.put("9F36", "Application Transaction Counter (ATC)");
        m.put("9F37", "Unpredictable Number");
        m.put("9F38", "Processing Options Data Object List (PDOL)");
        m.put("9F39", "Point-of-Service (POS) Entry Mode");
        m.put("9F3A", "Amount, Reference Currency");
        m.put("9F3B", "Application Reference Currency");
        m.put("9F3C", "Transaction Reference Currency Code");
        m.put("9F3D", "Transaction Reference Currency Exponent");
        m.put("9F40", "Additional Terminal Capabilities");
        m.put("9F41", "Transaction Sequence Counter");
        m.put("9F42", "Application Currency Code");
        m.put("9F43", "Application Reference Currency Exponent");
        m.put("9F44", "Application Currency Exponent");
        m.put("9F45", "Data Authentication Code");
        m.put("9F46", "ICC Public Key Certificate");
        m.put("9F47", "ICC Public Key Exponent");
        m.put("9F48", "ICC Public Key Remainder");
        m.put("9F49", "Dynamic Data Authentication Data Object List (DDOL)");
        m.put("9F4A", "Static Data Authentication Tag List");
        m.put("9F4B", "Signed Dynamic Application Data");
        m.put("9F4C", "ICC Dynamic Number");
        m.put("9F4D", "Log Entry");
        m.put("9F4E", "Merchant Name and Location");
        m.put("9F4F", "Log Format");
        m.put("9F52", "Application Default Action (ADA)");
        m.put("9F53", "Consecutive Transaction Limit (International)");
        m.put("9F54", "Consecutive Transaction Limit (International - Country)");
        m.put("9F55", "Issuer Authentication Indicator");
        m.put("9F56", "Issuer Authentication Indicator");
        m.put("9F57", "Issuer Country Code");
        m.put("9F58", "Consecutive Transaction Counter Limit");
        m.put("9F59", "Consecutive Transaction Counter International Limit");
        m.put("9F5A", "Application Program Identifier");
        m.put("9F5B", "Issuer Script Results");
        m.put("9F5C", "Cumulative Total Transaction Amount Limit");
        m.put("9F72", "Consecutive Transaction International Upper Limit");
        m.put("9F73", "Currency Conversion Factor");
        m.put("9F74", "VLP Issuer Authorization Code");
        m.put("9F75", "Cumulative Total Transaction Amount Upper Limit");
        m.put("9F76", "Secondary Application Currency Code");
        m.put("9F77", "VLP Funds Limit");
        m.put("9F78", "VLP Single Transaction Limit");
        m.put("9F79", "VLP Available Funds");
        m.put("9F7C", "Merchant Custom Data");
        m.put("DF01", "(Proprietary/Vendor-specific)");
        return Map.copyOf(m);
    }
}
