import Component from '@ember/component';

const RsaWizard = Component.extend({
  tagName: 'hbox',
  classNames: ['rsa-wizard-container'],

  steps: [],
  currentStepId: '',

  actions: {
    transitionToStep(stepId) {
      this.set('currentStepId', stepId);
    }
  }
});

export default RsaWizard;
