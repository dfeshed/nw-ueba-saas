/**
 * maps columns of a columnGroup for Events Table
 * @param {array[]} columns
 */
export const mapColumnGroupsForEventTable = (eventColumnGroups) => {
  const columnGroups = JSON.parse(JSON.stringify(eventColumnGroups));

  return columnGroups.map((cg) => {
    if (cg.contentType) {
      cg.isEditable = cg.contentType === 'USER';
      delete cg.contentType;
    }

    if (cg.columns) {
      cg.columns = cg.columns.map((col) => {
        if (col.hasOwnProperty('metaName') && col.hasOwnProperty('displayName')) {
          return {
            field: col.metaName,
            title: col.displayName,
            visible: col.visible,
            width: col.width
          };
        }
        return col;
      });
    }
    return cg;
  });
};
