// investigate-events

// THIS IS JUST AN EXAMPLE

// Note the use of 'engineResolverFor', this is what differentiates
// an integration test inside an engine from one inside an app/addon

import { moduleForComponent, test } from 'ember-qunit';
// import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../helpers/engine-resolver';

moduleForComponent('events-table', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate')
});

test('it renders', function(assert) {
  // this.render(hbs`{{events-table}}`);
  // assert.equal(this.$('.rsa-investigate-events-table').length, 1);
  assert.equal(0, 0);
});
