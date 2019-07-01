export const BASE_COLUMNS = [
  { field: 'time', title: 'Collection Time', width: 135 },
  { field: 'medium', title: 'Type' }
];

export default [
  {
    id: 'SUMMARY',
    name: 'Summary List',
    ootb: true,
    columns: BASE_COLUMNS.concat([
      { field: 'custom.theme', title: 'Theme' },
      { field: 'size', title: 'Size' },
      { field: 'custom.meta-summary', title: 'Summary', width: 2000 }
    ])
  }
];
