import Accordion from 'component-lib/components/rsa-content-accordion/component';
import layout from './template';
import { ieEdgeDetection } from 'component-lib/utils/browser-detection';

export default Accordion.extend({
  layout,

  animate: ieEdgeDetection()
});
