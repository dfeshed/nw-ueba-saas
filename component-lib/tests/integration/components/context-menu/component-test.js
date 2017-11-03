import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import $ from 'jquery';

const {
  Service
} = Ember;

const contextMenuStub = Service.extend({
  isActive: true,
  position: { left: 100, top: 650 },
  event: {
    view: {
      window: {
        innerHeight: 700
      }
    }
  },
  items: [
    {
      label: 'Item 1',
      action() { }
    },
    {
      label: 'Item 2',
      action() { }
    }
  ]
});

moduleForComponent('context-menu', 'Integration | Component | Context Menu', {
  integration: true,
  beforeEach() {
    this.register('service:context-menu', contextMenuStub);
    this.inject.service('context-menu', { as: 'contextMenu' });
  }
});

test('it calculates correct position', function(assert) {
  this.render(hbs`{{context-menu}}`);

  assert.equal($('.context-menu-container').attr('style'), 'left: 100px; top: 627px;', 'Expected correct style attribute to be set');
});