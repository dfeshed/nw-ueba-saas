import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import engineResolverFor from '../../../../helpers/engine-resolver';

let setState;

moduleForComponent('event-counter', 'Integration | Component | event counter', {
  resolver: engineResolverFor('investigate-events'),
  integration: true,
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders a given count', function(assert) {
  new ReduxDataHelper(setState).eventCount(55).build();
  this.render(hbs`{{events-table-container/event-counter}}`);
  assert.equal(this.$('.rsa-investigate-event-counter').length, 1, 'Expected root DOM element.');
  assert.equal(this.$('.rsa-investigate-event-counter').text().trim(), '55', 'Expected count value to be displayed in DOM.');
});

test('it does not show + if threshold is less than count', function(assert) {
  new ReduxDataHelper(setState).eventCount(55).eventThreshold(100).build();
  this.render(hbs`{{events-table-container/event-counter}}`);
  assert.equal(this.$('.rsa-investigate-event-counter__plus').length, 0, 'Expected to not find plus DOM element.');
});

test('it shows + icon if threshold is equal to count', function(assert) {
  new ReduxDataHelper(setState).eventCount(100).eventThreshold(100).build();
  this.render(hbs`{{events-table-container/event-counter}}`);
  assert.equal(this.$('.rsa-investigate-event-counter__plus').length, 1, 'Expected to find plus DOM element.');
});