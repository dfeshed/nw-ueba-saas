import { moduleForComponent, test } from 'ember-qunit';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import hbs from 'htmlbars-inline-precompile';
import { restorePowerSelect, patchPowerSelect } from '../../../../helpers/patch-power-select';
import $ from 'jquery';

import {
  clickTrigger,
  selectChoose,
  typeInSearch
} from '../../../../helpers/ember-power-select';
import { A } from '@ember/array';

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

moduleForComponent('packager-form/body-cell', 'Integration | Component | packager form/body-cell', {
  integration: true,
  afterEach() {
    restorePowerSelect();
  }
});

test('it renders packager form body cell', function(assert) {
  this.render(hbs`{{packager-form/body-cell}}`);
  const $el = this.$('.body-cell');
  assert.equal($el.length, 1, 'Expected to find packager form root element in DOM.');
});

test('there should be 5 options available for channel name', function(assert) {
  patchPowerSelect();

  this.setProperties({
    column,
    channelOptions,
    item
  });

  this.render(hbs`{{packager-form/body-cell column=column channelOptions=channelOptions item=item}}`);
  clickTrigger('.channel-select');
  assert.equal($('.ember-power-select-option').length, 5, 'Dropdown is rendered with correct number of items');
});

test('select application from channel name', function(assert) {
  this.setProperties({
    column,
    channelOptions,
    item
  });

  this.render(hbs`{{packager-form/body-cell column=column channelOptions=channelOptions item=item}}`);
  assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Application');

});

test('on entering channel name, channel options are increased', function(assert) {
  this.setProperties({
    column,
    channelOptions,
    item
  });

  this.render(hbs`{{packager-form/body-cell column=column channelOptions=channelOptions item=item}}`);
  clickTrigger('.channel-select');
  typeInSearch('test');

  // eslint-disable-next-line
  const e = $.Event('keydown', { keyCode: 13 });

  $('.ember-power-select-search-input').trigger(e);

  return waitFor(() => this.get('channelOptions').length === 6).then(() => {
    assert.equal(this.get('channelOptions').length, 6, 'Test is added as a option');
  });
});

test('event id is empty when filter is exclude', function(assert) {
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

  this.render(hbs`{{packager-form/body-cell column=column channelOptions=channelOptions item=item}}`);

  clickTrigger('.channel-select');
  selectChoose('.channel-select', 'Exclude');

  return waitFor(() => this.$('.ember-power-select-selected-item').text().trim() === 'Exclude').then(() => {
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Exclude');
    assert.equal(this.get('item.eventId'), '');
  });
});

test('event id is available in a row', function(assert) {
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

  this.render(hbs`{{packager-form/body-cell column=column channelOptions=channelOptions item=item}}`);

  return waitFor(() => this.$('.event-id').length === 1).then(() => {
    assert.equal(this.$('.event-id').length, 1);
  });
});

