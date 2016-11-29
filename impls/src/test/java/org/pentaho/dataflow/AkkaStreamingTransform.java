package org.pentaho.dataflow;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.impl.PublisherSource;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import akka.stream.*;
import akka.stream.javadsl.*;
/**
 * Created by nbaker on 11/14/16.
 */
public class AkkaStreamingTransform extends BaseTransform {

    public final static ActorSystem system = ActorSystem.create("QuickStart");
    public final static Materializer materializer = ActorMaterializer.create(system);
    public AkkaStreamingTransform() {
        super();
    }


    public void onNext(DataRow dataRow) {
        steps.get(0).onNext(dataRow);
    }

    @Override
    public void prepare() {
        assert steps.size() > 1;
        Source<DataRow, NotUsed> source = Source.fromPublisher(steps.get(0));
        Sink<DataRow, NotUsed> sink = Sink.fromSubscriber(steps.get(steps.size() - 1));

        for( int i=1; i<steps.size()-1; i++) {
            final Step step = steps.get(i);
            source = source.via( Flow.fromProcessor( () -> step));
        }
        source.to( sink ).run(materializer);
    }

    @Override
    public void shutdown() {

    }
}