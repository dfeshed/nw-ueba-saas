import EmObj from 'ember-object';
import { moduleFor, test } from 'ember-qunit';

const mockAppVersionService = EmObj.create({
  version: '11.0.0.0+fh7638'
});

const mockI18nService = EmObj.create({
  locale: 'foo'
});

const options = {
  appVersion: mockAppVersionService,
  i18n: mockI18nService
};

moduleFor('service:contextual-help', 'Unit | Service | contextual help', {});

test('it populates the global help url', function(assert) {
  const service = this.subject(options);

  service.set('topic', 'foo');
  service.set('module', 'foo');

  assert.equal(service.get('globalHelpUrl'), 'http://cms.netwitness.com/sadocs?locale=foo&version=11.0.0.0%252Bfh7638&module=foo&topic=foo');
});

test('it returns the contextual help url', function(assert) {
  const service = this.subject(options);

  const topic = 'foo';
  const module = 'foo';

  assert.equal(service.generateUrl(module, topic), 'http://cms.netwitness.com/sadocs?locale=foo&version=11.0.0.0%252Bfh7638&module=foo&topic=foo');
});
