/**
 * @file Stream abstract base class
 * Represents an observable sequence of values (e.g., events, data objects, anything).
 * Essentially a much simpler, less-functional version of RxJS.Observable.
 * @see https://github.com/Reactive-Extensions/RxJS/blob/master/doc/api/core/observable.md
 * @public
 */
import Ember from 'ember';
import { FromSocket, FromArray, ToArray } from './mixins';
const {
  Object: EmberObject,
  run
} = Ember;

export default EmberObject.extend(FromSocket, FromArray, ToArray, {

  hasBeenStopped: false,

  /**
   * Registers an observer to this stream.
   * The observer will have its callbacks notified whenever this stream emits a value.
   * If `autoStart()` has been called on this stream, the stream will call its own `start()` method after this
   * subscription is registered.
   * @returns {object} Subscription object, if successful; null otherwise.
   * @public
   */
  subscribe(observer) {
    const sub = {
      observer,
      stream: this,
      dispose() {
        this.stream.unsubscribe(this);
      }
    };
    this._subscriptions.push(sub);

    if (this._autoStart) {
      run(this, 'start');
    }
    return sub;
  },

  /**
   * Removes a subscription object from this stream.
   * Stops the notifications to the observer in the subscription.
   * @param {object} sub Subscription to be disposed.
   * @returns {object} This instance, for chaining.
   * @public
   */
  unsubscribe(sub) {
    this._subscriptions.removeObject(sub);
    return this;
  },

  /**
   * Tells this stream to automatically start itself after it has been subscribed to.
   * @param {boolean} flag If explicitly set to `false`, cancels the autoStart functionality; otherwise ignored.
   * @returns This instance, for chaining.
   * @public
   */
  autoStart(flag) {
    this._autoStart = (flag !== false);
    return this;
  },

  /**
   * Starts the data flow in the stream.
   * The callbacks will be passed whatever params are passed into this method.
   * The callbacks are also passed a reference to the `stop` function which
   * can be used to stop a stream at any time
   * @returns {object} This instance, for chaining.
   * @public
   */
  start() {
    this._notifyAll('onInit', [this.stop.bind(this), ...arguments]);
    this._super();
    return this;
  },

  /**
   * Stops the data flow in the stream.
   * The callbacks will be passed whatever params are passed into this method.
   * @returns {object} This instance, for chaining.
   * @public
   */
  stop() {
    // Do not notify if we have already notified
    if (!this.get('hasBeenStopped')) {
      this._notifyAll('onStopped', arguments);
      this.set('hasBeenStopped', true);
      this._super();
    }
    return this;
  },

  /**
   * Invokes the onNext callbacks (if any) of all observers.
   * The callbacks will be passed whatever params are passed into this method.
   * @returns {object} This instance, for chaining.
   * @public
   */
  next() {
    this._notifyAll('onNext', arguments);
    this._super(...arguments);
    return this;
  },

  /**
   * Invokes the onError callbacks (if any) of all observers, then disposes all subscriptions.
   * The callbacks will be passed whatever params are passed into this method.
   * @returns {object} This instance, for chaining.
   * @public
   */
  error() {
    this._notifyAll('onError', arguments)._disposeAll();
    this._super(...arguments);
    return this;
  },

  /**
   * Invokes the onCompleted callbacks (if any) of all observers, then disposes all subscriptions.
   * The callbacks will be passed whatever params are passed into this method.
   * @returns {object} This instance, for chaining.
   * @public
   */
  completed() {
    this._notifyAll('onCompleted', arguments)._disposeAll();
    this._super(...arguments);
    return this;
  },

  /**
   * Invokes the onTimout callbacks (if any) of all observers.
   * The timeout default wait time could be set using `streamOptions: {timeoutWait}`
   * The callbacks will be passed whatever params are passed into this method.
   * @returns {object} This instance, for chaining.
   * @public
   */
  timeout() {
    this._notifyAll('onTimeout', arguments);
    this._super(...arguments);
    return this;
  },

  /**
   * Invokes the given type of callbacks (if any) of all observers for this stream instance.
   * The callbacks will be passed whatever args array passed into this method, plus one additional
   * argument, which is a handle to this stream instance itself.
   * @param {string} type Either 'onNext', 'onError' or 'onCompleted'
   * @param {*[]} args The arguments to pass to the callbacks.
   * @returns {object} This instance, for chaining.
   * @private
   */
  _notifyAll(type, args) {
    const resolvedArgs = [].slice.call(args || []).concat([this]);

    this._subscriptions.forEach(function(sub) {
      const o = sub.observer;
      if (o[type]) {
        o[type](...resolvedArgs);
      }
    });
    return this;
  },

  /**
   * Removes all subscriptions from this stream instance.
   * @returns {object} This instance, for chaining.
   * @private
   */
  _disposeAll() {
    this._subscriptions.length = 0;
    return this;
  },

  // Initializes internal cache of subscriptions to this stream instance.
  init() {
    this._super(...arguments);
    this._subscriptions = [];
  },

  // Tears down internal cache of subscriptions to this stream instance.
  destroy() {
    this.stop();
    this._disposeAll();
    this._super(...arguments);
  }
});
