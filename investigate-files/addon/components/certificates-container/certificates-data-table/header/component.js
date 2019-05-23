import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { next } from '@ember/runloop';

const tableHeader = RSADataTableHeader.extend({
  actions: {
    toggleColumn(column) {
      column.toggleProperty('selected');
      const columns = this.get('table.visibleColumns');
      next(() => {
        this.onToggleColumn(columns);
      });
    }
  }
});
export default tableHeader;
