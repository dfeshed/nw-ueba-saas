import { module, test } from 'qunit';

import { setupTest } from 'ember-qunit';
import columnConfig from 'entity-details/utils/column-config';
module('Unit | Utils | column-config', (hooks) => {
  setupTest(hooks);

  test('it returns events table column for file schema', (assert) => {
    const columnArray = columnConfig('file');
    assert.equal(columnArray.length, 10, 'should return file columns along with base columns');
  });

  test('it returns events table column for active_directory schema', (assert) => {
    const columnArray = columnConfig('active_directory');
    assert.equal(columnArray.length, 8, 'should return active_directory columns along with base columns');
  });

  test('it returns events table column for authentication schema', (assert) => {
    const columnArray = columnConfig('authentication');
    assert.equal(columnArray.length, 13, 'should return authentication columns along with base columns');
  });

  test('it returns events table column for process schema', (assert) => {
    const columnArray = columnConfig('process');
    assert.equal(columnArray.length, 8, 'should return process columns along with base columns');
  });

  test('it returns events table column for registry schema', (assert) => {
    const columnArray = columnConfig('registry');
    assert.equal(columnArray.length, 20, 'should return registry column along with base columns');
  });

  test('it returns base columns for any other schema', (assert) => {
    const columnArray = columnConfig('test');
    assert.equal(columnArray.length, 5, 'should return base columns for any other schema');
  });

});