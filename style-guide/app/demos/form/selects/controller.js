import Ember from 'ember';

const {
  Controller
} = Ember;

const options = [ 'Foo', 'Bar', 'Baz' ];

export default Controller.extend({

  selected: null,

  options,

  isDisabled: true,

  actions: {
    setSelect(val) {
      this.set('selected', val);
    }
  }

});
