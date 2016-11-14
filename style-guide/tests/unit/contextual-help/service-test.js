import { moduleFor, test } from 'ember-qunit';

moduleFor('service:contextual-help', 'Unit | Service | contextual help', {
});

test('it populates the global help url', function(assert) {
  const service = this.subject();

  service.set('i18n', {});
  service.set('i18n.locale', 'foo');
  service.set('version', 'foo');
  service.set('topic', 'foo');
  service.set('module', 'foo');

  assert.equal(service.get('globalHelpUrl'), 'http://cms.netwitness.com/sadocs?locale=foo&version=foo&module=foo&topic=foo');
});

test('it returns the contextual help url', function(assert) {
  const service = this.subject();

  service.set('i18n', {});
  service.set('i18n.locale', 'foo');
  service.set('version', 'foo');

  const topic = 'foo';
  const module = 'foo';

  assert.equal(service.generateUrl(module, topic), 'http://cms.netwitness.com/sadocs?locale=foo&version=foo&module=foo&topic=foo');
});
