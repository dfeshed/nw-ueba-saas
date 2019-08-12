import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | alert-date', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it should render date from timestamp', async function(assert) {
    this.set('filterOptions', {
      filterValue: {
        name: 'alertTimeRange',
        operator: 'LESS_THAN',
        value: [ 3 ],
        unit: 'Months'
      }
    });
    await render(hbs`{{alert-date filterOptions=filterOptions}}`);
    assert.equal(findAll('.date-filter').length, 1);
  });

});