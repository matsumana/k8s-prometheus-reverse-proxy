package info.matsumana.psystrike.service;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.linecorp.armeria.common.HttpObject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpMessageSubscriber implements Subscriber<HttpObject> {

    private final HttpMessagePublisher publisher;
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
//        subscription.request(Long.MAX_VALUE);
        subscription.request(1);
        publisher.getSubscriber().onSubscribe(subscription);
//        subscription.request(1);
    }

    @Override
    public void onNext(HttpObject httpObject) {
        subscription.request(1);
        publisher.getSubscriber().onNext(httpObject);
    }

    @Override
    public void onError(Throwable t) {
        publisher.getSubscriber().onError(t);
    }

    @Override
    public void onComplete() {
        publisher.getSubscriber().onComplete();
    }
}
