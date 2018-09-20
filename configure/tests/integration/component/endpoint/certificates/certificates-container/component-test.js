import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | certificates-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('configure')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('container for certificates render', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-container').length, 1, 'certificates container has rendered.');
  });

  test('action bar is rendered', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-action-bar').length, 1, 'certificates action bar has rendered.');
  });

  test('certificates body is rendered', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-body').length, 1, 'certificates body has rendered.');
  });

  test('certificates footer is rendered', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-footer').length, 1, 'certificates footer has rendered.');
  });

  test('service-selector is rendered', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.service-selector').length, 1, 'service-selector has rendered.');
  });

  test('it renders error page when endpointserver is offline', async function(assert) {
    new ReduxDataHelper(setState)
      .isEndpointServerOffline(true)
      .build();
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-body').length, 0, 'certificates body has not rendered.');
    assert.equal(findAll('.error-page').length, 1, 'endpoint server is offline');
  });

  test('it renders certificate container when endpointserver is online', async function(assert) {
    new ReduxDataHelper(setState)
      .isEndpointServerOffline(false)
      .build();
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-body').length, 1, 'certificates body has rendered.');
    assert.equal(findAll('.error-page').length, 0, 'endpoint server is online');
  });
});
