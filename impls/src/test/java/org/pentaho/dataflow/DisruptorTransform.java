package org.pentaho.dataflow;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Implementaiton based on the LMAX Disruptor library
 * https://github.com/LMAX-Exchange/disruptor
 *
 * Created by nbaker on 11/7/16.
 */
public class DisruptorTransform extends BaseTransform {
    public static final EventTranslatorOneArg<DataRow, DataRow> EVENT_TRANSLATOR = new EventTranslatorOneArg<DataRow, DataRow>() {
        @Override
        public void translateTo(DataRow event, long sequence, DataRow data) {
            event.setData(data.getData());
        }
    };
    Disruptor<DataRow> disruptor;

    public DisruptorTransform() {

    }

    @Override
    public void shutdown() {
        for (Disruptor<DataRow> dataRowDisruptor : disruptors.values()) {
            try {
                dataRowDisruptor.shutdown(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<Step, Disruptor<DataRow>> disruptors = new HashMap<>();

    @Override
    public void prepare() {

        // Allocate Threads

        for( int i=steps.size()-1; i>=0; i--){
            final Step step = steps.get(i);
            Step next = ( i < steps.size() - 1 ) ? steps.get(i+1) : null;

            // Executor that will be used to construct new threads for consumers
            Executor executor = Executors.newFixedThreadPool(1);

            // Specify the size of the ring buffer, must be power of 2.
            int bufferSize = 8;

            // Construct the Disruptor
            Disruptor<DataRow> disruptor = new Disruptor<>(() -> new DataRow(), 8, Executors.defaultThreadFactory(), ProducerType.SINGLE,
                    new BusySpinWaitStrategy());


            Disruptor<DataRow> nextDisruptor = disruptors.get(next);
            if( nextDisruptor != null ) {
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
                        nextDisruptor.publishEvent(EVENT_TRANSLATOR, dataRow);
                    }
                });
            }


            // Connect the handler
            disruptor.handleEventsWith(new EventHandler<DataRow>() {
                @Override
                public void onEvent(DataRow event, long sequence, boolean endOfBatch) throws Exception {
                    step.onNext(event);
                }
            });

            // Start the Disruptor, starts all threads running
            disruptor.start();

            disruptors.put(step, disruptor );

        }


    }

    public void onNext(DataRow dataRow) {
        disruptors.get(steps.get(0)).publishEvent(EVENT_TRANSLATOR, dataRow);
    }
}
