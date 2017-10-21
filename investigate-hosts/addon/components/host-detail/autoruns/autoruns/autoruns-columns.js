const defaultColumns = [
  {
    field: 'fileName',
    title: 'File Name'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'path',
      title: 'Path',
      width: '20%',
      disableSort: true
    }
  ],
  windows: [
    {
      field: 'registryPath',
      title: 'REGISTRY PATH',
      width: '20%',
      disableSort: true
    }
  ],
  linux: [
    {
      field: 'path',
      title: 'Path',
      width: '20%',
      disableSort: true
    }
  ]
};

const _generateColumns = function(columns) {
  for (const key in columns) {
    if (columns.hasOwnProperty(key)) {
      columns[key] = [...defaultColumns, ...columns[key]];
    }
  }
  return columns;
};

columnsConfig = _generateColumns(columnsConfig);

export default columnsConfig;