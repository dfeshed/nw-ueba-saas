/**
 * @file RSA DOM Watcher mixin.
 * Equips a component with a `domIsReady` attribute, which indicates when its DOM is ready for manipulation.
 * @public
 */
import Ember from 'ember';

const {
  run,
  Mixin,
  $
} = Ember;

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
    run.schedule('afterRender', this, function() {
      this.set('domIsReady', true);
      if ($.isFunction(this.onDomIsReady)) {
        this.onDomIsReady();
      }
    });
  }
});
