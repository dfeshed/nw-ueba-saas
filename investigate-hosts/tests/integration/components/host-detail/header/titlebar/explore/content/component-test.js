import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import exploreData from '../../../../../state/explore.fileSearchResults';
import { patchSocket } from '../../../../../../../helpers/patch-socket';
import * as DataCreators from 'investigate-hosts/actions/data-creators/details';
import engineResolverFor from '../../../../../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { applyPatch, revertPatch } from '../../../../../../../helpers/patch-reducer';
import $ from 'jquery';

let setState;

moduleForComponent('host-detail/header/titlebar/explore/content', 'Integration | Component | endpoint host titlebar explore content', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('timezone');
    setState = (state) => {
      applyPatch(Immutable.from(state));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('Date time formate based on timezone', function(assert) {
  this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
  new ReduxDataHelper(setState).exploreData(exploreData.explore).build();
  this.render(hbs`{{host-detail/header/titlebar/explore/content }}`);
  const [snapShotDate] = this.$('.host-explore__content__snapshot:first').children();
  assert.equal(this.$(snapShotDate).text().trim(), '2017-11-17 03:31:39.000 pm (81)');
});

test('File found category navigation calls endpoint sockets', function(assert) {

  assert.expect(6);

  this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
  new ReduxDataHelper(setState).exploreData(exploreData.explore).build();
  this.render(hbs`{{host-detail/header/titlebar/explore/content }}`);
  const fileList = $('.host-explore__content__header__filename');

  const callback = DataCreators.loadDetailsWithExploreInput([]);
  assert.equal(typeof callback, 'function');

  patchSocket((method, modelName) => {
    assert.equal(method, 'getHostDetails');
    assert.equal(modelName, 'endpoint');
  });

  $(fileList[0]).click();
  const state = this.get('redux').getState();
  assert.equal(state.endpoint.detailsInput.scanTime, '1510889499000', 'Scan time validated');
  assert.equal(state.endpoint.explore.selectedTab.tabName, 'FILES', 'Selected tab validated');
  assert.equal(state.endpoint.explore.selectedTab.checksum,
              '1f3883f927e24ee4a238bf0939b47e5bf3c172bf60fa4c6d87b80a087e04246e',
              'checksum value validated');
});


test('Search results not found block test', function(assert) {
  this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
  new ReduxDataHelper(setState).fileSearchResults([]).searchStatus('complete').build();
  this.render(hbs`{{host-detail/header/titlebar/explore/content }}`);
  assert.equal($('.host-explore__no-results').is(':visible'), true, 'For no results host-explore__no-results panel validated');
});