package org.pentaho.dataflow;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

/**
 * Created by nbaker on 11/7/16.
 */
public class Step implements Processor<DataRow, DataRow> {

    protected Function<DataRow, DataRow> fun;
    protected Subscriber<? super DataRow> subscriber;


    public Step(Function<DataRow, DataRow> fun) {
        this.fun = fun;
    }



    @Override
    public void onNext(DataRow dataRow) {
        DataRow rowOut = fun.apply(dataRow);
        if( subscriber != null ){
            subscriber.onNext( rowOut );
        }
    }

    @Override
    public void subscribe(Subscriber<? super DataRow> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        // backpressure would be exerted against this subscription, NYI
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        // Nice! Coffee-time
    }
}
