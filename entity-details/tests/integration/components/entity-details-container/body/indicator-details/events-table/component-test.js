import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, waitUntil, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import { windowProxy } from 'component-lib/utils/window-proxy';

let setState;
let openStub = null;
let currentUrl = null;
let newTab = false;
module('Integration | Component | entity-details-container/body/indicator-details/events-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('entity-details')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    openStub = sinon.stub(windowProxy, 'openInNewTab').callsFake((urlPassed) => {
      currentUrl = urlPassed;
      newTab = true;
    });
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('helper:mount', function() {});
  });
  hooks.afterEach(function() {
    openStub.restore();
    currentUrl = null;
    newTab = false;
  });

  test('it should render proper event table for selected indicator', async function(assert) {

    new ReduxDataHelper(setState).build();
    await render(hbs`{{entity-details-container/body/indicator-details/events-table}}`);

    assert.equal(this.element.textContent.replace(/\s/g, '').indexOf('eventDate.epochSecond'), 0);
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7);
    assert.equal(findAll('.rsa-data-table-body-row').length, 85);
  });

  test('it should be able to pivot on click of UserName', async function(assert) {

    assert.expect(2);
    new ReduxDataHelper(setState).build();
    await render(hbs`{{entity-details-container/body/indicator-details/events-table}}`);

    return waitUntil(() => findAll('.rsa-data-table-body-row').length === 85).then(() => {
      click('.entity-details-container-body-indicator-details_events-table_href');
      return waitUntil(() => currentUrl !== null).then(() => {
        assert.ok(currentUrl.indexOf('investigate/events'));
        assert.ok(newTab);
      });
    });
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

  test('it should not show error if any problem fetching events from server', async function(assert) {

    new ReduxDataHelper(setState).events([]).indicatorEventError(true).build();
    await render(hbs`{{entity-details-container/body/indicator-details/events-table}}`);
    // SHould have loader div but should display error text not rsa loader in case of error.
    assert.equal(findAll('.entity-details_loader').length, 1);
    assert.equal(findAll('.rsa-loader').length, 0);
  });
});
