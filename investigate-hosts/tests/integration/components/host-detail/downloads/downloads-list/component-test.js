import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render, settled, triggerEvent, click } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { hostDownloads } from '../../../../components/state/downloads';
import Immutable from 'seamless-immutable';

const callback = () => {};
const e = {
  clientX: 20,
  clientY: 20,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
const wormhole = 'wormhole-context-menu';

let initState;

module('Integration | Component | downloads-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  hooks.afterEach(function() {
    revertPatch();
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('Downloads-list has loaded', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('disableActions', { isShowDeleteAction: true });
    await render(hbs`{{host-detail/downloads/downloads-list disableActions=disableActions}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'Downloads-list loaded');
  });

  test('On right clicking the row it renders the context menu', async function(assert) {
    initState({ endpoint: { hostDownloads } });
    this.set('disableActions', { isShowDeleteAction: true });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/downloads }}{{context-menu}}`);
    triggerEvent(findAll('.fileName')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 2, 'Context menu rendered with 2 items');
    });
  });
  test('On right clicking the row it renders the context menu with no delete option', async function(assert) {
    initState({ endpoint: { hostDownloads } });
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/downloads accessControl=accessControl}}{{context-menu}}`);
    triggerEvent(findAll('.fileName')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 1, 'Context menu rendered with 1 items');
    });
  });

  test('1 checkbox rendered as part of header', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('disableActions', { isShowDeleteAction: true });
    await render(hbs`{{host-detail/downloads/downloads-list disableActions=disableActions}}`);

    assert.equal(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1) .rsa-form-checkbox-label').length, 1, 'Column 1 is a checkbox');
  });

  test('1 sort enabled rendered as part of header', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    this.set('disableActions', { isShowDeleteAction: true });
    await render(hbs`{{host-detail/downloads/downloads-list disableActions=disableActions}}`);
    assert.equal(findAll('.is-sorted.desc i').length, 1, '1 column sort enabled');
    await click(find('.is-sorted.desc i'));
    assert.equal(findAll('.is-sorted.desc i').length, 0, 'column sort no longer decending');
    assert.equal(findAll('.is-sorted.asc i').length, 1, 'column sort changed to ascending');
  });

  test('if files are downloading display loader', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .areFilesLoading('wait')
      .build();
    this.set('disableActions', { isShowDeleteAction: true });
    await render(hbs`{{host-detail/downloads/downloads-list disableActions=disableActions}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loader rendered');
  });

  test('if files are done downloading loader is not displayed', async function(assert) {
    new ReduxDataHelper(initState)
      .hostDownloads(hostDownloads)
      .areFilesLoading('completed')
      .build();
    this.set('disableActions', { isShowDeleteAction: true });
    await render(hbs`{{host-detail/downloads/downloads-list disableActions=disableActions}}`);
    assert.equal(findAll('.rsa-loader').length, 0, 'loader not rendered');
  });
});
