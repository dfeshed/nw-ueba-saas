import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | Host Title Bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const HOST_DETAILS_TABS = [
    {
      label: 'investigateHosts.tabs.overview',
      name: 'OVERVIEW',
      componentClass: 'host-detail/overview',
      selected: false
    },
    {
      label: 'investigateHosts.tabs.process',
      name: 'PROCESS',
      componentClass: 'host-detail/process',
      selected: true
    },
    {
      label: 'investigateHosts.tabs.autoruns',
      name: 'AUTORUNS',
      componentClass: 'host-detail/autoruns',
      selected: false
    },
    {
      label: 'investigateHosts.tabs.files',
      name: 'FILES',
      componentClass: 'host-detail/files',
      selected: false
    },
    {
      label: 'investigateHosts.tabs.drivers',
      name: 'DRIVERS',
      componentClass: 'host-detail/drivers',
      selected: false
    },
    {
      label: 'investigateHosts.tabs.libraries',
      name: 'LIBRARIES',
      componentClass: 'host-detail/libraries',
      selected: false
    },
    {
      label: 'investigateHosts.tabs.systemInformation',
      name: 'SYSTEM',
      componentClass: 'host-detail/system-information',
      selected: false
    }
  ];

  test('Should match the tabs length in host-title-bar with the object length', async function(assert) {
    this.set('Tabs', HOST_DETAILS_TABS);
    await render(hbs`{{host-detail/utils/host-title-bar tabs=Tabs}}`);
    const renderedTabs = document.querySelectorAll('hbox.host-title-bar div.rsa-nav-tab');
    assert.equal(HOST_DETAILS_TABS.length, renderedTabs.length, 'Tabs length matches with the object length');
  });

  test('Should apply appropriate style to the tab for which selected property is set', async function(assert) {
    this.set('hostTabs', HOST_DETAILS_TABS);
    await render(hbs`{{host-detail/utils/host-title-bar tabs=hostTabs}}`);
    assert.equal(document.querySelector('.rsa-nav-tab.is-active div.label').textContent.trim(), 'Processes', 'Applied appropriate class to the tab for which selected property is set');
  });

  test('Should check that length of inactive tabs is correct', async function(assert) {
    this.set('hostTabs', HOST_DETAILS_TABS);
    await render(hbs`{{host-detail/utils/host-title-bar tabs=hostTabs}}`);
    const inactiveTabsLength = document.querySelectorAll('.rsa-nav-tab div.label').length - document.querySelectorAll('.rsa-nav-tab.is-active div.label').length;
    assert.equal(inactiveTabsLength, 6, 'Checked that length of inactive tabs is correct');
  });

  test('Should check that correct tab name is rendered on clicking it', async function(assert) {
    assert.expect();
    this.set('hostTabs', HOST_DETAILS_TABS);
    this.set('activate', (tabName) => {
      assert.equal('PROCESS', tabName, 'Correct tab name is rendered on clicking it');
    });
    await render(hbs`{{host-detail/utils/host-title-bar tabs=hostTabs defaultAction=(action activate)}}`);
    await click(findAll('.rsa-nav-tab')[1]);
  });

  test('Should check that hidden tabs are not rendered', async function(assert) {
    const HOST_DETAILS_TABS = [
      {
        label: 'investigateHosts.tabs.overview',
        name: 'OVERVIEW',
        componentClass: 'host-detail/overview',
        selected: true,
        hidden: false
      },
      {
        label: 'investigateHosts.tabs.process',
        name: 'PROCESS',
        componentClass: 'host-detail/process',
        selected: false,
        hidden: false
      },
      {
        label: 'investigateHosts.tabs.autoruns',
        name: 'AUTORUNS',
        componentClass: 'host-detail/autoruns',
        selected: false,
        hidden: true
      }
    ];
    this.set('Tabs', HOST_DETAILS_TABS);
    await render(hbs`{{host-detail/utils/host-title-bar tabs=Tabs}}`);
    const renderedTabs = document.querySelectorAll('hbox.host-title-bar div.rsa-nav-tab');
    assert.equal(renderedTabs.length, 2, 'Hidden tabs are not rendered');
  });

  test('Tab name and label are rendered when hasBlock is used', async function(assert) {
    const HOST_DETAILS_TABS = [
      {
        label: 'investigateHosts.tabs.overview',
        name: 'OVERVIEW',
        componentClass: 'host-detail/overview',
        selected: true
      }
    ];
    this.set('Tabs', HOST_DETAILS_TABS);
    await render(
      hbs`{{#host-detail/utils/host-title-bar tabs=Tabs as |tab|}}{{tab.name}}{{tab.label}}{{/host-detail/utils/host-title-bar}}`
    );
    const result = document.querySelector('hbox.host-title-bar').textContent.trim();
    assert.equal(result.substr(0, 8), 'OVERVIEW', 'Tab name is rendered');
    assert.equal(result.substr(8), 'investigateHosts.tabs.overview', 'Tab label is rendered');
  });
});