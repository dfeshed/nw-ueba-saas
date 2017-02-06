import Ember from 'ember';
import computed, { gt, filterBy, mapBy } from 'ember-computed-decorators';

const { Component, run, set } = Ember;

export default Component.extend({
  classNames: ['rsa-wizard'],
  attributeBindings: ['data-stepcount'],

  /**
   * The integer representing the current active step number in the wizard/workflow. If the user has progressed to step
   * number (2) out of (10) steps, this value is (2).
   * @property currentStep
   * @public
   */
  currentStep: 1,

  /**
   * All steps known to the Wizard/workflow. Each component step is registered with the wizard when it initialized
   * @property registeredSteps
   * @public
   */
  registeredSteps: null,

  /**
   * The complete history of step numbers that the user has visited
   * @property history
   * @public
   */
  history: null,

  /**
   * The label used for the NEXT button. Default provided, but localized version should be passed in via component
   * declaration
   * @property nextLabel
   * @public
   */
  nextLabel: 'Next',

  /**
   * The label used for the PREVIOUS button. Default provided, but localized version should be passed in via component
   * declaration
   * @property previousLabel
   * @public
   */
  previousLabel: 'Previous',

  /**
   * The label used for the CANCEL button. Default provided, but localized version should be passed in via component
   * declaration
   * @property cancelLabel
   * @public
   */
  cancelLabel: 'Cancel',

  /**
   * The label used for the FINISH button. Default provided, but localized version should be passed in via component
   * declaration
   * @property finishLabel
   * @public
   */
  finishLabel: 'Finish',

  @computed('currentStep', 'history')
  visitedSteps(currentStep, history) {
    history.push(currentStep);
    return history.uniq();
  },

  /**
   * Returns true if the current step is in an invalid state
   * @public
   * @param currentStepNumber
   * @param invalidStepNumbers
   */
  @computed('currentStep', 'invalidStepNumbers')
  isCurrentStepInvalid(currentStepNumber, invalidStepNumbers) {
    return invalidStepNumbers.includes(currentStepNumber);
  },

  /**
   * Returns an array set of all invalid steps or an empty array if there are none
   * @public
   * @returns {Array}
   */
  @filterBy('registeredSteps', 'isValid', false) invalidSteps: null,

  /**
   * Returns an array of only the step numbers that are currently invalid, or an empty array if none
   * @public
   * @returns {Array}
   */
  @mapBy('invalidSteps', 'stepNumber') invalidStepNumbers: null,

  /**
   * Returns the number of steps in the wizard
   * @public
   * @param registeredSteps
   */
  @computed('registeredSteps')
  totalSteps(registeredSteps) {
    return registeredSteps.length;
  },

  /**
   * Returns true if there is another step to be completed after the current step
   * @public
   * @param totalSteps
   * @param currentStep
   * @returns {boolean}
   */
  @computed('totalSteps', 'currentStep')
  hasNextStep(totalSteps, currentStep) {
    return currentStep < totalSteps;
  },

  /**
   * Returns true if there was a step in the wizard that preceded the current step
   * @public
   */
  @gt('currentStep', 1) hasPreviousStep: null,

  init() {
    this._super(...arguments);
    this.set('registeredSteps', []);
    this.set('history', []);
  },

  /**
   * Moves to the next step in the Wizard
   * @public
   */
  next() {
    if (this.get('hasNextStep')) {
      this.incrementProperty('currentStep');
    }
  },

  /**
   * Moves to the previous step in the Wizard
   * @public
   */
  previous() {
    if (this.get('hasPreviousStep')) {
      this.decrementProperty('currentStep');
    }
  },

  /**
   * Method for registering a workflow/wizard step with the Wizard component.
   * @public
   * @param step
   * @returns {boolean}
   */
  registerWorkflowStep(step) {
    run.next(()=> {
      const steps = this.get('registeredSteps');
      this.set('registeredSteps', [...steps, step]);
    });

    return false;
  },

  /**
   * Updates a step (identified by number) with a new valid/invalid state
   * @public
   * @param isValid
   * @param stepNumber
   */
  updateValidity(isValid, stepNumber) {
    const registeredSteps = this.get('registeredSteps');
    const step = registeredSteps.findBy('stepNumber', stepNumber);
    if (step && step.isValid !== isValid) {
      set(step, 'isValid', isValid);
    }
  },

  actions: {
    next() {
      this.next();
    },

    previous() {
      this.previous();
    },

    cancel() {
      this.sendAction('cancel');
    },

    validate() {
      this.sendAction('validate');
    }
  }
});
