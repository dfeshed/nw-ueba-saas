import Component from '@ember/component';

const RsaWizard = Component.extend({
  tagName: 'hbox',
  classNames: ['rsa-wizard-container'],

  // step objects expected to be passed in
  steps: [],
  // initial step ID expected to be passed in
  initialStepId: '',
  // current/active step ID managed internally
  currentStepId: '',
  // closure action expected to be passed in
  transitionToClose: null,
  // boolean expected to be passed in
  isWizardLoading: false,

  init() {
    this._super(...arguments);
    this.set('currentStepId', this.get('initialStepId'));
  },

  actions: {
    transitionToStep(stepId) {
      this.set('currentStepId', stepId);
    }
  }
});

export default RsaWizard;
