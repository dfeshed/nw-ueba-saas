import { module, test } from 'qunit';
import Service from '@ember/service';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render, settled, click } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { waitForSockets } from '../../../helpers/wait-for-sockets';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import teardownSockets from '../../../helpers/teardown-sockets';

let setState;

const NETWORK = 'NETWORK';

module('Integration | Component | recon container', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'flashMessages', 'service:flashMessages', 'i18n', 'service:i18n');
    setState = (state) => patchReducer(this, state);
    initialize(this.owner);
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident'
    }));
  });

  hooks.afterEach(function() {
    teardownSockets.apply(this);
  });

  test('recon container in standalone mode', async function(assert) {
    assert.expect(1);

    this.set('eventId', '5');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
      }}
    `);

    assert.equal(findAll('.header-button').length, 0, 'Recon container in standalone mode does not show \'close and expand\' button');
  });

  test('recon container in investigate-events', async function(assert) {
    assert.expect(1);

    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('eventId', '5');
    this.set('eventType', NETWORK);
    this.set('closeAction', () => {});
    this.set('expandAction', () => {});

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
        eventType=eventType
        closeAction=(action closeAction)
        expandAction=(action expandAction)
      }}
    `);

    assert.equal(findAll('.header-button').length, 2, 'Recon container when provided with a closeAction does not run in standalone mode and has \'close and expand\' buttons');
  });

  test('recon container with fatal error code - invalid session', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(124).build();

    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('eventId', '5');
    this.set('eventType', NETWORK);
    this.set('oldEventId', '5');

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
        eventType=eventType
        _previousEventId=oldEventId
      }}
    `);

    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Invalid session ID: 5', 'Appropriate error description for invaild session Id');
    assert.equal(findAll('.fatal-error-close-button').length, 1, 'Found a close recon button');
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');

    return settled().then(() => done());
  });

  test('recon container with fatal error code - sessionId too large', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(11).build();

    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('eventId', '5456544654654564654654');
    this.set('eventType', NETWORK);
    this.set('oldEventId', '5456544654654564654654');

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
        eventType=eventType
        _previousEventId=oldEventId
      }}
    `);

    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'The session id is too large to be handled: 5456544654654564654654', 'Appropriate error description for session Id too large');
    assert.equal(findAll('.fatal-error-close-button').length, 1, 'Found a close recon button');
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');

    return settled().then(() => done());
  });

  test('recon container with fatal error code - session unavailable 115', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(115).build();

    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('eventId', '5');
    this.set('eventType', NETWORK);
    this.set('oldEventId', '5');

    await render(hbs`
      {{recon-container
        eventId=eventId
        endpointId=endpointId
        eventType=eventType
        _previousEventId=oldEventId
      }}
    `);

    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Session is unavailable for viewing.', 'Session is unavailable');
    assert.equal(findAll('.fatal-error-close-button').length, 1, 'Found a close recon button');
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');

    return settled().then(() => done());
  });

  test('recon container with fatal error code - session unavailable 1000', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(1000).build();

    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('eventId', '5');
    this.set('eventType', NETWORK);
    this.set('oldEventId', '5');

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
        eventType=eventType
        _previousEventId=oldEventId
      }}
    `);

    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Session is unavailable for viewing.', 'Session is unavailable');
    assert.equal(findAll('.fatal-error-close-button').length, 1, 'Found a close recon button');
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');

    return settled().then(() => done());
  });

  test('recon container with fatal error code - service unavailable 3', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(3).build();

    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('eventId', '5');
    this.set('eventType', NETWORK);
    this.set('oldEventId', '5');

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
        eventType=eventType
        _previousEventId=oldEventId
      }}
    `);

    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'The service is unavailable', 'Service is unavailable');
    assert.equal(findAll('.fatal-error-close-button').length, 1, 'Found a close recon button');
    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');

    return settled().then(() => done());
  });

  test('recon container with fatal error should always have a close icon on the right, so the window can be closed', async function(assert) {
    const done = waitForSockets();
    const closeSelector = '.fatal-error-close-button';

    assert.expect(2);

    new ReduxDataHelper(setState).apiFatalErrorCode(1000).build();

    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('eventId', '5');
    this.set('eventType', NETWORK);
    this.set('oldEventId', '5');
    this.set('closeAction', function() {
      assert.ok('close action clicked');
      done();
    });

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
        eventType=eventType
        _previousEventId=oldEventId
        closeAction=closeAction
      }}
    `);

    assert.equal(findAll(closeSelector).length, 1, 'Found a close recon button');
    await click(closeSelector);
  });

  test('recon container with fatal error should have a shrink/expand toggle and clicking it should execute an action', async function(assert) {
    const done = assert.async();

    new ReduxDataHelper(setState).apiFatalErrorCode(1000).isReconExpanded(true).build();
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    this.set('eventId', '5');
    this.set('eventType', NETWORK);
    this.set('oldEventId', '5');
    this.set('expandAction', function() {
      assert.ok('action to expand recon');
      done();
    });
    this.set('shrinkAction', function() {
      assert.ok('action to shrink recon');
    });

    await render(hbs`
      {{recon-container
        endpointId=endpointId
        eventId=eventId
        eventType=eventType
        _previousEventId=oldEventId
        expandAction=expandAction
        shrinkAction=shrinkAction
      }}
    `);

    assert.ok(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is visible');

    await click('.rsa-icon-shrink-diagonal-2-filled');
    assert.notOk(find('.rsa-icon-shrink-diagonal-2-filled'), 'icon is not visible');
    assert.ok(find('.rsa-icon-expand-diagonal-4-filled'), 'icon is visible');

    await click('.rsa-icon-expand-diagonal-4-filled');
  });

});
