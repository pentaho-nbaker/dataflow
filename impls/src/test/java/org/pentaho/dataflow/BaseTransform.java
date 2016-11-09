package org.pentaho.dataflow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by nbaker on 11/9/16.
 */
public abstract class BaseTransform implements Transform {

    protected List<Step> steps = new ArrayList<>();

    public Transform next(Function<DataRow, DataRow> fun) {
        steps.add( new Step( fun ) );
        return this;
    }

}
