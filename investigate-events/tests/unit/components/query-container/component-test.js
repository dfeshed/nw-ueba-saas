import { moduleForComponent, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('query-container', 'Unit | Component | query container', {
  unit: true,
  needs: [
    'service:i18n',
    'service:redux'
  ],
  resolver: engineResolverFor('investigate-events')
});


test('it sets correct criteria when queryView is toggled to guided', function(assert) {
  const component = this.subject({
    filters: ['foo = bar', 'foo = baz'],
    freeFormText: 'medium = 1',
    queryView: 'guided'
  });
  const criteria = component.get('criteria');
  assert.equal(Array.isArray(criteria), true, 'Expected an array');
  assert.equal(criteria[0], 'foo = bar', 'Expected 1st filter');

});

test('it sets correct criteria when queryView is toggled to free-form', function(assert) {
  const component = this.subject({
    filters: ['foo = bar', 'foo = baz'],
    freeFormText: 'medium = 1',
    queryView: 'freeForm'
  });
  const criteria = component.get('criteria');

  assert.equal(typeof criteria, 'string', 'Expected a string');
  assert.equal(criteria, 'medium = 1', 'Expected filter');

});

test('it sets correct criteria when query is changed', function(assert) {
  const component = this.subject({
    filters: ['foo = bar', 'foo = baz'],
    freeFormText: 'medium = 1',
    queryView: 'freeForm'
  });
  const criteria = component.get('criteria');

  assert.equal(typeof criteria, 'string', 'Expected a string');
  assert.equal(criteria, 'medium = 1', 'Correct filter');

  component.set('queryView', 'freeForm');
  component.set('freeFormText', 'sessionid = 1');
  const newCriteria = component.get('criteria');

  assert.equal(typeof newCriteria, 'string', 'Expected a string');
  assert.equal(newCriteria, 'sessionid = 1', 'Criteria modified if filters are changed');

});