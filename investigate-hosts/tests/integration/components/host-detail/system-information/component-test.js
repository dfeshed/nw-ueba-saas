import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, click } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { linux, windows, mac } from '../../state/overview.hostdetails';
import engineResolver from '../../../../helpers/engine-resolver';
import { selectChoose } from 'ember-power-select/test-support/helpers';

let setState;

const linuxTabColumns = {
  hostFileEntries: ['IP ADDRESS', 'DNS NAME'],
  mountedPaths: ['PATH', 'FILE SYSTEM', 'REMOTE PATH', 'OPTIONS'],
  bashHistory: ['USER NAME', 'COMMAND']
};
const windowsTabColumns = {
  hostFileEntries: ['IP ADDRESS', 'DNS NAME'],
  networkShares: ['NAME', 'DESCRIPTION', 'PATH', 'PERMISSIONS', 'TYPE', 'MAX USERS', 'CURRENT USERS'],
  securityProducts: ['DISPLAY NAME', 'COMPANYNAME', 'INSTANCE', 'FEATURES', 'TYPE', 'VERSION'],
  windowsPatches: ['PATCHES']
};

module('Integration | Component | host-detail/system-information', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    setState = (machine) => {
      const { overview } = machine;
      const state = Immutable.from({ endpoint: { overview } });
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  // Test for the number of tabs rendered based on the OS selected
  // Mac
  test('Number of tabs rendered based on the OS selected, Mac : 3', async function(assert) {
    setState(mac);
    await render(hbs`{{host-detail/system-information}}`);
    const numberOfNavTabs = findAll('.system-information-wrapper .host-title-bar > div').length;
    assert.equal(numberOfNavTabs, 3, 'Number of tabs rendered based on the OS selected');
  });
  // Windows
  test('Number of tabs rendered based on the OS selected, Windows : 4', async function(assert) {
    setState(windows);
    await render(hbs`{{host-detail/system-information}}`);
    const numberOfNavTabs = findAll('.system-information-wrapper .host-title-bar > div').length;
    assert.equal(numberOfNavTabs, 4, 'Number of tabs rendered based on the OS selected');
  });
  // Linux
  test('Number of tabs rendered based on the OS selected, Linux : 3', async function(assert) {
    setState(linux);
    await render(hbs`{{host-detail/system-information}}`);
    const numberOfNavTabs = findAll('.system-information-wrapper .host-title-bar > div').length;
    assert.equal(numberOfNavTabs, 3, 'Number of tabs rendered based on the OS selected');
  });
  // *End* Test for the number of tabs rendered based on the OS selected

  // Tabs availabe when Windows OS agent is selected

  // Host File Entries present for all 3 OS types
  test('Host File Entries rendered', async function(assert) {
    setState(windows);

    await render(hbs`{{host-detail/system-information}}`);

    const hostFileEntries = findAll('.rsa-data-table-header-row > div').length;
    assert.equal(hostFileEntries, windowsTabColumns.hostFileEntries.length, 'Number of columns Host File Entries tab has');

    const hostFileEntriesResults = findAll('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(hostFileEntriesResults, 3, 'Number of resultant rows for Host File Entries');
  });

  // Network Shares
  test('Network Shares rendered', async function(assert) {
    setState(windows);

    await render(hbs`{{host-detail/system-information}}`);

    await click('.system-information-wrapper .host-title-bar > div:nth-child(2)');

    const networkShares = findAll('.rsa-data-table-header-row > div').length;
    assert.equal(networkShares, windowsTabColumns.networkShares.length, 'Number of columns Network Shares tab has');

    const networkSharesResults = findAll('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(networkSharesResults, 4, 'Number of resultant rows for Network Shares tab');
  });

  // Security Products
  test('Security Products rendered', async function(assert) {
    setState(windows);

    await render(hbs`{{host-detail/system-information}}`);

    await click('.system-information-wrapper .host-title-bar > div:nth-child(3)');
    const securityProducts = findAll('.rsa-data-table-header-row > div').length;
    assert.equal(securityProducts, windowsTabColumns.securityProducts.length, 'Number of columns Security Products tab has');

    const securityProductsResults = findAll('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(securityProductsResults, 5, 'Number of resultant rows for Security Products tab');
  });

  // Windows Patches
  test('Windows Patches rendered', async function(assert) {
    setState(windows);

    await render(hbs`{{host-detail/system-information}}`);

    await click('.system-information-wrapper .host-title-bar > div:nth-child(4)');

    const windowsPatches = findAll('.rsa-data-table-header-row > div').length;
    assert.equal(windowsPatches, windowsTabColumns.windowsPatches.length, 'Number of columns Windows Patches tab has');

    const windowsPatchesResults = findAll('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(windowsPatchesResults, 4, 'Number of resultant rows for Windows Patches tab');
  });

  // *End* Tabs availabe when Windows OS agent is selected

  // Tabs availabe when Linux OS agent is selected


  // Mounted Paths
  test('Mounted Paths rendered', async function(assert) {
    setState(linux);

    await render(hbs`{{host-detail/system-information}}`);

    await click('.system-information-wrapper .host-title-bar > div:nth-child(2)');

    const mountedPaths = findAll('.rsa-data-table-header-row > div').length;
    assert.equal(mountedPaths, linuxTabColumns.mountedPaths.length, 'Number of columns Mounted Paths tab has');
    const mountedPathsResults = findAll('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(mountedPathsResults, 3, 'Number of resultant rows for Mounted Paths tab');
  });

  // Bash History
  test('Bash History rendered', async function(assert) {
    setState(linux);

    await render(hbs`{{host-detail/system-information}}`);

    await click('.system-information-wrapper .host-title-bar > div:nth-child(3)');
    const bashHistory = findAll('.rsa-data-table-header-row > div').length;
    assert.equal(bashHistory, linuxTabColumns.bashHistory.length, 'Number of columns Bash History tab has');
    const bashHistoryResults = findAll('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(bashHistoryResults, 6, 'Number of resultant rows for Bash History tab');
  });

  // Bash History Filter Dropdown
  test('Filter Dropdown is present only for Bash History', async function(assert) {
    assert.expect(4);
    setState(linux);

    await render(hbs`{{host-detail/system-information}}`);
    assert.equal(findAll('.power-select').length, 0, 'Power select is not present when Bash History is not selected');

    await click('.system-information-wrapper .host-title-bar > div:nth-child(3)');
    assert.equal(findAll('.power-select').length, 1, 'Power select is present when Bash History is selected');

    assert.equal(findAll('.rsa-data-table-body .rsa-data-table-body-row').length, 6, 'Number of resultant rows for Bash History tab before filter applied');
    await selectChoose('.system-information__content', '.ember-power-select-option', 1);
    assert.equal(findAll('.rsa-data-table-body .rsa-data-table-body-row').length, 4, 'Number of resultant rows for Bash History tab after filter applied');
  });

  // Test if no results message is present
  test('No Results Found text present when there are no results', async function(assert) {
    assert.expect(1);
    setState(mac);
    await render(hbs`{{host-detail/system-information}}`);
    await click('.system-information-wrapper .host-title-bar > div:nth-child(2)');
    assert.equal(find('.message').textContent.trim(), 'No Results Found.', 'No Results Found text present when there are no results');
  });
});