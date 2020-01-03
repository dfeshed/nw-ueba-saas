import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';

@classic
export default class Header extends RSADataTableHeader {
  init() {
    super.init(...arguments);
    this.CONFIG_FIXED_COLUMNS = this.CONFIG_FIXED_COLUMNS || ['checkbox', 'name', 'fileName', 'fileProperties.score', 'dllFileName', 'driverFileName'];
  }

  @action
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
