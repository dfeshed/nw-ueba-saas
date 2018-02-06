import { moduleForComponent, test } from 'ember-qunit';

import {
  createFloat32Pill,
  createIPv4Pill,
  createIPv6Pill,
  createTextPill,
  createTimePill,
  createUInt8Pill,
  createUInt16Pill,
  createUInt32Pill,
  testSetupConfig
} from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment validation-client-side',
  testSetupConfig
);

test('it validates when metaFormat is Text and value is correct', function(assert) {
  createTextPill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is TimeT and value is not proper format', function(assert) {
  createTimePill(this, 'time', '=', 'notAtime');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a valid date.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is TimeT and value is correct', function(assert) {
  createTimePill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is IPv4 and value is not proper format', function(assert) {
  createIPv4Pill(this, 'ip', '=', 'notAnIp');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv4 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is IPv4 and value is correct', function(assert) {
  createIPv4Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is IPv6 and value is not proper format', function(assert) {
  createIPv6Pill(this, 'ip', '=', 'notAnIp');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv6 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is IPv6 and value is correct', function(assert) {
  createIPv6Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt8 and value is not proper format', function(assert) {
  createUInt8Pill(this, 'int', '=', 'notAnInt');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an 8 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt8 and value is correct', function(assert) {
  createUInt8Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt16 and value is not proper format', function(assert) {
  createUInt16Pill(this, 'int', '=', 'notAnInt');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 16 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt16 and value is correct', function(assert) {
  createUInt16Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt32 and value is not proper format', function(assert) {
  createUInt32Pill(this, 'int', '=', 'notAnInt');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt32 and value is correct', function(assert) {
  createUInt32Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is Float32 and value is not proper format', function(assert) {
  createFloat32Pill(this, 'float', '=', 'notAFloat');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Float.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is Float32 and value is correct', function(assert) {
  createFloat32Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});