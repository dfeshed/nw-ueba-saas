import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper from '../../../helpers/data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('events-table', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
    initialize({ '__container__': this.container });
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{events-table}}`);
  assert.equal(this.$('.rsa-investigate-events-table').length, 1);
});
