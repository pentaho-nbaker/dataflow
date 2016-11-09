package org.pentaho.dataflow;

/**
 * Created by nbaker on 11/7/16.
 */
public class StringDataRow extends DataRow {
    public StringDataRow(Object[] data, Class<?>[] classes) {
        super(data, classes);
    }

    public static StringDataRow of(Object[] data) {
        Class<?>[] classes = new Class[data.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = String.class;
        }
        return new StringDataRow( data, classes );
    }
}
