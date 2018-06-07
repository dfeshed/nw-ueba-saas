import Component from '@ember/component';
import contextMenuMixin from 'ember-context-menu';
import { inject as service } from '@ember/service';

export default Component.extend(contextMenuMixin, {
  tagName: 'span',
  classNames: ['content-context-menu'],
  contextualActions: service(),

  contextMenu(e) {
    /**
    * Component need to pass moduleName and metaName in context selection for using configured actions.
    * @private
    */
    let { moduleName, metaName, format } = this.get('contextSelection');
    /**
    * Since the events table is a special custom table which has html tags with meta and value injected from the javascript
    * we cannot use the rsa-context-menu component as-is. This extended class captures the right click event, extracts the
    * moduleName, meta and value from the html span, prepares the contextSelection property before invoking the parent rsa-context-menu action.
    * For this case module name need to get from component.
    * Refer /sa-ui/investigate-events/addon/components/events-table-container/events-table/component.js
    * @private
    */
    if (!moduleName) {
      moduleName = this.get('moduleName');
      metaName = this.get('metaName');
      format = this.get('metaFormat');
    }

    //  If moduleName is passed then contextItems will be ignored and rsa-context-menu will use configured actions.
    if (moduleName) {
      this.set('contextItems', this.get('contextualActions').getContextualActionsForGivenScope(moduleName, metaName, format));
    }
    this._super(e);
  }
});