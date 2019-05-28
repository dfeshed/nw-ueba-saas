import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled, triggerEvent } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { hostDownloads } from '../../../components/state/downloads';
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

module('Integration | Component | downloads', function(hooks) {
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

  test('Downloads has loaded', async function(assert) {

    await render(hbs`{{host-detail/downloads}}`);
    assert.equal(findAll('.host-downloads').length, 1, 'Downloads tab loaded');
  });

  test('Downloads column names', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads}}`);

    assert.equal(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1) .rsa-form-checkbox-label').length, 1, 'Column 1 is a checkbox');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'File name', 'Column 2 is File name');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Type', 'Column 3 is Type');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(4)').textContent.trim(), 'Downloaded', 'Column 4 is Downloaded');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Size', 'Column 5 is Size');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Date Requested', 'Column 6 is Date Requested');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(7)').textContent.trim(), 'checksumSha256', 'Column 7 is checksumSha256');

  });
  test('On right clicking the row it renders the context menu', async function(assert) {
    initState({ endpoint: { hostDownloads } });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-detail/downloads}}{{context-menu}}`);
    triggerEvent(findAll('.filename')[1], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 2, 'Context menu rendered with 2 items');
    });
  });
});
