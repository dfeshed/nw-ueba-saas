import EmberObject from '@ember/object';
import HighlightsEntitiesMixin from 'context/mixins/highlights-entities';
import { module, test } from 'qunit';
import jQuery from 'jquery';
import Evented from '@ember/object/evented';
import rsvp from 'rsvp';
import { next } from '@ember/runloop';
import { isArray } from '@ember/array';

module('Unit | Mixin | highlights entities');

const entityTypes = {
  code: 0,
  data: [
    'IP',
    'HOST'
  ]
};
const entityMetas = {
  code: 0,
  data: {
    IP: ['ip.src', 'ip.dst'],
    HOST: ['hostname.alias']
  }
};

const entityEndpointId = 'CONCENTRATOR-1';

const entityTooltipPanelId = 'panel-1';

const eventBusStub = EmberObject.extend(Evented, {}).create();

const contextStub = EmberObject.extend({
  types() {
    return rsvp.resolve(entityTypes);
  },
  metas() {
    return rsvp.resolve(entityMetas);
  },
  summary(entities, callback) {
    next(() => {
      (entities || []).forEach(({ type, id }, index) => {
        callback(
          type,
          id,
          'complete',

          // every other entity will receive an empty set of records, for testing
          index % 2 ?
            [{ name: 'incidents', count: 1 }] : []
        );
      });
    });
  }
}).create();

const FakeComponentClass = EmberObject.extend(HighlightsEntitiesMixin, {
  eventBus: eventBusStub,
  context: contextStub,
  entityEndpointId,
  entityTooltipPanelId,
  entityTooltipTriggerEvent: 'click',
  $(sel) {    // simulates Component.$() method
    if (this.element) {
      return sel ? jQuery(sel, this.element) : jQuery(this.element);
    }
  }
});

const values = [{
  cssClass: 'entity',
  type: 'IP',
  id: '10.20.30.40',
  metaKey: ''
}, {
  cssClass: 'entity',
  type: '',
  id: 'MACHINE-1',
  metaKey: 'hostname.alias'
}, {
  cssClass: 'entity',
  type: 'PORT',
  id: '8080',
  metaKey: ''
}, {
  cssClass: '',
  type: 'IP',
  id: '10.20.30.40',
  metaKey: ''
}];

// Create DOM for our tests. We want to ensure this works with SVG as well as normal HTML, so test SVG.
const innerHTML = values
  .map(({ cssClass, type, id, metaKey }) => `<g
    class="${cssClass}"
    data-entity-id="${id}"
    ${(cssClass === 'entity') ? ` data-entity-type="${type}" ` : ' '}
    data-meta-key="${metaKey}"><text>${id}</text></g>`)
  .join('');

test('it applies CSS classes, wires up clicks, and fires callbacks correctly', function(assert) {
  assert.expect(14);  // 14 = 8 + 3 asserts * every callback to onEntityContextFound = 8 + 3 * 2

  const element = document.createElement('svg');
  element.id = 'highlights-entities-test-element-1';
  element.innerHTML = innerHTML;
  document.body.appendChild(element);

  const subject = FakeComponentClass.create({
    element,
    autoHighlightEntities: true,
    onEntityContextFound: (type, id, $element, status, records) => {
      assert.ok(isArray(records), 'Expected callback to receive context data');
      assert.ok($element && $element.hasClass('is-context-enabled'), 'Expected callback only for enabled DOM nodes');
      assert.equal(
        !!(records && records.length),
        !!$element && $element.hasClass('has-context-data'),
        'Expected DOM nodes to be decorated only if they have records'
      );
    }
  });
  assert.ok(subject);
  subject.didInsertElement();
  const done = assert.async();

  // Use `next()` to wait long enough for `didInsertElement` to call the mixin's `highlightEntities()`.
  next(() => {

    // Use `next()` to wait long enough for `highlightEntities()` to complete DOM manipulations.
    next(() => {

      // Check that CSS classes were applied as expected.
      assert.equal(subject.$('.entity-has-been-validated').length, 3,
        'Expected DOM nodes with class="entity" to be marked with ".entity-has-been-validated"');
      assert.equal(subject.$('[data-entity-type]').length, 3,
        'Expected DOM nodes whose entity type was resolved to have a data-entity-type HTML attribute"');
      assert.equal(subject.$('.is-context-enabled').length, 2,
        'Expected DOM nodes with valid entity types, or with meta keys that map to valid entity types, to be context enabled');
      assert.equal(subject.$('[id]').length, 2,
        'Expected DOM nodes with valid entity types, or with meta keys that map to valid entity types, to be auto-assigned ids');
      assert.equal(subject.$('.is-not-context-enabled').length, 1,
        'Expected DOM node with invalid entity type to be context disabled');

      const tooltipSpy = () => {
        assert.ok(true, 'An event was heard that was intended to display the tooltip.');
      };

      const tooltipDisplayEventName = `rsa-content-tethered-panel-toggle-${entityTooltipPanelId}`;
      eventBusStub.on(tooltipDisplayEventName, tooltipSpy);

      // Clicking on a context-disabled DOM node should NOT trigger an eventBus event. No assert expected!
      // Note: it's easier to test the disabled scenario first. If you test the enabled scenario first, make sure you
      // account for the fact that any clicks (no matter the target) will toggle a tooltip that is already opened.
      subject.$('.is-not-context-enabled').click();

      // Clicking on a context-enabled DOM node should trigger an eventBus event to display the tooltip. Assert expected!
      subject.$('.is-context-enabled').first().click();

      eventBusStub.off(tooltipDisplayEventName, tooltipSpy);

      // Use `next` to wait long enough for context data to come back.
      next(() => {
        assert.equal(subject.$('.has-context-data').length, 1,
          'Expected DOM nodes with valid entity types, or with meta keys that map to valid entity types, to fetch context data');

        subject.willDestroyElement();
        done();
      });

    });

  });
});

test('it supports launching the tooltip from right clicks', function(assert) {
  assert.expect(3);

  const element2 = document.createElement('svg');
  element2.id = 'highlights-entities-test-element-2';
  element2.innerHTML = innerHTML;
  document.body.appendChild(element2);

  const subject = FakeComponentClass.create({
    element: element2,
    autoHighlightEntities: true,
    entityTooltipTriggerEvent: 'contextmenu'
  });
  subject.didInsertElement();
  const done = assert.async();

  // Use `next()` to wait long enough for `didInsertElement` to call the mixin's `highlightEntities()`.
  next(() => {

    // Use `next()` to wait long enough for `highlightEntities()` to complete DOM manipulations.
    next(() => {
      let spyCounter = 0;
      const tooltipSpy = () => {
        spyCounter++;
        assert.ok(true, 'An event was heard that was intended to display the tooltip.');
      };

      const tooltipDisplayEventName = `rsa-content-tethered-panel-toggle-${entityTooltipPanelId}`;
      eventBusStub.on(tooltipDisplayEventName, tooltipSpy);

      // "Normal" clicking on a context-enable DOM node should NOT trigger an eventBus event. No assert expected!
      subject.$('.is-context-enabled').first().click();
      assert.equal(spyCounter, 0, 'Expected no asserts yet');

      // Right-clicking on a context-enabled DOM node should trigger an eventBus event to display the tooltip. Assert expected!
      subject.$('.is-context-enabled').first().trigger('contextmenu');
      assert.equal(spyCounter, 1, 'Expected just one assert from the contextmenu event');

      eventBusStub.off(tooltipDisplayEventName, tooltipSpy);

      subject.willDestroyElement();
      done();
    });
  });
});

test('it does nothing by default because "autoHighlightEntities" is falsey', function(assert) {
  assert.expect(3);

  const element3 = document.createElement('svg');
  element3.id = 'highlights-entities-test-element-3';
  element3.innerHTML = innerHTML;
  document.body.appendChild(element3);

  const subject = FakeComponentClass.create({
    element: element3,
    entityTooltipTriggerEvent: 'click'
  });
  subject.didInsertElement();
  const done = assert.async();

  // Use `next()` to wait long enough for `didInsertElement` to call the mixin's `highlightEntities()`.
  next(() => {

    // Use `next()` to wait long enough for `highlightEntities()` to complete DOM manipulations.
    next(() => {

      // Check that CSS classes were NOT applied.
      assert.notOk(subject.$('.entity-has-been-validated').length);
      assert.notOk(subject.$('.is-context-enabled').length);
      assert.notOk(subject.$('.is-not-context-enabled').length);

      // Check that clicking on the entity DOM node does nothing.
      const tooltipSpy = () => {
        assert.ok(true, 'An event was heard that was intended to display the tooltip.');
      };
      const tooltipDisplayEventName = `rsa-content-tethered-panel-toggle-${entityTooltipPanelId}`;
      eventBusStub.on(tooltipDisplayEventName, tooltipSpy);

      // Clicking on a context-enable DOM node should NOT trigger an eventBus event. No assert expected!
      subject.$('.entity').first().click();

      eventBusStub.off(tooltipDisplayEventName, tooltipSpy);
      subject.willDestroyElement();
      done();
    });
  });
});
