import Component from '@ember/component';

export default Component.extend({
  classNames: 'severity-bar',
  selected: null,
  data: {
    Critical: null,
    High: null,
    Medium: null,
    Low: null
  },
  actions: {
    updateSeverity(severity) {
      if (this.get('updateSeverity')) {
        this.get('updateSeverity')(severity);
      }
    }
  }
});