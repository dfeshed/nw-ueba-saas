import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import Service from '@ember/service';


module('Unit | Services | stream-selector', function(hooks) {
  setupTest(hooks);

  test('stream-selector test for any', async function(assert) {
    const reduxStub = Service.extend({
      state: {
        endpointQuery: {
          serverId: 'typeAny',
          selectedMachineServerId: null
        }
      },
      getState() {
        return this.get('state');
      }
    });

    assert.expect(1);

    this.owner.register('service:redux', reduxStub);
    const streamSelectorService = this.owner.lookup('service:stream-selector');
    const serviceReturnValue = streamSelectorService.streamOptionSelector({ modelName: 'investigate-service', method: 'findAll' });
    assert.deepEqual(serviceReturnValue, {
      'requiredSocketUrl': 'endpoint/socket',
      'socketUrlPostfix': 'any'
    });
  });

  test('stream-selector test for eps', async function(assert) {
    const reduxStub = Service.extend({
      state: {
        endpointQuery: {
          serverId: 'typeBroker',
          selectedMachineServerId: 'typeEps'
        }
      },
      getState() {
        return this.get('state');
      }
    });

    assert.expect(1);

    this.owner.register('service:redux', reduxStub);
    const streamSelectorService = this.owner.lookup('service:stream-selector');
    const serviceReturnValue = streamSelectorService.streamOptionSelector({ modelName: 'endpoint', method: 'fileContextSearch' });
    assert.deepEqual(serviceReturnValue, {
      'requiredSocketUrl': 'endpoint/socket',
      'socketUrlPostfix': 'typeEps'
    });
  });

  test('stream-selector test for selected service Id', async function(assert) {
    const reduxStub = Service.extend({
      state: {
        endpointQuery: {
          serverId: 'typeBroker',
          selectedMachineServerId: 'typeEps'
        }
      },
      getState() {
        return this.get('state');
      }
    });

    assert.expect(1);

    this.owner.register('service:redux', reduxStub);
    const streamSelectorService = this.owner.lookup('service:stream-selector');
    const serviceReturnValue = streamSelectorService.streamOptionSelector({ modelName: 'endpoint', method: 'getAllSnapShots' });
    assert.deepEqual(serviceReturnValue, {
      'requiredSocketUrl': 'endpoint/socket',
      'socketUrlPostfix': 'typeBroker'
    });
  });

});