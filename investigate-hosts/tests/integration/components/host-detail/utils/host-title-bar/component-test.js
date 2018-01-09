import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../../helpers/engine-resolver';

moduleForComponent('host-detail/utils/host-title-bar', 'Integration | Component | Host Title Bar', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
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

test('Should match the tabs length in host-title-bar with the object length', function(assert) {
  this.set('Tabs', HOST_DETAILS_TABS);
  this.render(hbs`{{host-detail/utils/host-title-bar tabs=Tabs}}`);
  const renderedTabs = this.$(this.$('hbox.host-title-bar')[0]).find('div.rsa-nav-tab');
  assert.equal(HOST_DETAILS_TABS.length, renderedTabs.length, 'Tabs length matches with the object length');
});

test('Should apply appropriate style to the tab for which selected property is set', function(assert) {
  this.set('hostTabs', HOST_DETAILS_TABS);
  this.render(hbs`{{host-detail/utils/host-title-bar tabs=hostTabs}}`);
  assert.equal(this.$('.rsa-nav-tab.is-active').find('div.label').text(), 'Process', 'Applied appropriate class to the tab for which selected property is set');
});

test('Should check that length of inactive tabs is correct', function(assert) {
  this.set('hostTabs', HOST_DETAILS_TABS);
  this.render(hbs`{{host-detail/utils/host-title-bar tabs=hostTabs}}`);
  const inactiveTabsLength = this.$('.rsa-nav-tab').find('div.label').length - this.$('.rsa-nav-tab.is-active').find('div.label').length;
  assert.equal(inactiveTabsLength, 6, 'Checked that length of inactive tabs is correct');
});

test('Should check that correct tab name is rendered on clicking it', function(assert) {
  assert.expect();
  this.set('hostTabs', HOST_DETAILS_TABS);
  this.set('activate', (tabName) => {
    assert.equal('PROCESS', tabName, 'Correct tab name is rendered on clicking it');
  });
  this.render(hbs`{{host-detail/utils/host-title-bar tabs=hostTabs defaultAction=(action activate)}}`);
  this.$('.rsa-nav-tab:eq(1)').click();
});

test('Should check that hidden tabs are not rendered', function(assert) {
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
  this.render(hbs`{{host-detail/utils/host-title-bar tabs=Tabs}}`);
  const renderedTabs = this.$(this.$('hbox.host-title-bar')[0]).find('div.rsa-nav-tab');
  assert.equal(renderedTabs.length, 2, 'Hidden tabs are not rendered');
});

test('Tab name and label are rendered when hasBlock is used', function(assert) {
  const HOST_DETAILS_TABS = [
    {
      label: 'investigateHosts.tabs.overview',
      name: 'OVERVIEW',
      componentClass: 'host-detail/overview',
      selected: true
    }
  ];
  this.set('Tabs', HOST_DETAILS_TABS);
  this.render(hbs`{{#host-detail/utils/host-title-bar tabs=Tabs as |tab|}}{{tab.name}}{{tab.label}}{{/host-detail/utils/host-title-bar}}`);
  const result = this.$(this.$('hbox.host-title-bar')).text().trim();
  assert.equal(result.substr(0, 8), 'OVERVIEW', 'Tab name is rendered');
  assert.equal(result.substr(8), 'investigateHosts.tabs.overview', 'Tab label is rendered');
});