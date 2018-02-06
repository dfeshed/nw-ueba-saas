import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

const pressEnter = (input) => {
  input.trigger({
    type: 'keydown',
    which: 13,
    code: 'Enter'
  });
};

const pressSpace = (input) => {
  input.trigger({
    type: 'keydown',
    which: 32,
    code: 'Space'
  });
};

const testSetupConfig = {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
};

const ALL_META_OPTIONS = [
  { format: 'IPv4', metaName: 'alias.ip', flags: -2147482621, displayName: 'IP Aliases' },
  { format: 'Text', metaName: 'alert', flags: -2147483133, displayName: 'Alerts' },
  { format: 'IPv6', metaName: 'alias.ipv6', flags: -2147482878, displayName: 'IPv6 Aliases' },
  { format: 'MAC', metaName: 'alias.mac', flags: -2147482621, displayName: 'MAC Aliases' },
  { format: 'UInt64', metaName: 'bytes.src', flags: -2147482878, displayName: 'Bytes Sent' },
  { format: 'Float32', metaName: 'file.entropy', flags: -2147482877, displayName: 'File Entropy' },
  { format: 'UInt16', metaName: 'eth.type', flags: -2147482541, displayName: 'Ethernet Protocol' },
  { format: 'Int32', metaName: 'filename.size', flags: -2147482878, displayName: 'File Size' },
  { format: 'UInt8', metaName: 'ip.proto', flags: -2147482541, displayName: 'IP Protocol' },
  { format: 'TimeT', metaName: 'starttime', flags: -2147482621, displayName: 'Time Start' }];

const metaNameForFormat = (format) => {
  return ALL_META_OPTIONS.findBy('format', format).metaName;
};

const _createBasicPill = (type, test, meta, operator, value, options = {}) => {
  test.set('list', []);
  test.set('metaOptions', [{
    format: type,
    metaName: meta,
    displayName: meta
  }, {
    format: 'text',
    metaName: 'stuff',
    displayName: meta
  }]);
  test.set('setKeyboardPriority', () => {});
  test.set('deleteFilter', function(record) {
    if (options.deleteFilter) {
      options.deleteFilter(record);
    }
  });
  test.set('filterRecord', { id: 1001 });
  test.set('complexFilter', options.complexFilter || undefined);

  test.render(hbs`
    <style>
      input {
        color: black
      }
    </style>
    {{query-filters/query-filter-fragment
      validateWithServer=false
      filterList=list
      metaOptions=metaOptions
      editActive=true
      filterRecord=filterRecord
      complexFilter=complexFilter
      setKeyboardPriority=(action setKeyboardPriority)
      deleteFilter=(action deleteFilter)
    }}`
  );

  const $fragment = test.$('.rsa-query-fragment');
  const pillText = `${meta} ${operator} ${value}`;
  test.$('input').val(pillText.trim());
  pressEnter(test.$('input'));

  return $fragment;
};

const setupPill = (test) => {
  test.set('list', []);
  test.set('metaOptions', ALL_META_OPTIONS);
  test.set('setKeyboardPriority', () => {});

  test.render(hbs`
    {{query-filters/query-filter-fragment
      validateWithServer=true
      filterList=list
      metaOptions=metaOptions
      editActive=true
      setKeyboardPriority=(action setKeyboardPriority)
    }}`
  );

  const $fragment = test.$('.rsa-query-fragment');
  return $fragment;
};

const setupPillWithCustomProperties = (test) => {
  test.set('list', []);
  test.set('metaOptions', ALL_META_OPTIONS);
  test.set('setKeyboardPriority', () => {});
  test.set('deleteFilter', () => { });
  test.set('type', 'meta');
  test.set('meta', null);
  test.set('operator', null);
  test.set('value', null);

  test.render(hbs`
    {{query-filters/query-filter-fragment
      validateWithServer=true
      filterList=list
      metaOptions=metaOptions
      editActive=true
      setKeyboardPriority=(action setKeyboardPriority)
      deleteFilter=deleteFilter
      type=type
      meta=meta
      operator=operator
      value=value
    }}`
  );

  const $fragment = test.$('.rsa-query-fragment');
  return $fragment;
};

const createTextPill = (test, meta = 'action', operator = '=', value = '\'foo\'', options) => {
  return _createBasicPill('Text', test, meta, operator, value, options);
};

const createTimePill = (test, meta = 'time', operator = '=', value = new Date()) => {
  return _createBasicPill('TimeT', test, meta, operator, value);
};

const createIPv4Pill = (test, meta = 'ip', operator = '=', value = '127.0.0.1') => {
  return _createBasicPill('IPv4', test, meta, operator, value);
};

const createIPv6Pill = (test, meta = 'ip', operator = '=', value = '2001:0db8:85a3:0000:0000:8a2e:0370:7334') => {
  return _createBasicPill('IPv6', test, meta, operator, value);
};

const createUInt8Pill = (test, meta = 'int', operator = '=', value = '8') => {
  return _createBasicPill('UInt8', test, meta, operator, value);
};

const createUInt16Pill = (test, meta = 'int', operator = '=', value = '8') => {
  return _createBasicPill('UInt16', test, meta, operator, value);
};

const createUInt32Pill = (test, meta = 'int', operator = '=', value = '8') => {
  return _createBasicPill('UInt32', test, meta, operator, value);
};

const createFloat32Pill = (test, meta = 'float', operator = '=', value = '8.5') => {
  return _createBasicPill('Float32', test, meta, operator, value);
};

export {
  createFloat32Pill,
  createIPv4Pill,
  createIPv6Pill,
  createTextPill,
  createTimePill,
  createUInt8Pill,
  createUInt16Pill,
  createUInt32Pill,
  pressEnter,
  pressSpace,
  testSetupConfig,
  setupPill,
  metaNameForFormat,
  setupPillWithCustomProperties
};
