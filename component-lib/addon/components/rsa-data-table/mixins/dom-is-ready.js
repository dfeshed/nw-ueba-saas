/**
 * @file RSA DOM Watcher mixin.
 * Equips a component with a `domIsReady` attribute, which indicates when its DOM is ready for manipulation.
 * @public
 */
import $ from 'jquery';
import Mixin from 'ember-metal/mixin';
import run from 'ember-runloop';

export default Mixin.create({

  /**
   * This property will be set to `true` by this mixin after the consuming component's element has been rendered and
   * inserted and is ready for manipulation. This is a useful indicator, because if the consuming component tries
   * to manipulate its DOM before `domIsReady` is true, Ember will throw warnings about performance degradation.
   * Also, if a component wants to measure its DOM, it should wait until `domIsReady`, because HTML reflows can
   * still be in-progress beforehand, which may subsequently cause the DOM to resize.
   * @type {boolean}
   * @public
   */
  domIsReady: false,

  /**
   * Configurable callback to be invoked when `domIsReady` gets set to `true`.
   * @type {function}
   * @public
   */
  onDomIsReady: null,

  didInsertElement() {
    this._super(...arguments);
    run.schedule('afterRender', this, this.afterRender);
  },

  // Responsible for setting `domIsReady` to `true` and invoking `onDomIsReady` callback.
  // Does this after executing any inherited logic from `_super`. Why? Just in case the inherited logic does any DOM
  // manipulation. We'd like `domIsReady` to indicate that DOM is settled.
  afterRender() {
    this._super(...arguments);
    this.set('domIsReady', true);
    if ($.isFunction(this.onDomIsReady)) {
      this.onDomIsReady();
    }
  }
});
