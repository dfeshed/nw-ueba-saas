import { moduleForComponent, test, skip } from 'ember-qunit';

import {
  PillHelpers,
  testSetupConfig
} from './util';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment validation-client-side',
  testSetupConfig
);

test('it validates when metaFormat is Text and value is correct', function(assert) {
  PillHelpers.createTextPill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is TimeT and value is not proper format', function(assert) {
  PillHelpers.createTimeTPill(this, undefined, undefined, 'notAtime');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a valid date.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is TimeT and value is correct', function(assert) {
  PillHelpers.createTimeTPill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is IPv4 and value is not proper format', function(assert) {
  PillHelpers.createIPv4Pill(this, undefined, undefined, 'notAnIp');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv4 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is IPv4 and value is correct', function(assert) {
  PillHelpers.createIPv4Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

// ENABLE THESE TESTS WHEN
// https://bedfordjira.na.rsa.net/browse/ASOC-49213 #8 works
skip('it fails validation when metaFormat is IPv6 and value is not proper format', function(assert) {
  PillHelpers.createIPv6Pill(this, undefined, undefined, 'notAnIp');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv6 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

skip('it passes validation when metaFormat is IPv6 and value is correct', function(assert) {
  PillHelpers.createIPv6Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt8 and value is not proper format', function(assert) {
  PillHelpers.createUInt8Pill(this, undefined, undefined, 'notAnInt');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an 8 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt8 and value is correct', function(assert) {
  PillHelpers.createUInt8Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt16 and value is not proper format', function(assert) {
  PillHelpers.createUInt16Pill(this, undefined, undefined, 'notAnInt');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 16 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt16 and value is correct', function(assert) {
  PillHelpers.createUInt16Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt32 and value is not proper format', function(assert) {
  PillHelpers.createUInt32Pill(this, undefined, undefined, 'notAnInt');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt32 and value is correct', function(assert) {
  PillHelpers.createUInt32Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is Float32 and value is not proper format', function(assert) {
  PillHelpers.createFloat32Pill(this, undefined, undefined, 'notAFloat');
  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Float.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is Float32 and value is correct', function(assert) {
  PillHelpers.createFloat32Pill(this);
  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

// NOTE: MAC address is not validated client side