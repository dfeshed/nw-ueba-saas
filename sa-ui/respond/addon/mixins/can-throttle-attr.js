import Mixin from '@ember/object/mixin';
import { isEmpty } from '@ember/utils';
import { throttle } from '@ember/runloop';

/**
 * @class Can Throttle Attr mixin
 * Enables a Component to throttle changes in the value of a given attribute.
 * That is, copies values from of one attr to another attr, but uses a throttle to invoke the copy operation.
 * Thus the Component can use the (throttled) copy for its computed properties & template bindings.
 * @public
 */
export default Mixin.create({

  /**
   * The name of the property where values will be read from.
   * If empty, then no throttling is done.
   * @type {String}
   * @default ''
   * @public
   */
  throttleFromAttr: '',

  /**
   * The property name where throttled values will be written to.
   * If empty, then the name `${throttleFromAttr}Throttled` will be assumed.
   * @type {String}
   * @default ''
   * @public
   */
  throttleToAttr: '',

  /**
   * Number of milliseconds to space out the updates of the "to" attr.
   * If zero, the values from the "from" attr are copied immediately to the "to" attr.
   * @see https://emberjs.com/api/classes/Ember.run.html#method_throttle
   * @type {Number}
   * @default 0
   * @public
   */
  throttleInterval: 0,


  /**
   * Callback which kicks off the throttle that will update the "to" attr.
   * @private
   */
  throttledValueDidChange() {
    const ms = this.get('throttleInterval');
    if (ms) {

      // We want the throttle callback to trigger on the trailing edge of the time interval, not the leading edge.
      // So we must pass in `immediate` = `false` into `Ember.run.throttle()`.
      // @see https://emberjs.com/api/classes/Ember.Object.html#method_addObserver
      throttle(this, 'copyThrottledAttr', ms, false);
    } else {
      this.copyThrottledAttr();
    }
  },

  /**
   * Copies the current value of the "from" attr to the "to" attr.
   * @private
   */
  copyThrottledAttr() {
    if (this.get('isDestroying') || this.get('isDestroyed')) {
      return;
    }
    const throttleFromAttr = this.get('throttleFromAttr');
    const throttleToAttr = this.get('throttleToAttr') || `${throttleFromAttr}Throttled`;
    this.set(throttleToAttr, this.get(throttleFromAttr));
  },

  // Wire up observer for "from" attr, and sync "to" attr one time for initialization.
  // We must use an observer here, rather than didReceiveAttrs, because we want to support the use-cases where the
  // "from" attr is bound by ember-redux to the redux state; and ember-redux will not fire didReceiveAttrs with each update.
  didInsertElement() {
    this._super();
    const throttleFromAttr = this.get('throttleFromAttr');
    if (!isEmpty(throttleFromAttr)) {
      this.addObserver(throttleFromAttr, this, 'throttledValueDidChange');
      this.copyThrottledAttr();
    }
  },

  // Teardown observer for "from" attr.
  willDestroyElement() {
    this._super();
    const throttleFromAttr = this.get('throttleFromAttr');
    if (!isEmpty(throttleFromAttr)) {
      this.removeObserver(throttleFromAttr, this, 'throttledValueDidChange');
    }
  }
});
