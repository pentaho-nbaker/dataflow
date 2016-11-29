package org.pentaho.dataflow;

import org.pentaho.dataflow.tck.AbstractTransformTest;

/**
 * Created by nbaker on 11/14/16.
 */
public class AkkaStreamingTransformTest extends AbstractTransformTest {
    @Override
    protected Transform createTransform() {
        return new AkkaStreamingTransform();
    }
}