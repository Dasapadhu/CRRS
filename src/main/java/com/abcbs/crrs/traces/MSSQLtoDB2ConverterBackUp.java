package com.abcbs.crrs.traces;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSSQLtoDB2ConverterBackUp {
	
    public static String  queryConverter(String query){
        //List<String> lines = Files.readAllLines(Paths.get("mssql_queries.sql"));
        //List<String> converted = new ArrayList<>();
        
        //for (String line : query) {
        query = query.replaceAll("GETDATE\\(\\)", "CURRENT DATE");
        query = query.replaceAll("ISNULL\\(", "COALESCE(");
        query = query.replaceAll("\\[", "");
        query = query.replaceAll("\\]", "");
            // Add more rules as needed
            //converted.add(line);
        //}
        //Files.write(Paths.get("db2_queries.sql"), converted);
        Pattern isoTimestamp = Pattern.compile(
                "'(\\d{4})-(\\d{2})-(\\d{2})" +          // date
                "T(\\d{2}):(\\d{2}):(\\d{2})" +          // time
                "(?:\\.(\\d{3}))?" +                     // optional millis
                "(?:[+-]\\d{2}:\\d{2})?'"                // optional timezone
        );

        Matcher m = isoTimestamp.matcher(query);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {

            String year   = m.group(1);
            String month  = m.group(2);
            String day    = m.group(3);
            String hour   = m.group(4);
            String minute = m.group(5);
            String second = m.group(6);
            String millis = (m.group(7) == null) ? "000" : m.group(7);

            String original = m.group(0);  // entire matched literal

            // Detect if the left-hand side column is a LocalDate column
            boolean isLocalDate = isLocalDateField(query, m.start());

            String replacement;

            if (isLocalDate) {
                // DB2 DATE literal: YYYY-MM-DD
                replacement = "'" + year + "-" + month + "-" + day + "'";
            } else {
                // DB2 TIMESTAMP literal: YYYY-MM-DD HH:MM:SS.mmm000
                replacement = "'" + year + "-" + month + "-" + day +
                        " " + hour + ":" + minute + ":" + second +
                        "." + millis + "000'";
            }

            m.appendReplacement(sb, replacement);
        }

        m.appendTail(sb);
        return sb.toString();
    }


    /**
     * Detects whether the matched timestamp belongs to a LocalDate column.
     * Looks backward from the match to find: column_name = 'timestamp'
     */
    private static boolean isLocalDateField(String query, int timestampStartIndex) {

        // List of your LocalDate fields exactly as they appear in DB columns
        String[] localDateColumns = {
                "CHECK_STATUS_DATE",
                "STALE_DATE",
                "TRANSFER_DATE",
                "INITIAL_CHECK_DATE",
                "PPA_DATE",
                "REISSUE_CHECK_DATE",
                "REPORT_DATE",
                "EFF_ENT_DATE",
                "CONTROL_TO_DATE",
                "CONTROL_FROM_DATE"
        };

        // Pull 80 chars before the timestamp literal
        int start = Math.max(0, timestampStartIndex - 80);
        String context = query.substring(start, timestampStartIndex).toUpperCase();

        for (String col : localDateColumns) {
            if (context.contains(col + "=") || context.contains(col + " =")) {
                return true;
            }
        }

        return false;
    }
}
        
//        Pattern isoTimestamp = Pattern.compile(
//                "'(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(?:\\.(\\d{3}))?(?:[+-]\\d{2}:\\d{2})?'"
//        );
//
//        Matcher m = isoTimestamp.matcher(query);
//        StringBuffer sb = new StringBuffer();
//
//        while (m.find()) {
//            String year = m.group(1);
//            String month = m.group(2);
//            String day = m.group(3);
//            String hour = m.group(4);
//            String min = m.group(5);
//            String sec = m.group(6);
//            String millis = (m.group(7) == null) ? "000" : m.group(7);
//
//            String db2Ts = "'" + year + "-" + month + "-" + day +
//                           " " + hour + ":" + min + ":" + sec +
//                           "." + millis + "000'";
//
//            m.appendReplacement(sb, db2Ts);
//        }
//
//        m.appendTail(sb);
//        query = sb.toString();
//        
//        return query;
//    }
//}

