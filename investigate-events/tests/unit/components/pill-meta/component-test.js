import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { getPowerSelectOptions, getPowerSelectAPI } from '../../../helpers/meta-data-helper';

const powerSelectAPIOptions = getPowerSelectOptions();

module('Unit | Component | Pill Meta', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Can properly detect an exact meta match', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const metas = [
      { displayName: 'Foo', metaName: 'foo' },
      { displayName: 'Foo Bar', metaName: 'foo.bar' },
      { displayName: 'Baz', metaName: 'baz' }
    ];
    assert.notOk(comp._hasExactMatch('foobar', metas), 'Erroneously found matching item');
    assert.equal(comp._hasExactMatch('foo', metas), metas[0], 'Did not find matching item');
  });

  test('Does not detect an exact meta match if isIndexedByNone', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const metas = [
      { displayName: 'Foo', metaName: 'foo', isIndexedByNone: true },
      { displayName: 'Foo Bar', metaName: 'foo.bar' },
      { displayName: 'Baz', metaName: 'baz' }
    ];
    assert.notOk(comp._hasExactMatch('foo', metas), 'Shall not find match if isIndexedByNone');
  });

  test('_highlighter finds the first valid meta when it is the first element', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const powerSelectAPIOptions1 = [...powerSelectAPIOptions];

    // make the first element a valid meta
    powerSelectAPIOptions1[0].isIndexedByNone = false;
    powerSelectAPIOptions1[0].isIndexedByKey = true;
    powerSelectAPIOptions1[0].isIndexedByValue = false;
    powerSelectAPIOptions1[0].disabled = false;

    const powerSelectAPI1 = getPowerSelectAPI(powerSelectAPIOptions1);
    assert.equal(comp._highlighter(powerSelectAPI1), powerSelectAPIOptions1[0],
      'Shall return correct first valid meta');
  });

  test('_highlighter finds the first valid meta index when it is not the first element', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const powerSelectAPIOptions1 = [...powerSelectAPIOptions];

    // make all options invalid meta
    powerSelectAPIOptions1.forEach((option) => {
      option.isIndexedByNone = true;
      option.isIndexedByKey = false;
      option.isIndexedByValue = false;
      option.disabled = true;
    });

    // then make some element a valid meta
    const randomIndex = Math.floor(Math.random() * (powerSelectAPIOptions1.length - 2)) + 1;
    powerSelectAPIOptions1[randomIndex].isIndexedByNone = false;
    powerSelectAPIOptions1[randomIndex].isIndexedByKey = true;
    powerSelectAPIOptions1[randomIndex].isIndexedByValue = false;
    powerSelectAPIOptions1[randomIndex].disabled = false;

    const powerSelectAPI1 = getPowerSelectAPI(powerSelectAPIOptions1, randomIndex + 1);
    assert.equal(comp._highlighter(powerSelectAPI1), powerSelectAPIOptions1[randomIndex],
      'Shall return correct first valid meta');
  });

  test('_highlighter returns undefined if there is no valid meta', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const powerSelectAPIOptions1 = [...powerSelectAPIOptions];

    // make all options invalid meta
    powerSelectAPIOptions1.forEach((option) => {
      option.isIndexedByNone = true;
      option.isIndexedByKey = false;
      option.isIndexedByValue = false;
      option.disabled = true;
    });

    const powerSelectAPI1 = getPowerSelectAPI(powerSelectAPIOptions1);
    assert.equal(comp._highlighter(powerSelectAPI1), undefined,
      'Shall return undefined if no valid meta found');
  });
});
