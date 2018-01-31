import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { pressEnter, testSetupConfig } from './util';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment basic-pill-creation',
  testSetupConfig
);

test('it creates pill with exists', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    metaName: 'foo'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('foo exists');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'foo exists', 'Expected exists.');
});

test('it creates pill with !exists', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    metaName: 'foo'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('foo !exists');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'foo !exists', 'Expected !exists.');
});
