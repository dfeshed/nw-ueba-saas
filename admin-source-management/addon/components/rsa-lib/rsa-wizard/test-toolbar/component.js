import Component from '@ember/component';

const TestToolbar = Component.extend({
  tagName: 'hbox',
  classNames: ['test-toolbar'],

  // step object required to be passed in
  step: undefined,
  // closure action required to be passed in
  transitionToStep: undefined,

  actions: {
    transitionToPrevStep() {
      this.get('transitionToStep')(this.get('step').prevStepId);
    },
    transitionToNextStep() {
      this.get('transitionToStep')(this.get('step').nextStepId);
    }
  }

});

export default TestToolbar;
