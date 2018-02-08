import { moduleForComponent, test } from 'ember-qunit';
import {
  testSetupConfig,
  PillHelpers,
  pressEnter
} from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment interactivity-edit-keyboard',
  testSetupConfig
);

test('double click to edit and re-create pill', function(assert) {

  const $fragment = PillHelpers.createPillWithFormat(this, 'Text');
  assert.equal(this.$('.meta').text().trim(), 'alert = \'foo\'', 'Expected filter.');
  // double click to edit
  $fragment.find('.meta').dblclick();
  assert.ok($fragment.hasClass('edit-active'), 'Expected fragment to be editable.');
  // enter new value
  this.$('input').val('alert = bar');
  pressEnter(this.$('input'));
  assert.equal(this.$('.meta').text().trim(), 'alert = \'bar\'', 'Expected filter.');

});