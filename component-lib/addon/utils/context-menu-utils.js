import $ from 'jquery';
/**
 * Calculates Y-offset for context menu to avoid the it from going out of the screen
 * @public
 */
const APPROX_MENU_ITEM_HEIGHT = 35;

export function calculateContextMenuOffset(itemCount, screenHeight, yPos) {
  let menuItemHeight = $('.context-menu__item').outerHeight(true);
  if (!menuItemHeight) {
    // fallback in the case of first-time when the element is not yet rendered
    menuItemHeight = APPROX_MENU_ITEM_HEIGHT;
  }
  const menuHeight = (itemCount * menuItemHeight) + 3;
  const offset = screenHeight - (yPos + menuHeight);
  return offset < 0 ? offset : 0;
}