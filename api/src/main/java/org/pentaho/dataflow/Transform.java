package org.pentaho.dataflow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by nbaker on 11/9/16.
 */
public interface Transform {

    Transform next(Function<DataRow, DataRow> fun);

    void prepare();
    void shutdown();

    void onNext(DataRow dataRow);
}
