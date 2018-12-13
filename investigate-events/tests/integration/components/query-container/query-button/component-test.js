import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { click, find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import PILL_SELECTORS from '../pill-selectors';
import interactionCreators from 'investigate-events/actions/interaction-creators';

const cancelQuerySpy = sinon.spy(interactionCreators, 'cancelQuery');
const spys = [ cancelQuerySpy ];

let setState;

module('Integration | Component | query-button', function(hooks) {
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

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

  test('Button is disabled when the query is not ready', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(false)
      .isQueryRunning(false)
      .pillsDataEmpty()
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/query-button
        executeQuery=executeQuery
      }}
    `);

    assert.equal(findAll(PILL_SELECTORS.queryButtonDisabled).length, 1, 'button should be disabled');
  });

  test('Shows a textual label if query is NOT running', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .isQueryRunning(false)
      .build();

    await render(hbs`{{query-container/query-button}}`);
    assert.equal(find(PILL_SELECTORS.queryButton).textContent.trim(), 'Query Events', 'displays "query" label');
  });

  test('Shows a spinner if pills are validating while a query is running', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .pillsDataPopulated()
      .markValidationInProgress(['2'])
      .isQueryRunning(true)
      .build();

    await render(hbs`{{query-container/query-button}}`);
    assert.equal(findAll(PILL_SELECTORS.loadingQueryButton).length, 1, 'displays loading button');
  });

  test('Shows a textual label if a query can be canceled', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .isQueryRunning(true)
      .build();

    await render(hbs`{{query-container/query-button}}`);
    assert.equal(find(PILL_SELECTORS.queryButton).textContent.trim(), 'Cancel Query', 'displays "cancel" label');
  });

  test('Calls function to execute query', async function(assert) {
    const done = assert.async();
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .pillsDataEmpty()
      .isQueryRunning(false)
      .build();

    this.set('executeQuery', () => {
      assert.ok(true, 'executeQuery is called');
      done();
    });

    await render(hbs`
      {{query-container/query-button
        executeQuery=executeQuery
      }}
    `);
    await click(PILL_SELECTORS.queryButton);
  });

  test('Calls action to cancel query', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .hasRequiredValuesToQuery(true)
      .pillsDataEmpty()
      .isQueryRunning(true)
      .build();

    await render(hbs`{{query-container/query-button}}`);
    await click(PILL_SELECTORS.queryButton);
    assert.equal(cancelQuerySpy.callCount, 1, 'The cancel query action creator was called once');
  });
});