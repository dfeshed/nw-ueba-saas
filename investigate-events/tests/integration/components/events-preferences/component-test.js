import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper from '../../../helpers/data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('events-preferences', 'Integration | Component | events preferences', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
    initialize(this);
  }
});


test('it should not show preferences panel trigger if service is not selected', function(assert) {
  this.render(hbs`{{events-preferences}}`);
  assert.equal(this.$('.rsa-preferences-panel-trigger').length, 0);
});

test('it should shows preferences panel trigger after service selection', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{events-preferences}}`);
  assert.equal(this.$('.rsa-preferences-panel-trigger').length, 1);
});
