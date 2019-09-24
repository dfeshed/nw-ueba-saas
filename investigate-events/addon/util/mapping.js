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
            position: col.position,
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
