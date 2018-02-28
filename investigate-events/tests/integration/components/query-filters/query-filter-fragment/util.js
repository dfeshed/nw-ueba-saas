import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

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

const _setInputRange = ($input, rangeVal) => {
  $input.get(0).setSelectionRange(rangeVal, rangeVal);
};

// meta is always first, so can just set early in string
const setCursorAtMeta = ($input) => {
  _setInputRange($input, 2);
};

// operator location depends on length of meta
const setCursorAtOperator = ($input, metaType) => {
  const metaNameLength = metaNameForFormat(metaType).length;
  _setInputRange($input, metaNameLength + 2);
};

// value location depends on length of operator and meta
const setCursorAtValue = ($input, metaType, operator) => {
  const metaNameLength = metaNameForFormat(metaType).length;
  const opLength = operator.length;
  _setInputRange($input, metaNameLength + opLength + 3);
};

const testSetupConfig = {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
};

const ALL_META_OPTIONS = [
  { format: 'Float32', metaName: 'file.entropy', count: 4, flags: -2147482877, displayName: 'File Entropy', indexedBy: 'value' }, // IndexedByValue
  { format: 'IPv4', metaName: 'alias.ip', count: 4, flags: -2147482621, displayName: 'IP Aliases', indexedBy: 'value' },          // IndexedByValue
  { format: 'IPv6', metaName: 'alias.ipv6', count: 4, flags: -2147482621, displayName: 'IPv6 Aliases', indexedBy: 'value' },      // IndexByValue
  { format: 'MAC', metaName: 'alias.mac', count: 4, flags: -2147482621, displayName: 'MAC Aliases', indexedBy: 'value' },         // IndexedByValue
  { format: 'Text', metaName: 'alert', count: 7, flags: -2147483133, displayName: 'Alerts', indexedBy: 'value' },                 // IndexedByValue and text
  { format: 'TimeT', metaName: 'starttime', count: 4, flags: -2147482621, displayName: 'Time Start', indexedBy: 'value' },        // IndexedByValue
  { format: 'UInt8', metaName: 'ip.proto', count: 4, flags: -2147482541, displayName: 'IP Protocol', indexedBy: 'value' },        // IndexedByValue
  { format: 'UInt16', metaName: 'eth.type', count: 4, flags: -2147482541, displayName: 'Ethernet Protocol', indexedBy: 'value' }, // IndexedByValue
  { format: 'UInt32', metaName: 'bytes.src', count: 2, flags: -2147482878, displayName: 'Bytes Sent', indexedBy: 'key' },       // IndexedByKey
  { format: 'UInt64', metaName: 'filename.size', count: 2, flags: -2147482878, displayName: 'File Size', indexedBy: 'key' },    // IndexedByKey
  { format: 'UInt64', metaName: 'sessionid', count: 4, flags: -2147483631, displayName: 'Session ID', indexedBy: 'none' }        // special case - exists, !exists, =, !=
];

const metaNameForFormat = (format) => {
  const meta = ALL_META_OPTIONS.findBy('format', format);
  if (!meta) {
    throw new Error(`metaNameForFormat called with bad format: ${format}`);
  }
  return meta.metaName;
};

const metaForDropDowns = (indexedBy, metaName) => {
  const meta = ALL_META_OPTIONS.find(function(item) {
    return (item.indexedBy === indexedBy && item.metaName === metaName);
  });
  return meta;
};

const _createBasicPill = (type, test, meta, operator, value, options = {}) => {

  if (options.createPill === undefined) {
    options.createPill = true;
  }

  test.set('list', []);
  test.set('metaOptions', ALL_META_OPTIONS);
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

  if (options.createPill) {
    const pillText = `${meta} ${operator} ${value}`;
    test.$('input').val(pillText.trim());
    pressEnter(test.$('input'));
  }

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

const PillHelpers = {
  createTextPill(test, meta = metaNameForFormat('Text'), operator = '=', value = '"foo"', options) {
    return _createBasicPill('Text', test, meta, operator, value, options);
  },
  createTimeTPill(test, meta = metaNameForFormat('TimeT'), operator = '=', value = new Date(), options) {
    return _createBasicPill('TimeT', test, meta, operator, value, options);
  },
  createIPv4Pill(test, meta = metaNameForFormat('IPv4'), operator = '=', value = '127.0.0.1', options) {
    return _createBasicPill('IPv4', test, meta, operator, value, options);
  },
  createIPv6Pill(test, meta = metaNameForFormat('IPv6'), operator = '=', value = '2001:0db8:85a3:0000:0000:8a2e:0370:7334', options) {
    return _createBasicPill('IPv6', test, meta, operator, value, options);
  },
  createUInt8Pill(test, meta = metaNameForFormat('UInt8'), operator = '=', value = '8', options) {
    return _createBasicPill('UInt8', test, meta, operator, value, options);
  },
  createUInt16Pill(test, meta = metaNameForFormat('UInt16'), operator = '=', value = '8', options) {
    return _createBasicPill('UInt16', test, meta, operator, value, options);
  },
  createUInt32Pill(test, meta = metaNameForFormat('UInt32'), operator = '=', value = '8', options) {
    return _createBasicPill('UInt32', test, meta, operator, value, options);
  },
  createUInt64Pill(test, meta = metaNameForFormat('UInt64'), operator = '=', value = '8', options) {
    return _createBasicPill('UInt64', test, meta, operator, value, options);
  },
  createFloat32Pill(test, meta = metaNameForFormat('Float32'), operator = '=', value = '8.5', options) {
    return _createBasicPill('Float32', test, meta, operator, value, options);
  },
  createMACPill(test, meta = metaNameForFormat('MAC'), operator = '=', value = '8.5', options) {
    return _createBasicPill('MAC', test, meta, operator, value, options);
  },
  createPillWithFormat(test, format, options) {
    const helperName = `create${format}Pill`;
    return PillHelpers[helperName](test, undefined, undefined, undefined, options);
  }
};

export {
  ALL_META_OPTIONS,
  metaNameForFormat,
  PillHelpers,
  pressEnter,
  pressSpace,
  setCursorAtMeta,
  setCursorAtOperator,
  setCursorAtValue,
  setupPillWithCustomProperties,
  testSetupConfig,
  metaForDropDowns
};
