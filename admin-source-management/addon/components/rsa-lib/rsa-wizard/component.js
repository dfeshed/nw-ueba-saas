import Component from '@ember/component';

const RsaWizard = Component.extend({
  tagName: 'hbox',
  classNames: ['rsa-wizard-container'],

  // step objects expected to be passed in
  steps: [],
  // current step ID initially expected to be passed in
  currentStepId: '',
  // closure action expected to be passed in
  transitionToClose: null,
  // boolean expected to be passed in
  isWizardLoading: false,

  actions: {
    transitionToStep(stepId) {
      this.set('currentStepId', stepId);
    }
  }
});

export default RsaWizard;
