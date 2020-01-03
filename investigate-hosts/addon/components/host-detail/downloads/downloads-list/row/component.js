import classic from 'ember-classic-decorator';
import { classNameBindings } from '@ember-decorators/component';
import { computed } from '@ember/object';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';

/**
 * Extension of the Data Table default row class for supporting focus on the row
 * @public
 */
@classic
@classNameBindings('isRowChecked')
export default class Row extends DataTableBodyRow {
  @computed('item', 'selections')
  get isRowChecked() {
    const selections = this.selections || [];
    const isSelected = selections.findBy('id', this.item.id);
    return !!isSelected;
  }

  @computed('item')
  get contextItems() {
    const contextConf = [];
    if (this.get('disableActions').hasManageAccess) {
      contextConf.push({
        label: 'deleteFiles',
        order: 2,
        prefix: 'investigateHosts.downloads.buttons.',
        showDivider: true,
        action(selection, context) {
          context.deleteFiles();
        },
        disabled(selection, context) {
          return context.get('disableActions').deleteFile;
        }
      },
      {
        label: 'saveLocalCopy',
        order: 1,
        prefix: 'investigateHosts.downloads.buttons.',
        showDivider: true,
        action(selection, context) {
          context.saveLocalCopy();
        },
        disabled(selection, context) {
          return context.get('disableActions').saveLocalCopy;
        }
      });
    }

    return contextConf.sortBy('order');
  }
}
