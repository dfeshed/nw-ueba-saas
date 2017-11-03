import EmberContextMenu from 'ember-context-menu/components/context-menu';
import computed from 'ember-computed-decorators';
import { calculateContextMenuOffset } from 'component-lib/utils/context-menu-utils';

/**
 * This component extends the context-menu component from ember-context-menu and fixes the problem of the menu panel
 * going out of bounds when opened close to the screen border.
 * @public
 */
export default EmberContextMenu.extend({

  @computed('contextMenu.position.left', 'contextMenu.position.top', 'clickEvent.view.window.innerHeight', 'items.length')
  position: (xPos, yPos, screenHeight, itemCount) => {
    const offset = calculateContextMenuOffset(itemCount, screenHeight, yPos);
    const adjustedYPos = (offset < 0) ? (yPos + offset) : yPos;
    return `left: ${xPos}px; top: ${adjustedYPos}px;`;
  }
});