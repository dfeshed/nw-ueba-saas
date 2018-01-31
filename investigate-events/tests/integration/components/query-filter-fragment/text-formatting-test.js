import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { pressEnter, testSetupConfig } from './util';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment text-formatting',
  testSetupConfig
);

test('it manually quotes when metaFormat is Text and quotes are not included', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Text',
    metaName: 'action'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('action=foo');
  pressEnter(this.$('input'));

  assert.equal(this.$('.meta').text().trim(), 'action = "foo"', 'Expected to be quoted.');
});