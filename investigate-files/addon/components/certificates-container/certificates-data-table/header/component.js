import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { next } from '@ember/runloop';

@classic
class tableHeader extends RSADataTableHeader {
  @action
  toggleColumn(column) {
    const field = column.get('field');
    // certificateStatus and friendlyName required columns, don't allow to deselect
    if (field === 'certificateStatus' || field === 'friendlyName') {
      return;
    }
    column.toggleProperty('selected');
    const columns = this.get('table.visibleColumns');
    next(() => {
      this.onToggleColumn(columns);
    });
  }
}

export default tableHeader;
