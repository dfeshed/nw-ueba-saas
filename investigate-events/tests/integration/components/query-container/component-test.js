import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, findAll, render } from '@ember/test-helpers';

import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import PILL_SELECTORS from './pill-selectors';

let setState;

module('Integration | Component | query-container', function(hooks) {
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

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .queryStats()
      .build();
    await render(hbs`{{query-container}}`);
    assert.ok(find('.console-trigger'));

    assert.equal(findAll(PILL_SELECTORS.queryButton).length, 1, 'button should be present');
  });

  test('TimeRange should be updated when start/endTime in state is updated', async function(assert) {
    const startTimeinSec = 193885209; // Sun Feb 22 1976 20:00:09
    const endTimeinSec = 1519347609; // Thu Feb 22 2018 20:00:09
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .queryStats()
      .startTime(startTimeinSec)
      .endTime(endTimeinSec)
      .build();

    await render(hbs`{{query-container}}`);
    assert.equal(find('.rsa-date-time-range').getAttribute('title').trim(), 'Calculated duration: 42 years 59 seconds');
  });
});
