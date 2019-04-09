import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import queryUtils from 'investigate-events/actions/utils';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';

const params = {
  et: 0,
  eid: 1,
  mf: 'filename%20%3D%20<reston%3D\'virginia.sys>',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3,
  pdhash: 'foo,bar,baz'
};

const ipv6Addresses = ['2001:0db8:85a3:0000:0000:8a2e:0370:7334', '2001:20::', '::ffff:0.0.0.0', '100::', 'fe80::', '::1', '2002::', '2001:db8::', '::ffff:0:255.255.255.255'];

module('Unit | Helper | Actions Utils', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('parseBasicQueryParams correctly parses URI', function(assert) {
    assert.expect(7);
    const result = queryUtils.parseBasicQueryParams(params, DEFAULT_LANGUAGES);
    assert.equal(result.endTime, params.et, '"et" was not parsed to "endTime"');
    assert.equal(result.sessionId, params.eid, '"eid" was not parsed to "sessionId"');
    assert.equal(result.metaPanelSize, params.mps, '"mps" was not parsed to "metaPanelSize"');
    assert.equal(result.reconSize, params.rs, '"rs" was not parsed to "reconSize"');
    assert.equal(result.serviceId, params.sid, '"sid" was not parsed to "serviceId"');
    assert.equal(result.startTime, params.st, '"st" was not parsed to "startTime"');
    assert.deepEqual(result.pillDataHashes, ['foo', 'bar', 'baz'], '"pdhash" was not parsed to proper hashes');
  });

  test('parseBasicQueryParams correctly parses URI', function(assert) {
    assert.expect(1);

    const modParams = {
      ...params,
      pdhash: ['foo', 'a', 'z']
    };

    const result = queryUtils.parseBasicQueryParams(modParams, DEFAULT_LANGUAGES);
    assert.deepEqual(result.pillDataHashes, ['foo', 'a', 'z'], '"pdhash" handled array');
  });

  test('parseBasicQueryParams leaves hashes undefined if there are none', function(assert) {
    const result = queryUtils.parseBasicQueryParams({ rs: 'max' }, DEFAULT_LANGUAGES);
    assert.equal(result.pillDataHashes, undefined, '"pdhash" was not parsed to proper hashes');
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
      queryUtils
        .clientSideParseAndValidate(pillData.meta.format, value)
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

  test('clientSideParseAndValidate return error when metaFormat is UInt8 and value is a decimal', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'UInt8',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '2.5'
    };

    queryUtils
      .clientSideParseAndValidate(pillData.meta.format, pillData.value)
      .catch((error) => {
        assert.ok(error.meta, 'Filter is invalid');
        assert.equal(error.meta, 'You must enter an 8 bit Integer.', 'Invalid error message');
      });

  });

  test('clientSideParseAndValidate return error when metaFormat is UInt8 and value is negative', function(assert) {
    assert.expect(2);
    const pillData = {
      meta: {
        format: 'UInt8',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '-23'
    };

    queryUtils
      .clientSideParseAndValidate(pillData.meta.format, pillData.value)
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

  test('clientSideParseAndValidate passes when metaFormat is UInt8 and value is 0', function(assert) {
    assert.expect(1);
    const pillData = {
      meta: {
        format: 'UInt8',
        metaName: 'foo',
        flags: -2147482621,
        displayName: 'foo'
      },
      operator: '=',
      value: '0'
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

  test('selectPillsFromPosition returns an array with pills selected in right direction, including itself', function(assert) {
    assert.expect(2);
    const pills = [{
      a: 'foo'
    }, {
      a: 'bar'
    }, {
      a: 'baz'
    }, {
      a: 'bang'
    }, {
      a: 'boom'
    }];
    const selectedPills = queryUtils.selectPillsFromPosition(pills, 1, 'right');

    assert.equal(selectedPills.length, 4, 'Should output all objects from that position to its right');
    assert.deepEqual(selectedPills, [
      {
        a: 'bar'
      }, {
        a: 'baz'
      }, {
        a: 'bang'
      }, {
        a: 'boom'
      }
    ]);
  });

  test('selectPillsFromPosition returns an array with pills selected in left direction, including itself', function(assert) {
    assert.expect(2);
    const pills = [{
      a: 'foo'
    }, {
      a: 'bar'
    }, {
      a: 'baz'
    }, {
      a: 'bang'
    }, {
      a: 'boom'
    }];
    const selectedPills = queryUtils.selectPillsFromPosition(pills, 3, 'left');

    assert.equal(selectedPills.length, 4, 'Should output all objects from that position to its left');
    assert.deepEqual(selectedPills, [
      {
        a: 'foo'
      }, {
        a: 'bar'
      }, {
        a: 'baz'
      }, {
        a: 'bang'
      }
    ]);
  });

});
