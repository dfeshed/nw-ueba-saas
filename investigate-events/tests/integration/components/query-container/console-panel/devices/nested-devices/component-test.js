import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, click } from '@ember/test-helpers';

module('Integration | Component | nested-devices', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');

    this.set('isExpanded', true);
    this.set('height', 100);
    this.set('device', {
      serviceId: 'foo',
      elapsedTime: 1,
      on: true,
      serviceName: 'Foo',
      devices: [{
        serviceName: 'Bar',
        serviceId: 'bar',
        elapsedTime: 1,
        on: true
      }]
    });
    this.set('noChildren', {
      serviceId: 'foo',
      elapsedTime: 1,
      on: true,
      serviceName: 'Foo'
    });

    initialize(this.owner);
  });

  test('renders the correct dom', async function(assert) {
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height}}
      </ul>
    `);

    assert.ok(find('.nested-devices'));
    assert.equal(find('.nested-devices svg.vertical').getAttribute('viewBox'), '0 0 2 100');
    assert.equal(find('.nested-devices svg.vertical line').getAttribute('y2'), '100');
    assert.ok(find('.nested-devices .one-line-summary'));
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.open'));
    assert.equal(find('.nested-devices .one-line-summary .device').textContent.trim(), 'Foo');
    assert.equal(find('.nested-devices .one-line-summary .elapsed-time').textContent.trim(), '(1s)');
    assert.ok(find('.nested-devices .one-line-summary .expand'));
    assert.ok(find('.nested-devices .device-hierarchy'));
  });

  test('renders the correct dom when zero for elapsedTime', async function(assert) {
    this.set('noTime', {
      serviceId: 'foo',
      elapsedTime: 0,
      on: true,
      serviceName: 'Foo'
    });
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=noTime isExpanded=isExpanded height=height}}
      </ul>
    `);

    assert.equal(find('.nested-devices .one-line-summary .device').textContent.trim(), 'Foo');
    assert.notOk(find('.nested-devices .one-line-summary .elapsed-time'));
  });

  test('renders the correct dom when isSlowest', async function(assert) {
    this.set('isSlowest', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height isSlowest=isSlowest}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .slowest'));
  });

  test('renders the correct dom when warning', async function(assert) {
    this.set('warning', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height warning=warning}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .warning'));
  });

  test('renders the correct dom when inOfflinePath', async function(assert) {
    this.set('inOfflinePath', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height inOfflinePath=inOfflinePath}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .offline'));
  });

  test('does not render the children when no devices', async function(assert) {
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=noChildren height=height}}
      </ul>
    `);

    assert.notOk(find('.nested-devices .device-hierarchy'));
    assert.equal(findAll('.nested-devices .one-line-summary .circle.empty').length, 1);
  });

  test('renders the correct dom when no children and hasError', async function(assert) {
    this.set('hasError', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=noChildren height=height hasError=hasError}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.empty.error'));
  });

  test('renders the correct dom when no children and inOfflinePath', async function(assert) {
    this.set('inOfflinePath', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=noChildren height=height inOfflinePath=inOfflinePath}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.empty.offline'));
  });

  test('renders the correct dom when no children and inWarningPath', async function(assert) {
    this.set('inWarningPath', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=noChildren height=height inWarningPath=inWarningPath}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.empty.warning'));
  });

  test('renders the correct dom when with children and inWarningPath', async function(assert) {
    this.set('inWarningPath', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height inWarningPath=inWarningPath}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.open.warning'));
  });

  test('renders the correct dom when isExpanded, with children and hasError', async function(assert) {
    this.set('hasError', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height hasError=hasError}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.open.error'));
  });

  test('renders the correct dom when isExpanded, with children and inOfflinePath', async function(assert) {
    this.set('inOfflinePath', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height inOfflinePath=inOfflinePath}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.open.offline'));
  });

  test('renders the correct dom when closed with children', async function(assert) {
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=false height=height}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.closed'));
  });

  test('renders the correct dom when closed with children and inWarningPath', async function(assert) {
    this.set('inWarningPath', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=false height=height inWarningPath=inWarningPath}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.closed.warning'));
  });

  test('renders the correct dom when closed with children and inOfflinePath', async function(assert) {
    this.set('inOfflinePath', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=false height=height inOfflinePath=inOfflinePath}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.closed.offline'));
  });

  test('renders the correct dom when closed with children and hasError', async function(assert) {
    this.set('hasError', true);
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=false height=height hasError=hasError}}
      </ul>
    `);
    assert.ok(find('.nested-devices .one-line-summary .circle.populated.closed.error'));
  });

  test('expands/collapses on click', async function(assert) {
    await render(hbs`
      <ul class="device-hierarchy">
        {{query-container/console-panel/devices/nested-devices device=device isExpanded=isExpanded height=height}}
      </ul>
    `);

    await click('.nested-devices .one-line-summary');
    assert.equal(this.get('isExpanded'), false);
    await click('.nested-devices .one-line-summary');
    assert.equal(this.get('isExpanded'), true);
  });

});
