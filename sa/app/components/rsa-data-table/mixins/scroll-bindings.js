/**
 * @file Size Bindings mixin
 * Equips an Ember Component with `scrollLeft` and `scrollTop` attributes, which are read from the
 * Component's `element`. Basically, it allows a Component to expose its scroll position as observable attributes.
 *
 * Note: Assumes the dom-is-ready Mixin is already applied! Needs it to manage the `domIsReady` attribute.
 * @see ./mixins/dom-is-ready
 *
 * @public
 */
import Ember from 'ember';

const {
  computed,
  run,
  Mixin
} = Ember;

export default Mixin.create({

  /**
   * Enables/disables the binding of the Component's `scrollLeft` & `scrollTop` attributes.  When `true`,
   * those attributes will be live-updated to match the corresponding properties of the Component's `element`.
   * @type {string[]}
   * @default []
   * @public
   */
  _scrollBindingsEnabled: true,
  scrollBindingsEnabled: computed({
    get() {
      return this.get('_scrollBindingsEnabled');
    },
    set(key, value) {
      this.set('_scrollBindingsEnabled', !!value);
      this._scrollBindingsConfigDidChange();
      return !!value;
    }
  }),

  // Responds to change in `scrollBindingsEnabled` by attaching/detaching resize event listeners.
  _scrollBindingsConfigDidChange() {
    if (this.get('domIsReady') && this.get('scrollBindingsEnabled')) {
      this._initScrollBindings();
    } else {
      this._teardownScrollBindings();
    }
  },

  // Attach a scroll listener, and call it manually the 1st time.
  _initScrollBindings() {
    let { element } = this;
    if (!element) {
      return;
    }
    let callback = (() => {
      run.throttle(this, function() {
        this.setProperties({
          scrollLeft: element.scrollLeft,
          scrollTop: element.scrollTop
        });
      }, 51);
    });
    this.set('_scrollBindingsCallback', callback);
    this.$().on('scroll', callback);
    callback();
  },

  // Detach the last scroll listener.
  _teardownScrollBindings() {
    if (this.get('_scrollBindingsCallback')) {
      this.$().off('scroll', this.get('_scrollBindingsCallback'));
      this.set('_scrollBindingsCallback', null);
    }
  },

  // Callback that gets notified when Component `element` is ready to be measured.
  // @see mixins/dom-is-ready
  onDomIsReady() {
    this._super(...arguments);
    this._scrollBindingsConfigDidChange();
  },

  // Stop firing callbacks while element is destroying or you'll get Ember errors.
  willDestroyElement() {
    this._teardownScrollBindings();
    this._super(...arguments);
  }
});
