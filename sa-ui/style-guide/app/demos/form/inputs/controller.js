import Controller from '@ember/controller';

export default Controller.extend({

  lastEvent: 'Click in and then out of the input',

  actions: {
    handleFocus() {
      this.set('lastEvent', 'Focused');
    },
    handleBlur() {
      this.set('lastEvent', 'Blurred');
    }
  }
});
