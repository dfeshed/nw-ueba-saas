import Component from '@glimmer/component';
import { inject as service } from '@ember/service'; // with polyfill
import { action } from '@ember/object';
import { tracked } from '@glimmer/tracking';
import { mashUpData } from 'springboard-widget-lib/selectors/data-selector';

export default class CustomWidgetComponent extends Component {
  @service inViewport;

  element = null;

  isActive = false;

  data = null;

  @tracked isLoadingData = true;

  get widgetData() {
    return { ...this.data };
  }

  @action
  didInsertNode(element, [instance]) {
    const viewportTolerance = { right: 220 };
    const { onEnter } = instance.inViewport.watchElement(element, { viewportTolerance });
    onEnter(instance.didEnterViewport.bind(instance));
  }

  /**
   * Load the data only when component is visible
   */
  didEnterViewport() {
    if (!this.isActive && this.args.widget && this.args.fetchData) {
      this.isLoadingData = true;
      this.isActive = true;
      this.args.fetchData(this.args.widget).then((response) => {
        const newValue = mashUpData({ data: response.data, widget: this.args.widget });
        this.data = newValue;
        this.isLoadingData = false;
      });
    }
  }

  willDestroy() {
    this.inViewport.stopWatching(this.element);
    const { observerAdmin } = this.inViewport;
    if (observerAdmin) {
      observerAdmin.instance.elementRegistry.destroyRegistry();
      observerAdmin.instance.registry.destroyRegistry();
    }
    super.willDestroy();
  }
}
