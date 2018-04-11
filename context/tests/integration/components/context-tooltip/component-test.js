import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
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

moduleForComponent('context-tooltip', 'Integration | Component | context tooltip', {
  integration: true,
  beforeEach() {
    insertTetherFix();
    this.register('service:event-bus', eventBusStub);
    this.inject.service('event-bus', { as: 'eventBus' });
    initialize(this);
  },
  afterEach() {
    removeTetherFix();
  }
});

test('it renders', function(assert) {
  assert.expect(4);

  const done = waitForSockets();

  const model = { type: 'IP', id: '10.20.30.40' };

  const clickDataAction = (arg) => {
    assert.ok(true, 'Expected callback action to be invoked.');
    assert.equal(arg, model, 'Expected callback action to receive the model as an input argument.');
  };
  this.set('clickDataAction', clickDataAction);

  // rsa-content-tethered-panel requires us to render an element whose class matches the tooltip's panelId.
  this.render(hbs`<a class="foo">Link</a>{{context-tooltip panelId="foo" clickDataAction=clickDataAction}}`);

  return wait()
    .then(() => {
      // Simulate an event that will cause the tooltip to render its contents.
      this.get('eventBus').trigger('rsa-content-tethered-panel-display-foo', null, null, null, model);
      return wait();
    })
    .then(() => {
      assert.equal(this.$('.rsa-context-tooltip').length, 1, 'Expected to find root DOM node');
      assert.equal(this.$('.js-open-overview button').length, 1, 'Expected to find Open Overview DOM node');
      this.$('.js-open-overview button').click();
      return wait().then(() => done());
    });
});
