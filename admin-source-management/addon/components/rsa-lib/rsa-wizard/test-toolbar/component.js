import Component from '@ember/component';

const TestToolbar = Component.extend({
  tagName: 'hbox',
  classNames: ['test-toolbar'],

  // step object required to be passed in
  step: null,
  // closure action required to be passed in
  transitionToStep: null,
  // closure action expected to be passed in
  transitionToClose: null,

  actions: {
    transitionToPrevStep() {
      this.get('transitionToStep')(this.get('step').prevStepId);
    },
    transitionToNextStep() {
      this.get('transitionToStep')(this.get('step').nextStepId);
    },
    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default TestToolbar;
