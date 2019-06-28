import Service, { inject as service } from '@ember/service';

export default Service.extend({

  /**
   * @private
   * A boolean that tells us if the permissions have been loaded or not
   */
  permissionsAvailable: null,

  /**
   * @private
   * Holds any callbacks waiting on the availability of permission information
   */
  pendingCallbacks: null,

  /**
   * @private
   * The array of permissions as strings
   */
  permissions: null,

  /**
   * @private
   * The unsubscribe handler given by the redux service
   */
  unsubscribe: null,

  /**
   * @private
   * The ember-redux service that allows us to see the permissions
   */
  redux: service(),

  /**
   * @private
   * Listens for state changes up until the permissions are loaded
   */
  init() {
    this.set('permissionsAvailable', false);
    this.set('pendingCallbacks', []);
    this.set('unsubscribe', this.get('redux').subscribe(() => {
      const state = this.get('redux').getState().shared;
      if (state.availablePermissions) {
        this.set('permissionsAvailable', true);
        this.permissionsLoaded(state.availablePermissions);
        this.get('unsubscribe')();
      }
    }));
  },

  /**
   * Called when the permissions are first loaded
   * @private
   * @param {Array} permissions The list of permissions the user has
   */
  permissionsLoaded(permissions) {
    this.set('permissions', permissions);
    const pendingCallbacks = this.get('pendingCallbacks');
    pendingCallbacks.forEach((waiter) => {
      this.conditionallyExecute(waiter.requires, waiter.withPermission, waiter.withoutPermission);
    });
    this.set('pendingCallbacks', null);
  },

  /**
   * A simple way to conditionally execute code only if a user has a permission or set of permissions.
   * Also can execute code on a lack of permission.
   * @public
   * @param {Array} requires The list of permissions required
   * @param {Function} [withPermission] The function to call if the user has the required permissions
   * @param {Function} [withoutPermission] The function to call if the user does not have the required permissions
   */
  require(requires, withPermission, withoutPermission) {
    if (typeof requires === 'string') {
      requires = [ requires ];
    }
    const permissionsAvailable = this.get('permissionsAvailable');
    if (permissionsAvailable) {
      this.conditionallyExecute(requires, withPermission, withoutPermission);
    } else {
      this.set('pendingCallbacks', this.get('pendingCallbacks').concat({
        requires,
        withPermission,
        withoutPermission
      }));
    }
  },

  /**
   * @private
   */
  conditionallyExecute(requires, withPermission, withoutPermission) {
    const permissions = this.get('permissions');
    const hasPermission = requires.every((permission) => {
      return permissions.indexOf(permission) >= 0;
    });
    if (hasPermission && withPermission) {
      withPermission();
    } else if (!hasPermission && withoutPermission) {
      withoutPermission();
    }
  }
});
