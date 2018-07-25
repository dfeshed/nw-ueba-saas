import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { set, get } from '@ember/object';
import InvestigateUsers from 'investigate/components/investigate-users/component';

const testingContainer = '#ember-testing';
const componentName = 'component:investigate-users';
const ueba = 'user/689d0bb1-a5e4-4af0-8d2c-98aa02a8ac9b/alert/fefe1f9e-2cf9-491d-bfc3-c37f61dcc4d9';

module('Unit | Component | investigate-users', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.register(componentName, InvestigateUsers);
  });

  test('iframeUrl shows deep link when ueba value present and default url when ueba undefined', async function(assert) {
    assert.expect(2);

    const component = this.owner.lookup(componentName);
    set(component, 'ueba', ueba);

    component.appendTo(testingContainer);

    assert.equal(get(component, 'iframeUrl'), `/presidio/index.html#/${ueba}`);

    set(component, 'ueba', undefined);

    assert.equal(get(component, 'iframeUrl'), '/presidio/index.html');
  });

  test('iframeUrl constructs consistent deep link url when ueba value differs slightly from happy path', async function(assert) {
    assert.expect(4);

    const component = this.owner.lookup(componentName);
    set(component, 'ueba', `/${ueba}`);

    component.appendTo(testingContainer);

    assert.equal(get(component, 'iframeUrl'), `/presidio/index.html#/${ueba}`);

    set(component, 'ueba', `#/${ueba}`);
    assert.equal(get(component, 'iframeUrl'), `/presidio/index.html#/${ueba}`);

    set(component, 'ueba', `/presidio/index.html#/${ueba}`);
    assert.equal(get(component, 'iframeUrl'), `/presidio/index.html#/${ueba}`);

    set(component, 'ueba', null);
    assert.equal(get(component, 'iframeUrl'), '/presidio/index.html');
  });
});
