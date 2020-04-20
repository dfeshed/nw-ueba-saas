import Checkbox from '@ember/component/checkbox';

export default Checkbox.extend({
  classNames: ['rsa-form-checkbox'],

  classNameBindings: [
    'checked',
    'disabled',
    'error'
  ]
});
