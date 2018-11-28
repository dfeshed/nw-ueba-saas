import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import details from '../../../../data/presidio/user_details';

let setState;

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
  });

  test('it renders', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_name').textContent.trim(), 'file_qa_1_101', 'Should show entity name');
    assert.equal(find('.score').textContent.trim(), 220, 'Should show entity score');
  });

  test('it should allow user to be watched', async function(assert) {

    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(details.data[0]).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Watch Profile');
  });

  test('it should allow user to be unwatched', async function(assert) {
    const newData = { ...details.data[0], followed: true };
    new ReduxDataHelper(setState).entityId({ entityId: 123, entityType: 'user' }).entityDetails(newData).build();
    await render(hbs`{{entity-details-container/header}}`);
    assert.equal(find('.entity-details-container-header_watch').textContent.trim(), 'Stop Watching');
  });
});
