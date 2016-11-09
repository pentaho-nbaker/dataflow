package org.pentaho.dataflow;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by nbaker on 11/7/16.
 */
public class StreamingTransform extends BaseTransform {
    public StreamingTransform() {
        super();
    }


    public void onNext(DataRow dataRow) {
        steps.get(0).onNext(dataRow);
    }

    @Override
    public void prepare() {

        for( int i=steps.size()-1; i>=0; i--) {
            final Step step = steps.get(i);
            Step next = (i < steps.size() -1) ? steps.get(i + 1) : null;
            if( next != null ) {
                step.subscribe(new Subscriber<DataRow>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onNext(DataRow dataRow) {
                       next.onNext(dataRow);
                    }
                });
            }
        }
    }

    @Override
    public void shutdown() {

    }
}
