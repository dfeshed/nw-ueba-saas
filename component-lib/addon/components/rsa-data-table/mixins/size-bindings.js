/**
 * @file Size Bindings mixin
 * Equips an Ember Component with `clientWidth` and `clientHeight` attributes, which are read from the
 * Component's `element`. Basically, it allows a Component to expose its size as observable attributes.
 * The implementation leveraeges the 3rd party `javascript-detect-element-resize` library (imported via NPM).
 * @see https://github.com/sdecima/javascript-detect-element-resize
 *
 * @assumes dom-is-ready Mixin is already applied! Needs it to manage the `domIsReady` attribute.
 * @see ./mixins/dom-is-ready
 *
 * @assumes javascript detect-element-resize library has imported globals addResizeListener & removeResizeListener
 * @see https://github.com/sdecima/javascript-detect-element-resize
 *
 * @public
 */
import Ember from 'ember';
/* global addResizeListener */
/* global removeResizeListener */

const {
  computed,
  run,
  Mixin,
  $
  } = Ember;

export default Mixin.create({

  /**
   * Optional configurable callback to be invoked when a DOM size change is detected.
   * @type {function}
   * @public
   */
  sizeDidChange: null,

  /**
   * Enables/disables the binding of the Component's `clientWidth` & `clientHeight` attributes.  When `true`,
   * those attributes will be live-updated to match the corresponding properties of the Component's `element`.
   * @type {string[]}
   * @default []
   * @public
   */
  _sizeBindingsEnabled: true,
  sizeBindingsEnabled: computed({
    get() {
      return this.get('_sizeBindingsEnabled');
    },
    set(key, value) {
      this.set('_sizeBindingsEnabled', !!value);
      this._sizeBindingsConfigDidChange();
      return !!value;
    }
  }),

  // Responds to change in `sizeBindingsEnabled` by attaching/detaching resize event listeners.
  _sizeBindingsConfigDidChange() {
    if (this.get('domIsReady') && this.get('sizeBindingsEnabled')) {
      this._initSizeBindings();
    } else {
      this._teardownSizeBindings();
    }
  },

  // Attach a resize listener, and call it manually the 1st time.
  _initSizeBindings() {
    let { element } = this;
    if (!element) {
      return;
    }
    let callback = run.bind(this, function() {
      this.setProperties({
        clientWidth: element.clientWidth,
        clientHeight: element.clientHeight
      });
      if ($.isFunction(this.sizeDidChange)) {
        this.sizeDidChange();
      }
    });
    this.set('_sizeBindingsCallback', callback);
    addResizeListener(element, callback);
    callback();
  },

  // Detach the last resize listener, if any.
  _teardownSizeBindings() {
    if (this.get('_sizeBindingsCallback')) {
      removeResizeListener(this.element, this.get('_sizeBindingsCallback'));
      this.set('_sizeBindingsCallback', null);
    }
  },

  // Callback that gets notified when Component `element` is ready to be measured.
  // @see mixins/dom-is-ready
  onDomIsReady() {
    this._super(...arguments);
    this._sizeBindingsConfigDidChange();
  },

  // Stop firing callbacks while element is destroying or you'll get Ember errors.
  willDestroyElement() {
    this._teardownSizeBindings();
    this._super(...arguments);
  }
});
