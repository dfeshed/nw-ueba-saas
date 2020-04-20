import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import exploreData from '../../../../../state/explore.fileSearchResults';
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
    this.set('navigateToTab', () => {});
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Date time formate based on timezone', async function(assert) {
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    new ReduxDataHelper(setState).exploreData(exploreData.explore).build();
    await render(hbs`{{host-detail/header/titlebar/explore/content navigateToTab=navigateToTab}}`);
    const snapShotDate = document.querySelector('.host-explore__content__snapshot h3');
    assert.equal(snapShotDate.textContent.trim(), '2017-11-17 03:31:39.000 pm (81)');
  });

  test('File found category navigation calls endpoint sockets', async function(assert) {

    assert.expect(2);

    this.get('timezone').set('_selected', { zoneId: 'GMT' });
    new ReduxDataHelper(setState)
      .exploreData(exploreData.explore)
      .snapShot([{ serviceId: '123', scanStartTime: 1510889499000 }])
      .endpointQuery({ endpointQuery: { serverId: '123' } }).build();

    this.set('navigateToTab', ({ tabName }) => {
      assert.ok(true);
      assert.equal(tabName, 'FILES');
    });

    await render(hbs`{{host-detail/header/titlebar/explore/content navigateToTab=navigateToTab}}`);
    const fileList = document.querySelectorAll('.host-explore__content__header__filename');
    await click(fileList[0]);

  });


  test('Search results not found block test', async function(assert) {
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    new ReduxDataHelper(setState).fileSearchResults([]).searchStatus('complete').build();
    await render(hbs`{{host-detail/header/titlebar/explore/content navigateToTab=navigateToTab}}`);
    assert.equal(document.querySelectorAll('.host-explore__no-results').length, 1, 'For no results host-explore__no-results panel validated');
  });

  test('It displays the warning message', async function(assert) {
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    new ReduxDataHelper(setState).exploreData(exploreData.explore).isDataTruncated(true).build();
    await render(hbs`{{host-detail/header/titlebar/explore/content navigateToTab=navigateToTab}}`);
    assert.equal(document.querySelectorAll('.search-result-note').length, 1);
  });
});
