package info.matsumana.psystrike.service;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.linecorp.armeria.common.HttpObject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class HttpMessagePublisher implements Publisher<HttpObject> {

    @Getter(AccessLevel.PACKAGE)
    private Subscriber<? super HttpObject> subscriber;

//    @Setter(AccessLevel.PACKAGE)
//    private Subscription subscription;

    @Override
    public void subscribe(Subscriber<? super HttpObject> subscriber) {
        this.subscriber = subscriber;

//        if (subscription != null) {
//            subscription.request(1);
//        }

//        subscriber.onSubscribe(subscription);
    }
}
