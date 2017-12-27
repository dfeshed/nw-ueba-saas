export const BASE_COLUMNS = [
  { field: 'time', title: 'Event Time', width: 100 },
  { field: 'medium', title: 'Event Type' }
];

export default [
  {
    id: 'SUMMARY',
    name: 'Summary List',
    ootb: true,
    columns: BASE_COLUMNS.concat([
      { field: 'custom.theme', title: 'Theme' },
      { field: 'size', title: 'Size' },
      { field: 'custom.meta-summary', title: 'Summary', width: 'auto' }
    ])
  }
];
