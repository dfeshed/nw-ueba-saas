import { moduleForComponent, test } from 'ember-qunit';
import {
  testSetupConfig,
  pressSpace,
  metaNameForFormat,
  pressEnter,
  setupPillWithCustomProperties
} from './util';
import { typeInSearch, clickTrigger } from 'ember-power-select/test-support/helpers';

moduleForComponent(
  'query-filters/query-filter-fragment',
  'Integration | Component | query-filter-fragment monitor-component-variables',
  testSetupConfig
);


test('at every point of interaction, component should be set with proper values', function(assert) {

  setupPillWithCustomProperties(this);
  // initial properties check
  assert.equal(this.get('type'), 'meta', 'type set to meta initially');
  assert.equal(this.get('meta'), null, 'meta set to null initially');
  assert.equal(this.get('operator'), null, 'operator set to null initially');
  assert.equal(this.get('value'), null, 'value set to null initially');

  // type in meta
  this.$('input').focus();
  clickTrigger('.rsa-query-fragment');
  typeInSearch(metaNameForFormat('IPv4'));
  pressSpace(this.$('input'));

  // should set type and meta
  assert.equal(this.get('type'), 'operator', 'type set to operator!');
  assert.equal(this.get('meta'), 'alias.ip', 'meta set!');
  assert.equal(this.get('operator'), null, 'operator remains null');
  assert.equal(this.get('value'), null, 'value remains null');

  // type in meta and operator. Does not updateFilter if only operator is typed
  typeInSearch('alias.ip=');
  pressSpace(this.$('input'));

  // should set type and operator
  assert.equal(this.get('type'), 'value', 'type set to value');
  assert.equal(this.get('operator'), '=', 'operator set!');
  assert.equal(this.get('value'), null, 'value remains null');

  // type in the whole filter. Does not work if you paste just the operator or the value independently
  typeInSearch('alias.ip=127.0.0.1');
  pressEnter(this.$('input'));

  // finally value should be set. Filter complete
  assert.equal(this.get('value'), '127.0.0.1', 'value set!');
});