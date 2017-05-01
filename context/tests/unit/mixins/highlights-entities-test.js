import EmberObject from 'ember-object';
import HighlightsEntitiesMixin from 'context/mixins/highlights-entities';
import { module, test } from 'qunit';
import jQuery from 'jquery';
import Evented from 'ember-evented';
import rsvp from 'rsvp';
import { next } from 'ember-runloop';

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
    'ip.src': 'IP',
    'ip.dst': 'IP',
    'hostname.alias': 'HOST'
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
      (entities || []).forEach(({ type, id }) => {
        callback(type, id, [{ name: 'incidents', count: 1 }]);
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

const element = document.createElement('svg');
element.id = 'highlights-entities-test-element-1';
element.innerHTML = innerHTML;
document.body.appendChild(element);

test('it applies CSS classes, wires up clicks, and fires callbacks correctly', function(assert) {
  assert.expect(12);  // 11 = 8 + 2 asserts * every callback to onEntityContextFound = 6 + 2 * 2

  const subject = FakeComponentClass.create({
    element,
    onEntityContextFound: (type, id, $element, records) => {
      assert.ok(records && records.length, 'Expected callback to receive context data');
      assert.ok($element && $element.hasClass('is-context-enabled'), 'Expected callback only for enabled DOM nodes');
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
        assert.equal(subject.$('.has-context-data').length, 2,
          'Expected DOM nodes with valid entity types, or with meta keys that map to valid entity types, to fetch context data');

        subject.willDestroyElement();
        done();
      });

    });

  });
});
