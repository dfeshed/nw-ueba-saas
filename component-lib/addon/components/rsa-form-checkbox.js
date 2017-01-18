import Ember from 'ember';

const {
  Checkbox
} = Ember

export default Checkbox.extend({
  classNames: ['rsa-form-checkbox'],

  classNameBindings: [
    'checked',
    'disabled',
    'error'
  ]
});
