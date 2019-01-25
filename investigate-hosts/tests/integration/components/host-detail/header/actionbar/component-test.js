import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { click, find, findAll, render, waitUntil } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { snapShot } from '../../../../../data/data';

let setState;

module('Integration | Component | host detail actionbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('snapshot power select renders appropriate items', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    await clickTrigger();
    assert.ok(find('.actionbar .ember-power-select-trigger'), 'should render the power-select trigger');
    assert.equal(findAll('.ember-power-select-option').length, 4, 'dropdown  rendered with available snapShots');
  });

  skip('on selecting snapshot initializes the agent details input', async function(assert) {
    assert.expect(2);
    const redux = this.owner.lookup('service:redux');
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .scanTime('2017-08-29T10:23:49.452Z')
      .agentId(1345)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    await selectChoose('.actionbar', '.ember-power-select-option', 3);
    await waitUntil(() => {
      return redux.getState().endpoint.detailsInput.animation !== 'default';
    }, { timeout: 6000 });
    const { endpoint: { detailsInput: { animation, agentId } } } = redux.getState();
    assert.equal(animation, 'toUp');
    assert.equal(agentId, 1345);
  });

  skip('with scan time earlier than snapshot time, snapshot transitions down', async function(assert) {
    assert.expect(2);
    const redux = this.owner.lookup('service:redux');
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .scanTime('2017-01-01T10:23:49.452Z')
      .agentId(1345)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    await selectChoose('.actionbar', '.ember-power-select-option', 3);
    await waitUntil(() => {
      return redux.getState().endpoint.detailsInput.animation !== 'default';
    });
    const { endpoint: { detailsInput: { animation, agentId } } } = redux.getState();
    assert.equal(animation, 'toDown');
    assert.equal(agentId, 1345);
  });

  test('test for start scan button', async function(assert) {
    await render(hbs `{{host-detail/header/actionbar}}`);
    assert.ok(find('.host-start-scan-button'), 'scan-command renders giving the start scan button');
  });

  test('test for Export to JSON', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .scanTime('2017-01-01T10:23:49.452Z')
      .agentId(1)
      .build();
    await render(hbs `{{host-detail/header/actionbar}}`);

    patchSocket((method, model, query) => {
      assert.equal(method, 'exportFileContext');
      assert.deepEqual(query,
        {
          'data': {
            'agentId': 1,
            'categories': [
              'AUTORUNS'
            ],
            'scanTime': '2017-01-01T10:23:49.452Z'
          }
        });
    });

    await click('.host-action-buttons .action-button:nth-child(2) .rsa-form-button-wrapper');
  });

  test('test for Export to JSON disabled', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot([])
      .build();
    await render(hbs `{{host-detail/header/actionbar}}`);
    assert.ok(find('.host-action-buttons .action-button:nth-child(2) .rsa-form-button-wrapper.is-disabled'), 'Export to JSON disabled when no snapshots available');
  });

  test('test when Export to JSON is in download status', async function(assert) {
    new ReduxDataHelper(setState)
      .isJsonExportCompleted(false)
      .build();
    await render(hbs `{{host-detail/header/actionbar}}`);
    assert.ok(find('.host-action-buttons .action-button:nth-child(2) .rsa-form-button-wrapper.is-disabled'), 'Export to JSON disabled when in downloading state');
    assert.equal(find('.host-action-buttons .action-button:nth-child(2) .rsa-form-button-wrapper').textContent.trim(), 'Downloading', 'Export to JSON is in downloading state and button is disabled');
  });

  test('when JSON export is completed', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .build();
    await render(hbs `{{host-detail/header/actionbar}}`);
    assert.equal(find('.host-action-buttons .action-button:nth-child(2) .rsa-form-button-wrapper').textContent.trim(), 'Export to JSON', 'In initial state and when previous export is completed, button is active');
  });

  test('snapshot selection is disabled when process details is active', async function(assert) {
    new ReduxDataHelper(setState)
      .isProcessDetailsView(true)
      .snapShot(snapShot)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    await clickTrigger();
    assert.ok(find('.actionbar .ember-power-select-trigger'), 'should render the power-select trigger');
    assert.equal(findAll('.actionbar .ember-power-select-trigger[aria-disabled=true]').length, 1);
  });
});
