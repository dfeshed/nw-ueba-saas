import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { pressEnter, testSetupConfig } from './util';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment client-side-validation',
  testSetupConfig
);

test('it validates when metaFormat is Text and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Text',
    metaName: 'action'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('action="foo"');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is TimeT and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'TimeT',
    metaName: 'time'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('time=notAtime');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a valid date.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is TimeT and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'TimeT',
    metaName: 'time'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val(`action=${new Date()}`);
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is IPv4 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv4',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=notAnIp');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv4 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is IPv4 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv4',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=127.0.0.1');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is IPv6 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv6',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=notAnIp');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv6 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is IPv6 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv6',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=2001:0db8:85a3:0000:0000:8a2e:0370:7334');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt8 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt8',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an 8 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt8 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt8',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=8');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt16 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt16',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 16 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt16 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt16',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=8');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt32 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt32',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt32 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt32',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=8');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is Float32 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Float32',
    metaName: 'float'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('float=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Float.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is Float32 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Float32',
    metaName: 'float'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('float=8.5');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});