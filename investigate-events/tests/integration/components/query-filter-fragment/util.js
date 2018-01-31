import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

const pressEnter = (input) => {
  input.trigger({
    type: 'keydown',
    which: 13,
    code: 'Enter'
  });
};

const testSetupConfig = {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
};

const _createBasicPill = (type, test, meta, operator, value) => {
  test.set('list', []);
  test.set('metaOptions', [{
    format: type,
    metaName: meta
  }]);
  test.set('setKeyboardPriority', () => {});

  test.render(hbs`
    {{query-filter-fragment
      validateWithServer=false
      filterList=list
      metaOptions=metaOptions
      editActive=true
      setKeyboardPriority=(action setKeyboardPriority)
    }}`
  );

  const $fragment = test.$('.rsa-query-fragment');
  const pillText = `${meta} ${operator} ${value}`;

  test.$('input').val(pillText.trim());
  pressEnter(test.$('input'));

  return $fragment;
};

const createTextPill = (test, meta = 'action', operator = '=', value = '"foo"') => {
  return _createBasicPill('Text', test, meta, operator, value);
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
  testSetupConfig
};
