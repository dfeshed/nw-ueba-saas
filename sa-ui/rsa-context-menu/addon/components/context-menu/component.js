import { computed } from '@ember/object';
import EmberContextMenu from 'ember-context-menu/components/context-menu';
import { calculateContextMenuOffset } from 'rsa-context-menu/utils/context-menu-utils';
import { htmlSafe } from '@ember/string';

/**
 * This component extends the context-menu component from ember-context-menu and fixes the problem of the menu panel
 * going out of bounds when opened close to the screen border.
 * @public
 */
export default EmberContextMenu.extend({

  position: computed(
    'contextMenu.position.left',
    'contextMenu.position.top',
    'clickEvent.view.window.innerHeight',
    'items.length',
    function() {
      const offset = calculateContextMenuOffset(this.items?.length, this.clickEvent?.view?.window?.innerHeight, this.contextMenu?.position?.top);
      const adjustedYPos = (offset < 0) ? (this.contextMenu?.position?.top + offset) : this.contextMenu?.position?.top;
      return htmlSafe(`left: ${this.contextMenu?.position?.left}px; top: ${adjustedYPos}px;`);
    }
  )
});
