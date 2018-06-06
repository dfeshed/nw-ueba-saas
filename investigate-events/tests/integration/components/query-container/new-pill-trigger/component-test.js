import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';

import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { createBasicPill } from '../pill-util';

const newPillTrigger = '.new-pill-trigger';
const metaPowerSelectTrigger = '.pill-meta .ember-power-select-trigger';

const ESCAPE_KEY = '27';

let setState;

module('Integration | Component | new-pill-trigger', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('shows trigger by default', async function(assert) {
    await render(hbs`{{query-container/new-pill-trigger}}`);
    assert.ok(find(newPillTrigger).textContent === '|', 'trigger renders by default');
  });

  test('shows new pill entry when trigger is triggered', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/new-pill-trigger}}`);
    await click(newPillTrigger);
    assert.equal(findAll(metaPowerSelectTrigger).length, 1);
  });

  test('ESC key returns user to trigger', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    await render(hbs`{{query-container/new-pill-trigger}}`);
    await click(newPillTrigger);
    assert.equal(findAll(metaPowerSelectTrigger).length, 1);
    await focus(metaPowerSelectTrigger);
    await triggerKeyEvent(metaPowerSelectTrigger, 'keydown', ESCAPE_KEY);
    assert.equal(findAll(metaPowerSelectTrigger).length, 0);
  });

  test('it broadcasts a message when a pill is created', async function(assert) {
    new ReduxDataHelper(setState).language().pillsDataEmpty().build();
    assert.expect(1);
    this.set('handleMessage', (type) => {
      assert.equal(type, 'PILL::CREATED', 'Wrong message type');
    });
    await render(hbs`{{query-container/new-pill-trigger sendMessage=(action handleMessage)}}`);
    await click(newPillTrigger);
    await createBasicPill();
  });
});