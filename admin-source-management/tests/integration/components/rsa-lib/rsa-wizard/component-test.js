import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const steps = [
  {
    id: 'testStep1',
    nextStepId: 'testStep2',
    prevStepId: '',
    title: 'rsaWizard.tests.testStep1Label',
    stepComponent: 'rsa-lib/rsa-wizard/test-step',
    toolbarComponent: 'rsa-lib/rsa-wizard/test-toolbar'
  },
  {
    id: 'testStep2',
    nextStepId: 'testStep3',
    prevStepId: 'testStep1',
    title: 'rsaWizard.tests.testStep2Label',
    stepComponent: 'rsa-lib/rsa-wizard/test-step',
    toolbarComponent: 'rsa-lib/rsa-wizard/test-toolbar'
  },
  {
    id: 'testStep3',
    nextStepId: '',
    prevStepId: 'testStep2',
    title: 'rsaWizard.tests.testStep3Label',
    stepComponent: 'rsa-lib/rsa-wizard/test-step',
    toolbarComponent: 'rsa-lib/rsa-wizard/test-toolbar'
  }
];

// let someFunction;

module('Integration | Component | rsa-lib/rsa-wizard', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    // someFunction = (someArg) => {
    //   console.log('do something...');
    // };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    this.set('steps', steps);
    this.set('initialStepId', 'testStep1');
    this.set('transitionToClose', () => {});
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      transitionToClose=(action transitionToClose)}}`
    );
    assert.equal(findAll('.rsa-wizard-container').length, 1, 'The component appears in the DOM');
  });

  test('A loading spinner is displayed if the isWizardLoading property is true', async function(assert) {
    this.set('steps', steps);
    this.set('initialStepId', 'testStep1');
    this.set('transitionToClose', () => {});
    this.set('isWizardLoading', true);
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      transitionToClose=(action transitionToClose)
      isWizardLoading=isWizardLoading}}`
    );
    assert.equal(findAll('.rsa-wizard-container .rsa-wizard-loading-overlay .rsa-loader').length, 1, 'A loading spinner appears in the dom');
  });

  test('The list of step names should be rendered', async function(assert) {
    this.set('steps', steps);
    this.set('initialStepId', 'testStep1');
    this.set('transitionToClose', () => {});
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      transitionToClose=(action transitionToClose)}}`
    );

    // the list exists
    assert.equal(findAll('.rsa-wizard-container .rsa-wizard-left-container ul').length, 1, 'List of step names is rendered');

    // all the steps are listed
    assert.equal(findAll('.rsa-wizard-container .rsa-wizard-left-container ul li').length, 3, 'Correct number of steps are rendered');

    // only one should be active
    const actives = findAll('.rsa-wizard-container .rsa-wizard-left-container ul li a.active');
    assert.equal(actives.length, 1, 'One step is active');

    // the correct step should be active
    const [active] = actives;
    assert.equal(active.innerText, 'Test Step 1', 'Test Step 1 is active');
  });

  test('The correct initial step should be rendered', async function(assert) {
    this.set('steps', steps);
    this.set('initialStepId', 'testStep2');
    this.set('transitionToClose', () => {});
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      transitionToClose=(action transitionToClose)}}`
    );
    assert.equal(findAll('.rsa-wizard-container .testStep2').length, 1, 'Test Step 2 is rendered');
  });

  // TODO probably delete this test as it probably doesn't make sense anymore
  //      since we probably don't ever want to change currentStepId from outside the component
  test('The correct step should be rendered when changing currentStepId', async function(assert) {
    this.set('steps', steps);
    this.set('initialStepId', 'testStep2');
    this.set('currentStepId', 'testStep2');
    this.set('transitionToClose', () => {});
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      currentStepId=currentStepId
      transitionToClose=(action transitionToClose)}}`
    );

    // initial step
    assert.equal(findAll('.rsa-wizard-container .testStep2').length, 1, 'Test Step 2 is rendered');

    // transitioned to step
    this.set('currentStepId', 'testStep3');
    assert.equal(findAll('.rsa-wizard-container .testStep3').length, 1, 'Test Step 3 is rendered');
  });

  test('The bottom button toolbar should be rendered', async function(assert) {
    this.set('steps', steps);
    this.set('initialStepId', 'testStep1');
    this.set('transitionToClose', () => {});
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      transitionToClose=(action transitionToClose)}}`
    );
    assert.equal(findAll('.rsa-wizard-container .test-toolbar').length, 1, 'Test Toolbar is rendered');
  });

  test('The correct step should be rendered by triggering the transitionToStep action with Next/Previous buttons', async function(assert) {
    this.set('steps', steps);
    this.set('initialStepId', 'testStep1');
    this.set('transitionToClose', () => {});
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      transitionToClose=(action transitionToClose)}}`
    );

    // clicking Step 1's Next button should render Step 2
    const [step1NextBtn] = findAll('.rsa-wizard-container .test-toolbar .next-button button');
    await click(step1NextBtn);
    assert.equal(findAll('.rsa-wizard-container .testStep2').length, 1, 'Test Step 2 is rendered');

    // clicking Step 2's Next button should render Step 3
    const [step2NextBtn] = findAll('.rsa-wizard-container .test-toolbar .next-button button');
    await click(step2NextBtn);
    assert.equal(findAll('.rsa-wizard-container .testStep3').length, 1, 'Test Step 3 is rendered');

    // clicking Step 3's Previous button should render Step 2
    const [step3PrevBtn] = findAll('.rsa-wizard-container .test-toolbar .prev-button button');
    await click(step3PrevBtn);
    assert.equal(findAll('.rsa-wizard-container .testStep2').length, 1, 'Test Step 2 is rendered');

    // clicking Step 2's Previous button should render Step 1
    const [step2PrevBtn] = findAll('.rsa-wizard-container .test-toolbar .prev-button button');
    await click(step2PrevBtn);
    assert.equal(findAll('.rsa-wizard-container .testStep1').length, 1, 'Test Step 1 is rendered');
  });

  test('The transitionToClose() closure action should be properly set and triggered', async function(assert) {
    assert.expect(1);
    this.set('steps', steps);
    this.set('initialStepId', 'testStep1');
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{rsa-lib/rsa-wizard
      steps=steps
      initialStepId=initialStepId
      transitionToClose=(action transitionToClose)}}`
    );
    const [cancelBtn] = findAll('.rsa-wizard-container .test-toolbar .cancel-button button');
    await click(cancelBtn);
  });

});
