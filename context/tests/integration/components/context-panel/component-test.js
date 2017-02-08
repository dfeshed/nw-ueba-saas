import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

const {
  Service
} = Ember;

moduleForComponent('context-panel', 'Integration | Component | context-panel', {
  integration: true,
  beforeEach() {
    this.set('i18n', this.container.lookup('service:i18n'));
    this.set('model', this.container.lookup('service:model'));
  }
});

test('Test context panel should display error in Error cases.', function(assert) {
  this.set('entityId', '1.1.1.1');
  this.set('entityType', 'IP');
  const hoverStub = Service.extend({
    streamRequest: (obj)=> {
      obj.onError('TEST ERROR');
    }
  });
  this.register('service:request', hoverStub);
  this.inject.service('location-service', { as: 'request' });
  const done = assert.async(1);
  this.render(hbs`{{context-panel entityId=entityId entityType=entityType i18n=i18n }}`);

  return wait().then(() => {
    const [errorText] = this.$('.rsa-context-panel__error-text');
    assert.ok(errorText.innerText.indexOf('TEST ERROR') > 0, 'Should Have error message');
    done();
  });
});

test('Test context panel should display error in Error from CH', function(assert) {
  this.set('entityId', '1.1.1.1');
  this.set('entityType', 'IP');
  const hoverStub = Service.extend({
    streamRequest: (obj)=> {
      obj.onResponse({});
    }
  });
  this.register('service:request', hoverStub);
  this.inject.service('location-service', { as: 'request' });
  const done = assert.async(1);
  this.render(hbs`{{context-panel entityId=entityId entityType=entityType i18n=i18n }}`);

  return wait().then(() => {
    const [errorText] = this.$('.rsa-context-panel__error-text');
    assert.ok(errorText.innerText.indexOf('Error processing stream call for context lookup.') === 0, 'Should Have error message');
    done();
  });
});