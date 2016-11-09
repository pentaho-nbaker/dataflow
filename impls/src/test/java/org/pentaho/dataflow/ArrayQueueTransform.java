package org.pentaho.dataflow;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Created by nbaker on 11/7/16.
 */
public class ArrayQueueTransform extends BaseTransform {

    AtomicBoolean running = new AtomicBoolean(false);
    Map<Step, ArrayBlockingQueue<DataRow>> queues = new HashMap<>();

    public ArrayQueueTransform() {
    }

    @Override
    public void prepare() {
        running.compareAndSet(false, true);


        for( int i=steps.size()-1; i>=0; i--){
            final Step step = steps.get(i);
            Step next = ( i < steps.size() -1 ) ? steps.get(i+1) : null;
            ArrayBlockingQueue<DataRow> nextQueue = queues.get(next);

            final ArrayBlockingQueue<DataRow> queue = new ArrayBlockingQueue<>(5);
            queues.put(step, queue);


            if( nextQueue != null ) {
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
                        try {
                            nextQueue.put(dataRow);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            new Thread(() -> {
                try {
                    while ( running.get() || queue.size() > 0 ) {
                        step.onNext(queue.take());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

            }).start();
        }

    }

    @Override
    public void shutdown() {
        running.compareAndSet( true, false );
    }


    public void onNext(DataRow dataRow) {

        try {
            queues.get(steps.get(0)).put( dataRow );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
