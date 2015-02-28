package com.example.sephirot47.loudspeaker;

public class StringEscapeUtils
{
    public static String escape(String str)
    {
        if (str == null) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < str.length(); ++i)
        {
            char ch = str.charAt(i);

            switch (ch)
            {
                case '\'':
                    out.append("''");
                    break;
                default:
                    out.append(ch);
                    break;
            }
        }
        return out.toString();
    }
}