import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import { clickTrigger, selectChoose, typeInSearch } from 'ember-power-select/test-support/helpers';
import { A } from '@ember/array';

const ENTER_KEY = 13;

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
  filter: 'Include'
};

const channelOptions = A(channels);

module('Integration | Component | packager form/body-cell', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders packager form body cell', async function(assert) {
    await render(hbs`{{packager-form/body-cell}}`);

    assert.equal(findAll('.body-cell').length, 1, 'Expected to find packager form root element in DOM.');
  });

  test('there should be 5 options available for channel name', async function(assert) {
    this.setProperties({
      column,
      channelOptions,
      item
    });

    await render(hbs`
      {{packager-form/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);

    await clickTrigger('.channel-select');

    assert.equal(findAll('.ember-power-select-option').length, 5, 'Dropdown is rendered with correct number of items');
  });

  test('select application from channel name', async function(assert) {
    this.setProperties({
      column,
      channelOptions,
      item
    });

    await render(hbs`
      {{packager-form/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);

    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'Application');
  });

  test('on entering channel name, channel options are increased', async function(assert) {
    this.setProperties({
      column,
      channelOptions,
      item
    });

    await render(hbs`
      {{packager-form/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);

    await clickTrigger('.channel-select');
    await typeInSearch('test');
    await triggerKeyEvent('.ember-power-select-search-input', 'keydown', ENTER_KEY);

    assert.equal(this.get('channelOptions').length, 6, 'Test is added as a option');
  });

  test('event id is empty when filter is exclude', async function(assert) {
    const column = {
      field: 'filter',
      title: 'packager.channel.filter',
      width: '9vw',
      displayType: 'dropdown'
    };

    this.setProperties({
      column,
      item
    });

    await render(hbs`
      {{packager-form/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);

    await selectChoose('.channel-select', 'Exclude');

    assert.equal(find('.ember-power-select-selected-item').textContent.trim(), 'Exclude');
    assert.equal(this.get('item.eventId'), '');
  });

  test('event id is available in a row', async function(assert) {
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
      {{packager-form/body-cell
        column=column
        channelOptions=channelOptions
        item=item
      }}
    `);

    assert.equal(findAll('.event-id').length, 1);
  });
});