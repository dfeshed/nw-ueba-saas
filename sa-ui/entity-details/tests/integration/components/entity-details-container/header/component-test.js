import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../helpers/patch-fetch';
import dataIndex from '../../../../data/presidio';
import details from '../../../../data/presidio/user_details';
import Service from '@ember/service';

let setState, helpIds;
const contextualHelpStub = Service.extend({
  goToHelp: (moduleId, topicId) => {
    helpIds = { moduleId, topicId };
  }
});

module('Integration | Component | entity-details-container/header', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('entity-details')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('helper:mount', function() {});
    this.owner.register('service:contextualHelp', contextualHelpStub);
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
  });

  test('it renders', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_name').textContent.trim().indexOf('file_qa_1_101'), 0, 'Should show entity name');
    assert.ok(find('.entity-details-container-header_name').textContent.trim().indexOf('USER') > 0, 'Should show entity Type');
    assert.equal(find('.score').textContent.trim(), 220, 'Should show entity score');
  });

  test('it should not show header if entity type is not set', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123 }).entityType(null).entityDetails(details.data[0]).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_name').textContent.trim().indexOf('file_qa_1_101'), 0, 'Should show entity name');
    assert.ok(find('.entity-details-container-header_name').textContent.trim().indexOf('USER') === -1, 'Should not show entity Type');
    assert.equal(find('.score').textContent.trim(), 220, 'Should show entity score');
  });

  test('it should allow user to be watched', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Watch Profile');
  });


  test('it should allow user to be watched on click', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Watch Profile');
    await click('.rsa-form-button');
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Stop Watching');
  });

  test('it should allow user to be unwatched', async function(assert) {
    const newData = { ...details.data[0], followed: true };
    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(newData).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Stop Watching');
  });

  test('it should allow user to be unwatched on click', async function(assert) {
    const newData = { ...details.data[0], followed: true };
    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(newData).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Stop Watching');
    await click('.rsa-form-button');
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Watch Profile');
  });

  test('it should allow user test contextual help', async function(assert) {
    await render(hbs`{{entity-details-container/header}}`);
    await click('.rsa-icon-help-circle');
    assert.deepEqual(helpIds, { moduleId: 'investigation', topicId: 'InvestigateEntityDetails' });
  });
});
