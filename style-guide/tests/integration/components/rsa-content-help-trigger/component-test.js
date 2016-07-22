import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import wait from 'ember-test-helpers/wait';

const {
  Service,
  Evented
} = Ember;

const eventBusStub = Service.extend(Evented, {});

moduleForComponent('/rsa-content-help-trigger', 'Integration | Component | rsa-content-help-trigger', {
  integration: true,

  beforeEach() {
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
  }
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-content-help-trigger tooltip="foo"}}Trigger{{/rsa-content-help-trigger}}`);
  assert.equal(this.$('span.rsa-content-help-trigger.foo').length, 1);
});

test('it emits the rsa-content-tooltip-toggle event when clicking the trigger', function(assert) {
  this.render(hbs `{{#rsa-content-help-trigger tooltip="foo" triggerEvent="click"}}Trigger{{/rsa-content-help-trigger}}`);

  let spy = sinon.spy(this.get('eventBus'), 'trigger');

  this.$().find('.rsa-content-help-trigger').click();

  return wait().then(function() {
    assert.ok(spy.withArgs('rsa-content-tooltip-toggle-foo', 'click').calledOnce);
  });
});

test('it emits the rsa-content-tooltip-display event on mouseenter', function(assert) {
  this.render(hbs `{{#rsa-content-help-trigger tooltip="foo"}}Trigger{{/rsa-content-help-trigger}}`);

  let spy = sinon.spy(this.get('eventBus'), 'trigger');

  this.$().find('.rsa-content-help-trigger').mouseenter();

  return wait().then(function() {
    assert.ok(spy.withArgs('rsa-content-tooltip-display-foo', 'hover').calledOnce);
  });
});

test('it emits the rsa-content-tooltip-hide event on mouseleave', function(assert) {
  this.render(hbs `{{#rsa-content-help-trigger tooltip="foo"}}Trigger{{/rsa-content-help-trigger}}`);

  let spy = sinon.spy(this.get('eventBus'), 'trigger');

  this.$().find('.rsa-content-help-trigger').mouseleave();

  return wait().then(function() {
    assert.ok(spy.withArgs('rsa-content-tooltip-hide-foo', 'hover').calledOnce);
  });
});
