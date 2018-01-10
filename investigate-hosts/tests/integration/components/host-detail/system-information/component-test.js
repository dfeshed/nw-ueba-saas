import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';
import { linux, windows, mac } from '../../state/overview.hostdetails';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

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

moduleForComponent('host-detail/system-information', 'Integration | Component | endpoint host detail/system information', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    setState = (machine) => {
      const { overview } = machine;
      const state = Immutable.from({ endpoint: { overview } });
      applyPatch(state);
      this.inject.service('redux');
    };
  },

  afterEach() {
    revertPatch();
  }
});

// Test for the number of tabs rendered based on the OS selected
// Mac
test('Number of tabs rendered based on the OS selected, Mac : 3', function(assert) {
  setState(mac);

  this.render(hbs`{{host-detail/system-information}}`);

  return wait().then(() => {
    const numberOfNavTabs = this.$('.system-information-wrapper .host-title-bar > div').length;
    assert.equal(numberOfNavTabs, 3, 'Number of tabs rendered based on the OS selected');
  });
});
// Windows
test('Number of tabs rendered based on the OS selected, Windows : 4', function(assert) {
  setState(windows);

  this.render(hbs`{{host-detail/system-information}}`);

  return wait().then(() => {
    const numberOfNavTabs = this.$('.system-information-wrapper .host-title-bar > div').length;
    assert.equal(numberOfNavTabs, 4, 'Number of tabs rendered based on the OS selected');
  });
});
// Linux
test('Number of tabs rendered based on the OS selected, Linux : 3', function(assert) {
  setState(linux);

  this.render(hbs`{{host-detail/system-information}}`);

  return wait().then(() => {
    const numberOfNavTabs = this.$('.system-information-wrapper .host-title-bar > div').length;
    assert.equal(numberOfNavTabs, 3, 'Number of tabs rendered based on the OS selected');
  });
});
// *End* Test for the number of tabs rendered based on the OS selected

// Tabs availabe when Windows OS agent is selected

// Host File Entries present for all 3 OS types
test('Host File Entries rendered', function(assert) {
  setState(windows);

  this.render(hbs`{{host-detail/system-information}}`);

  return wait().then(() => {
    const hostFileEntries = this.$('.rsa-data-table-header-row > div').length;
    assert.equal(hostFileEntries, windowsTabColumns.hostFileEntries.length, 'Number of columns Host File Entries tab has');

    const hostFileEntriesResults = $('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(hostFileEntriesResults, 3, 'Number of resultant rows for Host File Entries');
  });
});

// Network Shares
test('Network Shares rendered', function(assert) {
  setState(windows);

  this.render(hbs`{{host-detail/system-information}}`);

  this.$('.system-information-wrapper .host-title-bar > div:nth-child(2)').click();

  return wait().then(() => {
    const networkShares = this.$('.rsa-data-table-header-row > div').length;
    assert.equal(networkShares, windowsTabColumns.networkShares.length, 'Number of columns Network Shares tab has');

    const networkSharesResults = $('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(networkSharesResults, 4, 'Number of resultant rows for Network Shares tab');
  });
});

// Security Products
test('Security Products rendered', function(assert) {
  setState(windows);

  this.render(hbs`{{host-detail/system-information}}`);

  this.$('.system-information-wrapper .host-title-bar > div:nth-child(3)').click();

  return wait().then(() => {
    const securityProducts = this.$('.rsa-data-table-header-row > div').length;
    assert.equal(securityProducts, windowsTabColumns.securityProducts.length, 'Number of columns Security Products tab has');

    const securityProductsResults = $('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(securityProductsResults, 5, 'Number of resultant rows for Security Products tab');
  });
});

// Windows Patches
test('Windows Patches rendered', function(assert) {
  setState(windows);

  this.render(hbs`{{host-detail/system-information}}`);

  this.$('.system-information-wrapper .host-title-bar > div:nth-child(4)').click();

  return wait().then(() => {
    const windowsPatches = this.$('.rsa-data-table-header-row > div').length;
    assert.equal(windowsPatches, windowsTabColumns.windowsPatches.length, 'Number of columns Windows Patches tab has');

    const windowsPatchesResults = $('.rsa-data-table-body .rsa-data-table-body-row').length;
    assert.equal(windowsPatchesResults, 4, 'Number of resultant rows for Windows Patches tab');
  });
});

// *End* Tabs availabe when Windows OS agent is selected

// Tabs availabe when Linux OS agent is selected


// Mounted Paths
test('Mounted Paths rendered', function(assert) {
  setState(linux);

  this.render(hbs`{{host-detail/system-information}}`);

  this.$('.system-information-wrapper .host-title-bar > div:nth-child(2)').click();

  return wait().then(() => {
    const mountedPaths = this.$('.rsa-data-table-header-row > div').length;
    assert.equal(mountedPaths, linuxTabColumns.mountedPaths.length, 'Number of columns Mounted Paths tab has');

    return wait().then(() => {
      const mountedPathsResults = $('.rsa-data-table-body .rsa-data-table-body-row').length;
      assert.equal(mountedPathsResults, 3, 'Number of resultant rows for Mounted Paths tab');
    });
  });
});

// Bash History
test('Bash History rendered', function(assert) {
  setState(linux);

  this.render(hbs`{{host-detail/system-information}}`);

  this.$('.system-information-wrapper .host-title-bar > div:nth-child(3)').click();

  return wait().then(() => {
    const bashHistory = this.$('.rsa-data-table-header-row > div').length;
    assert.equal(bashHistory, linuxTabColumns.bashHistory.length, 'Number of columns Bash History tab has');
    return wait().then(() => {
      const bashHistoryResults = $('.rsa-data-table-body .rsa-data-table-body-row').length;
      assert.equal(bashHistoryResults, 6, 'Number of resultant rows for Bash History tab');
    });
  });
});

// Bash History Filter Dropdown
test('Filter Dropdown is present only for Bash History', function(assert) {
  setState(linux);

  this.render(hbs`{{host-detail/system-information}}`);
  assert.equal(this.$('.power-select').length, 0, 'Power select is not present when Bash History is not selected');
  this.$('.system-information-wrapper .host-title-bar > div:nth-child(3)').click();

  return wait().then(() => {
    assert.equal(this.$('.power-select').length, 1, 'Power select is present when Bash History is selected');

    clickTrigger();
    selectChoose('.system-information__content', '.ember-power-select-option', 1);

    return wait().then(() => {
      const bashHistoryResults = $('.rsa-data-table-body .rsa-data-table-body-row').length;
      assert.equal(bashHistoryResults, 4, 'Number of resultant rows for Bash History tab after filter applied');
    });
  });
});

// Test if no results message is present
test('No Results Found text present when there are no results', function(assert) {
  setState(mac);

  this.render(hbs`{{host-detail/system-information}}`);

  this.$('.system-information-wrapper .host-title-bar > div:nth-child(2)').click();

  return wait().then(() => {
    assert.equal(this.$('.message')[0].textContent.trim(), 'No Results Found.', 'No Results Found text present when there are no results');
  });
});
