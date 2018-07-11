import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, find, findAll, click, settled } from '@ember/test-helpers';

import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import SELECTORS from './selectors';

let setState;

module('Integration | Component | query-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it disables the submit button when required values are missing', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(false)
      .build();
    await render(hbs`{{query-container}}`);
    assert.ok(find(SELECTORS.queryButton).classList.contains('is-disabled'), 'Expected is-disabled CSS class on the submit button.');
  });

  test('it enables the submit button when required values are present', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    assert.notOk(find(SELECTORS.queryButton).classList.contains('is-disabled'), 'Expected is-disabled CSS class on the submit button.');
  });

  test('it displays the correct number of query bar links and starts on next gen mode', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    assert.equal(findAll(SELECTORS.queryFormatToggleLinks).length, 2, 'Expected 2 query bars');
    assert.ok(find(SELECTORS.nextGenQueryBar), 'Expected to see Next Gen Query Bar');
  });

  test('Can toggle between views', async function(assert) {
    // const done = assert.async();
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    await click(SELECTORS.queryFormatFreeFormToggle);
    return settled().then(async () => {
      assert.ok(find(SELECTORS.freeFormQueryBar), 'Expected to see Free Form Query Bar');
      assert.ok(find(SELECTORS.freeFormQueryBarFocusedInput), 'Expected focus on free-form');
      assert.equal(find(SELECTORS.freeFormQueryBarInput).placeholder, 'Enter multiple complex statements consisting of a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');

      await click(SELECTORS.queryFormatNextGenToggle);
      return settled().then(() => {
        assert.ok(find(SELECTORS.nextGenQueryBar), 'Expected to see Next Gen Query Bar');
        assert.ok(find(SELECTORS.nextGenQueryBarFocusedInput), 'Expected focus on next gen');
      });
    });
  });

});