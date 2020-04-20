import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { waitForSockets } from '../../../helpers/wait-for-sockets';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const eventBusStub = Service.extend(Evented, {});

const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  const styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
    'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  const styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}

module('Integration | Component | context tooltip', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    insertTetherFix();
    this.owner.register('service:event-bus', eventBusStub);
    this.eventBus = this.owner.lookup('service:event-bus');
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    removeTetherFix();
  });

  test('it renders', async function(assert) {
    assert.expect(4);

    const done = waitForSockets();

    const model = { type: 'IP', id: '10.20.30.40' };

    const clickDataAction = (arg) => {
      assert.ok(true, 'Expected callback action to be invoked.');
      assert.equal(arg, model, 'Expected callback action to receive the model as an input argument.');
    };
    this.set('clickDataAction', clickDataAction);

    // rsa-content-tethered-panel requires us to render an element whose class matches the tooltip's panelId.
    await render(hbs`<a class="foo">Link</a>{{context-tooltip panelId="foo" clickDataAction=clickDataAction}}`);

    return settled()
      .then(() => {
        // Simulate an event that will cause the tooltip to render its contents.
        this.get('eventBus').trigger('rsa-content-tethered-panel-display-foo', null, null, null, model);
        return settled();
      })
      .then(async() => {
        assert.equal(document.querySelectorAll('.rsa-context-tooltip').length, 1, 'Expected to find root DOM node');
        assert.equal(document.querySelectorAll('.js-open-overview button').length, 1, 'Expected to find Open Overview DOM node');
        await click('.js-open-overview button');
        return settled().then(() => done());
      });
  });
});
