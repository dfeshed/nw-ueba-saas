import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import _ from 'lodash';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-policies/policy-wizard/policy-titlebar', function(hooks) {
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
    const state = new ReduxDataHelper(setState).policyWiz().build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-titlebar step=step}}`);
    assert.equal(findAll('.policy-wizard-titlebar').length, 1, 'The component appears in the DOM');
  });

  test('Titlebar appearance for empty policy name & empty description', async function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName(null)
      .policyWizDescription(null)
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-titlebar step=step}}`);

    const translation = this.owner.lookup('service:i18n');
    const expectedName = translation.t('adminUsm.policyWizard.newPolicy');
    const expectedNameTitle = '';
    assert.equal(findAll('.policy-name-label').length, 1, 'The policy name label appears in the DOM');
    const [nameEl] = findAll('.policy-name-label');
    assert.equal(nameEl.innerText, expectedName, `The policy name is ${expectedName}`);
    assert.equal(nameEl.title, expectedNameTitle, 'The policy name title is empty');

    const expectedDescription = '';
    const expectedDescriptionTitle = '';
    assert.equal(findAll('.policy-description-label').length, 1, 'The policy description label appears in the DOM');
    const [descriptionEl] = findAll('.policy-description-label');
    assert.equal(descriptionEl.innerText, expectedDescription, 'The policy description is empty');
    assert.equal(descriptionEl.title, expectedDescriptionTitle, 'The policy description title is empty');
  });

  test('Titlebar appearance for short policy name & short description', async function(assert) {
    const expectedName = 'Short Policy Name';
    const expectedNameTitle = expectedName;
    const expectedDescription = 'Short Policy Description';
    const expectedDescriptionTitle = expectedDescription;
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName(expectedName)
      .policyWizDescription(expectedDescription)
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-titlebar step=step}}`);

    assert.equal(findAll('.policy-name-label').length, 1, 'The policy name label appears in the DOM');
    const [nameEl] = findAll('.policy-name-label');
    assert.equal(nameEl.innerText, expectedName, `The policy name is ${expectedName}`);
    assert.equal(nameEl.title, expectedNameTitle, `The policy name title is ${expectedNameTitle}`);

    assert.equal(findAll('.policy-description-label').length, 1, 'The policy description label appears in the DOM');
    const [descriptionEl] = findAll('.policy-description-label');
    assert.equal(descriptionEl.innerText, expectedDescription, `The policy description is ${expectedDescription}`);
    assert.equal(descriptionEl.title, expectedDescriptionTitle, `The policy description title is ${expectedDescriptionTitle}`);
  });

  test('Titlebar appearance for long policy name & truncated long description', async function(assert) {
    const expectedName256 = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in';
    const expectedName256Title = expectedName256;
    const longDescription445 = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
    const expectedDescription256 = _.truncate(longDescription445, { length: 256, omission: '' });
    const expectedDescription256Title = _.truncate(longDescription445, { length: 256, omission: '...' });
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName(expectedName256)
      .policyWizDescription(longDescription445)
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-titlebar step=step}}`);

    assert.equal(findAll('.policy-name-label').length, 1, 'The policy name label appears in the DOM');
    const [nameEl] = findAll('.policy-name-label');
    assert.equal(nameEl.innerText, expectedName256, `The policy name is ${expectedName256}`);
    assert.equal(nameEl.title, expectedName256Title, `The policy name title is ${expectedName256Title}`);

    assert.equal(findAll('.policy-description-label').length, 1, 'The policy description label appears in the DOM');
    const [descriptionEl] = findAll('.policy-description-label');
    assert.equal(descriptionEl.innerText, expectedDescription256, `The policy description is ${expectedDescription256}`);
    assert.equal(descriptionEl.title, expectedDescription256Title, `The policy description title is ${expectedDescription256Title}`);
  });

});
