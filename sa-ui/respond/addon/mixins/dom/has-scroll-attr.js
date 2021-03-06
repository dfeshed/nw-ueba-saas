import { computed } from '@ember/object';
import Mixin from '@ember/object/mixin';
import { join, next, debounce } from '@ember/runloop';
import { isEmpty } from '@ember/utils';

/**
 * @class HasScrollAttr Mixin
 * Equips a Component with a read-only `scroll` attr that tracks the Component's DOM element's scrolling viewport.
 *
 * Wires up the consuming Component's `scrollLeft` & `scrollTop` attrs to the `scrollLeft` & `scrollTop` of a
 * configurable DOM element (by default, the Component's `element`).  Essentially this enables the Component to expose
 * its scroll coordinates as observable Ember properties.
 *
 * @public
 */
export default Mixin.create({
  /**
   * Configurable selector for the DOM element whose scroll events to listen for. If empty, `this.element` is assumed.
   * @type {String}
   * @public
   */
  scrollSelector: '',

  /**
   * Configurable debounce interval (in millisec) for scroll events.
   * @see Ember.run.debounce
   * @type {Number}
   * @public
   */
  scrollDebounce: 0,

  /**
   * Configurable prefix for the path of the attrs which will be written by this mixin.
   * @example By default, `scrollAttr` is 'scroll' so this mixin will write to the properties 'scrollLeft' &
   * 'scrollTop'. But if `scrollAttr` were set to 'foo.bar' then this mixin will try to write to the
   * property paths 'foo.barLeft' & 'foo.barTop'
   * @example If `scrollAttr` is 'foo.', then this mixin will write to the properties `foo.scrollLeft` & `foo.scrollTop`.
   * @default 'scroll'
   * @type {String}
   * @public
   */
  scrollAttr: 'scroll',

  /**
   * The DOM element whose scroll events to listen for.
   * @type {Object}
   * @private
   */
  scrollElement: computed('element', 'scrollSelector', function() {
    return isEmpty(this.scrollSelector) ? this.element : (this.element && this.element.querySelector(this.scrollSelector));
  }),

  /**
   * A callback function that reads the `scroll*` properties from `scrollElement` and writes them to the `scroll*` attrs
   * of this Component.
   * @type {Function}
   * @private
   */
  scrollCallback: computed('scrollElement', 'scrollDebounce', function() {
    if (!this.scrollElement) {
      return null;
    }
    const update = function() {
      const scrollAttr = this.get('scrollAttr');
      this.set(scrollAttr, {
        top: this.scrollElement.scrollTop,
        left: this.scrollElement.scrollLeft
      });
    };
    if (this.scrollDebounce) {
      return (() => {
        debounce(this, update, this.scrollDebounce);
      });
    } else {
      return (() => {
        join(this, update);
      });
    }
  }),

  // Attach a scroll listener, and call it manually the 1st time.
  initScrollAttr() {
    const { scrollCallback, scrollElement } = this.getProperties('scrollCallback', 'scrollElement');
    if (!scrollCallback) {
      return;
    }

    // Attach callback.
    scrollElement.addEventListener('scroll', scrollCallback);

    // caching the scrollback function and element used, as re-computing scrollElement would
    // re-compute scrollCallback which results in a new function other than the one attached to eventLisitener
    // which results in removeEventListener failing.
    this.set('scrollCallbackCached', scrollCallback);
    this.set('scrollElementCached', scrollElement);

    // Fire it once manually to initialize the `scroll*` properties.
    scrollCallback();
  },

  // Detach the last scroll listener.
  teardownScrollAttr() {
    const scrollElement = this.get('scrollElementCached');
    if (scrollElement) {
      scrollElement.removeEventListener('scroll', this.get('scrollCallbackCached'));
    }
  },

  didInsertElement() {
    this._super(...arguments);
    next(this, 'initScrollAttr');
  },

  willDestroyElement() {
    this.teardownScrollAttr();
    this._super(...arguments);
  }
});
