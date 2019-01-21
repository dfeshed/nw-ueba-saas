import EmberObject from '@ember/object';
import { Promise } from 'rsvp';

const Channel = EmberObject.extend({

  /**
   * @private
   * This holds a reference to the transport service. It is used to send
   * messages once they have had their route added by this class' send method.
   */
  transport: null,

  /**
   * @private
   * A two-item array containing the route as returned by the addChannel call
   * in transport.
   */
  route: null,

  /**
   * @private
   * Once this instance's send function has been called, this is set to a
   * callback function. Depending on the parameters passed to send, it could
   * be a standard callback function, or it could be a resolve function for a
   * promise.
   */
  pendingMessage: null,

  /**
   * @public
   * @param {*} message - A parsed object
   * This method is called by transport.handleIncomingMessage if it receives
   * a message destined for this channel. All messages that are being received
   * in channels are handled here, messages receieved outside of channels are
   * handled in transport.
   */
  handleIncomingChannelMessage(message) {
    this.get('pendingMessage')({ message, channel: this });
  },

  /**
   * Sends a message through the channel. If a callback is not provided, it
   * returns a promise instead. This can be useful if you know the message you
   * are sending will only return a single response. For use with chaining
   * promises, the callback returns an object which contains the keys message
   * and channel. This way chaining multiple send calls from transport.addChannel
   * is easier.
   * @public
   * @param {*} message - The message to send (in object form)
   * @param {*} callback - Called when a response is received. Could be called multiple times.
   * @param {*} errCallback - Called if there is an error sending the message
   */
  send(message, callback, errCallback) {
    let promise, onError;
    if (callback) {
      this.set('pendingMessage', callback);
      onError = errCallback;
    } else {
      promise = new Promise((resolve, reject) => {
        this.set('pendingMessage', resolve);
        onError = reject;
      });
    }
    this.get('transport').sendMessage({
      ...message,
      route: this.get('route')
    }, onError);
    if (!callback) {
      return promise;
    }
  },

  /**
   * @public
   * Deletes the channel and removes it from the hash of channels in transport
   */
  delete() {
    this.get('transport').sendMessage({
      message: 'delete',
      route: [ this.get('route')[0] ],
      params: {
        reason: 'complete'
      }
    }, (err) => {
      throw new Error(err);
    });
    this.get('transport').unregisterChannel(this);
    this.destroy();
  }
});

export default Channel;