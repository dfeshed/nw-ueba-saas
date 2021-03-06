import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, triggerKeyEvent, click } from '@ember/test-helpers';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { A } from '@ember/array';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

const channelFilters = [ { channel: 'System', filterType: 'INCLUDE', eventId: 'ALL' } ];
const column = {
  field: 'channel',
  title: 'packager.channel.name',
  width: '13vw',
  displayType: 'channelInput'
};
const channels = [
  'System',
  'Security',
  'Application',
  'Setup',
  'ForwardedEvents'
];
const item = {
  channel: 'Application',
  eventId: 'ALL',
  filterType: 'INCLUDE'
};

const channelOptions = A(channels);

module('Integration | Component | usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it renders windows-log-channel-filters/body-cell component', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();
    await render(hbs`{{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell}}`);
    assert.equal(findAll('.windows-log-channel-list .body-cell').length, 1, 'Expected to find packager form root element in DOM.');
  });

  test('there should be 5 options available for channel name', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    this.setProperties({
      column,
      channelOptions,
      item
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);
    await clickTrigger('.windows-log-channel-name');
    assert.equal(findAll('.ember-power-select-option').length, 5, 'Dropdown is rendered with correct number of items');
  });

  test('select application from channel name', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    this.setProperties({
      column,
      channelOptions,
      item
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'Application');
  });

  test('event id is available in a row', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    const column = {
      field: 'eventId',
      title: 'packager.channel.event',
      width: '13vw',
      displayType: 'EventInput'
    };

    this.setProperties({
      column,
      channelOptions,
      item
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);
    assert.equal(findAll('.event-id').length, 1);
  });

  test('event id is empty when filter is exclude', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    const column = {
      field: 'filterType',
      title: 'packager.channel.filter',
      width: '9vw',
      displayType: 'dropdown'
    };

    this.setProperties({
      column,
      item
    });

    this.set('channelUpdated', () => {
      assert.ok(true, 'channelUpdated should be called');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
        channelUpdated=channelUpdated
      }}
    `);

    await selectChoose('.windows-log-channel-filter', 'EXCLUDE');
    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'EXCLUDE', 'selected item matches Exclude');
    assert.equal(this.get('item.eventId'), '', 'Changing to exclude clears out eventId correctly');
  });

  test('on entering channel name, channel options are increased', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    const ENTER_KEY = 13;
    this.setProperties({
      column,
      channelOptions,
      item
    });

    this.set('channelUpdated', () => {
      assert.ok(true, 'channelUpdated should be called when channel options are increased');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
        channelUpdated=channelUpdated
      }}
    `);

    await clickTrigger('.windows-log-channel-name');
    await typeInSearch('test');
    await triggerKeyEvent('.ember-power-select-search-input', 'keydown', ENTER_KEY);

    assert.equal(this.get('channelOptions').length, 6, 'Test is added as a option');
  });

  test('Trailing space not allowed on entering the channel name', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    const ENTER_KEY = 13;
    const channelName = 'test ';
    this.setProperties({
      column,
      channelOptions,
      item
    });

    this.set('channelUpdated', () => {
      assert.ok(true, 'channelUpdated should be called when channel options are increased');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
        channelUpdated=channelUpdated
      }}
    `);

    await clickTrigger('.windows-log-channel-name');
    // channel name with a trailing space
    await typeInSearch(channelName);
    await triggerKeyEvent('.ember-power-select-search-input', 'keydown', ENTER_KEY);
    assert.equal(findAll('.windows-log-channel-name .ember-power-select-selected-item')[0].innerText, channelName.trim(), 'channel name added correctly without the trialing space');
  });

  test('Clicking anywhere outside the channel name box will also add the channel name', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    const channelName = 'xyz';
    this.setProperties({
      column,
      channelOptions,
      item
    });

    this.set('channelUpdated', () => {
      assert.ok(true, 'channelUpdated should be called when channel options are increased');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
        channelUpdated=channelUpdated
      }}
    `);

    await clickTrigger('.windows-log-channel-name');
    await typeInSearch(channelName);
    const channelDomEle = document.querySelector('.windows-log-channel-filters');
    await click(channelDomEle);
    assert.equal(findAll('.windows-log-channel-name .ember-power-select-selected-item')[0].innerText, channelName.trim(), 'channel name is added correctly on an external click');
  });

  test('If the typed in channel is already present in the dropdown, populate it on external click', async function(assert) {
    new ReduxDataHelper(setState)
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(channelFilters)
      .build();

    // System is already present in the dropdown
    const channelName = 'Application';
    this.setProperties({
      column,
      channelOptions,
      item
    });

    this.set('channelUpdated', () => {
      assert.ok(true, 'channelUpdated should be called when channel options are increased');
    });

    await render(hbs`
      {{usm-policies/policy-wizard/policy-types/windows-log/windows-log-channel-filters/body-cell
        column=column
        channelOptions=channelOptions
        item=item
        channelUpdated=channelUpdated
      }}
    `);

    await clickTrigger('.windows-log-channel-name');
    await typeInSearch(channelName);
    const channelDomEle = document.querySelector('.windows-log-channel-filters');
    await click(channelDomEle);
    assert.equal(findAll('.windows-log-channel-name .ember-power-select-selected-item')[0].innerText, channelName.trim(), 'Application is correctly polulated on external click');
  });
});