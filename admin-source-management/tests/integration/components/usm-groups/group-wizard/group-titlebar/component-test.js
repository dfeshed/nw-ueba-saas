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

module('Integration | Component | usm-groups/group-wizard/group-titlebar', function(hooks) {
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
    await render(hbs`{{usm-groups/group-wizard/group-titlebar step=step}}`);
    assert.equal(findAll('.group-wizard-titlebar').length, 1, 'The component appears in the DOM');
  });

  test('Titlebar appearance for empty group name & empty description', async function(assert) {
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName(null)
      .groupWizDescription(null)
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-titlebar step=step}}`);

    const translation = this.owner.lookup('service:i18n');
    const expectedName = translation.t('adminUsm.groupWizard.newGroup');
    const expectedNameTitle = '';
    assert.equal(findAll('.group-name-label').length, 1, 'The group name label appears in the DOM');
    const [nameEl] = findAll('.group-name-label');
    assert.equal(nameEl.innerText.trim(), expectedName, `The group name is ${expectedName}`);
    assert.equal(nameEl.title, expectedNameTitle, 'The group name title is empty');

    const expectedDescription = '';
    const expectedDescriptionTitle = '';
    assert.equal(findAll('.group-description-label').length, 1, 'The group description label appears in the DOM');
    const [descriptionEl] = findAll('.group-description-label');
    assert.equal(descriptionEl.innerText.trim(), expectedDescription, 'The group description is empty');
    assert.equal(descriptionEl.title, expectedDescriptionTitle, 'The group description title is empty');
  });

  test('Titlebar appearance for short group name & short description', async function(assert) {
    const expectedName = 'Short Group Name';
    const expectedNameTitle = expectedName;
    const expectedDescription = 'Short Group Description';
    const expectedDescriptionTitle = expectedDescription;
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName(expectedName)
      .groupWizDescription(expectedDescription)
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-titlebar step=step}}`);

    assert.equal(findAll('.group-name-label').length, 1, 'The group name label appears in the DOM');
    const [nameEl] = findAll('.group-name-label');
    assert.equal(nameEl.innerText.trim(), expectedName, `The group name is ${expectedName}`);
    assert.equal(nameEl.title, expectedNameTitle, `The group name title is ${expectedNameTitle}`);

    assert.equal(findAll('.group-description-label').length, 1, 'The group description label appears in the DOM');
    const [descriptionEl] = findAll('.group-description-label');
    assert.equal(descriptionEl.innerText.trim(), expectedDescription, `The group description is ${expectedDescription}`);
    assert.equal(descriptionEl.title, expectedDescriptionTitle, `The group description title is ${expectedDescriptionTitle}`);
  });

  test('Titlebar appearance for long group name & truncated long description', async function(assert) {
    const expectedName256 = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in';
    const expectedName256Title = expectedName256;
    const longDescription445 = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
    const expectedDescription256 = _.truncate(longDescription445, { length: 256, omission: '' });
    const expectedDescription256Title = _.truncate(longDescription445, { length: 256, omission: '...' });
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizName(expectedName256)
      .groupWizDescription(longDescription445)
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-titlebar step=step}}`);

    assert.equal(findAll('.group-name-label').length, 1, 'The group name label appears in the DOM');
    const [nameEl] = findAll('.group-name-label');
    assert.equal(nameEl.innerText.trim(), expectedName256, `The group name is ${expectedName256}`);
    assert.equal(nameEl.title, expectedName256Title, `The group name title is ${expectedName256Title}`);

    assert.equal(findAll('.group-description-label').length, 1, 'The group description label appears in the DOM');
    const [descriptionEl] = findAll('.group-description-label');
    assert.equal(descriptionEl.innerText.trim(), expectedDescription256, `The group description is ${expectedDescription256}`);
    assert.equal(descriptionEl.title, expectedDescription256Title, `The group description title is ${expectedDescription256Title}`);
  });

});
