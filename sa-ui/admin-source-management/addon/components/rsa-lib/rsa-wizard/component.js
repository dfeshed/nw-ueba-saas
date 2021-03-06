import Component from '@ember/component';
import { computed } from '@ember/object';

const RsaWizard = Component.extend({
  tagName: 'hbox',
  classNames: ['rsa-wizard-container'],

  // step objects expected to be passed in
  steps: null,
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
    this.steps = this.steps || [];
    this.set('currentStepId', this.get('initialStepId'));
  },

  currentStep: computed('currentStepId', function() {
    const step = this.steps.find((s) => s.id === this.currentStepId);
    return step;
  }),

  actions: {
    transitionToStep(stepId) {
      this.set('currentStepId', stepId);
    }
  }
});

export default RsaWizard;
