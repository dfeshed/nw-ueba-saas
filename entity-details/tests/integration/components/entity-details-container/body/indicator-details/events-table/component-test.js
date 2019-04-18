import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState;
module('Integration | Component | entity-details-container/body/indicator-details/events-table', function(hooks) {
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

  test('it should render proper event table for selected indicator', async function(assert) {

    new ReduxDataHelper(setState).build();
    await render(hbs`{{entity-details-container/body/indicator-details/events-table}}`);

    assert.equal(this.element.textContent.replace(/\s/g, '').indexOf('eventDate.epochSecond'), 0);
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7);
    assert.equal(findAll('.rsa-data-table-body-row').length, 85);

  });

  test('it should show loader if complete data is not there', async function(assert) {

    new ReduxDataHelper(setState).build();
    await render(hbs`{{entity-details-container/body/indicator-details/events-table}}`);
    assert.equal(findAll('.rsa-loader').length, 1);
  });

  test('it should not show loader if complete data is there', async function(assert) {

    new ReduxDataHelper(setState).totalEvents(85).build();
    await render(hbs`{{entity-details-container/body/indicator-details/events-table}}`);
    assert.equal(findAll('.rsa-loader').length, 0);
  });
});
