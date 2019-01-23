import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, triggerEvent, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';
import Service from '@ember/service';

const callback = () => {};
const e = {
  clientX: 10,
  clientY: 10,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
const wormhole = 'wormhole-context-menu';
const reduxStub = Service.extend({
  state: {
    investigate: {
      serviceId: '12345',
      timeRange: {
        unit: 'Days',
        value: 7
      }
    }
  },
  getState() {
    return this.get('state');
  }
});


module('Integration | Component | endpoint/base-property-panel/property-value', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    initialize(this.owner);
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    this.owner.register('service:redux', reduxStub);
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  test('it renders the tooltip-text', async function(assert) {
    await render(hbs`{{endpoint/base-property-panel/property-value}}`);
    assert.equal(findAll('.tooltip-text').length, 1, 'Expected to render the tooltip text content');
  });

  test('it renders the tooltip-text SIZE content', async function(assert) {
    const field = {
      format: 'SIZE',
      value: '1024'
    };
    this.set('field', field);
    await render(hbs`{{endpoint/base-property-panel/property-value property=field}}`);
    assert.equal(find('.tooltip-text .units').textContent.trim(), 'KB');
  });

  test('it renders the tooltip-text HEX content', async function(assert) {
    const field = {
      format: 'HEX',
      value: '16'
    };
    this.set('field', field);
    await render(hbs`{{endpoint/base-property-panel/property-value property=field}}`);
    assert.equal(find('.tooltip-text').textContent.trim(), '0x10');
  });

  test('it renders the tooltip-text SIGNATURE content', async function(assert) {
    const field = {
      format: 'SIGNATURE',
      value: null
    };
    this.set('field', field);
    await render(hbs`{{endpoint/base-property-panel/property-value property=field}}`);
    assert.equal(find('.tooltip-text').textContent.trim(), 'unsigned');
  });

  test('it renders the tooltip on mouse enter', async function(assert) {
    assert.expect(3);
    const field = {
      value: 'test value 123123 123123 123123 123123 123123'
    };
    this.set('field', field);
    await render(hbs`{{endpoint/base-property-panel/property-value property=field}}`);
    document.querySelector('.tooltip-text').setAttribute('style', 'width:100px');
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(find('.ember-tether .tool-tip-value').textContent.trim(), 'test value 123123 123123 123123 123123 123123');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });

  test('it renders the tooltip-text ACCORDIONLIST content', async function(assert) {
    const field = {
      format: 'ACCORDIONLIST',
      value: {
        library06: [
          '.interp01',
          '.interp02'
        ],
        library07: [
          '.interp01',
          '.interp02'
        ]
      }
    };
    this.set('field', field);
    await render(hbs`{{endpoint/base-property-panel/property-value property=field}}`);
    assert.equal(findAll('.properties__accordion__item').length, 3);
  });

  test('it open UEBA link, when clicked on the username', async function(assert) {
    const field = {
      field: 'name',
      value: 'corp\\raghs',
      showAsLink: true
    };
    this.set('field', field);
    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{endpoint/base-property-panel/property-value property=field}}{{context-menu}}
    `);
    await click('.userLink');
    return settled().then(async() => {
      assert.ok(actionSpy.calledOnce, 'Window.open is called');
      assert.ok(actionSpy.args[0][0].includes('investigate/users?ueba=/username/raghs'), 'valid link');
      actionSpy.restore();
    });
  });

  test('it redirects to investigate on doing the Analyze events', async function(assert) {
    const field = {
      field: 'ipv4',
      value: '192.25.25.255',
      showRightClick: true
    };
    this.set('field', field);
    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{endpoint/base-property-panel/property-value property=field}}{{context-menu}}
    `);
    triggerEvent(findAll('.user-name')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      assert.equal(menuItems.length, 2, '2 Context menu options rendered');
      await triggerEvent(`#${menuItems[1].id}`, 'mouseover');
      const subItems = findAll(`#${menuItems[1].id} > .context-menu--sub .context-menu__item`);
      assert.equal(subItems.length, 4, 'Sub menu rendered');
      click(`#${subItems[0].id}`);
      return settled().then(() => {
        assert.ok(actionSpy.calledOnce, 'Pivot service is invoked');
        actionSpy.resetHistory();
        actionSpy.restore();
      });
    });
  });

});
