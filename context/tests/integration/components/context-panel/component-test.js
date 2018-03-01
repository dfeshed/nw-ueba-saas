import Service from '@ember/service';
import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

moduleForComponent('context-panel', 'Integration | Component | context-panel', {
  integration: true
});

skip('Test context panel should display error in Error cases.', function(assert) {
  this.set('entityId', '1.1.1.1');
  this.set('entityType', 'IP');
  const hoverStub = Service.extend({
    streamRequest: (obj) => {
      obj.onError('TEST ERROR');
    }
  });
  this.register('service:request', hoverStub);
  this.inject.service('location-service', { as: 'request' });
  const done = assert.async(1);
  this.render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

  return waitFor('.rsa-context-panel__error-text').then(() => {
    const [errorText] = this.$('.rsa-context-panel__error-text');
    assert.ok(errorText.innerText.indexOf('TEST ERROR') > 0, 'Should Have error message');
    done();
  });
});

skip('Test context panel should display error in Error from CH', function(assert) {
  this.set('entityId', '1.1.1.1');
  this.set('entityType', 'IP');
  const hoverStub = Service.extend({
    streamRequest: (obj) => {
      obj.onResponse({});
    }
  });
  this.register('service:request', hoverStub);
  this.inject.service('location-service', { as: 'request' });
  const done = assert.async(1);
  this.render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

  return waitFor('.rsa-context-panel__error-text').then(() => {
    const [errorText] = this.$('.rsa-context-panel__error-text');
    assert.ok(errorText.innerText.indexOf('Error processing stream call for context lookup.') === 0, 'Should Have error message');
    done();
  });
});