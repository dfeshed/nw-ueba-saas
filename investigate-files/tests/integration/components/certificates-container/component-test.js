import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click, settled, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../helpers/patch-reducer';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

let initState;

module('Integration | Component | certificates-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.dateFormat = this.owner.lookup('service:dateFormat');
    this.timeFormat = this.owner.lookup('service:timeFormat');
    this.timezone = this.owner.lookup('service:timezone');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('container for certificates render', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-container').length, 1, 'certificates container has rendered.');
  });

  test('action bar is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-action-bar').length, 1, 'certificates action bar has rendered.');
  });

  test('certificates body is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-body').length, 1, 'certificates body has rendered.');
  });

  test('certificates footer is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-footer').length, 1, 'certificates footer has rendered.');
  });
  test('certificates filter panel rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.filter-wrapper').length, 1, 'certificates filter panel has rendered.');
  });
  test('certificates close icon should rendered', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .build();
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.close-certificate-view-button').length, 1, 'certificates close icon should rendered.');
    await click('.close-certificate-view-button button');
    return settled().then(() => {
      const state = this.owner.lookup('service:redux').getState();
      assert.equal(state.certificate.list.isCertificateView, false, 'Certificate view is closed');
    });
  });

  test('event analysis button rendered and disabled', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .build();
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-container .actionbar-pivot-to-investigate').length, 1, 'Event analysis button rendered');
    assert.equal(find('.certificates-container .pivot-to-investigate-button').classList.contains('is-disabled'), true, 'Event analysis button disabled');
  });

  test('event analysis button enabled', async function(assert) {
    new ReduxDataHelper(initState)
      .isCertificateView(true)
      .selectedCertificatesList(new Array(1))
      .build();
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-container .actionbar-pivot-to-investigate').length, 1, 'Event analysis button rendered');
    assert.equal(find('.certificates-container .pivot-to-investigate-button').classList.contains('is-disabled'), false, 'Event analysis button enabled');
  });

});
