package info.matsumana.psystrike.service;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.linecorp.armeria.common.HttpObject;

import lombok.AccessLevel;
import lombok.Getter;

public class HttpMessagePublisher implements Publisher<HttpObject> {

    @Getter(AccessLevel.PACKAGE)
    private Subscriber<? super HttpObject> subscriber;

    @Override
    public void subscribe(Subscriber<? super HttpObject> subscriber) {
        this.subscriber = subscriber;
    }
}
