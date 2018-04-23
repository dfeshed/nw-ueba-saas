import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import wait from 'ember-test-helpers/wait';

const eventBusStub = Service.extend(Evented, {});

moduleForComponent('/rsa-content-tethered-panel-trigger', 'Integration | Component | rsa-content-tethered-panel-trigger', {
  integration: true,

  beforeEach() {
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
  }
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{#rsa-content-tethered-panel-trigger panel="foo"}}Trigger{{/rsa-content-tethered-panel-trigger}}`);
  assert.equal(this.$('span.rsa-content-tethered-panel-trigger.foo').length, 1);
});

test('it emits the rsa-content-tethered-panel-display event on mouseenter', function(assert) {
  this.render(hbs `{{#rsa-content-tethered-panel-trigger panel="foo"}}Trigger{{/rsa-content-tethered-panel-trigger}}`);

  const spy = sinon.spy(this.get('eventBus'), 'trigger');

  this.$().find('.rsa-content-tethered-panel-trigger').mouseenter();

  return wait().then(function() {
    assert.ok(spy.withArgs('rsa-content-tethered-panel-display-foo').calledOnce);
  });
});

test('it emits the rsa-content-tethered-panel-hide event on mouseleave', function(assert) {
  this.render(hbs `{{#rsa-content-tethered-panel-trigger panel="foo"}}Trigger{{/rsa-content-tethered-panel-trigger}}`);

  const spy = sinon.spy(this.get('eventBus'), 'trigger');

  this.$().find('.rsa-content-tethered-panel-trigger').mouseleave();

  return wait().then(function() {
    assert.ok(spy.withArgs('rsa-content-tethered-panel-hide-foo').calledOnce);
  });
});
