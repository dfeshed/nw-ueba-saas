import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('events-table-context-menu', 'Integration | Component | events table context menu', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
    initialize({ '__container__': this.container });
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{events-table-context-menu contextItems=contextItems}}`);
  assert.equal(this.$('.content-context-menu').length, 1, 'Context menu trigger rendered');

});

test('it shows context menu on right click', function(assert) {
  this.render(hbs`{{#events-table-context-menu metaName=metaName metaValue=metaValue}}
                    <span id="right-click-target" metaname="ip.src" metavalue="1.1.1.1">Right click here</span>
                  {{/events-table-context-menu}}`);
  this.$('#right-click-target').trigger({
    type: 'contextmenu',
    clientX: 100,
    clientY: 100
  });

  assert.equal(this.get('metaName'), 'ip.src', 'meta name extracted from event and set');
  assert.equal(this.get('metaValue'), '1.1.1.1', 'meta value extracted from event and set');
});

test('context menu is deactivated on right clicking outside the target', function(assert) {
  assert.expect(1);
  const contextMenuService = {
    isActive: true,
    deactivate: () => {
      assert.step('deactivate called');
    }
  };
  this.set('contextMenuService', contextMenuService);
  this.render(hbs`{{#events-table-context-menu metaName=metaName metaValue=metaValue contextMenuService=contextMenuService}}
                    <span id="right-click-target" metaname="ip.src" metavalue="1.1.1.1">Right click here</span>
                    <span class="some-span">Foo</span>
                  {{/events-table-context-menu}}`);
  this.$('#right-click-target').trigger({
    type: 'contextmenu',
    clientX: 100,
    clientY: 100
  });

  // rt-click elsewhere
  this.$('.some-span').contextmenu();
});