import _ from 'lodash';

import { CONTENT_TYPE_PUBLIC, CONTENT_TYPE_USER } from 'investigate-events/constants/profiles';

/**
 * maps columns of a columnGroup for Events Table
 * @param {array[]} columns
 */
export const mapColumnGroupsForEventTable = (eventColumnGroups) => {
  const columnGroups = _.cloneDeep(eventColumnGroups);

  return columnGroups.map((cg) => {
    if (cg.contentType) {
      cg.isEditable = cg.contentType === CONTENT_TYPE_USER || cg.contentType === CONTENT_TYPE_PUBLIC;
      delete cg.contentType;
    }

    if (cg.columns) {
      cg.columns = cg.columns.map((col) => {
        if (col.hasOwnProperty('metaName') && col.hasOwnProperty('displayName')) {

          // fix certain column widths if they exist
          let { width } = col;
          if (col.metaName == 'custom.meta-summary' || col.metaName == 'custom-metasummary') {
            width = 2000;
          } else if (col.metaName == 'time') {
            width = 175;
          }

          return {
            field: col.metaName,
            title: col.displayName,
            // TODO add back when we decide to use it. As of now, they are not used and un-reliable
            // position: col.position,
            visible: col.visible,
            width
          };
        }
        return col;
      });
    }
    return cg;
  });
};
