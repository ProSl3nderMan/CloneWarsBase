/*
 Credit to Elementeral for the provided code. It has some edits from me in order to
 allow a defaulted translate hex color codes without starttag and endtag.
 Post can be found here: https://www.spigotmc.org/threads/hex-chat-class.449300/
 */

package me.prosl3nderman.clonewarsbase.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexColor {

    public static final char COLOR_CHAR = '\u00A7';

    public static String translateHexColorCodes(String message) {
        return translateHexColorCodes("&#", "", message);
    }

    public static String translateHexColorCodes(String startTag, String message) {
        return translateHexColorCodes(startTag, message);
    }

    public static String translateHexColorCodes(String startTag, String endTag, String message)
    {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
