import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import exploreData from '../../../../../state/explore.fileSearchResults';
import { patchSocket } from '../../../../../../../helpers/patch-socket';
import * as DataCreators from 'investigate-hosts/actions/data-creators/details';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { applyPatch, revertPatch } from '../../../../../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | endpoint host titlebar explore content', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.timezone = this.owner.lookup('service:timezone');
    setState = (state) => {
      applyPatch(Immutable.from(state));
      this.redux = this.owner.lookup('service:redux');
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Date time formate based on timezone', async function(assert) {
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    new ReduxDataHelper(setState).exploreData(exploreData.explore).build();
    await render(hbs`{{host-detail/header/titlebar/explore/content }}`);
    const snapShotDate = document.querySelector('.host-explore__content__snapshot h3');
    assert.equal(snapShotDate.textContent.trim(), '2017-11-17 03:31:39.000 pm (81)');
  });

  test('File found category navigation calls endpoint sockets', async function(assert) {

    assert.expect(6);

    this.get('timezone').set('_selected', { zoneId: 'GMT' });
    new ReduxDataHelper(setState)
      .exploreData(exploreData.explore)
      .snapShot([{ serviceId: '123', scanStartTime: 1510889499000 }])
      .endpointQuery({ endpointQuery: { serverId: '123' } }).build();
    await render(hbs`{{host-detail/header/titlebar/explore/content }}`);
    const fileList = document.querySelectorAll('.host-explore__content__header__filename');

    const callback = DataCreators.loadDetailsWithExploreInput([]);
    assert.equal(typeof callback, 'function');

    patchSocket((method, modelName) => {
      assert.equal(method, 'getHostDetails');
      assert.equal(modelName, 'endpoint');
    });

    await click(fileList[0]);
    const state = this.get('redux').getState();
    assert.equal(state.endpoint.detailsInput.scanTime, '1510889499000', 'Scan time validated');
    assert.equal(state.endpoint.explore.selectedTab.tabName, 'FILES', 'Selected tab validated');
    assert.equal(
      state.endpoint.explore.selectedTab.checksum,
      '1f3883f927e24ee4a238bf0939b47e5bf3c172bf60fa4c6d87b80a087e04246e',
      'checksum value validated'
    );
  });


  test('Search results not found block test', async function(assert) {
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    new ReduxDataHelper(setState).fileSearchResults([]).searchStatus('complete').build();
    await render(hbs`{{host-detail/header/titlebar/explore/content }}`);
    assert.equal(document.querySelectorAll('.host-explore__no-results').length, 1, 'For no results host-explore__no-results panel validated');
  });

  test('It displays the warning message', async function(assert) {
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    new ReduxDataHelper(setState).exploreData(exploreData.explore).isDataTruncated(true).build();
    await render(hbs`{{host-detail/header/titlebar/explore/content }}`);
    assert.equal(document.querySelectorAll('.search-result-note').length, 1);
  });
});
