import Ember from 'ember';
import computed, { alias } from 'ember-computed-decorators';

const { Mixin, observer, typeOf, getProperties, assert, isPresent, isEmpty, run } = Ember;

/**
 * A mixin representing the interface for a step in a wizard/workflow. Any component can use this
 * mixin, after which that component can be shown as one of the steps in a workflow.
 * @public
 */
export default Mixin.create({
  name: null,
  stepNumber: null,
  isValid: true,

  /**
   * Ensures that validity is updated *in the wizard* any time the step's validity changes. An observer is
   * used here instead of a custom computed get/set so that components that implement this mixin can easily
   * override isValid (e.g., make it a computed property / computed macro) without having to worry about
   * implementing the plumbing to ensure the Wizard is aware of the validity change.
   * @private
   */
  isValidChanged: observer('isValid', function() {
    run.once(this, 'updateValidity');
  }),

  /**
   * Whether the step is visible to the end user; determines which step is being shown in the Wizard at any time
   * @public
   */
  @computed('currentStep', 'stepNumber')
  isVisible(currentStep, stepNumber) {
    return currentStep === stepNumber;
  },

  /**
   * Returns the step number that is currently in use by the Wizard
   * @public
   */
  @alias('workflow.currentStep') currentStep: null,

  init() {
    this._super(...arguments);
    const workflow = this.get('workflow');
    const stepNumber = this.get('stepNumber');

    assert('You must define the "workflow" property on a Wizard Step component', (isPresent(workflow) && !isEmpty(workflow)));
    assert('You must define the "stepNumber" property on a Wizard Step component', (isPresent(stepNumber)));
    workflow.registerWorkflowStep(getProperties(this, 'name', 'stepNumber', 'isValid'));
  },

  /**
   * Updates the parent wizard/workflow with this step's valid/invalid state
   * @public
   */
  updateValidity() {
    const updateValidity = this.workflowResolve('updateValidity') || (() => {});
    updateValidity(this.get('isValid'), this.get('stepNumber'));
  },

  /**
   * Returns a property of the Wizard/Workflow (bound to the Wizard/workflow if it is a function). This allows
   * wizard-steps to invoke functions on the parent Wizard.
   * @public
   * @param key
   */
  workflowResolve(key) {
    const workflow = this.get('workflow');
    const property = workflow.get(key);

    if (property && typeOf(property) === 'function') {
      return property.bind(workflow);
    }

    return property;
  }
});
