import PropertyPanel from 'investigate-hosts/components/host-detail/base-property-panel/component';
import fileFormat from './file-format-config';

/**
 * Generic component for displaying the file context data inside host details page
 * @public
 */
export default PropertyPanel.extend({
  updateConfig(data, [...config]) {
    if (this.get('data').fileProperties) {
      const { fileProperties: { format } } = data;
      if (fileFormat[format]) {
        config.pushObject(fileFormat[format]);
      }
    }
  }
});
