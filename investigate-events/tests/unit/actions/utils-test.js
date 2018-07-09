import { module, test } from 'qunit';

import queryUtils from 'investigate-events/actions/utils';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

const params = {
  et: 0,
  eid: 1,
  mf: 'filename%3D<reston%3D\'virginia.sys>',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3
};

const ipv6Addresses = ['2001:0db8:85a3:0000:0000:8a2e:0370:7334', '2001:20::', '::ffff:0.0.0.0', '100::', 'fe80::', '::1', '2002::', '2001:db8::', '::ffff:0:255.255.255.255'];

module('Unit | Helper | query utils', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });


  test('parseQueryParams correctly parses URI', function(assert) {
    assert.expect(8);
    const result = queryUtils.parseQueryParams(params);
    assert.equal(result.endTime, params.et, '"et" was not parsed to "endTime"');
    assert.equal(result.sessionId, params.eid, '"eid" was not parsed to "sessionId"');
    assert.equal(result.metaFilter.uri, params.mf, '"mf" was not parsed to "metaFilter.uri"');
    assert.equal(result.metaFilter.conditions.length, 1, '"mf" was not parsed to "metaFilter.conditions"');
    assert.equal(result.metaPanelSize, params.mps, '"mps" was not parsed to "metaPanelSize"');
    assert.equal(result.reconSize, params.rs, '"rs" was not parsed to "reconSize"');
    assert.equal(result.serviceId, params.sid, '"sid" was not parsed to "serviceId"');
    assert.equal(result.startTime, params.st, '"st" was not parsed to "startTime"');
  });

  test('parseQueryParams correctly parses forward slashes and operators in text format conditions', function(assert) {
    assert.expect(3);
    const result = queryUtils.parseQueryParams(params);
    assert.equal(result.metaFilter.conditions[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result.metaFilter.conditions[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result.metaFilter.conditions[0].value, '<reston=\'virginia.sys>', 'forward slash was not parsed correctly');
  });

  test('transformTextToFilters returns filter object', function(assert) {
    assert.expect(2);
    const freeFormText = 'medium = 1';
    const result = queryUtils.transformTextToFilters(freeFormText);

    assert.deepEqual(result, { meta: 'medium ', operator: '=', value: ' 1' });
    assert.equal(result.complexFilter, undefined, 'Complex Filter doesnt exist');

  });

  test('transformTextToFilters returns complex filter object', function(assert) {
    assert.expect(4);
    const freeFormText = 'medium = 1 || medium = 32';
    const result = queryUtils.transformTextToFilters(freeFormText);

    assert.deepEqual(result, { complexFilter: 'medium = 1 || medium = 32' });
    assert.equal(result.meta, undefined, 'meta doesnt exist');
    assert.equal(result.operator, undefined, 'operator doesnt exist');
    assert.equal(result.value, undefined, 'value doesnt exist');

  });

  test('filterIsPresent return false when filters array and freeFormText are different', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium = 1';
    const filters = [{ meta: 'medium', operator: '=', value: '2' }];

    const result = queryUtils.filterIsPresent(filters, freeFormText);

    assert.notOk(result, 'Filter is not present');
  });

  test('filterIsPresent return true when filters array and freeFormText are same', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium = 1';
    const filters = [{ meta: 'medium', operator: '=', value: '1' }];

    const result = queryUtils.filterIsPresent(filters, freeFormText);

    assert.ok(result, 'Filter is present');
  });

  test('clientSideParseAndValidate return error when meta is TimeT and value is not in proper format', async function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'TimeT',
        metaName: 'starttime',
        flags: -2147482621,
        displayName: 'Time Start'
      },
      operator: '=',
      value: 'NotATime'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter a valid date.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes validation when meta is TimeT and value is in proper format', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'TimeT',
        metaName: 'starttime',
        flags: -2147482621,
        displayName: 'Time Start'
      },
      operator: '=',
      value: new Date()
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .then(() => assert.ok('Filter is valid'));

  });

  test('clientSideParseAndValidate return error when metaFormat is IPv4 and value is not in proper format', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'IPv4',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '127.0..1'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter an IPv4 address.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes when metaFormat is IPv4 and value is in proper format', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'IPv4',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '127.0.0.1'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .then(() => assert.ok('Filter is valid'));
  });

  test('clientSideParseAndValidate return error when metaFormat is IPv6 and value is not in proper format', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'IPv6',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '2001:0db8:85a3:0000:0000:8a2e:'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter an IPv6 address.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes when metaFormat is IPv6 and value is in proper format', function(assert) {
    assert.expect(ipv6Addresses.length);
    const pillData = {
      meta: {
        format: 'IPv6',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '='
    };

    ipv6Addresses.forEach((value) => {
      queryUtils.clientSideParseAndValidate(pillData.meta.format, value)
    .then(() => assert.ok('Filter is valid'));
    });

  });

  test('clientSideParseAndValidate return error when metaFormat is UInt8 and value is not in proper format', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'UInt8',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: 'bar'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter an 8 bit Integer.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes when metaFormat is UInt8 and value is in proper format', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'UInt8',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '3'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .then(() => assert.ok('Filter is valid'));

  });

  test('clientSideParseAndValidate return error when metaFormat is UInt16 and value is not in proper format', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'UInt16',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: 'bar'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter a 16 bit Integer.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes when metaFormat is UInt16 and value is in proper format', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'UInt16',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '3'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .then(() => assert.ok('Filter is valid'));

  });

  test('clientSideParseAndValidate return error when metaFormat is UInt32 and value is not in proper format', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'UInt32',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: 'bar'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter a 32 bit Integer.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes when metaFormat is UInt32 and value is in proper format', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'UInt32',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '3'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .then(() => assert.ok('Filter is valid'));

  });

  test('clientSideParseAndValidate return error when metaFormat is Float32 and value is not in proper format', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'Float32',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '5'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter a 32 bit Float.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes when metaFormat is Float32 and value is in proper format', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'Float32',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '3.3'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .then(() => assert.ok('Filter is valid'));

  });

  test('clientSideParseAndValidate return error when metaFormat is MAC and value is not in proper format', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'MAC',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '00:50:56:BA:60:1'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .catch((error) => {
      assert.ok(error.meta, 'Filter is invalid');
      assert.equal(error.meta, 'You must enter a MAC address.', 'Invalid error message');
    });

  });

  test('clientSideParseAndValidate passes when metaFormat is MAC and value is in proper format', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'MAC',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '00:50:56:BA:60:18'
    };

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
    .then(() => assert.ok('Filter is valid'));

  });
});
