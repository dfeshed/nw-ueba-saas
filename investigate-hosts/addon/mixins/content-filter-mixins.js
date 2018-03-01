import Mixin from '@ember/object/mixin';
import { run } from '@ember/runloop';
import $ from 'jquery';

/**
 Responsible for opening and closing the tether panel.
 @public
 */
export default Mixin.create({

  /**
   * Requirement is show the tethered panel component once it's added to the UI. Passing showFilterOnInsert flag to indicated
   * show on creation or not. If flag is to button will be clicked using jquery click() method
   * @public
   */
  didInsertElement() {
    const {
      isDestroyed,
      isDestroying,
      config: { showFilterOnInsert }
    } = this.getProperties('isDestroyed', 'isDestroying', 'config');

    if (!isDestroyed && !isDestroying && showFilterOnInsert) {
      run.next(() => {
        const $el = $(this.element);
        const height = $el.height();
        const width = $el.width();
        const panelId = `rsa-content-tethered-panel-toggle-${this.get('config.panelId')}`;
        this.get('eventBus').trigger(panelId, height, width, $el.attr('id'));
        this.set('config.showFilterOnInsert', false);
      });
    }
  }
});
