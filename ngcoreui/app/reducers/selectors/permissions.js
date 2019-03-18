import { createSelector } from 'reselect';

const _availablePermissions = (state) => state.availablePermissions;
const _deviceInfo = (state) => state.deviceInfo;

/**
 * Tests if a user can start/stop aggregation. Only checks for the manage
 * role of the currently running module.
 */
const hasNoAggPermission = createSelector(
  [ _availablePermissions, _deviceInfo ],
  (availablePermissions, deviceInfo) => {
    if (!availablePermissions || !deviceInfo) {
      // Assume they have permission until we know they do not
      return false;
    } else {
      return availablePermissions.indexOf(`${deviceInfo.module}.manage`) < 0;
    }
  }
);

/**
 * Tests if a user can start/stop decoder capture.
 */
const hasNoCapturePermission = createSelector(
  [ _availablePermissions ],
  (availablePermissions) => {
    if (!availablePermissions) {
      // Assume they have permission until we know they do not
      return false;
    } else {
      return availablePermissions.indexOf('decoder.manage') < 0;
    }
  }
);

/**
 * Tests if a user can shutdown the service
 */
const hasNoShutdownPermission = createSelector(
  [ _availablePermissions ],
  (availablePermissions) => {
    if (!availablePermissions) {
      // Assume they have permission until we know they do not
      return false;
    } else {
      return availablePermissions.indexOf('sys.manage') < 0;
    }
  }
);

export {
  hasNoAggPermission,
  hasNoCapturePermission,
  hasNoShutdownPermission
};
