import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import { testSetupConfig, setupPill, pressSpace, metaNameForFormat } from './util';
import { typeInSearch, clickTrigger } from 'ember-power-select/test-support/helpers';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment dropdown-has-relevant-operators',
  testSetupConfig
);

// array with type and the count that's expected
const type = [
  { format: 'IPv4', count: 4 },
  { format: 'IPv6', count: 4 },
  { format: 'Text', count: 7 },
  { format: 'MAC', count: 8 },
  { format: 'UInt64', count: 8 },
  { format: 'Float32', count: 8 },
  { format: 'UInt16', count: 8 },
  { format: 'Int32', count: 8 },
  { format: 'UInt8', count: 8 },
  { format: 'TimeT', count: 8 }
];

type.forEach(({ format, count }) => {
  test(`${format} should have relevant operators in drop-down`, function(assert) {
    const $fragment = setupPill(this);
    clickTrigger('.rsa-query-fragment'); // populates metaOptions dropdown
    typeInSearch(metaNameForFormat(format)); // selects one metaOption based on the current format
    pressSpace($fragment.find('input')); // hit space to populate operator dropdown
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, count, `There are ${count} relevant operators for type ${format}`);
  });
});
