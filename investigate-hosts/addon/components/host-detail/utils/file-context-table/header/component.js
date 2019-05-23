import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';

export default RSADataTableHeader.extend({

  CONFIG_FIXED_COLUMNS: ['checkbox', 'name', 'fileName', 'fileProperties.score', 'dllFileName', 'driverFileName'],

  actions: {
    toggleColumn(column) {
      if (!this.CONFIG_FIXED_COLUMNS.includes(column.field)) {
        column.toggleProperty('selected');
        const columns = this.get('table.visibleColumns');
        if (this.onToggleColumn) {
          this.onToggleColumn(columns);
        }
      }
    }
  }
});
