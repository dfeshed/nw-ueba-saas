import Component from '@ember/component';
import layout from './template';
import safeCallback from 'component-lib/utils/safe-callback';

export default Component.extend({
  tagName: '',
  layout,

  // passed down to rsa-content-tethered-panel
  panelId: 'context-tooltip-1',

  /**
   * Configurable optional action to be invoked when user clicks on a data record (e.g., the incidents count or
   * the alerts count).
   * When invoked, the function will receive three input parameters:
   * - hideAction: {Function} An action that hides the tooltips' rsa-content-tethered-panel when invoked.
   * - entity: ({type: String, id: String}} An object specifying the entity type (e.g., "IP") & identifier (e.g., "10.20.30.40").
   * - data: {{name: String, count: Number, severity: String, lastUpdated: Number}) The clicked data record.
   * @type {Function}
   * @public
   */
  clickDataAction: null,

  actions: {
    handleClick() {
      const args = [ ...arguments ];
      const [ hideAction ] = args;
      if (hideAction) {
        safeCallback(hideAction);
      }
      const otherArgs = args.slice(1);
      safeCallback(...otherArgs);
    }
  }
});
