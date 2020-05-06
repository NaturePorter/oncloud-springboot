package cn.oncloud.filter;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
    public XSSRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return StringEscapeUtils.escapeHtml4(value);
    }

    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        String[] newValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = StringEscapeUtils.escapeHtml4(values[i]);
        }
        return newValues;
    }
}
