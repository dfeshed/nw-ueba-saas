import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { waitUntil, settled } from '@ember/test-helpers';
import { throwSocket } from '../../helpers/patch-socket';
import { patchReducer } from '../../helpers/vnext-patch';
import { initializeRecon } from 'recon/actions/data-creators';
import { sessionNotFound, nextgenException, summaryFatalError } from './data';
import { bindActionCreators } from 'redux';
import { waitForRedux } from '../../helpers/wait-for-redux';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import { isContentError } from 'recon/reducers/data-selectors';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import teardownSockets from '../../helpers/teardown-sockets';

module('Unit | Actions | initializeRecon', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident'
    }));
  });

  hooks.afterEach(function() {
    teardownSockets.apply(this);
  });

  test('initializeRecon will flag fatal error when code 1000 is returned from web socket', async function(assert) {
    assert.expect(2);

    const done = throwSocket({ methodToThrow: 'query', modelNameToThrow: 'reconstruction-summary', message: sessionNotFound });

    new ReduxDataHelper((state) => patchReducer(this, state)).isTextView().build();

    const redux = this.owner.lookup('service:redux');
    const init = bindActionCreators(initializeRecon, redux.dispatch.bind(redux));

    init({
      aliases: {},
      language: {},
      eventId: '99719',
      endpointId: 'b103f57c-ed1a-4862-aa53-e30687f130b3'
    });

    await waitForRedux('recon.data.contentLoading', true);
    await waitForRedux('recon.data.contentLoading', false);

    const { recon } = redux.getState();
    assert.equal(recon.data.apiFatalErrorCode, 1000);
    assert.equal(isContentError(recon), false);

    return settled().then(() => done());
  });

  test('initializeRecon will flag error code 1 when NextgenException thrown server side', async function(assert) {
    assert.expect(4);

    const done = throwSocket({ methodToThrow: 'query', modelNameToThrow: 'reconstruction-summary', message: nextgenException });

    new ReduxDataHelper((state) => patchReducer(this, state)).isTextView().build();

    const redux = this.owner.lookup('service:redux');
    const init = bindActionCreators(initializeRecon, redux.dispatch.bind(redux));

    init({
      aliases: {},
      language: {},
      eventId: '99719',
      endpointId: 'b103f57c-ed1a-4862-aa53-e30687f130b3'
    });

    await waitForRedux('recon.data.contentLoading', true);
    await waitForRedux('recon.data.contentLoading', false);

    const { recon } = redux.getState();
    assert.equal(recon.header.headerError, true);
    assert.equal(recon.header.headerErrorCode, 1);
    assert.equal(recon.data.apiFatalErrorCode, 0);
    assert.equal(isContentError(recon), false);

    return settled().then(() => done());
  });

  test('initializeRecon will flag error code 3 when summaryFatalError thrown server side', async function(assert) {
    assert.expect(1);

    const done = throwSocket({ methodToThrow: 'query', modelNameToThrow: 'reconstruction-summary', message: summaryFatalError });

    new ReduxDataHelper((state) => patchReducer(this, state)).isTextView().build();

    const redux = this.owner.lookup('service:redux');
    const init = bindActionCreators(initializeRecon, redux.dispatch.bind(redux));

    init({
      aliases: {},
      language: {},
      eventId: '99719',
      endpointId: 'b103f57c-ed1a-4862-aa53-e30687f130b3'
    });

    await waitUntil(() => {
      const state = redux.getState();
      return state.recon.data.contentLoading === false;
    }, { timeout: 10000 });

    const { recon } = redux.getState();
    assert.equal(recon.data.apiFatalErrorCode, 3);

    await settled().then(() => done());
  });

  test('initializeRecon will fetch meta if none passed in via eventMeta', async function(assert) {
    assert.expect(2);
    const done = assert.async();
    const redux = this.owner.lookup('service:redux');
    const init = bindActionCreators(initializeRecon, redux.dispatch.bind(redux));

    init({
      aliases: [],
      endpointId: '1',
      eventId: '1',
      language: []
    });

    await waitUntil(() => {
      const { meta } = redux.getState().recon.meta;
      return Array.isArray(meta) && meta.length === 20;
    }, { timeout: 10000 });

    const { meta } = redux.getState().recon.meta;
    const [ metaEl ] = meta;
    assert.equal(typeof(metaEl), 'object', 'meta was not correct type');
    assert.equal(metaEl[0], 'service', 'meta key was incorrect');

    await settled().then(() => done());
  });
});
