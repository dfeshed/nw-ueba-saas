import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import {
  hasNoAggPermission,
  hasNoCapturePermission,
  hasNoShutdownPermission
} from 'ngcoreui/reducers/selectors/permissions';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | permissions', (hooks) => {

  setupTest(hooks);

  test('hasNoAggPermission is false when a user has permission for the appropriate module', (assert) => {
    const state = new ReduxDataHelper()
      .hasNoAggPermission()
      .availablePermissions('decoder.manage', 'sdk.meta', 'logs.manage')
      .module('decoder')
      .build();
    assert.deepEqual(hasNoAggPermission(state), false);
  });

  test('hasNoAggPermission is true when a user has permission for the wrong module', (assert) => {
    const state = new ReduxDataHelper()
      .hasNoAggPermission()
      .availablePermissions('decoder.manage', 'sdk.meta', 'logs.manage')
      .module('concentrator')
      .build();
    assert.deepEqual(hasNoAggPermission(state), true);
  });

  test('hasNoAggPermission is true when a user does not have permission at all', (assert) => {
    const state = new ReduxDataHelper()
      .hasNoAggPermission()
      .availablePermissions('sdk.meta', 'sdk.content', 'sdk.packets', 'logs.manage')
      .module('concentrator')
      .build();
    assert.deepEqual(hasNoAggPermission(state), true);
  });

  test('hasNoCapturePermission is false when a user has the decoder.manage permission', (assert) => {
    const state = new ReduxDataHelper()
      .hasNoCapturePermission()
      .availablePermissions('sdk.meta', 'sdk.content', 'sdk.packets', 'decoder.manage')
      .build();
    assert.deepEqual(hasNoCapturePermission(state), true);
  });

  test('hasNoCapturePermission is true when a user does not have the decoder.manage permission', (assert) => {
    const state = new ReduxDataHelper()
      .hasNoCapturePermission()
      .availablePermissions('sdk.meta', 'sdk.content', 'sdk.packets')
      .build();
    assert.deepEqual(hasNoCapturePermission(state), true);
  });

  test('hasNoShutdownPermission is false when a user has the decoder.manage permission', (assert) => {
    const state = new ReduxDataHelper()
      .hasNoShutdownPermission()
      .availablePermissions('sdk.meta', 'sdk.content', 'sdk.packets', 'sys.manage')
      .build();
    assert.deepEqual(hasNoShutdownPermission(state), true);
  });

  test('hasNoShutdownPermission is true when a user does not have the decoder.manage permission', (assert) => {
    const state = new ReduxDataHelper()
      .hasNoShutdownPermission()
      .availablePermissions('sdk.meta', 'sdk.content', 'sdk.packets')
      .build();
    assert.deepEqual(hasNoShutdownPermission(state), true);
  });

});
