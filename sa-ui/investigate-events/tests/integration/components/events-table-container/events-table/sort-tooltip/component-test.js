import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { findAll, render } from '@ember/test-helpers';

module('Integration | Component | sort-tooltip', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    initialize(this.owner);
  });

  test('it renders when disableSort', async function(assert) {
    this.setProperties({
      field: 'foo',
      disableSort: true,
      notIndexedAtValue: [],
      notSingleton: [],
      notValid: []
    });

    await render(hbs`
      {{events-table-container/events-table/sort-tooltip
        field=field
        disableSort=disableSort
        notIndexedAtValue=notIndexedAtValue
        notSingleton=notSingleton
        notValid=notValid
      }}
    `);
    assert.equal(findAll('section.sort-tooltip .disable-sort').length, 1);
  });

  test('it renders when notIndexedAtValue', async function(assert) {
    this.setProperties({
      field: 'foo',
      disableSort: false,
      notIndexedAtValue: ['foo'],
      notSingleton: [],
      notValid: []
    });

    await render(hbs`
      {{events-table-container/events-table/sort-tooltip
        field=field
        disableSort=disableSort
        notIndexedAtValue=notIndexedAtValue
        notSingleton=notSingleton
        notValid=notValid
      }}
    `);
    assert.equal(findAll('section.sort-tooltip .not-indexed-at-value').length, 1);
  });

  test('it renders when status is streaming', async function(assert) {
    this.setProperties({
      field: 'foo',
      disableSort: false,
      notIndexedAtValue: [],
      notSingleton: [],
      notValid: [],
      status: 'streaming'
    });

    await render(hbs`
      {{events-table-container/events-table/sort-tooltip
        field=field
        disableSort=disableSort
        notIndexedAtValue=notIndexedAtValue
        notSingleton=notSingleton
        notValid=notValid
        status=status
      }}
    `);
    assert.equal(findAll('section.sort-tooltip .is-streaming').length, 1);
  });

  test('it renders when notSingleton', async function(assert) {
    this.setProperties({
      field: 'foo',
      disableSort: false,
      notIndexedAtValue: [],
      notSingleton: ['foo'],
      notValid: []
    });

    await render(hbs`
      {{events-table-container/events-table/sort-tooltip
        field=field
        disableSort=disableSort
        notIndexedAtValue=notIndexedAtValue
        notSingleton=notSingleton
        notValid=notValid
      }}
    `);
    assert.equal(findAll('section.sort-tooltip .not-singleton').length, 1);
  });

  test('it renders when notValid', async function(assert) {
    this.setProperties({
      field: 'foo',
      disableSort: false,
      notIndexedAtValue: [],
      notSingleton: [],
      notValid: ['foo']
    });

    await render(hbs`
      {{events-table-container/events-table/sort-tooltip
        field=field
        disableSort=disableSort
        notIndexedAtValue=notIndexedAtValue
        notSingleton=notSingleton
        notValid=notValid
      }}
    `);
    assert.equal(findAll('section.sort-tooltip .not-valid').length, 1);
  });

  test('it renders when composed', async function(assert) {
    this.setProperties({
      field: 'foo',
      disableSort: false,
      notIndexedAtValue: [],
      notSingleton: [],
      notValid: []
    });

    await render(hbs`
      {{events-table-container/events-table/sort-tooltip
        field=field
        disableSort=disableSort
        notIndexedAtValue=notIndexedAtValue
        notSingleton=notSingleton
        notValid=notValid
      }}
    `);
    assert.equal(findAll('section.sort-tooltip .composed').length, 1);
  });

});
