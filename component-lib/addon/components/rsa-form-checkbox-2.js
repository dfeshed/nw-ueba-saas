import Ember from 'ember';

const {
  Checkbox
} = Ember

export default Checkbox.extend({
  classNameBindings: [
    'checked',
    'disabled'
  ]
});
