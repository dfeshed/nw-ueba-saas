import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

import queryUtils from 'investigate-events/actions/utils';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';

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

  test('parseBasicQueryParams correctly parses URI', function(assert) {
    assert.expect(6);
    const result = queryUtils.parseBasicQueryParams(params, DEFAULT_LANGUAGES);
    assert.equal(result.endTime, params.et, '"et" was not parsed to "endTime"');
    assert.equal(result.sessionId, params.eid, '"eid" was not parsed to "sessionId"');
    assert.equal(result.metaPanelSize, params.mps, '"mps" was not parsed to "metaPanelSize"');
    assert.equal(result.reconSize, params.rs, '"rs" was not parsed to "reconSize"');
    assert.equal(result.serviceId, params.sid, '"sid" was not parsed to "serviceId"');
    assert.equal(result.startTime, params.st, '"st" was not parsed to "startTime"');
  });


  test('parsePillDataFromUri correctly parses forward slashes and operators into pills', function(assert) {
    assert.expect(3);
    const result = queryUtils.parsePillDataFromUri(params.mf, DEFAULT_LANGUAGES);
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '<reston=\'virginia.sys>', 'forward slash was not parsed correctly');
  });

  test('parsePillDataFromUri correctly parses multiple params', function(assert) {
    assert.expect(7);
    const result = queryUtils.parsePillDataFromUri('filename%3D<reston%3D\'virginia.sys>/medium%3Dfoo', DEFAULT_LANGUAGES);
    assert.equal(result.length, 2, 'two pills came out');
    assert.equal(result[0].meta, 'filename', 'forward slash was not parsed correctly');
    assert.equal(result[0].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[0].value, '<reston=\'virginia.sys>', 'forward slash was not parsed correctly');
    assert.equal(result[1].meta, 'medium', 'forward slash was not parsed correctly');
    assert.equal(result[1].operator, '=', 'forward slash was not parsed correctly');
    assert.equal(result[1].value, 'foo', 'forward slash was not parsed correctly');
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

    queryUtils.clientSideParseAndValidate(pillData.meta.format, pillData.value)
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

  //
  // BEGIN transformTextToPillData
  //

  test('1. transformTextToPillData returns complex filter object because of ||', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium = 1 || medium = 32';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      // note is wrapped in quotes
      complexFilterText: '(medium = 1 || medium = 32)',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('2. transformTextToPillData treats lack of operator as a complex query', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      complexFilterText: 'medium',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('3. transformTextToPillData treats bad meta as complex query', function(assert) {
    assert.expect(1);
    const freeFormText = 'lakjsdlakjsd = yeah';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      complexFilterText: 'lakjsdlakjsd = yeah',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('4. transformTextToPillData treats operator that does not belong to meta as complex query', function(assert) {
    assert.expect(1);
    const freeFormText = 'sessionid contains 123';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      complexFilterText: 'sessionid contains 123',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('5. transformTextToPillData treats operator that requires value but does not have one as complex query', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium =';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      complexFilterText: 'medium =',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('6. transformTextToPillData treats operator that require no value but has one as complex query', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium exists 10';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      complexFilterText: 'medium exists 10',
      meta: undefined,
      operator: undefined,
      value: undefined
    });
  });

  test('transformTextToPillData handles when just meta and operator', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium exists';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      complexFilterText: undefined,
      meta: 'medium',
      operator: 'exists',
      value: undefined
    });
  });

  test('transformTextToPillData returns pill data object', function(assert) {
    assert.expect(1);
    const freeFormText = 'medium = 1';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(
      result,
      { meta: 'medium', operator: '=', value: '1', complexFilterText: undefined }
    );
  });

  test('transformTextToPillData returns populated pill object even if operator embedded in value', function(assert) {
    assert.expect(1);
    const freeFormText = 'user.dst = \'1=2\'';
    const result = queryUtils.transformTextToPillData(freeFormText, DEFAULT_LANGUAGES);

    assert.deepEqual(result, {
      complexFilterText: undefined,
      meta: 'user.dst',
      operator: '=',
      value: '\'1=2\''
    });
  });

  test('selectPillsFromPosition returns an array with pills selected in right direction', function(assert) {
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

    assert.equal(selectedPills.length, 3, 'Should output all objects from that position to its right');
    // Although it should return all objects to its right including itself, but because that
    // pill is already selected, we do not need it to be included in the selected pills array
    assert.deepEqual(selectedPills, [
      {
        a: 'baz'
      }, {
        a: 'bang'
      }, {
        a: 'boom'
      }
    ]);
  });

  test('selectPillsFromPosition returns an array with pills selected in left direction', function(assert) {
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

    assert.equal(selectedPills.length, 3, 'Should output all objects from that position to its left');
    // Although it should return all objects to its right including itself, but because that
    // pill is already selected, we do not need it to be included in the selected pills array
    assert.deepEqual(selectedPills, [
      {
        a: 'foo'
      }, {
        a: 'bar'
      }, {
        a: 'baz'
      }
    ]);
  });
});
