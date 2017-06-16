import Checkbox from 'ember-components/checkbox';

export default Checkbox.extend({
  classNames: ['rsa-form-checkbox'],

  classNameBindings: [
    'checked',
    'disabled',
    'error'
  ]
});