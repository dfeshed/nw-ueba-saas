import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

const {
  Service,
  Evented
} = Ember;

const eventBusStub = Service.extend(Evented, {});

moduleForComponent('/rsa-application-content', 'Integration | Component | rsa-application-content', {
  integration: true,

  beforeEach() {
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
  }
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-content}}`);
  let content = this.$().find('.rsa-application-content').length;
  assert.equal(content, 1);
});

test('it includes the proper classes when hasBlur is true', function(assert) {
  this.render(hbs `{{#rsa-application-content hasBlur=true}}foo{{/rsa-application-content}}`);
  let content = this.$().find('.rsa-application-content').first();
  assert.ok(content.hasClass('has-blur'));
});

test('it updates hasBlur when rsa-application-modal-did-open is triggered', function(assert) {
  this.set('initialBlur', false);
  this.render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
  assert.equal(this.get('initialBlur'), false);
  this.get('eventBus').trigger('rsa-application-modal-did-open', true);

  let that = this;
  return wait().then(function() {
    assert.equal(that.get('initialBlur'), true);
  });
});

