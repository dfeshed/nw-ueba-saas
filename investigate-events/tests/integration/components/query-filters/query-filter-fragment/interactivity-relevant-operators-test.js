import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import {
  testSetupConfig,
  PillHelpers,
  pressSpace,
  ALL_META_OPTIONS,
  metaForDropDowns
} from './util';
import { typeInSearch, clickTrigger } from 'ember-power-select/test-support/helpers';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment dropdown-has-relevant-operators',
  testSetupConfig
);

ALL_META_OPTIONS.forEach(({ indexedBy, count, metaName, expensiveCount }) => {
  test(`IndexedBy ${indexedBy} should have relevant operators in drop-down`, function(assert) {
    const meta = metaForDropDowns(indexedBy, metaName);
    const $fragment = PillHelpers.createPillWithFormat(this, meta.format, { createPill: false });
    clickTrigger('.rsa-query-fragment'); // populates metaOptions dropdown
    typeInSearch(meta.metaName); // selects one metaOption based on the current format
    pressSpace($fragment.find('input')); // hit space to populate operator dropdown
    assert.equal($('.ember-power-select-options li.ember-power-select-option').length, count, `There are ${count} relevant operators for type ${meta.format} and IndexedBy ${meta.indexedBy}`);
    assert.equal($('.ember-power-select-options li.ember-power-select-option .is-expensive').length, expensiveCount, `There are ${expensiveCount} expensive operators for type ${meta.format} and IndexedBy ${meta.indexedBy}`);
  });
});
