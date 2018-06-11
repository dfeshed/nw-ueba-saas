import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, find, findAll, click, settled } from '@ember/test-helpers';
import config from 'ember-get-config';

import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import SELECTORS from './selectors';
import PILL_SELECTORS from './pill-selectors';

const nextGenFeature = config.featureFlags.nextGen;

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

  test('it displays the correct number of query bar links', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    if (nextGenFeature) {
      assert.equal(findAll(SELECTORS.queryFormatToggleLinks).length, 3, 'Expected 3 query bars');
    } else {
      assert.equal(findAll(SELECTORS.queryFormatToggleLinks).length, 2, 'Expected 2 query bars');
    }
    assert.ok(find(SELECTORS.guidedQueryBar), 'Expected to see Guided Query Bar');
  });

  test('it displays free form query bar when clicked', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    assert.equal(find(SELECTORS.guidedQueryBarInput).placeholder, 'Enter individual statements consisting of a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');
    assert.ok(find(SELECTORS.guidedQueryBar), 'Expected to see Guided Query Bar');
    assert.notOk(find(SELECTORS.guidedQueryBarFocusedInput), 'Should not have focus the first time it renders');

    await click(SELECTORS.queryFormatFreeFormToggle);
    return settled().then(() => {
      assert.ok(find(SELECTORS.freeFormQueryBar), 'Expected to see Free Form Query Bar');
      assert.ok(find(SELECTORS.freeFormQueryBarFocusedInput), 'Expected focus on free-form');
      assert.equal(find(SELECTORS.freeFormQueryBarInput).placeholder, 'Enter multiple complex statements consisting of a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');

      click(SELECTORS.queryFormatGuidedToggle);
      return settled().then(() => {
        assert.ok(find(SELECTORS.guidedQueryBar), 'Expected to see Guided Query Bar');
        assert.ok(find(SELECTORS.guidedQueryBarFocusedInput), 'Expected focus on guided');
      });
    });

  });

  test('it displays nextGen query bar when clicked', async function(assert) {
    if (nextGenFeature) {
      new ReduxDataHelper(setState)
        .hasRequiredValuesToQuery(true)
        .build();
      await render(hbs`{{query-container}}`);
      assert.equal(find(SELECTORS.guidedQueryBarInput).placeholder, 'Enter individual statements consisting of a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');
      assert.ok(find(SELECTORS.guidedQueryBar), 'Expected to see Guided Query Bar');
      await click(SELECTORS.queryFormatNextGenToggle);
      return settled().then(() => {
        assert.ok(find(SELECTORS.nextGenQueryBar), 'Expected to see NextGen Query Bar');
        assert.equal(findAll(PILL_SELECTORS.allPills).length, 1, 'Expected to see Query Pills component');
      });
    } else {
      assert.ok(true);
    }
  });
});