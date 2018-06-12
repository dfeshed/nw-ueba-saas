import { module, test } from 'qunit';
import Service from '@ember/service';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render, settled } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { waitForSockets } from '../../../helpers/wait-for-sockets';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import teardownSockets from '../../../helpers/teardown-sockets';

let setState;

module('Integration | Component | recon container', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
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

    await render(hbs`{{recon-container eventId=eventId endpointId=endpointId}}`);

    assert.equal(findAll('.header-button').length, 0, 'Recon container in standalone mode does not show \'close and expand\' button');
  });

  test('recon container in investigate-events', async function(assert) {
    assert.expect(1);

    this.set('eventId', '5');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');
    this.set('closeAction', () => {});
    this.set('expandAction', () => {});

    await render(hbs`{{recon-container eventId=eventId endpointId=endpointId closeAction=(action closeAction) expandAction=(action expandAction)}}`);

    assert.equal(findAll('.header-button').length, 2, 'Recon container when provided with a closeAction does not run in standalone mode and has \'close and expand\' buttons');
  });

  test('recon container with fatal error code - invalid session', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(124).build();

    this.set('eventId', '5');
    this.set('oldEventId', '5');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    await render(hbs`{{recon-container eventId=eventId endpointId=endpointId oldEventId=oldEventId}}`);

    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Invalid session ID: 5', 'Appropriate error description for invaild session Id');

    return settled().then(() => done());
  });

  test('recon container with fatal error code - sessionId too large', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(11).build();

    this.set('eventId', '5456544654654564654654');
    this.set('oldEventId', '5456544654654564654654');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    await render(hbs`{{recon-container eventId=eventId endpointId=endpointId oldEventId=oldEventId}}`);
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'The session id is too large to be handled: 5456544654654564654654', 'Appropriate error description for session Id too large');

    return settled().then(() => done());
  });

  test('recon container with fatal error code - session unavailable 115', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(115).build();

    this.set('eventId', '5');
    this.set('oldEventId', '5');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    await render(hbs`{{recon-container eventId=eventId endpointId=endpointId oldEventId=oldEventId}}`);
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Session is unavailable for viewing.', 'Session is unavailable');

    return settled().then(() => done());
  });

  test('recon container with fatal error code - session unavailable 1000', async function(assert) {
    const done = waitForSockets();

    new ReduxDataHelper(setState).apiFatalErrorCode(1000).build();

    this.set('eventId', '5');
    this.set('oldEventId', '5');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    await render(hbs`{{recon-container eventId=eventId endpointId=endpointId oldEventId=oldEventId}}`);
    assert.equal(find('.rsa-panel-message .message').textContent.trim(), 'Session is unavailable for viewing.', 'Session is unavailable');

    return settled().then(() => done());
  });

});
