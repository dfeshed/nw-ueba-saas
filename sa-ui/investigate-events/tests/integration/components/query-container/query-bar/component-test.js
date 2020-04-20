import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';

import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import PILL_SELECTORS from '../pill-selectors';
import { toggleTab } from '../pill-util';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | Query Bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');

    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Initial render of query pills will not have focus', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    assert.equal(findAll(PILL_SELECTORS.metaInput).length, 1, 'pill meta is displayed');
    assert.equal(findAll(PILL_SELECTORS.metaInputFocused).length, 0, 'But it is not focused');
  });

  // TODO: add additional disabled states and uncomment
  // test('renders copy trigger', async function(assert) {
  //   this.set('executeQuery', () => {});
  //   await render(hbs`
  //     {{query-container/query-bar executeQuery=executeQuery}}
  //   `);
  //   assert.equal(findAll('.query-bar-selection .copy-trigger').length, 1);
  // });

  test('renders the correct dom hasWarning', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasWarning().build();
    this.set('executeQuery', () => {});
    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    assert.equal(findAll('.query-bar-selection.console-has-warning').length, 1);
  });

  test('renders the correct dom hasError', async function(assert) {
    new ReduxDataHelper(setState).queryStats().queryStatsHasError().build();
    this.set('executeQuery', () => {});
    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    assert.equal(findAll('.query-bar-selection.console-has-error').length, 1);
  });

  test('renders the correct dom isOpen', async function(assert) {
    new ReduxDataHelper(setState).hasRequiredValuesToQuery().withPreviousQuery().queryStats().queryStatsIsOpen().build();
    this.set('executeQuery', () => {});
    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    assert.equal(findAll('.query-bar-selection.is-console-open').length, 1);
  });

  test('Selecting a complex recent query creates multiple pill', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .language()
      .queryStats()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .recentQueriesUnfilteredList()
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    await clickTrigger(PILL_SELECTORS.meta);
    await toggleTab(PILL_SELECTORS.metaSelectInput);

    await settled();

    await selectChoose(PILL_SELECTORS.recentQuery, 'sessionid = 1 AND sessionid = 80');

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 3, '2 created pills plus a template is not present');
    assert.equal(findAll(PILL_SELECTORS.queryPill)[0].textContent.replace(/\s/g, ''), 'sessionid=1', 'pill text is in-correct');
    assert.equal(findAll(PILL_SELECTORS.queryPill)[1].textContent.replace(/\s/g, ''), 'sessionid=80', 'pill text is in-correct');
    done();

  });
});
