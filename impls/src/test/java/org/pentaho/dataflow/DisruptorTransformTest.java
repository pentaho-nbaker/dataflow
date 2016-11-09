package org.pentaho.dataflow;

import org.pentaho.dataflow.tck.AbstractTransformTest;

/**
 * Created by nbaker on 11/9/16.
 */
public class DisruptorTransformTest extends AbstractTransformTest {

    @Override
    protected Transform createTransform() {
        return new DisruptorTransform();
    }

}
