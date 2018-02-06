import { moduleForComponent, test, skip } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('query-filters/query-filter-fragment', 'Unit | Component | query filter fragment', {
  unit: true,
  needs: [
    'service:i18n',
    'service:redux'
  ],
  resolver: engineResolverFor('investigate-events')
});

test('it sets operatorOptions', function(assert) {
  const component = this.subject();

  const options = component.get('operatorOptions');

  assert.equal(options.get('length'), 8);

  assert.ok(options.findBy('displayName', '='), 'Expected to find =');
  assert.ok(options.findBy('displayName', '!='), 'Expected to find !=');
  assert.ok(options.findBy('displayName', '<'), 'Expected to find <');
  assert.ok(options.findBy('displayName', '<='), 'Expected to find <=');
  assert.ok(options.findBy('displayName', '>'), 'Expected to find >');
  assert.ok(options.findBy('displayName', '>='), 'Expected to find >=');
  assert.ok(options.findBy('displayName', 'exists'), 'Expected to find exists');
  assert.ok(options.findBy('displayName', '!exists'), 'Expected to find !exists');
});

test('it sets operatorOptions when metaFormat is Text', function(assert) {
  const component = this.subject({
    metaFormat: 'Text'
  });
  const options = component.get('operatorOptions');

  assert.equal(options.get('length'), 7);
  assert.ok(options.findBy('displayName', '='), 'Expected to find =');
  assert.ok(options.findBy('displayName', '!='), 'Expected to find !=');
  assert.ok(options.findBy('displayName', 'exists'), 'Expected to find exists');
  assert.ok(options.findBy('displayName', '!exists'), 'Expected to find !exists');
  assert.ok(options.findBy('displayName', 'contains'), 'Expected to find contains');
  assert.ok(options.findBy('displayName', 'begins'), 'Expected to find begins');
  assert.ok(options.findBy('displayName', 'ends'), 'Expected to find ends');
});

test('it sets operatorOptions when metaFormat is IPv4 or IPv6', function(assert) {
  const component = this.subject({
    metaFormat: 'IPv4'
  });
  const options = component.get('operatorOptions');

  assert.equal(options.get('length'), 4);
  assert.ok(options.findBy('displayName', '='), 'Expected to find =');
  assert.ok(options.findBy('displayName', '!='), 'Expected to find !=');
  assert.ok(options.findBy('displayName', 'exists'), 'Expected to find exists');
  assert.ok(options.findBy('displayName', '!exists'), 'Expected to find !exists');

  component.set('metaFormat', 'IPv6');

  assert.equal(options.get('length'), 4);
  assert.ok(options.findBy('displayName', '='), 'Expected to find =');
  assert.ok(options.findBy('displayName', '!='), 'Expected to find !=');
  assert.ok(options.findBy('displayName', 'exists'), 'Expected to find exists');
  assert.ok(options.findBy('displayName', '!exists'), 'Expected to find !exists');
});

skip('it sets isExpensive when metaIndex is anything but value', function(assert) {
  const component = this.subject({
    metaIndex: 'value',
    metaFormat: 'Text'
  });

  assert.notOk(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'contains').isExpensive, 'Expected contains to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'begins').isExpensive, 'Expected begins to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'ends').isExpensive, 'Expected ends to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
  component.set('metaFormat', null);
  assert.notOk(component.get('operatorOptions').findBy('displayName', '<').isExpensive, 'Expected < to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '<=').isExpensive, 'Expected <= to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '>').isExpensive, 'Expected > to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '>=').isExpensive, 'Expected >= to not be expensive');

  component.set('metaFormat', 'Text');
  component.set('metaIndex', 'key');

  assert.ok(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'contains').isExpensive, 'Expected contains to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'begins').isExpensive, 'Expected begins to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'ends').isExpensive, 'Expected ends to be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
  component.set('metaFormat', null);
  assert.ok(component.get('operatorOptions').findBy('displayName', '<').isExpensive, 'Expected < to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '<=').isExpensive, 'Expected <= to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '>').isExpensive, 'Expected > to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '>=').isExpensive, 'Expected >= to be expensive');

  component.set('metaFormat', 'Text');
  component.set('metaIndex', 'none');

  assert.ok(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'contains').isExpensive, 'Expected contains to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'begins').isExpensive, 'Expected begins to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'ends').isExpensive, 'Expected ends to be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
  component.set('metaFormat', null);
  assert.ok(component.get('operatorOptions').findBy('displayName', '<').isExpensive, 'Expected < to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '<=').isExpensive, 'Expected <= to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '>').isExpensive, 'Expected > to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '>=').isExpensive, 'Expected >= to be expensive');
});

test('it filters metaOptions without an index but allows time and sessionId regardless of index', function(assert) {
  const component = this.subject({
    metaOptions: [
      { metaName: 'time', flags: -2147483135 },
      { metaName: 'sessionid', flags: -2147483135 },
      { metaName: 'foo', flags: -2147483135 }
    ]
  });
  const options = component.get('filteredMetaOptions');

  assert.equal(options.get('length'), 2);
  assert.ok(options.findBy('metaName', 'time'), 'Expected to find time');
  assert.ok(options.findBy('metaName', 'sessionid'), 'Expected to find sessionid');
});
