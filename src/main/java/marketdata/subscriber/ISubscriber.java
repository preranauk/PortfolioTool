package marketdata.subscriber;

// Subscriber interface
interface ISubscriber<T> {
    void onEvent(T event);
}
