import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render, click } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { hostDownloads } from '../../../../../components/state/downloads';
import Immutable from 'seamless-immutable';

let initState;

module('Integration | Component | directory-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('subdirectory-access has loaded', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('data', { mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 389,
      children: [{ mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 400,
        children: [],
        ancestors: [
          5, 389
        ] }],
      ancestors: [
        5
      ] });
    await render(hbs`{{#host-detail/downloads/directory-wrapper/subdirectory-access data=data}}
      Test
    {{/host-detail/downloads/directory-wrapper/subdirectory-access}}`);
    assert.equal(findAll('.subdirectory').length, 1, 'subdirectory-access has loaded');
  });

  test('loader has loaded', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('data', { mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 389,
      children: [{ mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 400,
        children: [],
        ancestors: [
          5, 389
        ] }],
      ancestors: [
        5
      ] });
    this.set('isLoading', true);
    await render(hbs`{{#host-detail/downloads/directory-wrapper/subdirectory-access data=data isLoading=isLoading}}
      Test
    {{/host-detail/downloads/directory-wrapper/subdirectory-access}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loader has loaded');
    assert.equal(findAll('.mft-directory_arrow').length, 0, 'Arrows have not loaded');
  });

  test('arrow has loaded', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('data', { mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 389,
      children: [{ mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 400,
        children: [],
        ancestors: [
          5, 389
        ] }],
      ancestors: [
        5
      ] });
    await render(hbs`{{#host-detail/downloads/directory-wrapper/subdirectory-access data=data isLoading=isLoading}}
      Test
    {{/host-detail/downloads/directory-wrapper/subdirectory-access}}`);
    this.set('isLoading', false);
    assert.equal(findAll('.rsa-loader').length, 0, 'loader has not loaded');
    assert.equal(findAll('.mft-directory_arrow').length, 1, 'Arrows have loaded');
  });

  test('close arrow has loaded', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('data', { mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 389,
      children: [{ mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 400,
        children: [],
        ancestors: [
          5, 389
        ] }],
      ancestors: [
        5
      ] });
    this.set('isLoading', false);
    this.set('close', true);

    await render(hbs`{{#host-detail/downloads/directory-wrapper/subdirectory-access data=data isLoading=isLoading close=close}}
      Test
    {{/host-detail/downloads/directory-wrapper/subdirectory-access}}`);

    assert.equal(findAll('.mft-directory_arrow .rsa-icon-arrow-right-12-filled').length, 1, 'Close Arrow has loaded');
    assert.equal(findAll('.mft-directory_arrow .rsa-icon-arrow-down-12-filled').length, 0, 'Open Arrow has not loaded');
  });

  test('open arrow has loaded', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('data', { mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 389,
      children: [{ mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 400,
        children: [],
        ancestors: [
          5, 389
        ] }],
      ancestors: [
        5
      ] });
    this.set('isLoading', false);
    this.set('close', false);

    await render(hbs`{{#host-detail/downloads/directory-wrapper/subdirectory-access data=data isLoading=isLoading close=close}}
      Test
    {{/host-detail/downloads/directory-wrapper/subdirectory-access}}`);

    assert.equal(findAll('.mft-directory_arrow .rsa-icon-arrow-down-12-filled').length, 1, 'Open Arrow has loaded');
    assert.equal(findAll('.mft-directory_arrow .rsa-icon-arrow-right-12-filled').length, 0, 'Close Arrow has not loaded');
  });

  test('Toggling of arrows on click', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('data', { mftId: '5d19c6c7c8811e3057c68fd8',
      recordNumber: 389,
      children: [{ mftId: '5d19c6c7c8811e3057c68fd8',
        recordNumber: 400,
        children: [],
        ancestors: [
          5, 389
        ] }],
      ancestors: [
        5
      ] });
    this.set('isLoading', false);
    this.set('close', false);

    await render(hbs`{{#host-detail/downloads/directory-wrapper/subdirectory-access data=data isLoading=isLoading close=close}}
      Test
    {{/host-detail/downloads/directory-wrapper/subdirectory-access}}`);

    assert.equal(findAll('.mft-directory_arrow .rsa-icon-arrow-down-12-filled').length, 1, 'Open Arrow has loaded');
    await click(find('.mft-directory_arrow .rsa-icon-arrow-down-12-filled'));
    assert.equal(findAll('.mft-directory_arrow .rsa-icon-arrow-right-12-filled').length, 1, 'Close Arrow has loaded on click');

    assert.equal(findAll('.mft-directory_loader .rsa-loader').length, 0, 'loader has not loaded');
    await click(find('.mft-directory_arrow .rsa-icon-arrow-right-12-filled'));
    assert.equal(findAll('.mft-directory_loader .rsa-loader').length, 1, 'loader has loaded on click');
  });
});