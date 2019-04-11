import Component from '@ember/component';

export default Component.extend({
  tagName: '',
  actions: {
    initiateAlert(alertId) {
      this.get('initAlert')(alertId);
    }
  }
});
