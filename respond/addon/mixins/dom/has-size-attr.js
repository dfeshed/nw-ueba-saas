import Mixin from '@ember/object/mixin';
import { run, next, debounce } from '@ember/runloop';
import { isEmpty } from '@ember/utils';

/* global addResizeListener */
/* global removeResizeListener */

/**
 * @class HasSizeAttr Mixin
 * Equips a Component with a read-only `size` attr that tracks the Component's DOM element's size.
 *
 * Wires up the consuming Component's `size` attr to the measured dimensions of a configurable DOM element (by default,
 * the Component's `element`).  Essentially this enables the Component to expose its size as observable Ember properties.
 * The `size` attr will be set to an object with the properties `innerWidth`, `innerHeight`, `outerWidth` & `outerHeight`.
 * The attr name `size` is configurable, such that the properties will be written to some other attr if desired.
 *
 * This implementation leverages the 3rd party `javascript-detect-element-resize` library in order to listen for
 * resize events in a given arbitrary DOM node.
 * @see https://github.com/sdecima/javascript-detect-element-resize
 *
 * @public
 */
export default Mixin.create({
  /**
   * If true, this mixin's functionality will automatically be started up after `didInsertElement`.
   * Otherwise if falsey, it won't automatically wire up attrs to its DOM's size, but you can still
   * start this mixin's functionality by calling `initSizeAttrs` programmatically.
   * @type {Boolean}
   * @default true
   * @public
   */
  autoEnableSizeAttr: true,

  /**
   * Configurable selector for the DOM element whose resize events to listen for. If empty, `this.element` is assumed.
   * @type {String}
   * @public
   */
  sizeSelector: '',

  /**
   * Configurable debounce interval (in millisec) for resize events.
   * @see Ember.run.debounce
   * @type {Number}
   * @public
   */
  sizeDebounce: 0,

  /**
   * Configurable prefix for the path of the attrs which will be written by this mixin.
   * @example By default, `sizeAttr` is 'dom' so this mixin will write to the properties 'domInnerWidth',
   * 'domInnerHeight', 'domOuterWidth' & 'domOuterHeight'. But if `sizeAttr` were set to 'foo.bar' then this mixin
   * will try to write to the property paths 'foo.barInnerWidth', 'foo.barInnerHeight', 'foo.barOuterWidth' & 'foo.barOuterHeight'.
   * @default 'dom'
   * @type {String}
   * @public
   */
  sizeAttr: 'size',

  /**
   * Returns the DOM element whose resize events to listen for.
   * @returns {Object}
   * @private
   */
  getSizeElement() {
    const { element, sizeSelector } = this.getProperties('element', 'sizeSelector');
    return isEmpty(sizeSelector) ? element : (element && this.$(sizeSelector)[0]);
  },

  /**
   * Returns a callback function that reads the size properties from `sizeElement` and writes them to the attrs
   * of this Component.
   * @returns {Function}
   * @private
   */
  getSizeCallback() {
    const { sizeDebounce, sizeAttr } = this.getProperties('sizeDebounce', 'sizeAttr');
    const sizeElement = this.getSizeElement();
    if (!sizeElement) {
      return null;
    }
    const update = function() {
      this.set(sizeAttr, {
        innerWidth: sizeElement.clientWidth,
        innerHeight: sizeElement.clientHeight,
        outerWidth: sizeElement.offsetWidth,
        outerHeight: sizeElement.offsetHeight
      });
    };
    if (sizeDebounce) {
      return () => {
        debounce(this, update, sizeDebounce);
      };
    } else {
      return () => {
        run(this, update);
      };
    }
  },

  // Attach a resize listener, and call it manually the 1st time.
  initSizeAttr() {
    const sizeCallback = this.getSizeCallback();
    const sizeElement = this.getSizeElement();
    if (!sizeCallback || !sizeElement) {
      return;
    }

    // Attach callback.
    addResizeListener(sizeElement, sizeCallback);

    // Fire it once manually to initialize the size attrs.
    sizeCallback();

    this.set('sizeAttrInitialized', {
      sizeElement,
      sizeCallback
    });
  },

  // Detach the last resize listener.
  teardownSizeAttr() {
    // If a resize listener wasn't already attached, nothing to do here.
    // For example, if `autoEnableSizeAttr` was `false`, but then set to `true` long after didInsertElement.
    if (!this.get('sizeAttrInitialized')) {
      return;
    }

    const { sizeCallback, sizeElement } = this.get('sizeAttrInitialized');
    if (sizeElement && sizeCallback) {
      removeResizeListener(sizeElement, sizeCallback);
    }
  },

  didInsertElement() {
    this._super(...arguments);
    if (this.get('autoEnableSizeAttr')) {
      next(this, 'initSizeAttr');
    }
  },

  willDestroyElement() {
    if (this.get('autoEnableSizeAttr')) {
      this.teardownSizeAttr();
    }
    this._super(...arguments);
  }
});
