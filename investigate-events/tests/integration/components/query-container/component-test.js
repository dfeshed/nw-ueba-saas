import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { click, fillIn, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import PILL_SELECTORS from './pill-selectors';
import KEY_MAP from 'investigate-events/util/keys';

const ENTER_KEY = KEY_MAP.enter.code;

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

  test('it can execute a query via ENTER after deleting a selected meta', async function(assert) {
    const done = assert.async();
    assert.expect(0);
    new ReduxDataHelper(setState)
      .language()
      .canQueryGuided()
      .pillsDataEmpty()
      .build();

    this.set('executeQuery', () => {
      // This will timeout if the action isn't called
      done();
    });

    await render(hbs`
      {{query-container
        executeQuery=(action executeQuery)
      }}
    `);

    // select meta
    await selectChoose(PILL_SELECTORS.metaTrigger, PILL_SELECTORS.powerSelectOption, 0);// option a
    // focus back on meta
    await click(PILL_SELECTORS.meta);
    // Clear input to show all meta options
    await fillIn(PILL_SELECTORS.metaInput, '');
    // press ENTER to submit query
    await triggerKeyEvent(PILL_SELECTORS.metaSelectInput, 'keydown', ENTER_KEY);
  });
});
