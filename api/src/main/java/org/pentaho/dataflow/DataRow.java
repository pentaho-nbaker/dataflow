package org.pentaho.dataflow;

/**
 * Created by nbaker on 11/7/16.
 */
public class DataRow {
    public static final DataRow EMPTY = new DataRow( );
    private Object[] data;
    private Class<?>[] types;

    public DataRow() {
        data = new Object[0];
        types = new Class[0];
    }

    public DataRow(Object[] data, Class<?>[] types ){
        assert data.length == types.length;
        this.data = data;
        this.types = types;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public int getSize() {
        return data.length;
    }

    public Object get( int pos ){
        assert pos < data.length;
        return data[pos];
    }

    public Object[] getData() {
        return data;
    }
}
