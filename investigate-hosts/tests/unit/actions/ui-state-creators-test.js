import { module, setupTest, test } from 'ember-qunit';
import * as dataCreators from 'investigate-hosts/actions/ui-state-creators';
import ACTION_TYPES from 'investigate-hosts/actions/types';

module('Unit | Actions | Data Creators', function(hooks) {

  setupTest(hooks);

  test('Test action creator for toggleMachineSelected', function(assert) {
    const data = {
      id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
      machineIdentity: {
        machineName: 'RAR113-EPS',
        machineOsType: 'linux',
        agentMode: 'advanced'
      },
      version: '11.4.0.0',
      groupPolicy: { managed: true },
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0',
      agentStatus: { scanStatus: 'idle' }
    };
    const action = dataCreators.toggleMachineSelected(data);
    assert.equal(action.type, ACTION_TYPES.TOGGLE_MACHINE_SELECTED);
  });
});