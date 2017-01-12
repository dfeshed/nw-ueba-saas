import Ember from 'ember';

const {
  Checkbox
} = Ember

export default Checkbox.extend({
  classNames: ['rsa-form-checkbox-2'],

  classNameBindings: [
    'checked',
    'disabled',
    'error'
  ]
});
