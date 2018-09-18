import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { throwSocket } from '../../../../../helpers/patch-socket';

let setState;

module('Integration | Component | usm-groups/group-wizard/group-toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    const state = new ReduxDataHelper(setState).groupWiz().build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    assert.equal(findAll('.group-wizard-toolbar').length, 1, 'The component appears in the DOM');
  });

  test('Toolbar appearance for Identify Group step with invalid data', async function(assert) {
    const state = new ReduxDataHelper(setState).groupWiz().build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Toolbar appearance for Identify Group step valid data', async function(assert) {
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName('test name')
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Toolbar actions for Identify Group step with valid data', async function(assert) {
    assert.expect(1);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName('test name')
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    this.set('transitionToStep', () => {});
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step transitionToStep=(action transitionToStep)}}`);

    // skip prev-button & publish-button since they aren't rendered

    // update transitionToStep for next-button
    this.set('transitionToStep', (stepId) => {
      assert.equal(stepId, this.get('step').nextStepId, `transitionToStep(${stepId}) was called with the correct stepId by Next`);
    });
    const [nextBtnEl] = findAll('.next-button:not(.is-disabled) button');
    await click(nextBtnEl);

  });

  test('On failing to save a group, an error flash message is shown', async function(assert) {
    assert.expect(2);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName('test name')
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groupWizard.saveFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

  test('On successfully saving a group, a success flash message is shown, and the transitionToClose action is called', async function(assert) {
    assert.expect(3);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName('test name')
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );

    const done = assert.async();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groupWizard.saveSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

  test('On failing to save and publish a group, an error flash message is shown', async function(assert) {
    assert.expect(3);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName('test name')
      .build();
    this.set('step', state.usm.groupWizard.steps[3]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    assert.equal(findAll('.publish-button').length, 1, 'The Publish button appears in the DOM');

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groupWizard.savePublishFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });

    const [savePublishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(savePublishBtnEl);
  });

  test('On successfully saving and publishing a group, a success flash message is shown, and the transitionToClose action is called', async function(assert) {
    assert.expect(4);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName('test name')
      .build();
    this.set('step', state.usm.groupWizard.steps[3]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );
    assert.equal(findAll('.publish-button').length, 1, 'The Publish button appears in the DOM');

    const done = assert.async();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groupWizard.savePublishSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [savePublishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(savePublishBtnEl);
  });

  test('Name and description errors do not enable Next and Save buttons', async function(assert) {
    let testDesc = '';
    for (let index = 0; index < 220; index++) {
      testDesc += 'the-description-is-greater-than-8000-';
    }
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName('')
      .groupWizDescription(testDesc)
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is NOT enabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is NOT enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

});

