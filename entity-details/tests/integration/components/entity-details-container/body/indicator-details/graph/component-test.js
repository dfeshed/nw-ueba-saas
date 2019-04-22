import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let setState;

module('Integration | Component | entity-details-container/body/indicator-details/graph', function(hooks) {
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

  test('it should render chart for selected indicator', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/indicator-details/graph}}`);

    assert.ok(this.element.textContent.trim().indexOf('C:\\Windows\\System32\\svchost.exe') > -1);
  });

  test('it should have legends for pie chart', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/indicator-details/graph}}`);
    return waitUntil(() => this.$('svg').length === 1, { timeout: 2000 }).then(() => {
      assert.equal(findAll('svg > g > g >g > g > g > g > g:nth-child(2) > g > g').length, 4);
      assert.equal(findAll('svg > g > g >g > g > g > g > g').length, 7);
    });
  });

  test('it should render chart with anomaly', async function(assert) {
    new ReduxDataHelper(setState).build();

    await render(hbs`{{entity-details-container/body/indicator-details/graph}}`);
    return waitUntil(() => this.$('svg').length === 1, { timeout: 2000 }).then(() => {
      assert.ok(find('svg > g > g >g > g > g > g > g > g > g > g > g > g > g:nth-child(3) > g > g').outerHTML.indexOf('fill="#cc3300"') > -1);
      assert.ok(find('svg > g > g >g > g > g > g > g > g > g > g > g > g > g:nth-child(3) > g > g:nth-child(2)').outerHTML.indexOf('fill="#0d8ecf"') > -1);
      assert.ok(find('svg > g > g >g > g > g > g > g > g > g > g > g > g > g:nth-child(3) > g > g:nth-child(3)').outerHTML.indexOf('fill="#0a335c"') > -1);
      assert.ok(find('svg > g > g >g > g > g > g > g > g > g > g > g > g > g:nth-child(3) > g > g:nth-child(4)').outerHTML.indexOf('fill="#0d6ecd"') > -1);
    });
  });

  test('it should show loader till historical data is not there', async function(assert) {
    new ReduxDataHelper(setState).historicalData(null).build();

    await render(hbs`{{entity-details-container/body/indicator-details/graph}}`);
    assert.equal(findAll('.entity-details_loader').length, 1);
  });

  test('it should error if server has error while fetching historical data', async function(assert) {
    new ReduxDataHelper(setState).historicalData(null).indicatorGraphError(true).build();

    await render(hbs`{{entity-details-container/body/indicator-details/graph}}`);
    // SHould have loader div but should display error text not rsa loader in case of error.
    assert.equal(findAll('.entity-details_loader').length, 1);
    assert.equal(findAll('.rsa-loader').length, 0);
  });

  test('it should show loader till incident data is not there', async function(assert) {
    new ReduxDataHelper(setState).alerts(null).build();

    await render(hbs`{{entity-details-container/body/indicator-details/graph}}`);
    assert.equal(findAll('.entity-details_loader').length, 1);
  });
});
