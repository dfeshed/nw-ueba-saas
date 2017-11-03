import Ember from 'ember';

const {
  Controller
} = Ember;

export default Controller.extend({

  lastEvent: 'Click in and then out of the textarea',

  actions: {
    handleFocus() {
      this.set('lastEvent', 'Focused');
    },
    handleBlur() {
      this.set('lastEvent', 'Blurred');
    }
  }
});
