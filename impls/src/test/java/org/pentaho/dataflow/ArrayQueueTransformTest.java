package org.pentaho.dataflow;

import org.pentaho.dataflow.tck.AbstractTransformTest;

/**
 * Created by nbaker on 11/7/16.
 */
public class ArrayQueueTransformTest extends AbstractTransformTest {
    @Override
    protected Transform createTransform() {
        return new ArrayQueueTransform();
    }
}