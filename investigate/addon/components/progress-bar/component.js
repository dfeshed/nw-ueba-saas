import Ember from 'ember';

const { computed, run, Component, $ } = Ember;

export default Component.extend({
  classNames: 'rsa-progress-bar',
  classNameBindings: ['enabled'],
  attributeBindings: ['percent:data-percent'],

  /**
   * If true, indicates that progress is on-going and may change imminently. For cosmetics.
   * @type {boolean}
   * @public
   */
  enabled: false,

  /**
   * Number between 0 & 100 (inclusive) that indicates the amount of progress to be displayed.
   * If a non-numeric value is applied, 0 is assumed.  Numeric values less than zero will result in zero.
   * Numeric values greater than 100 will result in 100.  Fractional values between 0-100 are rounded to nearest integer.
   * @type {number}
   * @default 0
   * @public
   */
  _percent: 0,
  percent: computed({
    get() {
      return this._percent;
    },
    set(key, value) {
      if (!$.isNumeric(value)) {
        value = 0;
      }
      this._percent = value = Math.round(Math.min(100, Math.max(0, value)));
      this._percentDidChange();
      return value;
    }
  }),

  // Updates the DOM with the latest `percent` value.
  _percentDidChange() {
    if (this._$fill) {
      this._$fill.css('flex-basis', `${this._percent}%`);
    }
  },

  // Initialize the percent display in DOM after the first render.
  didInsertElement() {
    this._super(...arguments);

    // Cache reference to the DOM element whose size varies according to `percent`.
    this._$fill = this.$('.js-progress-bar__fill');

    run.schedule('afterRender', this, this._percentDidChange);
  },

  willDestroyElement() {
    this._$fill = null;
  }
});
