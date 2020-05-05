package info.matsumana.psystrike.service;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.linecorp.armeria.common.HttpObject;

public class HttpMessagePipe implements Subscriber<HttpObject>, Publisher<HttpObject> {

    private Subscription subscription;
    private Subscriber<? super HttpObject> subscriber;

    // Subscriber
    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;

        if (subscriber != null) {
            subscription.request(1);
        }
    }

    @Override
    public void onNext(HttpObject httpObject) {
        subscription.request(1);
        subscriber.onNext(httpObject);
    }

    @Override
    public void onError(Throwable t) {
        subscriber.onError(t);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    // Publisher
    @Override
    public void subscribe(Subscriber<? super HttpObject> subscriber) {
        this.subscriber = subscriber;

        if (subscription != null) {
            subscription.request(1);
        }
    }
}
