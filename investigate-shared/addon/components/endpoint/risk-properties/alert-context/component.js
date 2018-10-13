import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  layout,

  classNames: ['alert-context'],

  didRender() {
    this._super(arguments);
    const contexts = this.get('contexts');
    if (!this.get('selectedAlert') && contexts) {
      // Select first alert as default. This action sets the alert active and fetches corresponding events
      if (this.get('setAlertAction')) {
        this.get('setAlertAction')(contexts[0]);
      }
    }
  },

  actions: {
    handleClick(context) {
      this.get('setAlertAction')(context);
    }
  }
});