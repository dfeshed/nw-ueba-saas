import _ from 'lodash';
import { set } from '@ember/object';
import { getOuterHeight } from 'component-lib/utils/jquery-replacement';

/**
 * Calculates Y-offset for context menu to avoid the it from going out of the screen
 * @public
 */
const APPROX_MENU_ITEM_HEIGHT = 35;

export function calculateContextMenuOffset(itemCount, screenHeight, yPos) {
  let menuItemHeight = getOuterHeight(document.querySelector('.context-menu__item'), true);
  if (!menuItemHeight) {
    // fallback in the case of first-time when the element is not yet rendered
    menuItemHeight = APPROX_MENU_ITEM_HEIGHT;
  }
  const menuHeight = (itemCount * menuItemHeight) + 3;
  const offset = screenHeight - (yPos + menuHeight);
  return offset < 0 ? offset : 0;
}

/**
 * This utility to merge two context menu groups..
 * Input: [{label: 'a', action, label: 'b', subactions: [{label: 'c', action1}]}], [{label: 'd', action}, label: 'b', subactions: [{label: 'e', action1}]}]
 * Output: [{label: 'a', action}, {label: 'd', action}, {label: 'b', subactions: [{label: 'c', action1}, {label: 'e', action1}]}]
 * @public
 */
export const mergeObjectArray = (srcArray, mergeArray) => {
  _.forEach(mergeArray, (mergeObj) => {
    const existingObj = _.find(srcArray, _.matchesProperty('label', mergeObj.label));
    if (!existingObj) {
      srcArray.push(mergeObj);
    } else if (mergeObj.subActions) {
      set(existingObj, 'subActions', mergeObjectArray(existingObj.subActions, mergeObj.subActions));
    }
  });
  return srcArray;
};

/**
 * This returns all CSS styles supported for that compoent to form consolidated context menu items.
 * @public
 */
export const componentCSSList = {
  EventAnalysisPanel: ['nw-event-value', 'nw-event-value-drillable-equals', 'nw-event-value-drillable-not-equals'],
  Text: ['nw-event-value-drillable-contains']
};
