import RangeSlider from 'ember-cli-nouislider/components/range-slider';

export default RangeSlider.extend({
  classNameBindings: [
    'isError',
    'disabled:is-disabled',
    'isReadOnly'],

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
  }
});
