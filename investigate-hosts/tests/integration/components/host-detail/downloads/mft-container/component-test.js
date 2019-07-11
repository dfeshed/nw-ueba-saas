import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { hostDownloads } from '../../../../components/state/downloads';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;
module('Integration | Component | mft-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });
  test('Empty mft-container has rendered', async function(assert) {

    await render(hbs`{{host-detail/downloads/mft-container}}`);
    assert.equal(findAll('.mft-container').length, 1, 'mft-container rendered');
    assert.equal(findAll('.mft-bar').length, 0, 'mft-bar does not render');
    assert.equal(findAll('.mft-action').length, 0, 'mft-action does not render');
  });

  test('mft-container has rendered', async function(assert) {
    new ReduxDataHelper(setState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/mft-container}}`);
    assert.equal(findAll('.mft-container').length, 1, 'mft-container rendered');
    assert.equal(findAll('.mft-bar').length, 2, 'mft-container should rendered tree nad table panels');
    assert.equal(findAll('.mft-action').length, 2, 'mft-container should have two action bars');
  });

  test('Mft-container filter panel opens on click of filter button', async function(assert) {

    new ReduxDataHelper(setState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/mft-container}}`);
    assert.equal(findAll('.mft-container').length, 1, 'mft-container rendered');
    assert.equal(findAll('.mft-bar').length, 2, 'mft-container should rendered tree nad table panels');
    assert.equal(findAll('.mft-action').length, 2, 'mft-container should have two action bars');
    await click(find('.open-filter-panel'));
    assert.equal(findAll('.left-zone').length, 1, 'mft-container filter panel opens');
  });

});