import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import {
  testSetupConfig,
  metaNameForFormat,
  PillHelpers,
  pressSpace,
  ALL_META_OPTIONS
} from './util';
import { typeInSearch, clickTrigger } from 'ember-power-select/test-support/helpers';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment dropdown-has-relevant-operators',
  testSetupConfig
);

ALL_META_OPTIONS.forEach(({ format, count }) => {
  test(`${format} should have relevant operators in drop-down`, function(assert) {
    const metaName = metaNameForFormat(format);
    const $fragment = PillHelpers.createPillWithFormat(this, format, { createPill: false });
    clickTrigger('.rsa-query-fragment'); // populates metaOptions dropdown
    typeInSearch(metaName); // selects one metaOption based on the current format
    pressSpace($fragment.find('input')); // hit space to populate operator dropdown
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, count, `There are ${count} relevant operators for type ${format}`);
  });
});
