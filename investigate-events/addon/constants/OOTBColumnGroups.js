export const BASE_COLUMNS = [
  { metaName: 'time', displayName: 'Collection Time', width: 175 },
  { metaName: 'medium', displayName: 'Type' }
];

export default [
  {
    id: 'SUMMARY',
    name: 'Summary List',
    isEditable: false,
    columns: BASE_COLUMNS.concat([
      { metaName: 'custom.theme', displayName: 'Theme' },
      { metaName: 'size', displayName: 'Size' },
      { metaName: 'custom.meta-summary', displayName: 'Summary', width: 2000 }
    ])
  }
];
