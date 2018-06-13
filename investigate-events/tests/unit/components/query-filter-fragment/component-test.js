import { moduleForComponent, test } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { lookup } from 'ember-dependency-lookup';

moduleForComponent('query-filters/query-filter-fragment', 'Unit | Component | query filter fragment', {
  unit: true,
  needs: [
    'service:i18n',
    'service:redux'
  ],
  resolver: engineResolverFor('investigate-events')
});

test('it sets contextItems', function(assert) {
  const component = this.subject({
    meta: 'foo',
    operator: '=',
    value: 'bar',
    filterList: [],
    deleteFilter: () => {},
    executeQuery: () => {},
    i18n: lookup('service:i18n'),
    metaIndex: 'value',
    metaFormat: 'Text'
  });

  component.set('selected', false);
  component.set('hasRequiredValuesToQuery', false);
  assert.equal(component.get('contextItems.length'), 0, 'Expected no contextItems');

  component.set('selected', true);
  assert.equal(component.get('contextItems.length'), 3, 'Expected 3 contextItem');
  assert.equal(component.get('contextItems')[0].label, 'Query with selected filters', 'Expected contextItems[1].label to match');
  assert.equal(component.get('contextItems')[1].label, 'Query with selected filters in a new tab', 'Expected contextItems[2].label to match');
  assert.equal(component.get('contextItems')[2].label, 'Delete selected filters', 'Expected contextItems[0].label to match');
});

test('it sets operatorOptions', function(assert) {
  const component = this.subject();

  // will load the default operators
  const options = component.get('operatorOptions');

  assert.equal(options.get('length'), 7);

  assert.ok(options.findBy('displayName', '='), 'Expected to find =');
  assert.ok(options.findBy('displayName', '!='), 'Expected to find !=');
  assert.ok(options.findBy('displayName', 'begins'), 'Expected to find begins');
  assert.ok(options.findBy('displayName', 'contains'), 'Expected to find contains');
  assert.ok(options.findBy('displayName', 'ends'), 'Expected to find ends');
  assert.ok(options.findBy('displayName', 'exists'), 'Expected to find exists');
  assert.ok(options.findBy('displayName', '!exists'), 'Expected to find !exists');
});

test('it sets operatorOptions when metaFormat is Text', function(assert) {
  const component = this.subject({
    metaFormat: 'Text',
    metaIndex: 'value'
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

test('it sets operatorOptions when metaFormat is IPv4', function(assert) {
  const component = this.subject({
    metaFormat: 'IPv4',
    metaIndex: 'value'
  });
  const options = component.get('operatorOptions');

  assert.equal(options.get('length'), 4);
  assert.ok(options.findBy('displayName', '='), 'Expected to find =');
  assert.ok(options.findBy('displayName', '!='), 'Expected to find !=');
  assert.ok(options.findBy('displayName', 'exists'), 'Expected to find exists');
  assert.ok(options.findBy('displayName', '!exists'), 'Expected to find !exists');

});

test('it sets operatorOptions when metaFormat is IPv6', function(assert) {
  const component = this.subject({
    metaFormat: 'IPv',
    metaIndex: 'value'
  });
  const options = component.get('operatorOptions');

  assert.equal(options.get('length'), 4);
  assert.ok(options.findBy('displayName', '='), 'Expected to find =');
  assert.ok(options.findBy('displayName', '!='), 'Expected to find !=');
  assert.ok(options.findBy('displayName', 'exists'), 'Expected to find exists');
  assert.ok(options.findBy('displayName', '!exists'), 'Expected to find !exists');
});

test('it sets isExpensive when metaIndex is anything but value', function(assert) {
  const component = this.subject({
    metaIndex: 'value',
    metaFormat: 'Text'
  });

  assert.notOk(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to not be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'contains').isExpensive, 'Expected contains to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'begins').isExpensive, 'Expected begins to not be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'ends').isExpensive, 'Expected ends to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
  component.set('metaFormat', null);

  // when metaIndex is value and format is not text
  component.set('metaIndex', 'value');
  component.set('metaFormat', 'IPv4');

  assert.notOk(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
  component.set('metaFormat', null);

  // when metaIndex is key and format is text
  component.set('metaIndex', 'key');
  component.set('metaFormat', 'Text');

  assert.ok(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'contains').isExpensive, 'Expected contains to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'begins').isExpensive, 'Expected begins to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', 'ends').isExpensive, 'Expected ends to be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
  component.set('metaFormat', null);

  // when metaIndex is value and format is not text
  component.set('metaIndex', 'key');
  component.set('metaFormat', 'UInt64');

  assert.ok(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to be expensive');
  assert.ok(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
  component.set('metaFormat', null);

  // when metaIndex is none -> only sessionid
  component.set('metaFormat', 'Text');
  component.set('metaIndex', 'none');

  assert.notOk(component.get('operatorOptions').findBy('displayName', '=').isExpensive, 'Expected = to be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!=').isExpensive, 'Expected != to be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(component.get('operatorOptions').findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');

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
