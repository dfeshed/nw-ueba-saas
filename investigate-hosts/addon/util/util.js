/* Generates OS specific columns config */
export const generateColumns = (customColumns, defaultColumns) => {
  for (const key in customColumns) {
    if (customColumns.hasOwnProperty(key)) {
      customColumns[key] = [...defaultColumns, ...customColumns[key]];
    }
  }
  return customColumns;
};