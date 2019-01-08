import { run } from '@ember/runloop';
import RangeSlider from 'ember-cli-nouislider/components/range-slider';

export default RangeSlider.extend({
  classNameBindings: [
    'isError',
    'disabled:is-disabled',
    'isReadOnly'
  ],

  isError: false,
  isReadOnly: false,

  connect: true,
  min: 0,
  max: 100,
  tooltips: true,

  format: {
    to(value) {
      return value;
    },
    from(value) {
      return value;
    }
  },

  didInsertElement() {
    // we're extending https://github.com/kennethkalmer/ember-cli-nouislider/blob/master/addon/components/range-slider.js
    // super's version of didInsertElement() only calls super's setup() function,
    // so we'll skip calling this._super() here so it only runs once, and call it from our own setup() below
    this.setup();
  },

  setup() {
    // super's setup() gives a reference to this.slider (also adds event listeners for built in events, etc.)
    this._super();

    // get the slider handle so's we can add listener(s)
    // * if we want to add a listener without jQuery style we can use one of...
    //    - this.slider.target.querySelector('.noUi-handle');
    //    - this.get('element').querySelector('.noUi-handle');
    const sliderHandle = this.sliderHandle = this.$('.noUi-handle');

    // currently only handling sliders using a single handle
    // * if we want to support multi handles we may need to get them separately with one of or combo of...
    //    - class names of 'noUi-handle-lower' & 'noUi-handle-upper'
    //    - data-handle attr value (ex. data-handle="0", data-handle="1", etc.)
    if (sliderHandle.length !== 1) {
      return;
    }

    // add the listener (* keep in mind we can't use an anonymous function if we switch to using add/removeEventListener()'s)
    sliderHandle.on('keydown', (e) => {
      run(this, function() {
        const value = Number(this.slider.get());
        // left arrow
        if (e.which === 37) {
          this.slider.set(value - 1);
          this.sendAction('on-change', value - 1);
        }
        // right arrow
        if (e.which === 39) {
          this.slider.set(value + 1);
          this.sendAction('on-change', value + 1);
        }
      });
    });
  },

  willDestroyElement() {
    // super's version of willDestroyElement() only calls super's teardown() function,
    // so we'll skip calling this._super() here so it only runs once, and call it from our own teardown() below
    this.teardown();
  },

  teardown() {
    // remove all listeners
    this.sliderHandle.off();
    this._super();
  }

});
