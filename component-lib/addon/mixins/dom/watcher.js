/**
 * @file RSA DOM Watcher mixin.
 * Enables a component to observe when its DOM is ready for manipulation; and
 * to observe changes in its element's DOM properties (e.g., scroll position and/or
 * inner size) by continually watching and caching those property values in observable component attributes
 * (e.g, "scrollTop", "clientHeight").
 * A simplified and slightly more generalized version of ember-collection's ember-native-scrollable component.
 * @see https://github.com/emberjs/ember-collection
 * @public
 */
import Ember from 'ember';

const {
  Mixin,
  computed,
  run
} = Ember;

export default Mixin.create({

  /**
   * This property will be set to `true` by this mixin after the consuming component's element has been rendered and
   * inserted and is ready for manipulation. This is a useful indicator, because if the consuming component tries
   * to manipulate its DOM before `domIsReady` is true, Ember will throw warnings about performance degradation.
   * @readonly
   * @type {boolean}
   * @public
   */
  domIsReady: false,

  /**
   * If false, this mixin will stop updating the watched public attributes (`scrollTop`, `clientHeight`, etc) of the object
   * that is using it. This effectively disables the mixin's functionality. Used as an optimization, when the
   * functionality needs to be temporarily disabled due to scarce memory resources.
   * @type {boolean}
   * @default true
   * @public
   */
  _watchEnabled: true,
  watchEnabled: computed({
    get() {
      return this.get('_watchEnabled');
    },

    set(key, value) {
      this.set('_watchEnabled', value);
      this._watchEnabledDidChange();
      return value;
    }
  }),

  /**
   * Responds to change in `watchEnabled` by setting up or tearing down listeners for the watched properties.
   * @usage Called after didInsertElement, and after change in `silentScrolling`.
   * @private
   */
  _watchEnabledDidChange() {
    if (this.get('_watchEnabled')) {
      this._startWatch();
    } else {
      this._stopWatch();
    }
  },

  /**
   * List of DOM properties that this mixin will watch in this component's `this.element`.
   * For each given DOM property, an attribute of the same name will be populated on this instance
   * at run-time, as long as `watchEnabled` is truthy.
   * @type {string[]}
   * @default []
   * @public
   */
  watchBindings: [],

  /**
   * Configurable callback to be invoked whenever one or more watched properties changes value.
   * This is an alternative to defining observes on the individual watched properties. The difference here is
   * that this callback will only be invoked once if 2+ properties change simultaneously.
   * This function will be invoked with the following arguments:
   * `observed`: a handle to this instance;
   * `diff`: a hash of newly changed property values, where each hash key is the name of a watched property whose value
   * has changed, and each hash value is an object with properties `was` and `current`.
   * @type {function}
   * @public
   */
  watchedDidChange: null,

  /**
   * Kicks off an animation thread that will continually read the watched properties of this component's
   * element. The latest results are cached into a private hash so they can be compared against subsequent future
   * results.
   * The benefit of using an animation thread for this purpose is that size changes can be detected even if they
   * are indirectly caused by HTML reflows anywhere on the page. This way there's no need to attach resize listeners to
   * individual HTML elements, which is ideal, because generally speaking this component is not aware of other
   * HTML elements on the page.
   * @private
   */
  _startWatch() {
    const me = this;

    function step() {
      me._doWatch();
      nextStep();
    }
    function nextStep() {
      if (window.requestAnimationFrame) {
        me._animationFrame = requestAnimationFrame(step);
      }
    }

    // Performance optimization: cache & validate some locales just once at the start of the watch.
    this._watchedDidChange = (typeof this.get('watchedDidChange') === 'function') ? this.get('watchedDidChange') : null;
    this._watchBindings = this.get('watchBindings') || [];
    this._lastWatchedValues = {};

    if (this._watchBindings.length) {
      step();
    }
  },

  /**
   * Aborts the currently running animation thread (if any) that is monitoring properties in this component's .element.
   * Assumes a handle to the animation thread would be cached.
   * @private
   */
  _stopWatch() {
    if (this._animationFrame) {
      cancelAnimationFrame(this._animationFrame);
      this._animationFrame = undefined;
    }
  },

  /**
   * Reads the properties of this component's element. The results are cached into the `watched` public attr if they
   * have changed from their previous inspection.
   * @private
   */
  _doWatch() {
    let { element } = this;
    if (!element) {
      return;
    }

    let changed = false;
    let last = this._lastWatchedValues;
    let replace = {};
    let diff = {};

    this._watchBindings.forEach((prop) => {
      let current = element[prop];
      let was = last[prop];

      if (current !== was) {
        last[prop] = replace[prop] = current;
        diff[prop] = { was, current };
        changed = true;
      }
    });

    if (changed) {
      run(() => {
        this.setProperties(replace);
        if (this._watchedDidChange) {
          this._watchedDidChange(this, diff);
        }
      });
    }
  },

  didInsertElement() {
    run.schedule('afterRender', this, function() {
      this.set('domIsReady', true);
      this._watchEnabledDidChange();
    });
    this._super(...arguments);
  },

  willDestroyElement() {
    this._stopWatch();
    this._super(...arguments);
  }
});
